package me.auftraggeber.ipp.server.api.jmdns;

public enum PrinterInformation {
    TXT_VERSION("txtvers"),
    QUEUE_TOTAL("qtotal"),
    SUPPORTED_FORMATS("pdl"),
    DESCRIPTION("note"),
    MODEL_NAME("product"),
    PRIORITY("priority"),
    UNIVERSAL_RASTER_FORMAT("URF"),
    COLOR("Color"),
    DUPLEX("Duplex"),
    SCAN("Scan"),
    PATH("rp");

    private final String key;

    PrinterInformation(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
