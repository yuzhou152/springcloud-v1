package com.example.springcloudserver.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.springcloudserver.pojo.ZgUser;

@RestController
public class ServerController {

	@Autowired
	private DiscoveryClient client;
	
	@RequestMapping(value = "hystrix", method = RequestMethod.GET)
	public String hystrix() {
		ServiceInstance instance = client.getLocalServiceInstance();
		System.out.println("/hystrix, host:" + instance.getHost() + " service_id:" + instance.getServiceId());
		return "hystrix";
	}
	
	@RequestMapping(value = "hystrix-timeout", method = RequestMethod.GET)
	public String hystrixTimeout() {
		ServiceInstance instance = client.getLocalServiceInstance();
		LockSupport.parkNanos(3000000000L);
		System.out.println("/hystrix-timeout, host:" + instance.getHost() + " service_id:" + instance.getServiceId());
		return "hystrix-timeout";
	}

	@RequestMapping(value = "getObject/{id}", method = RequestMethod.GET)
	public ZgUser getObject(@PathVariable long id) {
		ServiceInstance instance = client.getLocalServiceInstance();
		System.out.println("/hystrix, host:" + instance.getHost() + " service_id:" + instance.getServiceId());
		return new ZgUser(1,"name1");
	}
	
	@RequestMapping(value = "getList/{userId}", method = RequestMethod.GET)
	public List<ZgUser> getObject(@PathVariable List<Long> userId) {
		ServiceInstance instance = client.getLocalServiceInstance();
		System.out.println("/hystrix, host:" + instance.getHost() + " service_id:" + instance.getServiceId() + " param:" + userId.toString());
		List<ZgUser> result = new ArrayList<ZgUser>();
		result.add(new ZgUser(1,"name1"));
		result.add(new ZgUser(10,"name1"));
		return result;
	}
	
	
}
