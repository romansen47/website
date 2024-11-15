package com.example.demo.controller.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.controller.helper.ViewControllerHelper;
import com.example.demo.elements.KEY;
import com.example.demo.model.DisplayedField;

import demo.chess.definitions.Color;
import demo.chess.definitions.engines.EngineConfig;
import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.definitions.engines.impl.NoMoveFoundException;
import demo.chess.definitions.moves.Move;
import demo.chess.definitions.moves.MoveList;
import demo.chess.definitions.states.State;
import demo.chess.game.Game;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The `MainViewController` class is the primary controller for managing and
 * rendering the main view of the chess application. This controller handles
 * essential functionalities, including:
 *
 * - **Game Setup and Initialization**: Configures game settings, initializes
 * the chessboard view, and establishes engine configurations, readying the
 * application for gameplay. - **User Interaction**: Processes user actions,
 * such as resetting the board, starting a new game, adjusting engine match
 * settings, and managing piece movement. The controller ensures all moves and
 * settings adhere to the game's current configuration. - **Game State
 * Management**: Utilizes helper methods to handle the chess game state by
 * updating clocks, resetting the game board, saving and loading game states,
 * and configuring UI elements for proper display of the chessboard and pieces.
 * - **Settings Management**: Handles the retrieval and application of settings
 * related to the appearance, UCI engine configurations, and other customizable
 * game aspects. This allows users to tailor the gameplay experience, with
 * settings for color themes, board orientation, player time increments, and
 * more. - **Engine Management**: Configures and initiates UCI (Universal Chess
 * Interface) engines for automated move analysis, evaluation, and gameplay. It
 * also provides methods to start engine matches and synchronize UCI engine
 * activity with user gameplay. - **WebSocket Communication**: Ensures real-time
 * communication with the frontend, providing live updates on clocks, moves, and
 * state changes. This keeps the game state consistent across different clients
 * and interfaces.
 *
 * This class acts as a bridge between user interactions and game logic,
 * ensuring smooth gameplay, responsive UI updates, and robust control of the
 * chess engine configurations. It leverages the `ViewControllerHelper` for
 * encapsulating complex UI setup and configuration tasks, promoting code
 * modularity and clarity.
 */
@Controller
public class MainViewController extends ControllerTemplate {

	/**
	 * Logger instance for logging important events and errors within the
	 * controller.
	 */
	protected static final Logger logger = LogManager.getLogger();

	/**
	 * Helper instance for managing various view-related operations and
	 * configuration adjustments.
	 * <p>
	 * This helper provides utility methods that support complex UI interactions and
	 * setup tasks for the main view.
	 * </p>
	 */
	@Autowired
	private ViewControllerHelper helper;

	/**
	 * Initializes the chess game by setting up displayable elements, engines, and
	 * shutdown hooks. This method is called after the bean has been constructed.
	 *
	 * @throws Exception if initialization fails
	 */
	@PostConstruct
	public void init() throws Exception {
		Game chessGame = getChessGame();
		setup();
		helper.createNewPiecesFromExistingPieces(chessGame);
		helper.createShutdownHooks(evaluationEngines);
		helper.createShutdownHooks(playerEngines);
		helper.setupEngineConfigurations();
		helper.createNewPiecesFromExistingPieces((Game) get(KEY.CHESSGAME));
	}

	/**
	 * Initializes the chess game and sets up the displayable elements for the
	 * initial board configuration. This method is called after the bean has been
	 * constructed.
	 *
	 * @throws Exception if s.t. goes wrong
	 */
	@Override
	public void setup() throws Exception {
		super.setup();
		final Game chessGame = getChessGame();
		helper.seupClocks(chessGame);
		helper.setUnsetViewVariables(this.getEvaluationEngine());
		helper.createNewFields();

	}

