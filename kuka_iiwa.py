import pybullet as p
import math
import time
from datetime import datetime
import pybullet_data


p.connect(p.GUI)
p.setAdditionalSearchPath(pybullet_data.getDataPath())
p.loadURDF("plane.urdf", [0, 0, -0.3])
robot = p.loadURDF("kuka_iiwa/model.urdf", [0, 0, 0])

p.resetBasePositionAndOrientation(robot, [0, 0, 0], [0, 0, 0, 1])
kukaEndEffectorIndex = 6
numJoints = p.getNumJoints(robot)
p.setRealTimeSimulation(0)

kukaEndEffectorIndex = 6
pos = [0,1,1]
orientation = p.getQuaternionFromEuler([0, 0., 0.])

jointPoses = p.calculateInverseKinematics(robot, kukaEndEffectorIndex, pos, targetOrientation = orientation)



for i in range(numJoints):
    p.setJointMotorControl2(bodyIndex=robot, jointIndex=i, controlMode=p.POSITION_CONTROL, targetPosition=jointPoses[i])
for _ in range(300):
    p.stepSimulation()
    time.sleep(1./10.)

effector = p.getJointState(robot, kukaEndEffectorIndex)

print(effector)
p.disconnect()