package ut.pcmspf.common.rpc.thrift.server;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ut.pcmspf.common.rpc.thrift.server.properties.ThriftServerDiscoveryProperties;
import ut.pcmspf.common.rpc.thrift.server.properties.ThriftServerProperties;

import javax.annotation.PreDestroy;
import java.net.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Configuration
@AutoConfigureAfter(ThriftServerAutoConfiguration.class)
@ConditionalOnProperty(name = "spring.thrift.server.discovery.enabled", havingValue = "true")
@Import(ThriftServerAutoConfiguration.class)
public class ThriftServerDiscoveryConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThriftServerDiscoveryConfiguration.class);

    private static final String REGISTRY_URL_TEMPLATE = "http://%s:%d";
    private static final String HEALTH_CHECK_URL_TEMPLATE = "%s:%d";
    private Instance instance;
    @Autowired
    private NamingService service;
    @PreDestroy
    public void destroy() {
        if (Objects.nonNull(instance) && Objects.nonNull(service)) {
            try {
                service.deregisterInstance(instance.getServiceName(), instance);
                LOGGER.info("Deregister instance {}",instance);
            } catch (NacosException e) {
                LOGGER.error("Deregister instance error", e);
            }
        }
    }

    @Bean
    public NamingService thriftNacosClient(ThriftServerProperties thriftServerProperties, @Autowired(required = false) InetUtils utils) throws UnknownHostException, NacosException {
        ThriftServerDiscoveryProperties discoveryProperties = thriftServerProperties.getDiscovery();
        String discoveryHostAddress = discoveryProperties.getHost();
        Integer discoveryPort = discoveryProperties.getPort();
        LOGGER.info("Service discovery server host {}, port {}", discoveryHostAddress, discoveryPort);

        String serviceName = thriftServerProperties.getServiceId();
        String serverHostAddress;
        if (Objects.nonNull(utils)) {
            serverHostAddress = utils.findFirstNonLoopbackAddress().getHostAddress();
        } else {
            serverHostAddress = findFirstNonLoopbackAddress();
        }
        int servicePort = thriftServerProperties.getPort();

        String serviceId = String.join(":", serviceName, serverHostAddress, String.valueOf(servicePort));

        LOGGER.info("Service id {}", serviceId);
        LOGGER.info("Service name {}", serviceName);
        LOGGER.info("Service host address {}, port {}", serverHostAddress, servicePort);

        List<String> serviceTags = discoveryProperties.getTags();
        if (CollectionUtils.isNotEmpty(serviceTags)) {
            LOGGER.info("Service tags [{}]", String.join(", ", serviceTags));
        }
        String discoveryUrl = String.format(REGISTRY_URL_TEMPLATE, discoveryHostAddress, discoveryPort);
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, discoveryUrl);
        properties.setProperty(PropertyKeyConst.NAMESPACE, discoveryProperties.getNamespace());

        NamingService naming = NamingFactory.createNamingService(properties);
        Instance instance = new Instance();
        instance.setIp(serverHostAddress);
        instance.setPort(servicePort);
        instance.setServiceName(serviceName);
        naming.registerInstance(serviceName, instance);
        this.instance = instance;
//        naming.registerInstance(serviceName, serverHostAddress, servicePort, serviceName);
        return naming;
    }

    private String findFirstNonLoopbackAddress() {
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                if (nic.isUp()) {
                    Enumeration<InetAddress> nicInetAddresses = nic.getInetAddresses();
                    while (nicInetAddresses.hasMoreElements()) {
                        InetAddress address = nicInetAddresses.nextElement();
                        if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
            return "localhost";
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
}