	/**
	 * Handles GET requests for the main view of the chess application. Populates
	 * the model with the necessary data to render the view.
	 *
	 * @param model    The model used to pass data to the view.
	 * @param response The HTTP response object.
	 * @return The name of the Thymeleaf template to render.
	 * @throws Exception If any error occurs during processing.
	 */
	@GetMapping("/")
	@SuppressWarnings("unchecked")
	protected String mainView(Model model, HttpServletResponse response) throws Exception {

		// Initialize the time allocated for each player
		int timeForEachPlayer = viewConfig.getTimeForEachPlayer();
		int timeForWhite = timeForEachPlayer;
		int timeForBlack = timeForEachPlayer;

		model.addAttribute("viewConfig", viewConfig);
		// Retrieve the color label from the configuration
		String color = viewConfig.getColor().label;

		// Calculate the remaining time for both players
		timeForWhite = (int) getChessGame().getWhitePlayer().getChessClock().getTime(TimeUnit.SECONDS);
		timeForBlack = (int) getChessGame().getBlackPlayer().getChessClock().getTime(TimeUnit.SECONDS);

		// Convert the remaining time into minutes and seconds
		int minutesWhite = (timeForEachPlayer - timeForWhite) / 60;
		int secondsWhite = Math.max(0, (timeForEachPlayer - timeForWhite) % 60);
		int minutesBlack = (timeForEachPlayer - timeForBlack) / 60;
		int secondsBlack = Math.max(0, (timeForEachPlayer - timeForBlack) % 60);

		// Format the time strings for display in "MM:SS" format
		String secondsWhiteAsString = secondsWhite < 10 ? "0" + secondsWhite : String.valueOf(secondsWhite);
		String secondsBlackAsString = secondsBlack < 10 ? "0" + secondsBlack : String.valueOf(secondsBlack);
		String whiteTimeString = minutesWhite + ":" + secondsWhiteAsString;
		String blackTimeString = minutesBlack + ":" + secondsBlackAsString;

		// Add attributes to the model (this must be done last)
		helper.addModelAttributes(color, whiteTimeString, blackTimeString, model);

		((List<DisplayedField>) get(KEY.FIELDS)).clear();
		helper.createNewFields();
		helper.createNewPiecesFromExistingPieces((Game) get(KEY.CHESSGAME));
		webSocketService.updateClocks();
		webSocketService.updateMoveList();

		return "mainView";
	}

	/**
	 * Reloads the game state from a local save file, resetting and setting up the
	 * game.
	 *
	 * @throws Exception if reloading fails
	 */
	protected void reloadGame() throws Exception {
		helper.saveGame("local.txt", getChessGame());
		setup();
		loadGame("local.txt");
	}

	/**
	 * Resets the chessboard to its initial state, reconfiguring the board and UI
	 * elements.
	 *
	 * @return A redirect URL to the main view with the reset board.
	 * @throws Exception if reset fails
	 */
	@Override
	protected String reset() throws Exception {
		put(KEY.ENGINE_MATCH, false);
		createNewGame();
		setup();
		this.helper.setUnsetViewVariables(this.getEvaluationEngine());
		helper.createNewPiecesFromExistingPieces((Game) get(KEY.CHESSGAME));
		return "redirect:/?reset=true";
	}

