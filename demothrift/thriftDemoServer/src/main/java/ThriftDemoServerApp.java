import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import ut.pcmspf.common.rpc.thrift.server.annotation.EnableThriftServer;

/**
 * @author pengsheng
 * @version 1.0
 * @date 2021/6/29 8:21
 */
@EnableDiscoveryClient
@EnableThriftServer
@SpringBootApplication
@ComponentScan(value = "cn.ut")
@Slf4j
public class ThriftDemoServerApp {

    public static void main(String[] args) {
        SpringApplication.run(ThriftDemoServerApp.class, args);
    }
}
