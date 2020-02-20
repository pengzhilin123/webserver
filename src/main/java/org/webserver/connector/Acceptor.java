package org.webserver.connector;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * 监听连接
 */
public class Acceptor implements Runnable {
    private final static Logger logger = Logger.getLogger(Acceptor.class.getPackageName());
    private Connector connector;

    Acceptor(Connector connector) {
        this.connector = connector;
    }

    @Override
    public void run() {
        SocketChannel client;

        while (connector.isRunning()) {
            try {
                client = connector.accept(); // ServerSocketChannel处于阻塞模式下，不会返回null，无需判断
                client.configureBlocking(false); // 将client设为非阻塞模式，注册到轮询线程
                logger.info(String.format("%s 接收到新的连接请求 %s",
                        Thread.currentThread().getName(), client.getRemoteAddress()));
                connector.registerToPoller(client);
            } catch (IOException e) {
                e.printStackTrace();
                logger.warning(String.format("%s 出现异常[%s]", Thread.currentThread().getName(), e.getMessage()));
            }
        }
    }
}