	/**
	 * Starts a new game with the specified configuration from the client.
	 *
	 * @param params The configuration parameters for the new game.
	 * @return A redirect URL indicating the board reset status.
	 * @throws Exception if game start fails
	 */
	@PostMapping("/reset-board")
	@ResponseBody
	protected String startNewGame(@RequestBody Map<String, Object> params) throws Exception {

		Game chessGame = (Game) get(KEY.CHESSGAME);
		put(KEY.ENGINE_MATCH, false);

		int timeForEachPlayer = Integer.parseInt((String) params.get("timeForEachPlayer"));
		int incrementForWhite = Integer.parseInt((String) params.get("incrementForWhite"));
		int incrementForBlack = Integer.parseInt((String) params.get("incrementForBlack"));
		int additionalTime = Integer.parseInt((String) params.get("additionalTime"));

		viewConfig.setTimeForEachPlayer(timeForEachPlayer);
		viewConfig.setIncrementForWhite(incrementForWhite);
		chessGame.setIncrementForWhite(incrementForWhite);

		viewConfig.setIncrementForBlack(incrementForBlack);
		chessGame.setIncrementForBlack(incrementForBlack);
		viewConfig.setAdditionalTime(additionalTime);

		chessGame.getWhitePlayer().setAdditionalTime(additionalTime);
		chessGame.getBlackPlayer().setAdditionalTime(additionalTime);

		boolean isFlipped = ((String) params.get("startingColor")).equals("BLACK") ? true : false;
		viewConfig.setIsFlipped(isFlipped);
		if (chessGame != null) {
			if (!chessGame.getWhitePlayer().getChessClock().isStopped()) {
				chessGame.getWhitePlayer().getChessClock().stop();
			}
			if (!chessGame.getBlackPlayer().getChessClock().isStopped()) {
				chessGame.getBlackPlayer().getChessClock().stop();
			}
		}
		createNewGame();
		chessGame = (Game) get(KEY.CHESSGAME);
		chessGame.setIncrementForWhite(incrementForWhite * 1000);
		viewConfig.setIncrementForWhite(incrementForWhite);
		chessGame.getWhitePlayer().getChessClock().setIncrementMillis(incrementForWhite * 1000l);
		chessGame.setIncrementForBlack(incrementForBlack * 1000);
		viewConfig.setIncrementForBlack(incrementForBlack);
		chessGame.getBlackPlayer().getChessClock().setIncrementMillis(incrementForBlack * 1000l);
		chessGame.getWhitePlayer().setAdditionalTime(additionalTime);
		setup();
		this.helper.setUnsetViewVariables(this.getEvaluationEngine());

		helper.createNewPiecesFromExistingPieces(chessGame);

		webSocketService.sendReloadSignal();
		if (isFlipped) {
			Thread.sleep(200l);
			webSocketService.triggerUciEngineMove();
		}
		return "redirect:/?reset=true";
	}

	/**
	 * Starts an engine match, where both players are controlled by chess engines.
	 * Initializes the clocks and settings for both engines and triggers the first
	 * move by the UCI engine.
	 *
	 * @throws Exception if starting the engine match fails
	 */
	@PostMapping("/startEngineMatch")
	@ResponseBody
	protected void startEngineGame() throws Exception {
		put(KEY.ENGINE_MATCH, true);
		Game chessGame = getChessGame();
		chessGame.getWhitePlayer().getChessClock().setIncrementMillis(viewConfig.getIncrementForWhite() * 1000l);
		chessGame.getBlackPlayer().getChessClock().setIncrementMillis(viewConfig.getIncrementForBlack() * 1000l);
		put(KEY.CHESSGAME, chessGame);
		setup();
		this.helper.setUnsetViewVariables(this.getEvaluationEngine());
		helper.createNewPiecesFromExistingPieces(chessGame);
		String blackPlayer = get(KEY.PLAYER_ENGINE_FOR_BLACK).toString();
		String whitePlayer = get(KEY.PLAYER_ENGINE_FOR_WHITE).toString();
		webSocketService.sendMessage(whitePlayer + "  vs. " + blackPlayer );
		Thread.sleep(200l);
		webSocketService.triggerUciEngineMove();
	}

	/**
	 * Shuts down the application by exiting the runtime.
	 *
	 * @throws Exception if shutdown fails
	 */
	@PostMapping("/shutDown")
	@ResponseBody
	protected void shutDown() throws Exception {
		Runtime.getRuntime().exit(0);
	}

	/**
	 * Allows the current player to resign the game, setting the game state to
	 * indicate resignation and stopping the player's clock.
	 *
	 * @return A redirect to the main view after the resignation.
	 * @throws Exception if resignation processing fails
	 */
	@GetMapping("/resign")
	protected String resign() throws Exception {
		put(KEY.ENGINE_MATCH, false);
		Game chessGame = this.getChessGame();
		if (!chessGame.getWhitePlayer().getChessClock().isStopped()) {
			chessGame.getWhitePlayer().getChessClock().stop();
		}
		if (!chessGame.getBlackPlayer().getChessClock().isStopped()) {
			chessGame.getBlackPlayer().getChessClock().stop();
		}
		if (viewConfig.getIsFlipped()) {
			chessGame.setState(State.BLACK_RESIGNED);
		} else {
			chessGame.setState(State.WHITE_RESIGNED);
		}
		return "redirect:/";
	}

