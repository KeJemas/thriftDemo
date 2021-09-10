namespace java cn.ut.demo.thrift

struct Result {
            1:i32 code,
            2:string msg
    }

service ThriftDemoService {

             Result sayHello(1:string userName)

             Result hello()
    }