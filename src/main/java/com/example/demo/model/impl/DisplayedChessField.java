package com.example.demo.model.impl;

import com.example.demo.model.DisplayedField;

import demo.chess.definitions.Color;
import demo.chess.definitions.fields.Field;

public class DisplayedChessField implements DisplayedField {

	private Color color;
	private int width;
	private int height;
	private int top;
	private int left;
	private int rank;
	private int file;
	private Field field;

	/**
	 * @param file the file to set
	 */
	public void setFile(int file) {
		this.file = file;
	}

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

	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * @return the file
	 */
	public int getFile() {
		return file;
	}

	/**
	 * @return the field
	 */
	@Override
	public Field getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	@Override
	public void setField(Field field) {
		this.field = field;
	}

}
