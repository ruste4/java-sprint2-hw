package exceptions;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ManagerSaveException extends UncheckedIOException {
    public ManagerSaveException(IOException cause) {
        super(cause);
    }
}
