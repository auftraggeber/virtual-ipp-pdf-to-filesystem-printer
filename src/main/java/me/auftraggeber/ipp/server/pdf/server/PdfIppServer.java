package me.auftraggeber.ipp.server.pdf.server;

import com.hp.jipp.encoding.IppPacket;
import com.hp.jipp.model.Operation;
import me.auftraggeber.ipp.server.api.IPPServer;
import me.auftraggeber.ipp.server.api.handler.IppRequestHandler;

import java.io.IOException;

public class PdfIppServer extends IPPServer {

    public PdfIppServer(String name) throws IOException {
        super(name);
    }

    @IppRequestHandler(Operation.Code.getPrinterAttributes)
    public IppPacket getPrinterAttributes(IppPacket packet) {
        return null;
    }
}
