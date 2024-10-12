package com.example.demo.model;

import demo.chess.definitions.Color;
import demo.chess.definitions.fields.Field;

public interface DisplayedField {

	Color getColor();

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

	void setColor(Color color);

	/**
	 * @return the field
	 */
	Field getField();

	/**
	 * @param field the field to set
	 */
	void setField(Field field);

}
