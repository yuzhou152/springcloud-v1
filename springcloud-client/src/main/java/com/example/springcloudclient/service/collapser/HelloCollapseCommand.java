package com.example.springcloudclient.service.collapser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.example.springcloudclient.pojo.ZgUser;
import com.example.springcloudclient.service.HelloService;
import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;

public class HelloCollapseCommand extends HystrixCollapser<List<ZgUser>, ZgUser, Long>{

	private HelloService helloService;
	private Long userId;
	
	public HelloCollapseCommand(HelloService helloService, Long userId) {
		super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("helloCollapseCommand"))
				.andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(100)));
		this.helloService = helloService;
		this.userId = userId;
	}
	
	@Override
	public Long getRequestArgument() {
		return userId;
	}

	@Override
	protected HystrixCommand<List<ZgUser>> createCommand(
			Collection<com.netflix.hystrix.HystrixCollapser.CollapsedRequest<ZgUser, Long>> requests) {
		List<Long> userIds = new ArrayList<Long>(requests.size());
		userIds.addAll(requests.stream().map(CollapsedRequest::getArgument).collect(Collectors.toList()));
		userIds.forEach(v->System.out.println(v));
		return new HelloBatchCommand(helloService, userIds);
	}

	@Override
	protected void mapResponseToRequests(List<ZgUser> batchResponse,
			Collection<com.netflix.hystrix.HystrixCollapser.CollapsedRequest<ZgUser, Long>> requests) {
		int count = 0;
		for (CollapsedRequest<ZgUser, Long> collapsedRequest : requests) {
			ZgUser user = batchResponse.get(count++);
			collapsedRequest.setResponse(user);
		}
	}


}
