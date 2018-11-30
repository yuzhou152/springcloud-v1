package com.example.springcloudclient.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import rx.Observable;
import rx.Subscriber;

import com.example.springcloudclient.pojo.ZgUser;
import com.netflix.hystrix.HystrixCollapser.Scope;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;

/**
 * Hystrix	断路器
 * 1.降级处理 @HystrixCommand-fallbackMethod
 * 2.缓存 @CacheResult @CacheRemove @CacheKey
 * 3.仓壁 @HystrixCommand-commandKey,groupKey,threadPoolKey
 * 4.请求合并 @HystrixCollapser ！测试未通过
 */
@Service
public class HelloService {

	@Autowired
	private RestTemplate restTemplate;

	
	/**
	 * 常规模式
	 * 仓壁
	 * 缓存
	 * 降级
	 */
	@CacheResult
	@HystrixCommand(fallbackMethod = "helloFallbackUserful", commandKey="helloServiceUserful", groupKey="helloService", threadPoolKey="replyonprocess_helloservice")
	public String helloServiceUserful(@CacheKey Long id) {
	    String result = restTemplate.getForEntity("http://HELLO-SERVICE/hystrix", String.class).getBody();
		return result;
	}
	public String helloFallbackUserful(Long id, Throwable e) {
		return "error";
	}
	
	
	/**
	 * 仓壁设置
	 * 未设置ThreadPoolKey则根据组名进行仓壁划分，用于统计
	 */
	@HystrixCommand(fallbackMethod = "helloFallback", commandKey="helloService", groupKey="GroupName", threadPoolKey="ThreadPoolKey",
			commandProperties={
			@HystrixProperty(name="execution.isolation.strategy", value="THREAD")
			})
	
	public String helloService() {
		return restTemplate.getForEntity("http://HELLO-SERVICE/hystrix", String.class).getBody();
	}
	public String helloFallback(Throwable e) {
		return "error";
	}
	
	@HystrixCommand(fallbackMethod = "helloFallbackTimeout")
	public String helloServiceTimeout() {
		return restTemplate.getForEntity("http://HELLO-SERVICE/hystrix-timeout", String.class).getBody();
	}
	public String helloFallbackTimeout(Throwable e) {
		return "timeout 2s";
	}
	
	/**
	 * 请求合并
	 */
	@HystrixCollapser(batchMethod="helloServiceGetList", scope = Scope.GLOBAL, 
			collapserProperties={@HystrixProperty(name="timerDelayInMilliseconds",value="10")})
	public ZgUser hystrixGetObjectById(Long id) {
		return restTemplate.getForEntity("http://HELLO-SERVICE/getObject/{" + id + "}", ZgUser.class, id).getBody();
	}
	@HystrixCommand(fallbackMethod = "helloFallbackList")
	public List<ZgUser> helloServiceGetList(List<Long> userId) {
		String idStr = "";
		for (Long long1 : userId) {
			idStr = idStr + long1.toString()+ ",";
		}
		idStr = idStr.substring(0, idStr.length()-1);
		List<ZgUser> result = restTemplate.getForEntity("http://HELLO-SERVICE/getList/" + idStr, List.class).getBody();
		System.out.println(result);
		return result;
	}

	public List<ZgUser> helloFallbackList(List<Long> userId, Throwable e) {
		List<ZgUser> l = new ArrayList<ZgUser>();
		userId.forEach(v->l.add(new ZgUser(Integer.valueOf(v.toString()),"error")));
		return l;
	}
	
	/**
	 * 请求方式一
	 * 被观察者发射多次数据，只有一个观察者监控		HystrixObservableCommand注解实现   
	 */
	@HystrixCommand(fallbackMethod = "getObjectFallback", observableExecutionMode = ObservableExecutionMode.EAGER)//or LAZY
	public Observable<ZgUser> hystrixObservableGetObject(Long id) {
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
	 * 请求方式二
	 * 被观察者发射一次数据，每个被观察者都对应一个观察者	HystrixCommand的注解实现
	 */
	@HystrixCommand(fallbackMethod = "getObjectFallback", ignoreExceptions={Exception.class})
	public ZgUser hystrixGetObjectSync(Long id) {
		return restTemplate.getForEntity("http://HELLO-SERVICE/getObject/{" + id + "}", ZgUser.class, id).getBody();
	}
	@HystrixCommand(fallbackMethod = "getObjectFallback")
	public Future<ZgUser> hystrixGetObjectASync(Long id) {
		return new AsyncResult<ZgUser>() {
			@Override
			public ZgUser invoke() {
				return restTemplate.getForEntity("http://HELLO-SERVICE/getObject/{" + id + "}", ZgUser.class, id).getBody();
			}
		};
	}
	@HystrixCommand(fallbackMethod = "getObjectFallbackReliable")
	public ZgUser getObjectFallback(Long id, Throwable e) {
		return restTemplate.getForEntity("http://HELLO-SERVICE/unknow/{" + id + "}", ZgUser.class, id).getBody();
	}
	public ZgUser getObjectFallbackReliable(Long id, Throwable e) {
		return new ZgUser(404,"fall back method error");
	}
	

	
	/**
	 * cache
	 */
//	@CacheResult(cacheKeyMethod="getUserByIdCache")//需要初始化上下文，并对上下文做threadlocal
	@CacheResult
	@HystrixCommand(commandKey="helloServiceCache")
	public ZgUser hystrixGetObjectCache(@CacheKey("id") ZgUser user) {
		return restTemplate.getForEntity("http://HELLO-SERVICE/getObject/{" + user.getId() + "}", ZgUser.class, user.getId()).getBody();
	}
	private String getUserByIdCache(Long id) {
		return String.valueOf(id);
	}
	

	@CacheRemove(commandKey="helloServiceCache")
	@HystrixCommand
	public void update(@CacheKey("id") ZgUser user) {
		restTemplate.getForEntity("http://HELLO-SERVICE/getObject/{" + user.getId() + "}", ZgUser.class, user.getId()).getBody();
	}
}
