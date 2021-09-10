package ut.pcmspf.common.rpc.thrift.client.discovery;

public interface ServerListUpdater {

    public interface UpdateAction {
        void doUpdate();
    }

    void start(UpdateAction updateAction);

    void stop();

}
