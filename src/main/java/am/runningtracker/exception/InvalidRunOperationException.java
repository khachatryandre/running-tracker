package am.runningtracker.exception;

public class InvalidRunOperationException extends AppException {
    public static final String defaultErrorType = "Invalid run operation";

    public InvalidRunOperationException(String message) {
        super(defaultErrorType, message);
    }
}
