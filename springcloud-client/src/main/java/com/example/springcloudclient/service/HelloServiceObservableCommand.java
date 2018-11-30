package com.example.springcloudclient.service;

import org.springframework.web.client.RestTemplate;

import rx.Observable;
import rx.Subscriber;

import com.example.springcloudclient.pojo.ZgUser;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.HystrixRequestCache;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;

/**
 * 可被一个订阅者多次发射
 */
public class HelloServiceObservableCommand extends HystrixObservableCommand<ZgUser>{
	private final static HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("CommandKeyName");
	
	private RestTemplate restTemplate;
	
	private Long id;
	
	public HelloServiceObservableCommand(RestTemplate restTemplate, Long id) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GroupName"))//未设置ThreadPoolKey则根据组名进行仓壁划分，用于统计
				.andCommandKey(commandKey));
		this.restTemplate = restTemplate;
		this.id = id;
	}

	@Override
	protected Observable<ZgUser> construct() {
		return Observable.create(new Observable.OnSubscribe<ZgUser>() {
			@Override
			public void call(Subscriber<? super ZgUser> observer) {
				if (!observer.isUnsubscribed()) {
					ZgUser zgUser = restTemplate.getForObject("http://HELLO-SERVICE/getObject/{"+id+"}", ZgUser.class, id);
					observer.onNext(zgUser);
					observer.onCompleted();
				}
			}
		});
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
	protected Observable<ZgUser> resumeWithFallback() {
		return null;
	}

}
