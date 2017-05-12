package com.arteco.mvc.model;

/**
 * Created by amalagraba on 08/03/2017.
 * Arteco Consulting Sl
 * mailto: info@arteco-consulting.com
 */
public enum MimeType {

    HTML("text/html"),
    XML("application/xml"),
    JSON("application/json"),
    JS("application/javascript"),
    CSS("text/css"),
    PNG("image/png"),
    JPEG("image/jpeg"),
    JPG("image/jpeg"),
    GIF("image/gif"),
    TEXT("text/plain"),
    PDF("application/pdf"),
    DOC("application/msword"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    XLS("application/vnd.ms-excel"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    ODT("application/vnd.oasis.opendocument.text"),
    ODS("application/vnd.oasis.opendocument.spreadsheet"),
    OTHER("application/octet-stream");

    private String mimeType;

    MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public static String getMimeByName(String name) {
        for (MimeType mime : values()) {
            if (mime.name().equalsIgnoreCase(name)) {
                return mime.getMimeType();
            }
        }
        return OTHER.getMimeType();
    }
}
