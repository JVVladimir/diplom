package ru.hse.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.GUI.ClientGUILead;
import ru.hse.business.SynchronizationManager;
import ru.hse.utils.Encrypter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class NetManagerLead implements ConnectionListener {

    private static final Logger log = LoggerFactory.getLogger(NetManagerLead.class);

    public static final int CONNECT = 200;
    public static final int INIT_W = 201;
    public static final int INIT_X = 202;
    public static final int TRAIN = 203;
    public static final int SYNC_DONE = 204;
    public static final int SEND = 205;
    public boolean isReady = false;
    public byte[] key;

    public int epochs, limit;
    public static final int SYNC_LIMIT = 40;
    public static final int EPOCHS_MAX = 150;

    private final static int PORT = 15600;

    private volatile boolean responseReceived;

    private Server server;
    public Connection connection;
    public SynchronizationManager synchronizationManager;

    public NetManagerLead() { server = new Server(this, PORT); }

    @Override
    public void onReceivedMessage(Connection connection, Object data) {
        if (data instanceof Message) {
            Message message = (Message) data;
            switch (message.getCommand()) {
                case CONNECT:
                    isReady = false;
                    responseReceived = true;
                    break;
                case INIT_W:
                    limit = 0;
                    epochs = 0;
                    responseReceived = true;
                    break;
                case INIT_X:
                    synchronizationManager.setOut2(message.getOut());
                    responseReceived = true;
                    break;
                case TRAIN:
                    synchronizationManager.setOut2(message.getOut());
                    responseReceived = true;
                    break;
                case SYNC_DONE:
                    responseReceived = true;
                    break;
                case SEND:
                    System.out.println(new String(message.getMessage(), StandardCharsets.UTF_8));
                    //System.out.println(new String(Encrypter.decrypt(message.getMessage(), key), StandardCharsets.UTF_8));
                    break;
            }
        }
    }

    public Map <String, String> runApp() {
        Map<String , String> map = new UsersSearcher(PORT).search();
        if(map.size() == 0) {
            log.info("Не найдено ни одного соединения!");
            return null;
        }
        Map.Entry<String, String> entry = map.entrySet().iterator().next();
        log.info("Found user: {}", entry);
        String ip = entry.getKey();
        try {
            connection = new Connection(this, ip, PORT);
            connection.sendMessage(new Message(CONNECT, System.getProperty("user.name"), null));
            waitResponse();
        } catch (RuntimeException ex) {
            log.info("Не удалось подключиться к абоненту!");
            return null;
        }
        return map;
    }

    public void setSynchronizationManager(SynchronizationManager synchronizationManager) {
        this.synchronizationManager = synchronizationManager;
    }

    // TODO: подумать не переделать ли под поток, возвращающий результат задачи и таймер (вдруг задача не выполнится никогда)
    public void waitResponse() {
        while (!responseReceived)
            Thread.yield();
        responseReceived = false;
    }

}
