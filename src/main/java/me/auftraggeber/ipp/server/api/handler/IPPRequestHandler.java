package me.auftraggeber.ipp.server.api.handler;

import com.hp.jipp.encoding.IppPacket;

@FunctionalInterface
public interface IPPRequestHandler {

    IppPacket handleRequest(IppPacket packet);
}
