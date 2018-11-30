package com.example.springcloudserver.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestHandler {

	@Autowired
	private DiscoveryClient client;

	@RequestMapping(value = "/hello-get", method = RequestMethod.GET)
	public String indexGet() {
		ServiceInstance instance = client.getLocalServiceInstance();
		System.out.println("/hello, host:" + instance.getHost() + " service_id:" + instance.getServiceId());
		return "Hello World Get";
	}

	@RequestMapping(value = "/hello-post", method = RequestMethod.POST)
	public String indexPost(@RequestBody String data) {
		ServiceInstance instance = client.getLocalServiceInstance();
		System.out.println("/hello, host:" + instance.getHost() + " service_id:" + instance.getServiceId());
		return "Hello World Post, data : " + data;
	}

	@RequestMapping(value = "/hello-put", method = RequestMethod.PUT)
	public String indexPut(@RequestBody String data) {
		ServiceInstance instance = client.getLocalServiceInstance();
		System.out.println("/hello, host:" + instance.getHost() + " service_id:" + instance.getServiceId());
		return "Hello World Put, data : " + data;
	}

	@RequestMapping(value = "/hello-delete/{data}", method = RequestMethod.DELETE)
	public String indexDelete(@PathVariable String data) {
		ServiceInstance instance = client.getLocalServiceInstance();
		System.out.println("/hello, host:" + instance.getHost() + " service_id:" + instance.getServiceId());
		return "Hello World Delete, data : " + data;
	}
	
}
