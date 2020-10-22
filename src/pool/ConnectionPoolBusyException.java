package pool;

public class ConnectionPoolBusyException extends RuntimeException {
    public ConnectionPoolBusyException(String msg){
        super(msg);
    }
    public ConnectionPoolBusyException(){}
}
