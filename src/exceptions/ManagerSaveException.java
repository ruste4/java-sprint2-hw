package exceptions;

import java.io.IOError;

public class ManagerSaveException extends IOError {
    public ManagerSaveException(Throwable cause) {
        super(cause);
    }
}
