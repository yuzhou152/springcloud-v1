package com.example.springcloudclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringCloudApplication	//@EnableCircuitBreaker+@EnableDiscoveryClient+@SpringBootApplication
//@EnableCircuitBreaker	//开启断路器功能
//@EnableDiscoveryClient	//让该应用注册为Eureka客户端的应用，以获得服务发现的能力
//@SpringBootApplication
public class SpringcloudClientApplication extends SpringBootServletInitializer {

	@Bean				//注册RestTemplate为Bean
	@LoadBalanced		//开启客户端负载
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	public static void main(String[] args) {
		SpringApplication.run(SpringcloudClientApplication.class, args);
	}
	
	/**
	 * <pre>configure(extends SpringBootServletInitializer,override method configure ,for tomcat container Initializing. )   
	 * @param builder</pre>
	 */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SpringcloudClientApplication.class);
    }
}
