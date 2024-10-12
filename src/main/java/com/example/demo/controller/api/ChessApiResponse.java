package com.example.demo.controller.api;

public class ChessApiResponse<T> {

	private boolean success;
	private T data;
	private String comment;

	public ChessApiResponse(boolean success, T data) {
		this.success = success;
		this.data = data;
	}

	public ChessApiResponse<T> addComment(String comment) {
		this.comment = comment;
		return this;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
