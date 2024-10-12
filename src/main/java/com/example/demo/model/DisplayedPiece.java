package com.example.demo.model;

import demo.chess.definitions.pieces.Piece;

/**
 * Interface representing an element that can be displayed in the application.
 * <p>
 * This interface defines the methods required to manage the properties and
 * behavior of a displayable element, such as a chess piece, within the
 * application.
 * </p>
 */
public interface DisplayedPiece {

	/**
	 * Returns the chess piece associated with this displayable element.
	 *
	 * @return the chess piece
	 */
	Piece getPiece();

	/**
	 * Returns the visible order of this displayable element.
	 *
	 * @return the visible order
	 */
	int getVisibleOrder();

	/**
	 * Sets the visible order of this displayable element.
	 *
	 * @param visibleOrder the visible order to set
	 */
	void setVisibleOrder(int visibleOrder);

	/**
	 * Returns the image path of this displayable element.
	 *
	 * @return the image path
	 */
	String getImagePath();

	/**
	 * Sets the image path of this displayable element.
	 *
	 * @param imagePath the image path to set
	 */
	void setImagePath(String imagePath);

	/**
	 * Returns the width of this displayable element.
	 *
	 * @return the width
	 */
	int getWidth();

	/**
	 * Sets the width of this displayable element.
	 *
	 * @param width the width to set
	 */
	void setWidth(int width);

	/**
	 * Returns the height of this displayable element.
	 *
	 * @return the height
	 */
	int getHeight();

	/**
	 * Sets the height of this displayable element.
	 *
	 * @param height the height to set
	 */
	void setHeight(int height);

	/**
	 * Returns the top position of this displayable element.
	 *
	 * @return the top position
	 */
	int getTop();

	/**
	 * Sets the top position of this displayable element.
	 *
	 * @param top the top position to set
	 */
	void setTop(int top);

	/**
	 * Returns the left position of this displayable element.
	 *
	 * @return the left position
	 */
	int getLeft();

	/**
	 * Sets the left position of this displayable element.
	 *
	 * @param left the left position to set
	 */
	void setLeft(int left);

	/**
	 * Sets the chess piece associated with this displayable element.
	 *
	 * @param piece the chess piece to set
	 */
	void setPiece(Piece piece);
}
