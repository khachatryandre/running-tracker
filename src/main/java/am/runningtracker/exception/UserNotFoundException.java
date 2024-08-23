package am.runningtracker.exception;

public class UserNotFoundException extends AppException {
    private static final String defaultErrorType = "User not found";
    private static final String defaultMessage = "User you are trying to access doesn't exist";

    public UserNotFoundException(String message) {
        super(defaultErrorType, message);
    }

    public UserNotFoundException() {
        super(defaultErrorType, defaultMessage);
    }
}