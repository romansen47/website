package com.example.demo.controller.api;

/**
 * Generic wrapper class for API responses in the chess application. It
 * encapsulates the response data, a success status, and an optional comment.
 *
 * @param <T> The type of data being returned in the response
 */
public class ChessApiResponse<T> {

	// Indicates whether the API call was successful
	private boolean success;

	// Generic data object to hold the response payload
	private T data;

	// Optional comment for additional response information
	private String comment;

	/**
	 * Constructor to initialize a response with success status and data payload.
	 *
	 * @param success Indicates if the API call was successful
	 * @param data    The data to return in the response
	 */
	public ChessApiResponse(boolean success, T data) {
		this.success = success;
		this.data = data;
	}

	/**
	 * Adds an optional comment to the response for extra context or information.
	 *
	 * @param comment The comment to be added
	 * @return The updated ChessApiResponse object
	 */
	public ChessApiResponse<T> addComment(String comment) {
		this.comment = comment;
		return this;
	}

	/**
	 * Getter for the success status of the response.
	 *
	 * @return true if the response indicates success; false otherwise
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Setter for the success status of the response.
	 *
	 * @param success The success status to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * Getter for the data payload of the response.
	 *
	 * @return The data contained in the response
	 */
	public T getData() {
		return data;
	}

	/**
	 * Setter for the data payload of the response.
	 *
	 * @param data The data to set in the response
	 */
	public void setData(T data) {
		this.data = data;
	}

	/**
	 * Getter for the optional comment associated with the response.
	 *
	 * @return The comment text
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Setter for the optional comment associated with the response.
	 *
	 * @param comment The comment text to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
}
