import jnius_config
import os
jnius_config.set_classpath(os.getenv('PJNIUS_CLASSPATH'))
print(os.getenv('PJNIUS_CLASSPATH'))
from jnius import autoclass

ServiceDiscovery = autoclass('com.obomprogramador.grpc.ApacheCuratorDiscovery')
serviceDiscovery = ServiceDiscovery('127.0.0.1:2181')

print(serviceDiscovery.getInstanceAddress('verifySignature'))
print(serviceDiscovery.getInstanceAddress('verifySignature'))
print(serviceDiscovery.getInstanceAddress('verifySignature'))

