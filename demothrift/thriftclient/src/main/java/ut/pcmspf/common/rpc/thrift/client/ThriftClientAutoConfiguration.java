package ut.pcmspf.common.rpc.thrift.client;

import ut.pcmspf.common.rpc.thrift.client.common.ThriftClientContext;
import ut.pcmspf.common.rpc.thrift.client.pool.TransportKeyedObjectPool;
import ut.pcmspf.common.rpc.thrift.client.pool.TransportKeyedPooledObjectFactory;
import ut.pcmspf.common.rpc.thrift.client.properties.NacosPropertiesCondition;
import ut.pcmspf.common.rpc.thrift.client.properties.ThriftClientPoolProperties;
import ut.pcmspf.common.rpc.thrift.client.properties.ThriftClientProperties;
import ut.pcmspf.common.rpc.thrift.client.properties.ThriftClientPropertiesCondition;
import ut.pcmspf.common.rpc.thrift.client.scanner.ThriftClientBeanScanProcessor;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value = {NacosPropertiesCondition.class, ThriftClientPropertiesCondition.class})
@ConditionalOnClass(name = {"com.alibaba.cloud.nacos.NacosConfigAutoConfiguration"})
@EnableConfigurationProperties(ThriftClientProperties.class)
public class ThriftClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ThriftClientBeanScanProcessor thriftClientBeanScannerConfigurer() {
        return new ThriftClientBeanScanProcessor();
    }

    @Bean
    public GenericKeyedObjectPoolConfig keyedObjectPoolConfig(ThriftClientProperties properties) {
        ThriftClientPoolProperties poolProperties = properties.getPool();
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMinIdlePerKey(poolProperties.getPoolMinIdlePerKey());
        config.setMaxIdlePerKey(poolProperties.getPoolMaxIdlePerKey());
        config.setMaxWaitMillis(poolProperties.getPoolMaxWait());
        config.setMaxTotalPerKey(poolProperties.getPoolMaxTotalPerKey());
        config.setTestOnCreate(poolProperties.isTestOnCreate());
        config.setTestOnBorrow(poolProperties.isTestOnBorrow());
        config.setTestOnReturn(poolProperties.isTestOnReturn());
        config.setTestWhileIdle(poolProperties.isTestWhileIdle());
        config.setFairness(true);
        config.setJmxEnabled(false);
        return config;
    }

    @Bean
    @ConditionalOnMissingBean
    public TransportKeyedPooledObjectFactory transportKeyedPooledObjectFactory(
            ThriftClientProperties properties) {
        return new TransportKeyedPooledObjectFactory(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public TransportKeyedObjectPool transportKeyedObjectPool(
            GenericKeyedObjectPoolConfig config, TransportKeyedPooledObjectFactory poolFactory) {
        return new TransportKeyedObjectPool(poolFactory, config);
    }

    @Bean
    @ConditionalOnMissingBean
    public ThriftClientBeanPostProcessor thriftClientBeanPostProcessor() {
        return new ThriftClientBeanPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ThriftClientContext thriftClientContext(
            ThriftClientProperties properties, TransportKeyedObjectPool objectPool) {
        return ThriftClientContext.context(properties, objectPool);
    }
}
