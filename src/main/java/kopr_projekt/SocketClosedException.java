package kopr_projekt;

public class SocketClosedException extends Exception{
    public SocketClosedException() {
    }

    public SocketClosedException(String message) {
        super(message);
    }

    public SocketClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
