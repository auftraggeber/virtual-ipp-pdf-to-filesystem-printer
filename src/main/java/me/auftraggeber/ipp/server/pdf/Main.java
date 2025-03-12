package me.auftraggeber.ipp.server.pdf;

import me.auftraggeber.ipp.server.api.IppServer;
import me.auftraggeber.ipp.server.api.jmdns.PrinterRegistry;
import me.auftraggeber.ipp.server.pdf.server.PdfIppServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String printerName;
        if (args.length >= 1) {
            printerName = args[0];
        }
        else {
            printerName = "Virtual PDF Printer";
        }

        try (final PrinterRegistry printerRegistry = new PrinterRegistry()) {
            final IppServer ippServer = new PdfIppServer(printerName);

            printerRegistry.register(ippServer);

            synchronized (ippServer) {
                while (!ippServer.isClosed()) {
                    LOGGER.debug("Waiting for IPP server to close...");
                    ippServer.wait();
                }
            }
        }
        catch (IOException | InterruptedException e) {

        }
    }
}
