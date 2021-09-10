package ut.pcmspf.common.rpc.thrift.client.loadbalancer;

import ut.pcmspf.common.rpc.thrift.client.common.ThriftServerNode;

public interface IRule {

    ThriftServerNode choose(String key);

    void setLoadBalancer(ILoadBalancer lb);

    ILoadBalancer getLoadBalancer();

}
