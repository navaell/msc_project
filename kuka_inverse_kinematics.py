import pybullet
import pybullet_data
import time

pybullet.connect(pybullet.GUI)
pybullet.setAdditionalSearchPath(pybullet_data.getDataPath())
plane = pybullet.loadURDF("plane.urdf")

#loaded with a fixed base
robot = pybullet.loadURDF("kuka_experimental/kuka_kr210_support/urdf/kr210l150.urdf",
                          [0, 0, 0], useFixedBase=1)

pybullet.setGravity(0, 0, -9.81)
pybullet.setRealTimeSimulation(0)

orientation = pybullet.getQuaternionFromEuler(([3.14, 0., 0.]))
targetPositionsJoints = pybullet.calculateInverseKinematics(robot, 6, [0.1, 0.1, 0.1], targetOrientation = orientation)
pybullet.setJointMotorControlArray(robot, range(6), pybullet.POSITION_CONTROL,targetPositions=targetPositionsJoints)


target = pybullet.calculateInverseKinematics(robot, 7, [0.1, 0.1, 0.1], targetOrientation=orientation)
pybullet.setJointMotorControlArray(robot, range(6), pybullet.POSITION_CONTROL,targetPositions=target)

for _ in range(300):
   pybullet.stepSimulation()
   time.sleep(1./100.)

#pybullet.disconnect()
