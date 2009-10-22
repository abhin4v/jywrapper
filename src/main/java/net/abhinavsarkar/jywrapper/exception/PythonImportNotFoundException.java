package net.abhinavsarkar.jywrapper.exception;

/**
 * @author AbhinavSarkar
 *
 */
public class PythonImportNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 3287534701588858305L;

	public PythonImportNotFoundException() {
	}

	public PythonImportNotFoundException(String message) {
		super(message);
	}

	public PythonImportNotFoundException(Throwable cause) {
		super(cause);
	}

	public PythonImportNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
