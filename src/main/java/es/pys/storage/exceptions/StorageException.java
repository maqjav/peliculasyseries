package es.pys.storage.exceptions;

public class StorageException extends Exception {

	private static final long serialVersionUID = 1L;

	public StorageException() {
		super();
	}

	public StorageException(String message) {
		super(message);
	}

	public StorageException(final Throwable throwable) {
		super(throwable);
	}

	public StorageException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
