package com.example.springcloudserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient	//让该应用注册为Eureka客户端的应用，开启服务端负载
@SpringBootApplication
public class SpringcloudServerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SpringcloudServerApplication.class, args);
	}

	/**
	 * <pre>configure(extends SpringBootServletInitializer,override method configure ,for tomcat container Initializing. )   
	 * @param builder</pre>
	 */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SpringcloudServerApplication.class);
    }
}
