package com.chanbinme.envers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;

//@EnableEnversRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@SpringBootApplication
public class SpringEnversApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringEnversApplication.class, args);
    }

}
