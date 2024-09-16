package indeed.googledrive_derisking.exceptions;

public class FileOperationException extends RuntimeException {
    public FileOperationException(final String message, final Throwable e) {
        super(message, e);
    }
}
