package de.craftsarmy.ytc.contexts;

public enum RequestType {

    GET,
    PUT,
    POST,
    PATCH,
    DELETE,

    UNKNOWN;

    public static RequestType parse(String type) {
        switch (type.toLowerCase().trim()) {
            case "get":
                return RequestType.GET;
            case "post":
                return RequestType.POST;
            case "put":
                return RequestType.PUT;
            case "patch":
                return RequestType.PATCH;
            case "delete":
                return RequestType.DELETE;
            default:
                return RequestType.UNKNOWN;
        }
    }

}
