package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan({"td.controller", "td.service", "td.login"})
@EnableJpaRepositories(basePackages="td.model.dao")
@EnableElasticsearchRepositories(basePackages="td.model.dao")
@EntityScan("td.model.domain")
public class TdProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TdProjectApplication.class, args);
	}

}
