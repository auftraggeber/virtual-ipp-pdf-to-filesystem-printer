package me.auftraggeber.ipp.server.api;

import me.auftraggeber.ipp.server.api.handler.IppRequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

public abstract class IppServer implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(IppServer.class);

    private final String name;
    private final ServerSocket serverSocket;

    public IppServer(final String name, final ServerSocket serverSocket) {
        this.name = name;
        this.serverSocket = serverSocket;

        // accept all incoming connections
        new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    new Thread(new IppRequestMapper(this, serverSocket.accept())).start();
                }
                catch (IOException e) {
                    LOGGER.error("Error while accepting incoming connection", e);
                }
            }
        }).start();
        LOGGER.info("Created IPP server: {}", name);
    }

    public IppServer(final String name) throws IOException {
        this(name, 0);
    }

    public IppServer(final String name, final int port) throws IOException {
        this(name, new ServerSocket(port));
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public boolean isClosed() {
        return serverSocket.isClosed();
    }

    /**
     * Closes this stream and releases any system resources associated with it. If the stream is already closed then
     * invoking this method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised to relinquish the underlying resources and to
     * internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        serverSocket.close();
        synchronized (this) {
            notifyAll();
        }
    }
}
