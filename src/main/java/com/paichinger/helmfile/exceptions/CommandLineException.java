package com.paichinger.helmfile.exceptions;

public class CommandLineException extends RuntimeException {
	private final int returnCode;
	private final String errorMessage;
	
	
	public CommandLineException(int returnCode, String errorMessage) {
		this.returnCode = returnCode;
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public int getReturnCode() {
		return returnCode;
	}
}
