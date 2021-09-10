package ut.pcmspf.common.rpc.thrift.client.properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import ut.pcmspf.common.rpc.thrift.client.ThriftClientBeanPostProcessor;
import ut.pcmspf.common.rpc.thrift.client.common.ThriftClientContext;

public class NacosPropertiesCondition extends SpringBootCondition {

	private static final Logger LOGGER = LoggerFactory.getLogger(NacosPropertiesCondition.class);

    private static final String SPRING_CLOUD_NACOS_ADDRESS = "spring.cloud.nacos.server-addr";
    private static final String SPRING_CLOUD_NACOS_ADDRESS2 = "spring.cloud.nacos.discovery.server-addr";
    private static final String SPRING_CLOUD_NACOS_NAMESPACE = "spring.cloud.nacos.discovery.namespace";


    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {

        Environment environment = context.getEnvironment();
        String nacosAddress = environment.getProperty(SPRING_CLOUD_NACOS_ADDRESS);
        if(StringUtils.isBlank(nacosAddress)){
        	nacosAddress = environment.getProperty(SPRING_CLOUD_NACOS_ADDRESS2);
        }
        if(nacosAddress!=null && nacosAddress.indexOf(",")>0){
        	nacosAddress = nacosAddress.substring(0,nacosAddress.indexOf(","));
        }
        LOGGER.info("==nacosAddress:"+nacosAddress);
        String namespace = environment.getProperty(SPRING_CLOUD_NACOS_NAMESPACE);
        ThriftClientContext.registry(nacosAddress);
        ThriftClientContext.setNamespace(namespace);
        return new ConditionOutcome(StringUtils.isNotBlank(nacosAddress),
                "Nacos server address is " + nacosAddress);
    }
}
