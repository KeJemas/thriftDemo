package cn.ut.demo.service;

import cn.ut.demo.thrift.Result;
import cn.ut.demo.thrift.ThriftDemoService;
import org.apache.thrift.TException;
import ut.pcmspf.common.rpc.thrift.server.annotation.ThriftService;

/**
 * @author pengsheng
 * @version 1.0
 * @date 2021/6/29 8:28
 */

@ThriftService(name = "thriftDemoServerRpc", version = 2.0)
public class ThriftDemoServiceImpl implements ThriftDemoService.Iface {

    private String message = "Hello, %s";

    @Override
    public Result sayHello(String userName) throws TException {
        System.out.println("exe");
        return new Result(200, String.format(message, userName));
    }

    @Override
    public Result hello() throws TException {
        return new Result(200, "hello world");
    }
}
