package com.example.springcloudclient.service.collapser;

import java.util.List;


import com.example.springcloudclient.pojo.ZgUser;
import com.example.springcloudclient.service.HelloService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

public class HelloBatchCommand extends HystrixCommand<List<ZgUser>>{

	private final static HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("CommandKeyName");
	private HelloService helloService;
	private List<Long> userId;
	
	public HelloBatchCommand(HelloService helloService, List<Long> userId) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GroupName"))//未设置ThreadPoolKey则根据组名进行仓壁划分，用于统计
				.andCommandKey(commandKey)
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ThreadPoolKey")));
		this.helloService = helloService;
		this.userId = userId;
	}

	@Override
	protected List<ZgUser> run() throws Exception {
		return helloService.helloServiceGetList(userId);
	}
	
}
