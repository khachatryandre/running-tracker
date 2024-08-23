package am.runningtracker.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final String errorType;

    public AppException(String errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

}