	/**
	 * Allows the current player to resign the game, setting the game state to
	 * indicate resignation and stopping the player's clock.
	 *
	 * @return A redirect to the main view after the resignation.
	 * @throws ExecutionException if sth strange happens
	 * @throws InterruptedException  when trying to read engine output
	 * @throws IOException when trying to read engine output
	 * @throws NoMoveFoundException if no move is found for some reason
	 * @throws Exception if resignation processing fails
	 */
	@GetMapping("/startGameAnalysis")
	protected String startGameAnalysis() throws IOException, InterruptedException, ExecutionException, NoMoveFoundException {
		MoveList moveList = getChessGame().getMoveList();
		long time = 1000l;
		EvaluationEngine engine = (EvaluationEngine) get(KEY.EVALUATION_ENGINE);
		engine.stopEvaluation();
		engine.clearChachedLines();
		EngineConfig config = (EngineConfig) get(KEY.ENGINE_CONFIG_EVAL);
		Game tmpGame = admin.dummyGame();
		for (Move move : moveList) {
			this.webSocketService.sendMessage("Analizing move " + move.toString());
			engine.getBestLines(tmpGame, config);
			Thread.sleep(time);
			List<Move> moves = tmpGame.getPlayer().getValidMoves(tmpGame);
			Move simMove = null;
			for (Move exchangeMove : moves) {
				if(exchangeMove.toString().equals(move.toString())) {
					simMove = exchangeMove;
					continue;
				}
			}
			if (simMove == null) {
				throw new NoMoveFoundException(move.toString());
			}
			if (engine.getCachedBestLines().get(tmpGame.getMoveList().toString()).size() < 5) {
				this.webSocketService.sendMessage("Analizing move " + move.toString());
				Thread.sleep(time);
			}
			String key = tmpGame.getMoveList().toString();
			tmpGame.apply(simMove);
			engine.stopEvaluation();
			logger.info("Move {} - {} evaluated lines", move, engine.getCachedBestLines().get(key).size());
		}
		return "redirect:/";
	}
	/**
	 * Handles the GET request for the settings page. Populates the model with
	 * configuration options.
	 *
	 * @param model The model used to pass data to the view.
	 * @return The name of the settings view.
	 */
	@GetMapping("/settings")
	protected String settings(Model model) {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		suspendIfNeeded(chessGame);

		model.addAttribute("viewConfig", viewConfig);
		model.addAttribute("evaluationEngines", evaluationEngines);
		return "settings";
	}

	/**
	 * Suspends the clocks of both players if they are currently running. This
	 * method is used during settings or other interruptions.
	 *
	 * @param chessGame The current game instance.
	 */
	private void suspendIfNeeded(Game chessGame) {
		if (chessGame.getWhitePlayer().getChessClock().isRunning()) {
			chessGame.getWhitePlayer().getChessClock().suspend();
		}
		if (chessGame.getBlackPlayer().getChessClock().isRunning()) {
			chessGame.getBlackPlayer().getChessClock().suspend();
		}
	}

	/**
	 * Handles the GET request for the UCI engine settings page, displaying
	 * available player engines and other configurations.
	 *
	 * @param model The model used to pass data to the view.
	 * @return The name of the UCI engine settings view.
	 */
	@GetMapping("/uciEngine-settings")
	protected String uciEngineSettings(Model model) {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		suspendIfNeeded(chessGame);
		model.addAttribute("playerEngines", playerEngines);
		model.addAttribute("viewConfig", viewConfig);
		return "uciEngine-settings";
	}

	/**
	 * Handles the GET request for presentation settings, such as color themes.
	 * Populates the model with available color options.
	 *
	 * @param model The model used to pass data to the view.
	 * @return The name of the presentation settings view.
	 */
	@GetMapping("/presentation-settings")
	protected String presentationSettings(Model model) {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		suspendIfNeeded(chessGame);
		List<String> colorList = Arrays.asList("GREEN", "BROWN", "RED", "BLUE", "YELLOW");
		model.addAttribute("colorList", colorList);
		model.addAttribute("viewConfig", viewConfig);
		return "presentation-settings";
	}

