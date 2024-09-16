package indeed.googledrive_derisking.exceptions;

public class FolderOperationException extends RuntimeException {
    public FolderOperationException(final String message, final Throwable e) {
        super(message, e);
    }
}
