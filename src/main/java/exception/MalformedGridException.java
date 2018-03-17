package exception;

public class MalformedGridException extends Exception {

    public MalformedGridException(){
        super();
    }

    public MalformedGridException(String message){
        super(message);
    }
}
