package ru.job4j;

public class Req {

    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;

    public Req(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    public static Req of(String content) {
        String[] splitHttpRequestType = content.split("HTTP/1.1");
        String[] splitSlash = splitHttpRequestType[0].split("/");
        String recType = "GET";
        if (splitSlash[0].contains("POST")) {
            recType = "POST";
        }
        String mode = "queue";
        if (splitSlash[1].contains("topic")) {
            mode = "topic";
        }
        String name = splitSlash[2].trim();
        String param = "";
        if (recType.equals("POST")) {
            String[] splitText = content.split("/");
            String[] messages = splitText[splitText.length - 1].split("\n");
            param = messages[2].trim();
        }
        if (recType.equals("GET") && (splitSlash.length >= 4)) {
           param = splitSlash[3].trim();
        }
        return new Req(recType, mode, name, param);
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }
}
