package com.example.demo.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AppAdmin;
import com.example.demo.controller.ChessController;
import com.example.demo.elements.Attributes;
import com.example.demo.elements.KEY;
import com.example.demo.model.Config;
import com.example.demo.model.DisplayedPiece;
import com.example.demo.websockets.WebSocketService;

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
 * elements, and interactions with the chess engines (UCI-based).
 * </p>
 * <p>
 * Key responsibilities include:
 * - **Game Initialization**: Setting up player engines and creating a new game if necessary.
 * - **Game Loading**: Loading a saved game state and updating UI elements accordingly.
 * - **Engine Management**: Managing multiple engines for evaluation and player moves.
 * - **WebSocket Communication**: Broadcasting updates to the frontend via WebSocket.
 * </p>
 */
public abstract class ControllerTemplate implements ChessController {

    // Autowired dependencies to provide application configuration, admin functionalities,
    // engine configurations, and WebSocket services.

	/**
	 * Provides administrative functionalities for managing chess game configurations,
	 * player settings, and system operations within the application.
	 */
	@Autowired
	protected AppAdmin admin;

	/**
	 * A collection of evaluation engines, each mapped by a unique engine name.
	 * These engines are responsible for analyzing chess positions and providing
	 * evaluations based on configured parameters.
	 */
	@Autowired
	protected Map<String, EvaluationEngine> evaluationEngines;

	/**
	 * A collection of player engines, each mapped by a unique engine name.
	 * These engines represent AI-driven chess players that generate moves
	 * based on preconfigured settings and parameters.
	 */
	@Autowired
	protected Map<String, PlayerEngine> playerEngines;

	/**
	 * Service providing WebSocket communication capabilities for real-time updates.
	 * Used to synchronize game states, clocks, and moves across clients.
	 */
	@Autowired
	protected WebSocketService webSocketService;

	/**
	 * Contains application-level configuration settings for the chess UI and
	 * game presentation, such as board size, colors, and other visual preferences.
	 */
	@Autowired
	protected Config viewConfig;

	/**
	 * A map-like structure that stores shared attributes and state data for the
	 * chess application. Facilitates communication and data sharing across
	 * different application components.
	 */
	@Autowired
	protected Attributes attributes;

	/**
     * Initializes the controller, ensuring that player engines for both white and
     * black players are set up using the STOCKFISH_16 engine by default if not
     * already defined in the attributes.
     *
     * @throws Exception if any error occurs during setup
     */
	public void setup() throws Exception {
		if (get(KEY.PLAYER_ENGINE_FOR_WHITE) == null) {
			put(KEY.PLAYER_ENGINE_FOR_WHITE, playerEngines.get(Engine.STOCKFISH_16.toString()));
		}
		if (get(KEY.PLAYER_ENGINE_FOR_BLACK) == null) {
			put(KEY.PLAYER_ENGINE_FOR_BLACK, playerEngines.get(Engine.STOCKFISH_16.toString()));
		}
	}

	 /**
     * Loads a saved game state from a specified file path and updates displayed
     * piece positions to reflect the loaded state. Non-active pieces are removed
     * from the display list.
     *
     * @param path the path to the saved game file
     * @throws Exception if any error occurs during game loading
     */
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

	/**
     * Retrieves an attribute by key from the attributes store.
     *
     * @param playerEngineForBlack the attribute key
     * @return the value associated with the key
     */
	public Object get(KEY playerEngineForBlack) {
		return attributes.get(playerEngineForBlack);
	}

	/**
     * Stores an attribute with a specified key and value in the attributes store.
     *
     * @param key the attribute key
     * @param value the value to store
     */
	public void put(KEY key, Object value) {
		attributes.put(key, value);
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

    /**
     * Creates and returns a new `Game` instance. Stops existing engines and clocks,
     * and initializes a new game with the configured player time.
     *
     * @return the newly created `Game` instance
     * @throws Exception if game creation fails
     */
	protected Game createNewGame() throws Exception {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		if (chessGame != null) {
			evaluationEngines.entrySet().stream().forEach(entry -> entry.getValue().stopEvaluation());
			playerEngines.entrySet().stream().forEach(entry -> entry.getValue().stopEvaluation());
			if (chessGame.getWhitePlayer().getChessClock().isStarted()) {
				chessGame.getWhitePlayer().getChessClock().stop();
			}
			if (chessGame.getBlackPlayer().getChessClock().isStarted()) {
				chessGame.getBlackPlayer().getChessClock().stop();
			} 
			evaluationEngines.entrySet().stream().forEach(entry -> entry.getValue().clearChachedLines());
		}
		chessGame = admin.chessGame(viewConfig.getTimeForEachPlayer());
		put(KEY.CHESSGAME, chessGame);
		return chessGame;
	}

    /**
     * Retrieves the current `Game` instance or creates a new game if none exists.
     *
     * @return the current `Game` instance
     * @throws Exception if game retrieval or creation fails
     */
	protected Game getChessGame() throws Exception {
		Game chessGame = ((Game) get(KEY.CHESSGAME));
		if (chessGame == null) {
			chessGame = createNewGame();
		}
		return chessGame;
	}

	 /**
     * Retrieves the logger for the implementing class.
     *
     * @return the logger
     */
	protected abstract Logger getLogger();

    /**
     * Resets the controller state and any related resources.
     *
     * @return a string indicating the result of the reset operation
     * @throws Exception if reset fails
     */
	protected abstract String reset() throws Exception;

    /**
     * Retrieves the active `EvaluationEngine`. If none is set, returns the default
     * `FRUIT` evaluation engine.
     *
     * @return the current `EvaluationEngine` instance
     */
	protected EvaluationEngine getEvaluationEngine() {
		EvaluationEngine evaluationEngine = (EvaluationEngine) get(KEY.EVALUATION_ENGINE);
		if (evaluationEngine == null) {
			return this.evaluationEngines.get(Engine.FRUIT.toString());
		}
		return evaluationEngine;
	}
}
