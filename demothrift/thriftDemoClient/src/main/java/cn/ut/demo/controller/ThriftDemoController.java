package cn.ut.demo.controller;

import cn.ut.demo.service.ThriftDemoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author pengsheng
 * @version 1.0
 * @date 2021/6/29 8:48
 */
@RestController
@RequestMapping("/thriftDemoClient")
@Slf4j
public class ThriftDemoController {

    @Resource
    private ThriftDemoService thriftDemoService;

    @GetMapping("sayHello")
    @ResponseBody
    public String sayHello(String userName) throws TException {
        String result = thriftDemoService.sayHello(userName);
        return result;
    }

    @GetMapping("hello")
    @ResponseBody
    public String hello() throws TException {
        return thriftDemoService.hello();
    }
}
