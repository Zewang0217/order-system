package org.zewang.ordersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class OrderSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderSystemApplication.class, args);
    }

}
