package com.xpanse.cp.product.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomExceptionHandler {
	private static final Logger logger = LogManager.getLogger(CustomExceptionHandler.class);

	public CustomExceptionHandler() {
	}

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<?> handleResourceNotFoundException(ProductNotFoundException ex) {

		// Create an error response
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
				ex.getMessage());
		// Log the error (optional)
		logger.error("ResourceNotFoundException Exception occurred: {}", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<?> handleInvalidRequestException(InvalidRequestException ex) {

		// Create an error response
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				ex.getMessage());
		logger.error("InvalidRequestException Exception occurred: | {} ", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DuplicateProductException.class)
	public ResponseEntity<?> handleDuplicateProductException(DuplicateProductException ex) {

		// Create an error response
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				ex.getMessage());
		logger.error("DuplicateProductException Exception occurred: | {} ", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

}
