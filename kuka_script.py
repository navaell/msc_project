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

input("prompt: ")


while (pybullet.isConnected()):
  pybullet.stepSimulation()



#moves blue joint 90deg
#pybullet.resetSimulation()
#plane = pybullet.loadURDF("plane.urdf")
#robot = pybullet.loadURDF("kuka_experimental/kuka_kr210_support/urdf/kr210l150.urdf",
#                         [0, 0, 0], useFixedBase=1)
#pybullet.setGravity(0, 0, -9.81)
#pybullet.setRealTimeSimulation(0)
#pybullet.setJointMotorControl2(robot, 0, pybullet.POSITION_CONTROL,targetPosition=1.5)
#for _ in range(20000):
  # pybullet.stepSimulation()
  #time.sleep(1./30000.)

