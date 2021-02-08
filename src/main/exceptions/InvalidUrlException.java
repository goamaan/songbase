package exceptions;

public class InvalidUrlException extends Exception {
    public InvalidUrlException(String s) {
        super(s);
    }
}