package cn.ut.demo.thrift;

import ut.pcmspf.common.rpc.thrift.client.annotation.ThriftClient;
import ut.pcmspf.common.rpc.thrift.client.common.ThriftClientAware;

/**
 * @author pengsheng
 * @version 1.0
 * @date 2021/6/29 8:44
 */
@ThriftClient(serviceId = "thriftDemoServerRpc", refer = ThriftDemoService.class, version = 2.0)
public interface ThriftDemoClient extends ThriftClientAware<ThriftDemoService.Client> {
}
