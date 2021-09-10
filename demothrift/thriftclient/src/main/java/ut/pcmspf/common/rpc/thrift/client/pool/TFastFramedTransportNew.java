package ut.pcmspf.common.rpc.thrift.client.pool;

import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;

public class TFastFramedTransportNew extends TFastFramedTransport implements EnhancedInstance {

	private Object dynamicField;

	public TFastFramedTransportNew(TTransport underlying) {
		super(underlying);
		// TODO Auto-generated constructor stub
	}




	public  void setSkyWalkingDynamicField(Object value){
		this.dynamicField = value;
	}


	public Object getSkyWalkingDynamicField(){
		return dynamicField;
	}

}