	/**
	 * Updates various settings related to the evaluation and analysis capabilities
	 * of the chess UI, particularly configurations for the UCI engine.
	 * <p>
	 * This method allows configuring settings such as whether to show arrows for
	 * moves, enable evaluation displays, and set up the UCI engine's depth and
	 * multi-variation analysis.
	 * </p>
	 *
	 * @param showArrows                        Flag indicating if arrows should be
	 *                                          displayed on the board to show
	 *                                          suggested moves or analysis results.
	 * @param showEvaluation                    Flag indicating if the evaluation
	 *                                          bar should be displayed, providing
	 *                                          insight into the current position
	 *                                          strength.
	 * @param showUciEngineLines                Flag indicating if UCI engine lines
	 *                                          (move suggestions) should be shown
	 *                                          in the UI.
	 * @param uciEngineActive                   Flag to enable or disable the UCI
	 *                                          engine for move suggestions and
	 *                                          analysis.
	 * @param updateIntervall                   Interval for updates, controlling
	 *                                          how frequently the engine analysis
	 *                                          is updated.
	 * @param multiPVForEvaluationEngine        Number of variations to display from
	 *                                          the UCI engine’s analysis
	 *                                          (multi-principal variation).
	 * @param uciEngineDepthForEvaluationEngine Maximum depth for the UCI engine's
	 *                                          analysis, defining the calculation
	 *                                          depth.
	 * @param selectedEngine                    The engine selected for evaluation,
	 *                                          which will be set as the active
	 *                                          engine for analysis.
	 * @return A redirect to the main view after applying the updated settings.
	 * @throws Exception If an error occurs while updating the engine
	 *                   configurations.
	 */

	@PostMapping("/updateSettings")
	protected String updateSettings(@RequestParam(defaultValue = "false") boolean showArrows,
			@RequestParam(defaultValue = "false") boolean showEvaluation,
			@RequestParam(defaultValue = "false") boolean showUciEngineLines,
			@RequestParam(defaultValue = "false") boolean uciEngineActive, @RequestParam int updateIntervall,
			@RequestParam int multiPVForEvaluationEngine, @RequestParam int uciEngineDepthForEvaluationEngine,
			@RequestParam(required=false) String selectedEngine) throws Exception {

		EvaluationEngine selected = evaluationEngines.get(selectedEngine);
		put(KEY.EVALUATION_ENGINE, selected);
		viewConfig.setEvaluationEngine(selectedEngine);
		viewConfig.setUpdateIntervall(updateIntervall);

		viewConfig.setShowArrows(showArrows);
		viewConfig.setShowEvaluation(showEvaluation);
		viewConfig.setShowUciEngineLines(showUciEngineLines);

		/////////////////////////////////////////
		viewConfig.setUciEngineDepthForEvaluationEngine(uciEngineDepthForEvaluationEngine);
		((EngineConfig) get(KEY.ENGINE_CONFIG_EVAL)).setDepth(uciEngineDepthForEvaluationEngine);

		viewConfig.setMultiPVForEvaluationEngine(multiPVForEvaluationEngine);
		((EngineConfig) get(KEY.ENGINE_CONFIG_EVAL)).setMultiPV(multiPVForEvaluationEngine);

		viewConfig.setUciEngineActive(uciEngineActive);

		return "redirect:/";
	}

