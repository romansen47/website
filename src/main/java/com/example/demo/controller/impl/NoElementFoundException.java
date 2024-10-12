package com.example.demo.controller.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import demo.chess.definitions.pieces.Piece;
import demo.chess.game.Game;

public class NoElementFoundException extends Exception {

	private static final Logger logger = LogManager.getLogger(NoElementFoundException.class);

	final Game game;

	final Piece piece;

	public NoElementFoundException(Game game, Piece piece) {
		super();
		this.game = game;
		this.piece = piece;
	}

	private static final long serialVersionUID = 3545191087387619058L;

	@Override
	public void printStackTrace() {
		logger.error("Moves performed: {}\rpiece without element: {}", game.getMoveList().toArray(), piece.toString());
	}

}
