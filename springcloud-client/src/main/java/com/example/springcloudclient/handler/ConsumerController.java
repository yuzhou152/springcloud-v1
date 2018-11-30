package com.example.springcloudclient.handler;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import rx.Observable;

import com.example.springcloudclient.pojo.ZgUser;
import com.example.springcloudclient.service.HelloService;
import com.example.springcloudclient.service.HelloServiceCommand;
import com.example.springcloudclient.service.HelloServiceObservableCommand;
import com.example.springcloudclient.service.collapser.HelloCollapseCommand;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

/**
 * 请求方式两种：snyc,async
 * 写法两种：1.HystrixCommand一次订阅一次请求	2.HystrixObservableCommand一次订阅多次请求
 */
@RestController
public class ConsumerController {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	HelloService helloService;

	/**
	 * 常规模式
	 * 仓壁
	 * 缓存
	 * 降级
	 */
	@RequestMapping(value = "/ribbon-consumer-hystrix-normal/{id}", method = RequestMethod.GET)
	public String normal(@PathVariable long id) {
	    HystrixRequestContext context = HystrixRequestContext.initializeContext();
		return helloService.helloServiceUserful(id);
	}
	
	@RequestMapping(value = "/ribbon-consumer-hystrix", method = RequestMethod.GET)
	public String hystrix() {
		return helloService.helloService();
	}
	
	@RequestMapping(value = "/ribbon-consumer-hystrix-timeout", method = RequestMethod.GET)
	public String hystrixTimeout() {
		return helloService.helloServiceTimeout();
	}
	
	@RequestMapping(value = "/ribbon-consumer-hystrix-getObject", method = RequestMethod.GET)
	public ZgUser hystrixgetObject() {
		//sync hot Observable many request		HystrixObservableCommand
		Observable<ZgUser> obSyncMany = new HelloServiceObservableCommand(restTemplate, 1L).observe();
		//async cold Observable many request	HystrixObservableCommand
		Observable<ZgUser> obASyncMany = new HelloServiceObservableCommand(restTemplate, 1L).toObservable();
		//sync/async cold Observable 		HystrixObservableCommand
		Observable<ZgUser> zgUserSyncMany = helloService.hystrixObservableGetObject(1L);

		//sync 执行				HystrixCommand
		ZgUser u = new HelloServiceCommand(restTemplate, 1L).execute();
		//async 执行				HystrixCommand
		Future<ZgUser> futureUser = new HelloServiceCommand(restTemplate, 1L).queue();
		//sync cold Observable	HystrixCommand
		Observable<ZgUser> obSync = new HelloServiceCommand(restTemplate, 1L).observe();
		//async cold Observable	HystrixCommand
		Observable<ZgUser> obASync = new HelloServiceCommand(restTemplate, 1L).toObservable();
		//sync 执行				HystrixCommand
		ZgUser zgUser = helloService.hystrixGetObjectSync(3L);
		//async 执行				HystrixCommand
		Future<ZgUser> fZgUser = helloService.hystrixGetObjectASync(3L);
		
		try {
			System.out.println(zgUser);
			ZgUser async = futureUser.get();
			System.out.println(u);
			System.out.println(async);
			ZgUser async2 = fZgUser.get();
			obASync.subscribe();
			obASyncMany.subscribe();
			zgUserSyncMany.subscribe();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return u;
	}

	@RequestMapping(value = "/ribbon-consumer-hystrix-cache", method = RequestMethod.GET)
	public ZgUser hystrixCache() {
		//初始化context
	    HystrixRequestContext context = HystrixRequestContext.initializeContext();
		ZgUser u = new HelloServiceCommand(restTemplate, 1L).execute();
		ZgUser u2 = new HelloServiceCommand(restTemplate, 1L).execute();
		HelloServiceCommand.flushCache(1L);
		ZgUser u3 = new HelloServiceCommand(restTemplate, 1L).execute();

		helloService.hystrixGetObjectCache(new ZgUser(1, "name"));
		helloService.hystrixGetObjectCache(new ZgUser(1, "name"));
		helloService.update(new ZgUser(1, "name"));
		helloService.hystrixGetObjectCache(new ZgUser(1, "name"));
		helloService.hystrixGetObjectCache(new ZgUser(1, "name"));
		//关闭context
		context.shutdown();
		return null;
	}

	@RequestMapping(value = "/ribbon-consumer-hystrix-collapse", method = RequestMethod.GET)
	public ZgUser hystrixCollapse() {
		//B版本中测试未通过
		System.out.println(helloService.hystrixGetObjectById(1L));
		System.out.println(helloService.hystrixGetObjectById(1L));
		return null;
	}
}
