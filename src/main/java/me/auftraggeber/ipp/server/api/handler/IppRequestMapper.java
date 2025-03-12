package me.auftraggeber.ipp.server.api.handler;

import com.hp.jipp.encoding.IppInputStream;
import com.hp.jipp.encoding.IppOutputStream;
import com.hp.jipp.encoding.IppPacket;
import com.hp.jipp.model.Operation;
import me.auftraggeber.ipp.server.api.IppServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class IppRequestMapper implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(IppRequestMapper.class);

    private final IppServer server;
    private final Socket socket;

    public IppRequestMapper(final IppServer server, final Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        try (IppOutputStream outputStream = new IppOutputStream(socket.getOutputStream())) {
            final IppPacket ippPacket = readPacketFromHttpRequest();
            if (ippPacket == null) return;
            final Operation operation = ippPacket.getOperation();

            outputStream.write((IppPacket) findHandlerForOperation(operation).invoke(server, ippPacket));
        }
        catch (NoSuchMethodException e) {
            LOGGER.warn(e.getMessage());
        }
        catch (IllegalAccessException | InvocationTargetException | IOException e) {
            LOGGER.error("Error writing packet", e);
        }
    }

    private IppPacket readPacketFromHttpRequest() {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // strip http headers
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                LOGGER.debug("Stripped IPP request header: {}", line);
            }

            // read chars from input stream
            final StringBuilder sb = new StringBuilder();
            while (reader.ready()) {
                sb.append((char) reader.read());
            }

            // convert to stream
            IppInputStream ippInputStream = new IppInputStream(new ByteArrayInputStream(sb.toString().getBytes()));
            try {
                final var ippPacket = ippInputStream.readPacket();
                LOGGER.info("IPP request: {}", ippPacket);
                return ippPacket;
            }
            catch (IOException e) {
                LOGGER.error("Error while reading IPP packet", e);
            }
        }
        catch (IOException e) {
            LOGGER.error("Error while handling IPP request", e);
        }
        return null;
    }

    private Method findHandlerForOperation(final Operation operation) throws NoSuchMethodException {
        for (Method method : server.getClass().getMethods()) {
            final var handler = method.getAnnotation(IppRequestHandler.class);
            if (handler == null) continue;
            if (handler.value() != operation.getCode()) continue;
            if (method.getParameterCount() != 1) continue;
            if (method.getParameterTypes()[0] != IppPacket.class) continue;
            if (method.getReturnType() != IppPacket.class) continue;

            return method;
        }

        throw new NoSuchMethodException("Could not find handler for " + operation.getName());
    }
}
