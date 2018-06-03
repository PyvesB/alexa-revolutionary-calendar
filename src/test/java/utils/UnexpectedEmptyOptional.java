package utils;

import org.opentest4j.AssertionFailedError;

public class UnexpectedEmptyOptional extends AssertionFailedError {

	private static final long serialVersionUID = 1L;

	public UnexpectedEmptyOptional() {
		super("expected non empty optional");
	}

}
