package com.example.demo.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AppAdmin;
import com.example.demo.ImagePath;
import com.example.demo.controller.ChessController;
import com.example.demo.elements.Attributes;
import com.example.demo.model.Config;
import com.example.demo.model.DisplayedPiece;
import com.example.demo.websockets.WebSocketService;

import demo.chess.definitions.Color;
import demo.chess.definitions.PieceType;
import demo.chess.definitions.engines.Engine;
import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.definitions.engines.PlayerEngine;
import demo.chess.definitions.engines.impl.EvaluationUciEngine;
import demo.chess.definitions.engines.impl.PlayerUciEngine;
import demo.chess.definitions.moves.MoveList;
import demo.chess.definitions.moves.impl.MoveListImpl;
import demo.chess.definitions.pieces.Piece;
import demo.chess.game.Game;
import demo.chess.load.GameLoader;
import org.apache.logging.log4j.Logger;

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
	protected Map<Engine, EvaluationEngine> evaluationEngines;
	
	@Autowired
	protected Map<Engine, PlayerEngine> playerEngines;
	 
	@Autowired
	protected WebSocketService webSocketService;

	@Autowired
	protected Config viewConfig;

	@Autowired
	protected Attributes attributes;

	public void setup() throws Exception{
		put("playerEngineForWhite", playerEngines.get(Engine.STOCKFISH));
		put("playerEngineForBlack", playerEngines.get(Engine.STOCKFISH));
	}

	protected void loadGame(String path) throws Exception {
		GameLoader loader = new GameLoader();
		MoveList copy = new MoveListImpl();
		copy.addAll(((Game) get("chessGame")).getMoveList());
		loader.loadGame(path, ((Game) get("chessGame")));
		List<DisplayedPiece> listOfPiecesToRemove = new ArrayList<>();
		if ((boolean) get("regular")) {
			for (Piece piece : ((Game) get("chessGame")).getWhitePlayer().getPieces()) {
				DisplayedPiece element = getElement(piece);
				element.setTop((8 - piece.getField().getRank()) * viewConfig.getSquareSize());
				element.setLeft(
						viewConfig.getLeftOffset() + (piece.getField().getFile() - 1) * viewConfig.getSquareSize());
			}
			for (Piece piece : ((Game) get("chessGame")).getBlackPlayer().getPieces()) {
				DisplayedPiece element = getElement(piece);
				element.setTop((8 - piece.getField().getRank()) * viewConfig.getSquareSize());
				element.setLeft(
						viewConfig.getLeftOffset() + (piece.getField().getFile() - 1) * viewConfig.getSquareSize());
			}
			for (DisplayedPiece element : (List<DisplayedPiece>) get("elements")) {
				if (!((Game) get("chessGame")).getWhitePlayer().getPieces().contains(element.getPiece())
						&& !((Game) get("chessGame")).getBlackPlayer().getPieces().contains(element.getPiece())) {
					listOfPiecesToRemove.add(element);
				}
			}
		} else {
			for (Piece piece : ((Game) get("chessGame")).getWhitePlayer().getPieces()) {
				DisplayedPiece element = getElement(piece);
				element.setTop((piece.getField().getRank() - 1) * viewConfig.getSquareSize());
				element.setLeft(
						viewConfig.getLeftOffset() + (8 - piece.getField().getFile()) * viewConfig.getSquareSize());
			}
			for (Piece piece : ((Game) get("chessGame")).getBlackPlayer().getPieces()) {
				DisplayedPiece element = getElement(piece);
				element.setTop((piece.getField().getRank() - 1) * viewConfig.getSquareSize());
				element.setLeft(
						viewConfig.getLeftOffset() + (8 - piece.getField().getFile()) * viewConfig.getSquareSize());
			}

			for (DisplayedPiece element : (List<DisplayedPiece>) get("elements")) {
				if (!((Game) get("chessGame")).getWhitePlayer().getPieces().contains(element.getPiece())
						&& !((Game) get("chessGame")).getBlackPlayer().getPieces().contains(element.getPiece())) {
					listOfPiecesToRemove.add(element);
				}
			}
		}
		((List<DisplayedPiece>) get("elements")).removeAll(listOfPiecesToRemove);
	}

	public Object get(String s) {
		return attributes.get(s);
	}

	public void put(String s, Object o) {
		attributes.put(s, o);
	}

	protected String getImagePath(Color color, PieceType pieceType) {
		String imagePath;
		switch (color) {
		case WHITE:
			switch (pieceType) {
			case KING:
				imagePath = ImagePath.WHITE_KING.path;
				break;
			case QUEEN:
				imagePath = ImagePath.WHITE_QUEEN.path;
				break;
			case ROOK:
				imagePath = ImagePath.WHITE_ROOK.path;
				break;
			case KNIGHT:
				imagePath = ImagePath.WHITE_KNIGHT.path;
				break;
			case BISHOP:
				imagePath = ImagePath.WHITE_BISHOP.path;
				break;
			default:
				imagePath = ImagePath.WHITE_PAWN.path;
				break;
			}
			break;
		default:
			switch (pieceType) {
			case KING:
				imagePath = ImagePath.BLACK_KING.path;
				break;
			case QUEEN:
				imagePath = ImagePath.BLACK_QUEEN.path;
				break;
			case ROOK:
				imagePath = ImagePath.BLACK_ROOK.path;
				break;
			case KNIGHT:
				imagePath = ImagePath.BLACK_KNIGHT.path;
				break;
			case BISHOP:
				imagePath = ImagePath.BLACK_BISHOP.path;
				break;
			default:
				imagePath = ImagePath.BLACK_PAWN.path;
				break;
			}
		}
		return imagePath;
	}

	/**
	 * Returns the displayable element associated with the given chess piece.
	 *
	 * @param piece the chess piece
	 * @return the displayable element associated with the given chess piece
	 * @throws NoElementFoundException if no displayable element is found for the
	 *                                 given piece
	 */
	protected DisplayedPiece getElement(Piece piece) throws NoElementFoundException {
		for (DisplayedPiece element : (List<DisplayedPiece>) get("elements")) {
			if (element.getPiece().equals(piece)) {
				return element;
			}
		}
		throw new NoElementFoundException(((Game) get("chessGame")), piece);
	}

	protected Game getChessGame() {
		return ((Game) get("chessGame"));
	}
	
	protected abstract Logger getLogger();
	
	protected abstract String reset() throws Exception;

	protected EvaluationEngine getEvaluationEngine() {
		return this.evaluationEngines.get(Engine.STOCKFISH);
	}
}
