package cn.ut.demo.service;

import org.apache.thrift.TException;

/**
 * @author pengsheng
 * @version 1.0
 * @date 2021/6/29 8:57
 */
public interface ThriftDemoService {

    String sayHello(String userName) throws TException;

    String hello() throws TException;
}
