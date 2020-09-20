import json
import threading

import pybullet as p
import pybullet_data
import zmq

receivedNewKinematics = False
newKinematics = []


def thread_function():
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


x = threading.Thread(target=thread_function)
x.start()

p.connect(p.GUI)
p.setAdditionalSearchPath(pybullet_data.getDataPath())
p.loadURDF("plane.urdf", [0, 0, -0.3])
robot = p.loadURDF("kuka_iiwa/model.urdf", [0, 0, 0])

p.resetBasePositionAndOrientation(robot, [0, 0, 0], [0, 0, 0, 1])
kukaEndEffectorIndex = 6
numJoints = p.getNumJoints(robot)
p.setRealTimeSimulation(0)


def execute_inverse_kinematics(x, y, z, alpha, theta, psy):
    pos = [x, y, z]
    orientation = p.getQuaternionFromEuler([alpha, theta, psy])
    jointPoses = p.calculateInverseKinematics(robot, kukaEndEffectorIndex, pos, targetOrientation=orientation)

    for i in range(numJoints):
        p.setJointMotorControl2(bodyIndex=robot, jointIndex=i, controlMode=p.POSITION_CONTROL,
                                targetPosition=jointPoses[i])


while True:
    if receivedNewKinematics:
        receivedNewKinematics = False
        execute_inverse_kinematics(newKinematics[0], newKinematics[1], newKinematics[2], newKinematics[3],
                                   newKinematics[4], newKinematics[5])
    p.stepSimulation()
    # time.sleep(1./10.)

p.disconnect()
