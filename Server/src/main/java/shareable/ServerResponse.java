package shareable;

import java.io.Serializable;

public class ServerResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        SUCCESS, ERROR, INFO, DONE
    }

    private Status status;
    private String message;
    private Object data; 

    public ServerResponse(Status status, String message) {
        this(status, message, null);
    }

    public ServerResponse(Status status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public Status getStatus() { return status; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
}