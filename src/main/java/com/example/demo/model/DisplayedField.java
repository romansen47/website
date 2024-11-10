package com.example.demo.model;

import demo.chess.definitions.Color;
import demo.chess.definitions.fields.Field;

/**
 * Represents a displayable chessboard field, with properties for visual display
 * and positioning, as well as underlying data for game logic such as rank,
 * file, and field. The interface provides methods for managing display
 * attributes like position and size, as well as game-specific attributes like
 * color and field reference.
 */
public interface DisplayedField {

	/**
	 * Gets the color of this field, typically representing either a light or dark
	 * square.
	 *
	 * @return the color of the field
	 */
	Color getColor();

	/**
	 * Returns the width of this displayable element.
	 *
	 * @return the width of the element
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
	 * @return the height of the element
	 */
	int getHeight();

	/**
	 * Sets the height of this displayable element.
	 *
	 * @param height the height to set
	 */
	void setHeight(int height);

	/**
	 * Returns the top position (vertical offset) of this displayable element in
	 * pixels.
	 *
	 * @return the top position of the element
	 */
	int getTop();

	/**
	 * Sets the top position (vertical offset) of this displayable element in
	 * pixels.
	 *
	 * @param top the top position to set
	 */
	void setTop(int top);

	/**
	 * Returns the left position (horizontal offset) of this displayable element in
	 * pixels.
	 *
	 * @return the left position of the element
	 */
	int getLeft();

	/**
	 * Sets the left position (horizontal offset) of this displayable element in
	 * pixels.
	 *
	 * @param left the left position to set
	 */
	void setLeft(int left);

	/**
	 * Sets the color of this displayable element, usually representing the color of
	 * the square.
	 *
	 * @param color the color to set
	 */
	void setColor(Color color);

	/**
	 * Gets the `Field` associated with this displayable element, which contains the
	 * logical location on the chessboard, such as `e4` or `d2`.
	 *
	 * @return the field associated with this displayable element
	 */
	Field getField();

	/**
	 * Sets the `Field` associated with this displayable element, representing its
	 * logical location on the chessboard.
	 *
	 * @param field the field to set
	 */
	void setField(Field field);

	/**
	 * Gets the rank (row) number of this field, usually a value from 1 to 8.
	 *
	 * @return the rank of the field
	 */
	int getRank();

	/**
	 * Sets the rank (row) number of this field, usually a value from 1 to 8.
	 *
	 * @param rank the rank to set
	 */
	void setRank(int rank);

	/**
	 * Gets the file (column) number of this field, usually a value from 1 (for `a`)
	 * to 8 (for `h`).
	 *
	 * @return the file of the field
	 */
	int getFile();

	/**
	 * Sets the file (column) number of this field, typically a value from 1 to 8.
	 *
	 * @param file the file to set
	 */
	void setFile(int file);
}
