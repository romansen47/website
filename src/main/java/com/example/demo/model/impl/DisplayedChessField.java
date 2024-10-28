package com.example.demo.model.impl;

import com.example.demo.model.DisplayedField;

import demo.chess.definitions.Color;
import demo.chess.definitions.fields.Field;

/**
 * `DisplayedChessField` represents a displayable field in the chess UI, with properties
 * such as color, dimensions, and position on the board. This class provides methods to
 * get and set visual attributes, as well as the underlying chess logic fields.
 * <p>
 * Each `DisplayedChessField` instance corresponds to a square on the chessboard with a
 * specific color (black or white), size, and position. It also maintains the logical
 * field data (rank and file) for integrating with the chess game logic.
 * </p>
 */
public class DisplayedChessField implements DisplayedField {

	/** The color of the chess field, indicating whether it is a light or dark square. */
    private Color color;

    /** The width of the chess field in pixels. */
    private int width;

    /** The height of the chess field in pixels. */
    private int height;

    /** The top position of the chess field in the UI layout, in pixels. */
    private int top;

    /** The left position of the chess field in the UI layout, in pixels. */
    private int left;

    /** The rank of the chess field (row position on the chessboard). */
    private int rank;

    /** The file of the chess field (column position on the chessboard). */
    private int file;

    /** The actual field reference in the chess game logic, representing its location and identity. */
    private Field field;

    /**
     * Constructs a new DisplayedChessField with specified properties for UI display.
     *
     * @param color  the color of the chess field, indicating whether it is light or dark
     * @param width  the width of the chess field in pixels
     * @param height the height of the chess field in pixels
     * @param top    the top position of the chess field in the UI layout, in pixels
     * @param left   the left position of the chess field in the UI layout, in pixels
     * @param field  the field representation in the chess logic, including its location and identity
     */
	public DisplayedChessField(Color color, int width, int height, int top, int left, Field field) {
		this.setColor(color);
		this.setWidth(width);
		this.setHeight(height);
		this.setTop(top);
		this.setLeft(left);
		this.setField(field);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
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
	public int getRank() {
		return rank;
	}

	@Override
	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public int getFile() {
		return file;
	}

	@Override
	public void setFile(int file) {
		this.file = file;
	}

	@Override
	public Field getField() {
		return field;
	}

	@Override
	public void setField(Field field) {
		this.field = field;
	}

}
