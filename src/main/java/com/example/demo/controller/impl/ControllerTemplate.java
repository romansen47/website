package com.example.demo.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AppAdmin;
import com.example.demo.ImagePath;
import com.example.demo.controller.ChessController;
import com.example.demo.elements.Attributes;
import com.example.demo.elements.KEY;
import com.example.demo.model.Config;
import com.example.demo.model.DisplayedPiece;
import com.example.demo.websockets.WebSocketService;

import demo.chess.definitions.Color;
import demo.chess.definitions.PieceType;
import demo.chess.definitions.engines.Engine;
import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.definitions.engines.PlayerEngine;
import demo.chess.definitions.pieces.Piece;
import demo.chess.game.Game;
import demo.chess.load.GameLoader;

/**
 * Abstract controller class providing common functionalities for the Chess
 * application.
 * <p>
 * This class serves as a base template for chess-related controllers, offering
 * common methods and attributes needed to manage the chess game state, UI
 * elements, and interactions with the UciEngine engine.
 * </p>
 */
public abstract class ControllerTemplate implements ChessController {

	@Autowired
	protected AppAdmin admin;

	@Autowired
	protected Map<String, EvaluationEngine> evaluationEngines;

	@Autowired
	protected Map<String, PlayerEngine> playerEngines;

	@Autowired
	protected WebSocketService webSocketService;

	@Autowired
	protected Config viewConfig;

	@Autowired
	protected Attributes attributes;

	public void setup() throws Exception {
		if (get(KEY.PLAYER_ENGINE_FOR_WHITE) == null) {
			put(KEY.PLAYER_ENGINE_FOR_WHITE, playerEngines.get(Engine.STOCKFISH_16.toString()));
		}
		if (get(KEY.PLAYER_ENGINE_FOR_BLACK) == null) {
			put(KEY.PLAYER_ENGINE_FOR_BLACK, playerEngines.get(Engine.STOCKFISH_16.toString()));
		}
	}

	@SuppressWarnings("unchecked")
	protected void loadGame(String path) throws Exception {
		reset();
		GameLoader loader = new GameLoader();
		loader.loadGame(path, ((Game) get(KEY.CHESSGAME)));
		List<DisplayedPiece> listOfPiecesToRemove = new ArrayList<>();
		if ((boolean) get(KEY.REGULAR)) {
			for (Piece piece : ((Game) get(KEY.CHESSGAME)).getWhitePlayer().getPieces()) {
				DisplayedPiece element = getElement(piece);
				element.setTop((8 - piece.getField().getRank()) * viewConfig.getSquareSize());
				element.setLeft(
						viewConfig.getLeftOffset() + (piece.getField().getFile() - 1) * viewConfig.getSquareSize());
			}
			for (Piece piece : ((Game) get(KEY.CHESSGAME)).getBlackPlayer().getPieces()) {
				DisplayedPiece element = getElement(piece);
				element.setTop((8 - piece.getField().getRank()) * viewConfig.getSquareSize());
				element.setLeft(
						viewConfig.getLeftOffset() + (piece.getField().getFile() - 1) * viewConfig.getSquareSize());
			}
			for (DisplayedPiece element : (List<DisplayedPiece>) get(KEY.ELEMENTS)) {
				if (!((Game) get(KEY.CHESSGAME)).getWhitePlayer().getPieces().contains(element.getPiece())
						&& !((Game) get(KEY.CHESSGAME)).getBlackPlayer().getPieces().contains(element.getPiece())) {
					listOfPiecesToRemove.add(element);
				}
			}
		} else {
			for (Piece piece : ((Game) get(KEY.CHESSGAME)).getWhitePlayer().getPieces()) {
				DisplayedPiece element = getElement(piece);
				element.setTop((piece.getField().getRank() - 1) * viewConfig.getSquareSize());
				element.setLeft(
						viewConfig.getLeftOffset() + (8 - piece.getField().getFile()) * viewConfig.getSquareSize());
			}
			for (Piece piece : ((Game) get(KEY.CHESSGAME)).getBlackPlayer().getPieces()) {
				DisplayedPiece element = getElement(piece);
				element.setTop((piece.getField().getRank() - 1) * viewConfig.getSquareSize());
				element.setLeft(
						viewConfig.getLeftOffset() + (8 - piece.getField().getFile()) * viewConfig.getSquareSize());
			}

			for (DisplayedPiece element : (List<DisplayedPiece>) get(KEY.ELEMENTS)) {
				if (!((Game) get(KEY.CHESSGAME)).getWhitePlayer().getPieces().contains(element.getPiece())
						&& !((Game) get(KEY.CHESSGAME)).getBlackPlayer().getPieces().contains(element.getPiece())) {
					listOfPiecesToRemove.add(element);
				}
			}
		}
		webSocketService.updateMoveList();
		((List<DisplayedPiece>) get(KEY.ELEMENTS)).removeAll(listOfPiecesToRemove);
	}

	public Object get(KEY playerEngineForBlack) {
		return attributes.get(playerEngineForBlack);
	}

	public void put(KEY s, Object o) {
		attributes.put(s, o);
	}
	
	/**
	 * Returns the displayable element associated with the given chess piece.
	 *
	 * @param piece the chess piece
	 * @return the displayable element associated with the given chess piece
	 * @throws NoElementFoundException if no displayable element is found for the
	 *                                 given piece
	 */
	@SuppressWarnings("unchecked") 
	protected DisplayedPiece getElement(Piece piece) throws NoElementFoundException {
		for (DisplayedPiece element : (List<DisplayedPiece>) get(KEY.ELEMENTS)) {
			if (element.getPiece().equals(piece)) {
				return element;
			}
		}
		throw new NoElementFoundException(((Game) get(KEY.CHESSGAME)), piece);
	}

	protected Game getChessGame() {
		return ((Game) get(KEY.CHESSGAME));
	}

	protected abstract Logger getLogger();

	protected abstract String reset() throws Exception;

	protected EvaluationEngine getEvaluationEngine() {
		EvaluationEngine evaluationEngine = (EvaluationEngine) get(KEY.EVALUATION_ENGINE);
		if (evaluationEngine == null) {
			return this.evaluationEngines.get(Engine.FRUIT.toString());
		}
		return evaluationEngine;
	}
}
