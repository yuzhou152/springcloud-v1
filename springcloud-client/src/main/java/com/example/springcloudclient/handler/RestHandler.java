package com.example.springcloudclient.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RestHandler {
	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(value = "/ribbon-consumer-get", method = RequestMethod.GET)
	public String indexGet() {
		ResponseEntity<String> response = restTemplate.getForEntity("http://HELLO-SERVICE/hello-get", String.class);
		String result1 = response.getBody();
		String result2 = restTemplate.getForObject("http://HELLO-SERVICE/hello-get", String.class);
		return "result1 : " + result1 + " result2 : " + result2;
	}

	@RequestMapping(value = "/ribbon-consumer-post", method = RequestMethod.GET)
	public String indexPost() {
		ResponseEntity<String> response = restTemplate.postForEntity("http://HELLO-SERVICE/hello-post", "Send Data", String.class);
		String result1 = response.getBody();
		String result2 = restTemplate.postForObject("http://HELLO-SERVICE/hello-post", "Send Data", String.class);
		return "result1 : " + result1 + " result2 : " + result2;
	}

	@RequestMapping(value = "/ribbon-consumer-put", method = RequestMethod.GET)
	public String indexPut() {
		restTemplate.put("http://HELLO-SERVICE/hello-put", "Send Data");
		return "put sucessful, void method.";
	}

	@RequestMapping(value = "/ribbon-consumer-delete", method = RequestMethod.GET)
	public String indexDelete() {
		restTemplate.delete("http://HELLO-SERVICE/hello-delete/1002");
		return "delete sucessful, void method.";
	}
}
