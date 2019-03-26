package talk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class TalkApp {
    public static void main(String[] args) {
        SpringApplication.run(TalkApp.class, args);
    }
}
