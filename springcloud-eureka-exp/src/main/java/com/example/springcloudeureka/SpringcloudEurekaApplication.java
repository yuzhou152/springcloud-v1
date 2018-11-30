package com.example.springcloudeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@EnableEurekaServer
@SpringBootApplication
public class SpringcloudEurekaApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SpringcloudEurekaApplication.class, args);
	}

	/**
	 * <pre>configure(extends SpringBootServletInitializer,override method configure ,for tomcat container Initializing. )   
	 * @param builder</pre>
	 */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SpringcloudEurekaApplication.class);
    }
}
