package me.auftraggeber.ipp.server.api.jmdns;

import me.auftraggeber.ipp.server.api.IPPServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class PrinterRegistry implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrinterRegistry.class);

    private final JmDNS jmdns;
    private final Map<String, String> properties = getPrinterInformation();

    public PrinterRegistry() throws IOException {
        this(JmDNS.create(InetAddress.getLocalHost()));
    }

    public PrinterRegistry(JmDNS jmdns) {
        this.jmdns = jmdns;
    }

    @NotNull
    private static Map<String, String> getPrinterInformation() {
        return new HashMap<>() {{
            put(PrinterInformation.TXT_VERSION.getKey(), "1");
            put(PrinterInformation.QUEUE_TOTAL.getKey(), "1");
            put(PrinterInformation.SUPPORTED_FORMATS.getKey(), "application/pdf,application/postscript");
            put(PrinterInformation.DESCRIPTION.getKey(), "Virtual Printer from JmDNS");
            put(PrinterInformation.MODEL_NAME.getKey(), "(VirtualPrinter 1.0)");
            put(PrinterInformation.PRIORITY.getKey(), "10");
            put(PrinterInformation.PATH.getKey(), "/ipp/printer");
        }};
    }

    public void setInformation(final PrinterInformation information, final String value) {
        properties.put(information.getKey(), value);
    }

    public ServiceInfo register(final String name, final int port) throws IOException {
        final ServiceInfo serviceInfo = ServiceInfo.create(
                "_ipp._tcp.local.",
                name,
                port,
                0,
                0,
                properties
        );

        this.jmdns.registerService(serviceInfo);
        LOGGER.info("Registered service: {}", serviceInfo);
        return serviceInfo;
    }

    public ServiceInfo register(final IPPServer server) throws IOException {
        return register(server.getName(), server.getPort());
    }

    public void unregister(final ServiceInfo serviceInfo) {
        this.jmdns.unregisterService(serviceInfo);
        LOGGER.info("Unregistered service: {}", serviceInfo);
    }

    @Override
    public void close() throws IOException {
        this.jmdns.close();
    }
}
