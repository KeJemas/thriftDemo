package ut.pcmspf.common.rpc.thrift.client.loadbalancer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import ut.pcmspf.common.rpc.thrift.client.discovery.ThriftNacosServerNode;
import ut.pcmspf.common.rpc.thrift.client.discovery.ThriftNacosServerNodeList;

public abstract class AbstractLoadBalancer implements ILoadBalancer<ThriftNacosServerNode> {

    public abstract ThriftNacosServerNode chooseServerNode(String key);

    @Override
    public Map<String, LinkedHashSet<ThriftNacosServerNode>> getAllServerNodes() {
        return getThriftServerNodeList().getServerNodeMap();
    }

    @Override
    public Map<String, LinkedHashSet<ThriftNacosServerNode>> getRefreshedServerNodes() {
        return getThriftServerNodeList().refreshThriftServers();
    }

    @Override
    public List<ThriftNacosServerNode> getServerNodes(String key) {
        return getThriftServerNodeList().getThriftServer(key);
    }

    @Override
    public List<ThriftNacosServerNode> getRefreshedServerNodes(String key) {
        return getThriftServerNodeList().refreshThriftServer(key);
    }

    public abstract ThriftNacosServerNodeList getThriftServerNodeList();

}
