package com.alibaba.csd.jetty.io;

import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.io.ConnectedEndPoint;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.nio.AsyncConnection;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import org.eclipse.jetty.io.nio.SelectorManager;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * User: shiyou.xusy
 * Date: 12-5-25
 * Time: ÏÂÎç3:48
 */
public class JettyNio {

    public static void main(String[] args) throws Exception {
        SelectorManager selectorManager = new SelectorManager() {
            public boolean dispatch(Runnable task) {
                ThreadPool pool = new QueuedThreadPool();
//               if (pool==null)
//                   pool=getServer().getThreadPool();
                return pool.dispatch(task);
            }

            @Override
            protected void endPointClosed(final SelectChannelEndPoint endpoint) {
//               SelectChannelConnector.this.endPointClosed(endpoint);
            }

            @Override
            protected void endPointOpened(SelectChannelEndPoint endpoint) {
                // TODO handle max connections and low resources
//               connectionOpened(endpoint.getConnection());
            }

            @Override
            protected void endPointUpgraded(ConnectedEndPoint endpoint, Connection oldConnection) {
//               connectionUpgraded(oldConnection,endpoint.getConnection());
            }

            @Override
            public AsyncConnection newConnection(SocketChannel channel, AsyncEndPoint endpoint, Object attachment) {
//               return SelectChannelConnector.this.newConnection(channel,endpoint);
                return new AsyncConnection() {
                    public void onInputShutdown() throws IOException {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Connection handle() throws IOException {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public long getTimeStamp() {
                        return 0;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public boolean isIdle() {
                        return false;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public boolean isSuspended() {
                        return false;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public void onClose() {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public void onIdleExpired(long idleForMs) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }
                };
            }

            @Override
            protected SelectChannelEndPoint newEndPoint(SocketChannel channel, SelectSet selectSet, SelectionKey sKey) throws IOException {
                SelectChannelEndPoint endp = new SelectChannelEndPoint(channel, selectSet, sKey, 1000);
                endp.setConnection(selectSet.getManager().newConnection(channel, endp, sKey.attachment()));
                return endp;
            }
        };


        // Create a new server socket
        ServerSocketChannel server = ServerSocketChannel.open();
        // Set to blocking mode
        server.configureBlocking(true);

        // Bind the server socket to the local host and port
        server.socket().setReuseAddress(true);
        InetSocketAddress addr = new InetSocketAddress(8087);//:new InetSocketAddress(getHost(),getPort());
        server.socket().bind(addr, 1);


        if (server != null && server.isOpen() && selectorManager.isStarted()) {
            SocketChannel channel = server.accept();
            channel.configureBlocking(false);
            Socket socket = channel.socket();
            socket.setTcpNoDelay(true);
//            if (_soLingerTime >= 0)
//                socket.setSoLinger(true,_soLingerTime / 1000);
//            else
            socket.setSoLinger(false, 0);
            selectorManager.register(channel);
        }

        selectorManager.dispatch(new Runnable() {
            public void run() {
                System.out.println("OK");
            }
        });
    }
}