	/**
	 * Handles POST requests to update settings for the UCI (Universal Chess
	 * Interface) engine for both players.
	 * <p>
	 * This method accepts configuration parameters for the UCI engine specific to
	 * the white and black players, including engine depth, thread count, hash size,
	 * contempt factor, move overhead, Elo rating, and the selected engine for each
	 * player. These parameters are passed to the helper to update the engine
	 * settings accordingly.
	 * </p>
	 *
	 * @param uciEngineDepthForWhite Depth of analysis for the white player’s UCI
	 *                               engine.
	 * @param threadsForWhite        Number of threads allocated to the white
	 *                               player’s engine.
	 * @param hashSizeForWhite       Hash size (in MB) for the white player’s engine
	 *                               to use for caching.
	 * @param contemptForWhite       Contempt factor for the white player’s engine,
	 *                               influencing engine bias.
	 * @param moveOverheadForWhite   Time overhead in milliseconds allocated to each
	 *                               move for the white engine.
	 * @param uciEloForWhite         Elo rating to be used as a strength setting for
	 *                               the white player’s engine.
	 * @param uciEngineDepthForBlack Depth of analysis for the black player’s UCI
	 *                               engine.
	 * @param threadsForBlack        Number of threads allocated to the black
	 *                               player’s engine.
	 * @param hashSizeForBlack       Hash size (in MB) for the black player’s engine
	 *                               to use for caching.
	 * @param contemptForBlack       Contempt factor for the black player’s engine,
	 *                               influencing engine bias.
	 * @param moveOverheadForBlack   Time overhead in milliseconds allocated to each
	 *                               move for the black engine.
	 * @param uciEloForBlack         Elo rating to be used as a strength setting for
	 *                               the black player’s engine.
	 * @param selectedEngineForWhite The engine selected for the white player.
	 * @param selectedEngineForBlack The engine selected for the black player.
	 * @return A redirect to the main view after the UCI engine settings have been
	 *         updated.
	 * @throws Exception if updating the UCI engine settings fails.
	 */
	@PostMapping("/uciEngine-settings")
	protected String updateUciEngineSettings(@RequestParam int uciEngineDepthForWhite,
			@RequestParam int threadsForWhite, @RequestParam int hashSizeForWhite, @RequestParam int contemptForWhite,
			@RequestParam int moveOverheadForWhite, @RequestParam int uciEloForWhite,
			@RequestParam int uciEngineDepthForBlack, @RequestParam int threadsForBlack,
			@RequestParam int hashSizeForBlack, @RequestParam int contemptForBlack,
			@RequestParam int moveOverheadForBlack, @RequestParam int uciEloForBlack,
			@RequestParam(required=false) String selectedEngineForWhite, @RequestParam(required=false) String selectedEngineForBlack) throws Exception {

		helper.updateUciEngineSettings(uciEngineDepthForWhite, threadsForWhite, hashSizeForWhite, contemptForWhite,
				moveOverheadForWhite, uciEloForWhite, uciEngineDepthForBlack, threadsForBlack, hashSizeForBlack,
				contemptForBlack, moveOverheadForBlack, uciEloForBlack, selectedEngineForWhite, selectedEngineForBlack,
				this.playerEngines);
		return "redirect:/";
	}

	/**
	 * Updates the presentation settings for the chessboard view.
	 * <p>
	 * This method allows the client to configure settings related to the visual
	 * appearance and animation of the chessboard, such as color, offset, and square
	 * size.
	 * </p>
	 *
	 * @param color                  The selected color theme for the chessboard.
	 *                               The method will set this based on available
	 *                               color values, defaulting to GREEN if the input
	 *                               is invalid.
	 * @param silent                 Whether the UI should operate in silent mode.
	 *                               If true, audio or notifications are suppressed.
	 * @param shortAlgebraicNotation Determines if short algebraic notation should
	 *                               be used in displaying moves.
	 * @param animationDuration      The duration of animations (e.g., for piece
	 *                               movement) in milliseconds.
	 * @param leftOffset             The left offset of the chessboard, in pixels.
	 *                               This configures horizontal positioning.
	 * @param squareSize             The size of each square on the chessboard, in
	 *                               pixels.
	 * @return A redirect to the main view with the updated presentation settings.
	 */
	@PostMapping("/presentationSettings")
	protected String updatePresentationSettings(@RequestParam String color,
			@RequestParam(defaultValue = "false") boolean silent,
			@RequestParam(defaultValue = "false") boolean shortAlgebraicNotation, @RequestParam int animationDuration,
			@RequestParam int leftOffset, @RequestParam int squareSize) {

		viewConfig.setLeftOffset(leftOffset);
		viewConfig.setSquareSize(squareSize);
		viewConfig.setSilent(silent);
		viewConfig.setShortAlgebraicNotation(shortAlgebraicNotation);
		viewConfig.setColor(Arrays.stream(Color.values()).filter(enumValue -> enumValue.name().equals(color))
				.findFirst().orElse(Color.GREEN));
		viewConfig.setAnimationDuration(animationDuration);

		return "redirect:/";
	}

	/**
	 * Provides a logger instance specific to this controller.
	 *
	 * @return the logger for this controller
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
