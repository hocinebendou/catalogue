package bio.tech.catalog.web.error;

public class FileTypeNotSupportedException  extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public FileTypeNotSupportedException () { super(); }

    public FileTypeNotSupportedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FileTypeNotSupportedException(final String message) {
        super(message);
    }

    public FileTypeNotSupportedException(final Throwable cause) {
        super(cause);
    }
}
