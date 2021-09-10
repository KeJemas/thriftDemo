package ut.pcmspf.common.rpc.thrift.client.loadbalancer;

import com.google.common.collect.Lists;

import ut.pcmspf.common.rpc.thrift.client.common.ThriftServerNode;
import ut.pcmspf.common.rpc.thrift.client.discovery.ServerListUpdater;
import ut.pcmspf.common.rpc.thrift.client.discovery.ThriftNacosServerListUpdater;
import ut.pcmspf.common.rpc.thrift.client.discovery.ThriftNacosServerNode;
import ut.pcmspf.common.rpc.thrift.client.discovery.ThriftNacosServerNodeList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class ThriftNacosServerListLoadBalancer extends AbstractLoadBalancer {

    private Logger log = LoggerFactory.getLogger(getClass());

    private ThriftNacosServerNodeList serverNodeList;
    private IRule rule;

    private volatile ServerListUpdater serverListUpdater;

    private final ServerListUpdater.UpdateAction updateAction = this::updateListOfServers;

    public ThriftNacosServerListLoadBalancer(ThriftNacosServerNodeList serverNodeList, IRule rule) {
        this.serverNodeList = serverNodeList;
        this.rule = rule;
        this.serverListUpdater = new ThriftNacosServerListUpdater();
        this.startUpdateAction();
    }

    @Override
    public ThriftNacosServerNodeList getThriftServerNodeList() {
        return this.serverNodeList;
    }

    @Override
    public ThriftNacosServerNode chooseServerNode(String key) {
        if (rule == null) {
            return null;
        } else {
            ThriftServerNode serverNode;
            try {
                serverNode = rule.choose(key);
            } catch (Exception e) {
                log.warn("LoadBalancer [{}]:  Error choosing server for key {}", getClass().getSimpleName(), key, e);
                return null;
            }

            if (serverNode instanceof ThriftNacosServerNode) {
                return (ThriftNacosServerNode) serverNode;
            }
        }

        return null;
    }

    private synchronized void startUpdateAction() {
        log.info("Using serverListUpdater {}", serverListUpdater.getClass().getSimpleName());
        if (serverListUpdater == null) {
            serverListUpdater = new ThriftNacosServerListUpdater();
        }

        this.serverListUpdater.start(updateAction);
    }

    public void stopServerListRefreshing() {
        if (serverListUpdater != null) {
            serverListUpdater.stop();
        }
    }

    private void updateListOfServers() {
        Map<String, LinkedHashSet<ThriftNacosServerNode>> thriftNacosServers = this.serverNodeList.refreshThriftServers();

        List<String> serverList = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, LinkedHashSet<ThriftNacosServerNode>> serverEntry : thriftNacosServers.entrySet()) {
            serverList.add(
                    sb.append(serverEntry.getKey())
                            .append(": ")
                            .append(serverEntry.getValue())
                            .toString()
            );
            sb.setLength(0);
        }

        //log.info("Refreshed thrift serverList: [" + String.join(", ", serverList) + "]");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ThriftNacosServerListLoadBalancer:");
        sb.append(super.toString());
        sb.append("ServerList:").append(String.valueOf(serverNodeList));
        return sb.toString();
    }
}
