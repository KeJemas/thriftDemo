package ut.pcmspf.common.rpc.thrift.server.context;

import ut.pcmspf.common.rpc.thrift.server.argument.THsHaServerArgument;
import ut.pcmspf.common.rpc.thrift.server.properties.ThriftServerProperties;
import ut.pcmspf.common.rpc.thrift.server.wrapper.ThriftServiceWrapper;

import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class THsHaServerContext implements ContextBuilder {

    private static THsHaServerContext serverContext;
    private THsHaServer.Args args;

    private THsHaServerContext() {
    }

    public static THsHaServerContext context() {
        if (Objects.isNull(serverContext)) {
            serverContext = new THsHaServerContext();
        }
        return serverContext;
    }

    @Override
    public ContextBuilder prepare() {
        return context();
    }

    @Override
    public TServer buildThriftServer(ThriftServerProperties properties,
                                     List<ThriftServiceWrapper> serviceWrappers)
            throws TTransportException, IOException {
        serverContext = (THsHaServerContext) prepare();
        serverContext.args = new THsHaServerArgument(serviceWrappers, properties);
        return new THsHaServer(args);
    }

}

