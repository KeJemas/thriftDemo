import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import ut.pcmspf.common.rpc.thrift.client.annotation.EnableThriftClient;

/**
 * @author pengsheng
 * @version 1.0
 * @date 2021/6/29 8:41
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(value="cn.ut")
@EnableThriftClient
public class ThriftDemoClientApp {
    public static void main( String[] args )
    {
        SpringApplication.run(ThriftDemoClientApp.class, args);
    }
}
