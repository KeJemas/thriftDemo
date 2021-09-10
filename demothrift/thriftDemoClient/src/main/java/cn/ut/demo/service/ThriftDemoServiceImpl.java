package cn.ut.demo.service;

import cn.ut.demo.thrift.Result;
import cn.ut.demo.thrift.ThriftDemoClient;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;
import ut.pcmspf.common.rpc.thrift.client.annotation.ThriftRefer;

/**
 * @author pengsheng
 * @version 1.0
 * @date 2021/6/29 8:58
 */
@Service
public class ThriftDemoServiceImpl implements ThriftDemoService {

    @ThriftRefer
    private ThriftDemoClient thriftDemoService;

    @Override
    public String sayHello(String userName) throws TException {
        Result result = thriftDemoService.client().sayHello(userName);
        return result.getMsg();
    }

    @Override
    public String hello() throws TException {
        return thriftDemoService.client().hello().getMsg();
    }
}
