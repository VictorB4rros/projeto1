package com.lpg3.projeto1.services.exceptions;

import java.io.Serial;

public class DatabaseException extends RuntimeException {

	@Serial
    private static final long serialVersionUID = 1L;

	public DatabaseException(String msg) {
		super(msg);
	}
}