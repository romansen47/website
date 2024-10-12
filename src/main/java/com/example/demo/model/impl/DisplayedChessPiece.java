package com.example.demo.model.impl;

import com.example.demo.model.DisplayedPiece;

import demo.chess.definitions.pieces.Piece;

/**
 * Represents a displayable element on the chessboard, such as a chess piece.
 */
public class DisplayedChessPiece implements DisplayedPiece {

	private String imagePath;
	private int width;
	private int height;
	private int top;
	private int left;
	private int visibleOrder = 0;
	private Piece piece;

	/**
	 * Constructs a new DisplayedPiece with the specified attributes.
	 *
	 * @param imagePath the path to the image representing the element
	 * @param width     the width of the element
	 * @param height    the height of the element
	 * @param top       the top position of the element
	 * @param left      the left position of the element
	 * @param piece     the chess piece associated with this element
	 */
	public DisplayedChessPiece(String imagePath, int width, int height, int top, int left, Piece piece) {
		this.imagePath = imagePath;
		this.width = width;
		this.height = height;
		this.top = top;
		this.left = left;
		this.setPiece(piece);
	}

	@Override
	public String getImagePath() {
		return imagePath;
	}

	@Override
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int getTop() {
		return top;
	}

	@Override
	public void setTop(int top) {
		this.top = top;
	}

	@Override
	public int getLeft() {
		return left;
	}

	@Override
	public void setLeft(int left) {
		this.left = left;
	}

	@Override
	public int getVisibleOrder() {
		return this.visibleOrder;
	}

	@Override
	public void setVisibleOrder(int visibleOrder) {
		this.visibleOrder = visibleOrder;
	}

	@Override
	public Piece getPiece() {
		return piece;
	}

	@Override
	public void setPiece(Piece piece) {
		this.piece = piece;
	}

	@Override
	public String toString() {
		return getPiece().toString();
	}
}
