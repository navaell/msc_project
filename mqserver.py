import json
import threading

import pybullet as p
import pybullet_data
import zmq

receivedNewKinematics = False
newKinematics = []


def zero_mq_server():
    """
    This function starts the ZeroMQ server that listens to commands from the mobile phone. This should be started
    on a new thread
    """
    global receivedNewKinematics
    global newKinematics
    context = zmq.Context()
    socket = context.socket(zmq.REP)
    socket.bind("tcp://*:5558")
    while True:
        message = socket.recv()
        print("Received: %s" % message)
        kinematics = json.loads(message)
        newKinematics = [kinematics["x_coord"], kinematics["y_coord"], kinematics["z_coord"], kinematics["a_orient"],
                         kinematics["b_orient"], kinematics["c_orient"]]
        receivedNewKinematics = True
        socket.send(b"Received")


# start the zero mq server on a new thread
zero_mq_server_thread = threading.Thread(target=zero_mq_server)
zero_mq_server_thread.start()

# start PyBullet
p.connect(p.GUI)
p.setAdditionalSearchPath(pybullet_data.getDataPath())
p.loadURDF("plane.urdf", [0, 0, -0.3])
robot = p.loadURDF("kuka_iiwa/model.urdf", [0, 0, 0])

p.resetBasePositionAndOrientation(robot, [0, 0, 0], [0, 0, 0, 1])
kukaEndEffectorIndex = 6
numJoints = p.getNumJoints(robot)
p.setRealTimeSimulation(0)


def execute_inverse_kinematics(x, y, z, alpha, theta, psy):
    """
    Executes the inverse kinematics for a given position and orientation
    """
    pos = [x, y, z]
    orientation = p.getQuaternionFromEuler([alpha, theta, psy])
    joint_poses = p.calculateInverseKinematics(robot, kukaEndEffectorIndex, pos, targetOrientation=orientation)

    for i in range(numJoints):
        p.setJointMotorControl2(bodyIndex=robot, jointIndex=i, controlMode=p.POSITION_CONTROL,
                                targetPosition=joint_poses[i])


# On a loop, keep running the simulation while checking if new commands have been received from BulletBot. If new
#  commands have been received, execute them
while True:
    if receivedNewKinematics:
        receivedNewKinematics = False
        execute_inverse_kinematics(newKinematics[0], newKinematics[1], newKinematics[2], newKinematics[3],
                                   newKinematics[4], newKinematics[5])
    p.stepSimulation()
