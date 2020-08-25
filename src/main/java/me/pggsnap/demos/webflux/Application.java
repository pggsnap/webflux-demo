package me.pggsnap.demos.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;

/**
 * @author pggsnap
 * @date 2020/4/20
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
//        SpringApplication application = new SpringApplication(Application.class);
////        System.out.println("===========");
////        System.out.println(application.getMainApplicationClass().getName());
////        System.out.println("===========");
//        ConfigurableApplicationContext context = application.run(args);
////        System.out.println("messageSource: " + context.getBean(MessageSource.class));
//        System.out.println("SpringApplicationRunListener: " + context.getBean(SpringApplicationRunListener.class));

//        System.setProperty("reactor.netty.http.server.accessLogEnabled", "true");
        SpringApplication.run(Application.class, args);
    }
}
