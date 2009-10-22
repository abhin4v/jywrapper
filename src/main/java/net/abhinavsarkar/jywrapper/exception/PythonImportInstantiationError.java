package net.abhinavsarkar.jywrapper.exception;

/**
 * @author AbhinavSarkar
 *
 */
public class PythonImportInstantiationError extends RuntimeException {

	private static final long serialVersionUID = -6419405475631226539L;

	public PythonImportInstantiationError() {
	}

	public PythonImportInstantiationError(String message) {
		super(message);
	}

	public PythonImportInstantiationError(Throwable cause) {
		super(cause);
	}

	public PythonImportInstantiationError(String message, Throwable cause) {
		super(message, cause);
	}

}
