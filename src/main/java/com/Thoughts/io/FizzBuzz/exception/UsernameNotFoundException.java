package com.Thoughts.io.FizzBuzz.exception;

public class UsernameNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public UsernameNotFoundException(String message) {
		super(message);
	}
}
