package com.example.springcloudclient.service;

import org.springframework.web.client.RestTemplate;

import com.example.springcloudclient.pojo.ZgUser;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixRequestCache;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;

/**
 * 可被一个订阅者发射一次
 */
public class HelloServiceCommand extends HystrixCommand<ZgUser>{
	private final static HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("CommandKeyName");
	
	private RestTemplate restTemplate;
	
	private Long id;
	
	public HelloServiceCommand(RestTemplate restTemplate, Long id) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GroupName"))//未设置ThreadPoolKey则根据组名进行仓壁划分，用于统计
				.andCommandKey(commandKey)
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ThreadPoolKey")));
		this.restTemplate = restTemplate;
		this.id = id;
	}

	@Override
	protected ZgUser run() throws Exception {
		return restTemplate.getForObject("http://HELLO-SERVICE/getObject/{"+id+"}", ZgUser.class, id);
//		return restTemplate.getForObject("http://HELLO-SERVICE/hystrix-timeout", ZgUser.class, id);
	}

	/**
	 * add cache	需要初始化上下文，并对上下文做threadlocal
	 */
	@Override
	protected String getCacheKey() {
		return String.valueOf(id);
	}
	
	/**
	 * clear cache
	 */
	public static void flushCache(Long id) {
		HystrixRequestCache.getInstance(commandKey, HystrixConcurrencyStrategyDefault.getInstance()).clear(String.valueOf(id));
	}
	
	/**
	 * 服务降级
	 */
	@Override
	protected ZgUser getFallback() {
		return new ZgUser(500, "time out");
	}
}
