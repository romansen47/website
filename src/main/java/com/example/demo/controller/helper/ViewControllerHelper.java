package com.example.demo.controller.helper;

import java.io.IOException;
import java.util.Map;

import org.springframework.ui.Model;

import demo.chess.definitions.engines.ChessEngine;
import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.definitions.engines.PlayerEngine;
import demo.chess.game.Game;

/**
 * The `ViewControllerHelper` interface defines essential methods for managing
 * and configuring the user interface of a chess game application, specifically
 * the frontend display attributes, game state, engine settings, and real-time
 * updates. Implementations of this interface facilitate interactions between
 * the backend chess logic and the frontend view, ensuring that the visual
 * elements remain in sync with the actual game state.
 *
 * Core responsibilities include: - Adding attributes to the model for rendering
 * the chessboard and UI elements. - Creating new fields and pieces for display
 * based on game state changes. - Configuring engine settings and managing
 * engine shutdown hooks. - Setting up game clocks with specified increments and
 * handling timeout actions. - Saving and updating game states to support
 * resumable gameplay and adjustments to engine parameters for improved
 * performance and player experience.
 *
 * This interface acts as a bridge, maintaining a separation between game logic
 * and UI configuration to enable a responsive and interactive user experience.
 */
public interface ViewControllerHelper {

	/**
	 * Adds attributes to the model to render the chessboard view, including the
	 * color theme, time left for each player, and other UI settings.
	 *
	 * @param color           The theme color of the chessboard.
	 * @param whiteTimeString The formatted time remaining for the white player.
	 * @param blackTimeString The formatted time remaining for the black player.
	 * @param model           The Model instance to which attributes are added.
	 * @param evaluationEngines 
	 */
	void addModelAttributes(String color, String whiteTimeString, String blackTimeString, Model model, Map<String, EvaluationEngine> evaluationEngines);

	/**
	 * Creates new fields representing the chessboard squares, setting up attributes
	 * such as color and coordinates for display.
	 */
	void createNewFields();

	/**
	 * Initializes and sets up view variables that are not yet set, typically
	 * related to the chess engine's evaluation configuration.
	 *
	 * @param evaluationEngine The evaluation engine instance being configured.
	 */
	void setUnsetViewVariables(EvaluationEngine evaluationEngine);

	/**
	 * Configures settings for UCI (Universal Chess Interface) engines to ensure
	 * they are ready for gameplay with parameters such as depth and threading.
	 */
	void setupEngineConfigurations();

	/**
	 * Creates new display representations for chess pieces based on their current
	 * positions in the given Game instance.
	 *
	 * @param chessGame The current game instance, providing piece positions.
	 */
	void createNewPiecesFromExistingPieces(Game chessGame);

	/**
	 * Sets up shutdown hooks for all chess engines to ensure they close gracefully
	 * upon application termination, preventing resource leaks.
	 *
	 * @param playerEngines A map of engines that require shutdown hooks.
	 */
	void createShutdownHooks(Map<String, ? extends ChessEngine> playerEngines);

	/**
	 * Initializes clocks for both players in the game, with appropriate settings
	 * for increment and actions when a player's time runs out.
	 *
	 * @param chessGame The game instance for which clocks are being set up.
	 */
	void seupClocks(Game chessGame);

	/**
	 * Saves the current game state to a specified file path, storing moves and
	 * positions to resume or review the game later.
	 *
	 * @param path      The file path to save the game data.
	 * @param chessGame The game instance containing the moves to be saved.
	 * @throws IOException If an I/O error occurs during the save process.
	 */
	void saveGame(String path, Game chessGame) throws IOException;

	/**
	 * Updates settings specific to UCI engines, such as depth, threads, hash size,
	 * and move overhead, for both white and black players.
	 *
	 * @param uciEngineDepthForWhite The depth setting for the white engine.
	 * @param threadsForWhite        The number of threads for the white engine.
	 * @param hashSizeForWhite       The hash size for the white engine.
	 * @param contemptForWhite       The contempt setting for the white engine.
	 * @param moveOverheadForWhite   The move overhead setting for the white engine.
	 * @param uciEloForWhite         The ELO rating for the white engine.
	 * @param uciEngineDepthForBlack The depth setting for the black engine.
	 * @param threadsForBlack        The number of threads for the black engine.
	 * @param hashSizeForBlack       The hash size for the black engine.
	 * @param contemptForBlack       The contempt setting for the black engine.
	 * @param moveOverheadForBlack   The move overhead setting for the black engine.
	 * @param uciEloForBlack         The ELO rating for the black engine.
	 * @param selectedEngineForWhite The name of the selected engine for white.
	 * @param selectedEngineForBlack The name of the selected engine for black.
	 * @param playerEngines          A map of available player engines to apply
	 *                               settings.
	 */
	void updateUciEngineSettings(int uciEngineDepthForWhite, int threadsForWhite, int hashSizeForWhite,
			int contemptForWhite, int moveOverheadForWhite, int uciEloForWhite, int uciEngineDepthForBlack,
			int threadsForBlack, int hashSizeForBlack, int contemptForBlack, int moveOverheadForBlack,
			int uciEloForBlack, String selectedEngineForWhite, String selectedEngineForBlack,
			Map<String, PlayerEngine> playerEngines);
	
	void downloadGameAnalysis();

}
