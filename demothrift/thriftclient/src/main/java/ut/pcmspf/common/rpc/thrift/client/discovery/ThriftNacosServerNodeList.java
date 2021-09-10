package ut.pcmspf.common.rpc.thrift.client.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ut.pcmspf.common.rpc.thrift.client.common.ThriftServerNodeList;
import ut.pcmspf.common.rpc.thrift.client.exception.ThriftClientException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 获取服务列表
 *
 * @author chenzhijie1
 */
public class ThriftNacosServerNodeList extends ThriftServerNodeList<ThriftNacosServerNode> {

    private final NamingService namingService;
//    private final HealthClient healthClient;
//    private final CatalogClient catalogClient;

    private static ThriftNacosServerNodeList serverNodeList = null;

    public static ThriftNacosServerNodeList singleton(NamingService namingService) {
        if (serverNodeList == null) {
            synchronized (ThriftNacosServerNodeList.class) {
                if (serverNodeList == null) {
                    serverNodeList = new ThriftNacosServerNodeList(namingService);
                }
            }
        }
        return serverNodeList;
    }

    private ThriftNacosServerNodeList(NamingService namingService) {
        this.namingService = namingService;
    }

    @Override
    public List<ThriftNacosServerNode> getThriftServer(String serviceName) {
        // TODO: 2020/9/7 Bug 这里如果有服务下线,serverNodeMap还没有被更新,会获取到下线服务,导致服务调用失败
        if (MapUtils.isNotEmpty(this.serverNodeMap) && (this.serverNodeMap.containsKey(serviceName))) {
            LinkedHashSet<ThriftNacosServerNode> serverNodeSet = this.serverNodeMap.get(serviceName);
            if (CollectionUtils.isNotEmpty(serverNodeSet)) {
                return Lists.newArrayList(serverNodeSet);
            }
        }

        return refreshThriftServer(serviceName);
    }

    @Override
    public List<ThriftNacosServerNode> refreshThriftServer(String serviceName) {
        List<ThriftNacosServerNode> serverNodeList = Lists.newArrayList();
        //List<ServiceHealth> serviceHealthList = healthClient.getAllServiceInstances(serviceName).getResponse();
        List<Instance> instanceList = null;
        try {
            instanceList = namingService.selectInstances(serviceName, true);
        } catch (NacosException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        filterAndCompoServerNodes(serverNodeList, instanceList);

        if (CollectionUtils.isNotEmpty(serverNodeList)) {
            this.serverNodeMap.put(serviceName, Sets.newLinkedHashSet(serverNodeList));
        } else {
            //如果节点已经移除，应该同步在serverNodeMap中去除Key,防止调用到已经离线的服务
            this.serverNodeMap.remove(serviceName);
        }

        return serverNodeList;
    }

    @Override
    public Map<String, LinkedHashSet<ThriftNacosServerNode>> getThriftServers() {
        if (MapUtils.isNotEmpty(this.serverNodeMap)) {
            return this.serverNodeMap;
        }

        return refreshThriftServers();
    }

    @Override
    public Map<String, LinkedHashSet<ThriftNacosServerNode>> refreshThriftServers() {
        if (MapUtils.isEmpty(this.serverNodeMap)) {
            return this.serverNodeMap;
        }
        List<String> serverNames = Lists.newArrayList(this.serverNodeMap.keySet());
        for (String serverName : serverNames) {
            this.refreshThriftServer(serverName);
        }
        return this.serverNodeMap;
    }


    private static ThriftNacosServerNode getThriftConsulServerNode(Instance node) {
        ThriftNacosServerNode serverNode = new ThriftNacosServerNode();

        serverNode.setNode(node.getServiceName());
        serverNode.setAddress(node.getIp());
        serverNode.setPort(node.getPort());
        serverNode.setHost(ThriftNacosServerUtils.findHost(node));

        serverNode.setServiceId(node.getInstanceId());
//        serverNode.setTags(service.getTags());
//        serverNode.setHealth(ThriftConsulServerUtils.isPassingCheck(node));
        serverNode.setHealth(true);

        return serverNode;
    }


    private void filterAndCompoServerNodes(List<ThriftNacosServerNode> serverNodeList, List<Instance> instanceList) {
        if (instanceList == null)
            return;
        for (Instance serviceHealth : instanceList) {
            ThriftNacosServerNode serverNode = getThriftConsulServerNode(serviceHealth);
            if (serverNode == null) {
                continue;
            }

            if (!serverNode.isHealth()) {
                continue;
            }

//            if (CollectionUtils.isEmpty(serverNode.getTags())) {
//                continue;
//            }
            serverNodeList.add(serverNode);
        }
    }

//    private static class ThriftConsulResponseCallback implements ConsulResponseCallback<List<ServiceHealth>> {
//
//        List<ThriftConsulServerNode> serverNodeList;
//
//        public ThriftConsulResponseCallback(List<ThriftConsulServerNode> serverNodeList) {
//            this.serverNodeList = serverNodeList;
//        }
//
//        @Override
//        public void onComplete(ConsulResponse<List<ServiceHealth>> consulResponse) {
//            List<ServiceHealth> response = consulResponse.getResponse();
//            for (ServiceHealth serviceHealth : response) {
//                ThriftConsulServerNode serverNode = getThriftConsulServerNode(serviceHealth);
//                serverNodeList.add(serverNode);
//            }
//        }
//
//        @Override
//        public void onFailure(Throwable throwable) {
//            throw new ThriftClientException("Failed to query service instances from consul agent", throwable);
//        }
//
//    }

}
