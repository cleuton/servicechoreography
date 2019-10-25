package com.obomprogramador.grpc;

import java.util.logging.Logger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;

public class ApacheCuratorWrapper {
	
	private CuratorFramework curatorFramework;
	private String zkAddress;
	private ServiceDiscovery<Object> serviceDiscovery; 
	private final String BASE_NAME = "/";
	private static final Logger logger = Logger.getLogger(ApacheCuratorWrapper.class.getName());
	
	public ApacheCuratorWrapper(String zookeeperAdress) { 
		this.zkAddress = zookeeperAdress;
		this.curatorFramework = 
				CuratorFrameworkFactory.newClient(this.zkAddress, new RetryNTimes(5, 1000));
		curatorFramework.start();
	}
	
	public String registerInstance(String url, int port, String name) throws Exception {
		ServiceInstance<Object> serviceInstance = ServiceInstance.builder()
		        .address(url)
		        .port(port)
		        .name(name)
		        .uriSpec(new UriSpec("gRPC://" + url + ":" + port))
		        .build();
		
		this.serviceDiscovery = ServiceDiscoveryBuilder.builder(Object.class)
			    .basePath(this.BASE_NAME)
			    .client(curatorFramework)
			    .thisInstance(serviceInstance)
			    .build();
		this.serviceDiscovery.start();
		logger.info("Registered instance id: " + serviceInstance.getId() + " name: " + serviceInstance.getName());
		return serviceInstance.getId();
	}
	
	
}
