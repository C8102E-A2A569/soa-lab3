package c8102ea2a569.service1jetty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Service1JettyApplication {

    public static void main(String[] args) {
        SpringApplication.run(Service1JettyApplication.class, args);
    }

}
