package me.auftraggeber.ipp.server.api.handler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IppRequestHandler {

    int value();
}
