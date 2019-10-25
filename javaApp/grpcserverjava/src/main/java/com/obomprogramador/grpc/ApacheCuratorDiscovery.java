package com.obomprogramador.grpc;

import java.util.logging.Logger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;

public class ApacheCuratorDiscovery {
	private CuratorFramework curatorFramework;
	private String zkAddress;
	private ServiceDiscovery<Object> serviceDiscovery; 
	private final String BASE_NAME = "/";
	private ServiceProvider serviceProvider;
	private static final Logger logger = Logger.getLogger(ApacheCuratorWrapper.class.getName());

	public ApacheCuratorDiscovery(String zookeeperAdress) throws Exception { 
		this.zkAddress = zookeeperAdress;
		this.curatorFramework = 
				CuratorFrameworkFactory.newClient(this.zkAddress, new RetryNTimes(5, 1000));
		curatorFramework.start();
		this.serviceDiscovery = ServiceDiscoveryBuilder.builder(Object.class)
			    .basePath(this.BASE_NAME)
			    .client(curatorFramework)
			    .build();
		this.serviceDiscovery.start();		
	}
	
	public String getInstanceAddress(String serviceName) throws Exception {
		String address = null;
		if (this.serviceProvider == null) {
			this.serviceProvider = 
					this.serviceDiscovery
					.serviceProviderBuilder()
					.serviceName(serviceName)
					.providerStrategy(new RoundRobinStrategy<Object>())
					.build();
			this.serviceProvider.start();
		}
		ServiceInstance<Void> si = this.serviceProvider.getInstance();
		address = si.getAddress() + ":" + si.getPort();	
		return address;
	}
}
