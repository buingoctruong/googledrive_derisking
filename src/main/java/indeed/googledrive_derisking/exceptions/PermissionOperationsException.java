package indeed.googledrive_derisking.exceptions;

public class PermissionOperationsException extends RuntimeException {
    public PermissionOperationsException(final String message, final Throwable e) {
        super(message, e);
    }
}
