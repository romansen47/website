package com.example.demo.controller.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import demo.chess.definitions.pieces.Piece;
import demo.chess.game.Game;

/**
 * Exception thrown when an expected UI element for a specific chess piece is
 * not found.
 * <p>
 * This exception is used to signal that a chess piece does not have an
 * associated display element, which may happen during board rendering or move
 * validation processes.
 * </p>
 */
public class NoElementFoundException extends Exception {

	/** Logger instance for capturing error details related to this exception. */
	private static final Logger logger = LogManager.getLogger(NoElementFoundException.class);

	/** The game context in which the exception occurred. */
	final Game game;

	/** The chess piece for which no UI element was found. */
	final Piece piece;

	/**
	 * Constructs a new {@code NoElementFoundException} with the specified game and
	 * piece.
	 *
	 * @param game  the game instance associated with the missing element
	 * @param piece the chess piece without an associated UI element
	 */
	public NoElementFoundException(Game game, Piece piece) {
		super();
		this.game = game;
		this.piece = piece;
	}

	/** Unique identifier for serialization. */
	private static final long serialVersionUID = 3545191087387619058L;

	/**
	 * Logs the stack trace along with the game state and details of the missing
	 * piece.
	 * <p>
	 * Overrides the default {@code printStackTrace} to log an error message
	 * including the list of moves performed and details of the piece without a UI
	 * element.
	 * </p>
	 */
	@Override
	public void printStackTrace() {
		logger.error("Moves performed: {}\rpiece without element: {}", game.getMoveList().toArray(), piece.toString());
	}
}
