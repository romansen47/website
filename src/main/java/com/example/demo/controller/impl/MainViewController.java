package com.example.demo.controller.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import com.example.demo.model.DisplayedField;
import com.example.demo.model.DisplayedPiece;
import com.example.demo.model.impl.DisplayedChessPiece;

import demo.chess.definitions.Color;
import demo.chess.definitions.pieces.Piece;
import demo.chess.definitions.states.State;
import demo.chess.game.Game;
import demo.chess.save.GameSaver;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This class is the controller responsible for managing the main view of the
 * chess application. It handles user input, updates the model, and returns the
 * appropriate view to be rendered.
 */
@Controller
public class MainViewController extends ControllerTemplate {

	protected static final Logger logger = LogManager.getLogger();

	@Autowired
	private ViewControllerHelper helper;

	@PostConstruct
	public void init() throws Exception {
		setup();
	}
	
	/**
	 * Initializes the chess game and sets up the displayable elements for the
	 * initial board configuration. This method is called after the bean has been
	 * constructed.
	 *
	 * @throws Exception
	 */
	@Override
	public void setup() throws Exception {

		boolean startUp = false;
		Game tmpChessGame = getChessGame();
		if (tmpChessGame == null) {
			tmpChessGame = admin.chessGame(viewConfig.getTimeForEachPlayer());
			put("chessGame", tmpChessGame);
			startUp = true;
		}
		final Game chessGame = tmpChessGame;

		put("stockfishDepthForEvaluation", 0.5);
		put("regular", !viewConfig.getIsFlipped());
		put("silent", false);
		put("remainingTimeForWhite", chessGame.getWhitePlayer().getChessClock().getTime(TimeUnit.MILLISECONDS));
		put("remainingTimeForBlack", chessGame.getBlackPlayer().getChessClock().getTime(TimeUnit.MILLISECONDS));

		chessGame.getWhitePlayer().setupClock(viewConfig.getTimeForEachPlayer(), viewConfig.getIncrementForWhite(),
				() -> {	chessGame.setState(State.LOST_ON_TIME);
						if (!chessGame.getWhitePlayer().getChessClock().isStopped()) {
							chessGame.getWhitePlayer().getChessClock().stop(); 
						}
						if (!chessGame.getBlackPlayer().getChessClock().isStopped()) {
							chessGame.getBlackPlayer().getChessClock().stop(); 
						}
						webSocketService.sendMessage("White lost on time!");
				});

		chessGame.getBlackPlayer().setupClock(viewConfig.getTimeForEachPlayer(), viewConfig.getIncrementForBlack(),
				() -> { chessGame.setState(State.LOST_ON_TIME);
						if (!chessGame.getWhitePlayer().getChessClock().isStopped()) {
							chessGame.getWhitePlayer().getChessClock().stop(); 
						}
						if (!chessGame.getBlackPlayer().getChessClock().isStopped()) {
							chessGame.getBlackPlayer().getChessClock().stop(); 
						}
						webSocketService.sendMessage("Black lost on time!");
				});

		helper.setUnsetViewVariables();

		((List<DisplayedPiece>) get("elements")).clear();
		((List<DisplayedField>) get("fields")).clear();

		helper.createNewFields();
		if (startUp) {
			createNewPiecesFromExistingPieces();
		}

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
	protected String mainView(Model model, HttpServletResponse response, boolean update, boolean resigned) throws Exception {
		
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
		helper.addAttributes(color, whiteTimeString, blackTimeString, model);
 
		((List<DisplayedField>) get("fields")).clear();
		helper.createNewFields();
		createNewPiecesFromExistingPieces();
		webSocketService.updateClocks();

		if (resigned) {
			this.webSocketService.sendMessage(getChessGame().getState().label);
		}
		return "mainView";
	}

	protected void createNewPiecesFromExistingPieces() {
		((List<DisplayedPiece>) get("elements")).clear();
		List<DisplayedPiece> newElements = new ArrayList<>();
		if ((boolean) get("regular")) {
			for (Piece piece : getChessGame().getWhitePlayer().getPieces()) {
				newElements.add(new DisplayedChessPiece(getImagePath(piece.getColor(), piece.getType()),
						viewConfig.getSquareSize(), viewConfig.getSquareSize(),
						(8 - piece.getField().getRank()) * viewConfig.getSquareSize(),
						(piece.getField().getFile() - 1) * viewConfig.getSquareSize() + viewConfig.getLeftOffset(),
						piece));
			}
			for (Piece piece : getChessGame().getBlackPlayer().getPieces()) {
				newElements.add(new DisplayedChessPiece(getImagePath(piece.getColor(), piece.getType()),
						viewConfig.getSquareSize(), viewConfig.getSquareSize(),
						(8 - piece.getField().getRank()) * viewConfig.getSquareSize(),
						(piece.getField().getFile() - 1) * viewConfig.getSquareSize() + viewConfig.getLeftOffset(),
						piece));
			}
		} else {
			for (Piece piece : getChessGame().getWhitePlayer().getPieces()) {
				newElements.add(new DisplayedChessPiece(getImagePath(piece.getColor(), piece.getType()),
						viewConfig.getSquareSize(), viewConfig.getSquareSize(),
						(piece.getField().getRank() - 1) * viewConfig.getSquareSize(),
						(8 - piece.getField().getFile()) * viewConfig.getSquareSize() + viewConfig.getLeftOffset(),
						piece));
			}
			for (Piece piece : getChessGame().getBlackPlayer().getPieces()) {
				newElements.add(new DisplayedChessPiece(getImagePath(piece.getColor(), piece.getType()),
						viewConfig.getSquareSize(), viewConfig.getSquareSize(),
						(piece.getField().getRank() - 1) * viewConfig.getSquareSize(),
						(8 - piece.getField().getFile()) * viewConfig.getSquareSize() + viewConfig.getLeftOffset(),
						piece));
			}
		}
		((List<DisplayedPiece>) get("elements")).addAll(newElements);
	}

	protected void reloadGame() throws Exception {
		saveGame("local.txt");
		setup();
		loadGame("local.txt");
	}

	protected void saveGame(String path) throws IOException {
		GameSaver saver = new GameSaver();
		saver.saveGame(getChessGame().getMoveList(), path);
	}

	/**
	 * Handles POST requests to reset the chessboard to its initial state.
	 *
	 * @return A message indicating that the chessboard has been reset.
	 * @throws Exception If any error occurs during the reset.
	 */
	@Override 
	protected String reset() throws Exception {
		put("engineMatch", false);
		Game chessGame = (Game) get("chessGame");
		chessGame.getWhitePlayer().getChessClock().stop();
		chessGame.getBlackPlayer().getChessClock().stop();
		put("chessGame", admin.chessGame(viewConfig.getTimeForEachPlayer()));
		setup();
		this.helper.setUnsetViewVariables();
		createNewPiecesFromExistingPieces();
		return "redirect:/?reset=true";
	}
	
	/**
	 * Handles POST requests to reset the chessboard to its initial state.
	 *
	 * @return A message indicating that the chessboard has been reset.
	 * @throws Exception If any error occurs during the reset.
	 */ 
	@PostMapping("/reset-board")
	@ResponseBody
	protected String startNewGame(@RequestBody Map<String, Object> params) throws Exception {

		put("engineMatch", false);
		int timeForEachPlayer = Integer.parseInt((String) params.get("timeForEachPlayer"));
	    int incrementForWhite = Integer.parseInt((String) params.get("incrementForWhite"));
	    int incrementForBlack = Integer.parseInt((String) params.get("incrementForBlack"));
	    
	    viewConfig.setTimeForEachPlayer(timeForEachPlayer);
	    viewConfig.setIncrementForWhite(incrementForWhite);
	    viewConfig.setIncrementForBlack(incrementForBlack);

	    boolean isFlipped = ((String) params.get("startingColor")).equals("BLACK") ? true : false;
	    viewConfig.setIsFlipped(isFlipped);
	    Game chessGame = (Game) get("chessGame");
	    if (chessGame != null) { 
			if (!chessGame.getWhitePlayer().getChessClock().isStopped()) {
		    	chessGame.getWhitePlayer().getChessClock().stop(); 
			} 
			if (!chessGame.getBlackPlayer().getChessClock().isStopped()) {
		    	chessGame.getBlackPlayer().getChessClock().stop(); 
			}
	    }
	    chessGame = admin.chessGame(viewConfig.getTimeForEachPlayer());
		viewConfig.setIncrementForWhite(incrementForWhite);
		chessGame.getWhitePlayer().getChessClock().setIncrementMillis(incrementForWhite * 1000l);
		viewConfig.setIncrementForBlack(incrementForBlack);
		chessGame.getBlackPlayer().getChessClock().setIncrementMillis(incrementForBlack * 1000l);
		viewConfig.setTimeForEachPlayer(timeForEachPlayer);
		put("chessGame", chessGame);
		setup();
		this.helper.setUnsetViewVariables();

		createNewPiecesFromExistingPieces();
		
		webSocketService.sendReloadSignal();
	    if (isFlipped) {
	    	Thread.sleep(200l);
			webSocketService.triggerStockfishMove();
	    }
		return "redirect:/?reset=true";
	}
	
	@PostMapping("/startEngineMatch")
	@ResponseBody
	protected void startEngineGame() throws Exception {
		put("engineMatch", true);
		Game chessGame = getChessGame();//admin.chessGame(viewConfig.getTimeForEachPlayer());
		chessGame.getWhitePlayer().getChessClock().setIncrementMillis(viewConfig.getIncrementForWhite() * 1000l);
		chessGame.getBlackPlayer().getChessClock().setIncrementMillis(viewConfig.getIncrementForBlack() * 1000l);
		put("chessGame", chessGame);
		setup();
		this.helper.setUnsetViewVariables();
		createNewPiecesFromExistingPieces();
		webSocketService.sendReloadSignal();
		Thread.sleep(200l);
		webSocketService.triggerStockfishMove();
	}

	@GetMapping("/resign")
	protected String resign() {
		put("engineMatch", false);
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
		return "redirect:/?resigned=true";
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
		model.addAttribute("viewConfig", viewConfig);
		return "settings";
	}
	 
	@GetMapping("/stockfish-settings")
	protected String stockfishSettings(Model model) { 
		model.addAttribute("viewConfig", viewConfig);
		return "stockfish-settings";
	} 
	 
	@GetMapping("/presentation-settings")
	protected String presentationSettings(Model model) { 
		List<String> colorList = Arrays.asList("GREEN", "BROWN", "RED", "BLUE", "YELLOW");
		model.addAttribute("colorList", colorList);
		model.addAttribute("viewConfig", viewConfig); 
		return "presentation-settings";
	}
 

	/**
	 * Handles the POST request to update the settings. Applies the new settings and
	 * reloads the game if necessary.
	 *
	 * @param stockfishActive   The new value for the Stockfish activation setting.
	 * @param color             The selected color theme.
	 * @param timeForEachPlayer The new time allocated for each player.
	 * @param leftOffset        The new left offset for the chessboard.
	 * @param squareSize        The new size for each square on the chessboard.
	 * @param stockfishDepth    The new depth for Stockfish analysis.
	 * @return A redirect to the main view with updated settings.
	 * @throws Exception If any error occurs during the update.
	 */
	@PostMapping("/updateSettings")
	protected String updateSettings(@RequestParam(
			defaultValue = "false") boolean showArrows,
			@RequestParam(defaultValue = "false") boolean showEvaluation,
			@RequestParam(defaultValue = "false") boolean showStockfishLines,
			@RequestParam(defaultValue = "false") boolean stockfishActive,
			@RequestParam int updateIntervall,
			@RequestParam int multiPVForEvaluationEngine,
			@RequestParam int stockfishDepthForEvaluationEngine) throws Exception {
		
		Game chessGame = getChessGame();
		viewConfig.setUpdateIntervall(updateIntervall); 
		
		viewConfig.setShowArrows(showArrows);
		viewConfig.setShowEvaluation(showEvaluation);
		viewConfig.setShowStockfishLines(showStockfishLines);

		/////////////////////////////////////////
		viewConfig.setStockfishDepthForEvaluationEngine(stockfishDepthForEvaluationEngine);
		evaluationEngine.setDepth(stockfishDepthForEvaluationEngine);

		viewConfig.setMultiPVForEvaluationEngine(multiPVForEvaluationEngine);
		evaluationEngine.setMultiPV(multiPVForEvaluationEngine);

		viewConfig.setStockfishActive(stockfishActive);

		return "redirect:/?update=true";
	}
	 
	
	/**
	 * Handles the POST request to update the settings. Applies the new settings and
	 * reloads the game if necessary.
	 *
	 * @param stockfishActive   The new value for the Stockfish activation setting.
	 * @param color             The selected color theme.
	 * @param timeForEachPlayer The new time allocated for each player.
	 * @param leftOffset        The new left offset for the chessboard.
	 * @param squareSize        The new size for each square on the chessboard.
	 * @param stockfishDepth    The new depth for Stockfish analysis.
	 * @return A redirect to the main view with updated settings.
	 * @throws Exception If any error occurs during the update.
	 */
	@PostMapping("/stockfishSettings")
	protected String updateStockfishSettings(
			@RequestParam int stockfishDepthForWhite,
			@RequestParam int threadsForWhite,
			@RequestParam int hashSizeForWhite, 
			@RequestParam int contemptForWhite,
			@RequestParam int moveOverheadForWhite, 
			@RequestParam int uciEloForWhite, 
			@RequestParam int stockfishDepthForBlack, 
			@RequestParam int threadsForBlack,
			@RequestParam int hashSizeForBlack, 
			@RequestParam int contemptForBlack,
			@RequestParam int moveOverheadForBlack, 
			@RequestParam int uciEloForBlack) throws Exception {


		viewConfig.setThreadsForWhite(threadsForWhite);
		playerEngineForWhite.setThreads(threadsForBlack);
		
		viewConfig.setHashSizeForWhite(hashSizeForWhite);
		playerEngineForWhite.setHashSize(hashSizeForWhite);

		viewConfig.setContemptForWhite(contemptForWhite);
		playerEngineForWhite.setContempt(contemptForWhite); 

		viewConfig.setUciEloForWhite(uciEloForWhite);
		playerEngineForWhite.setUciElo(uciEloForWhite);

		viewConfig.setMoveOverheadForWhite(moveOverheadForWhite);
		playerEngineForWhite.setMoveOverhead(moveOverheadForWhite);

		viewConfig.setStockfishDepthForWhite(stockfishDepthForWhite);
		playerEngineForWhite.setDepth(stockfishDepthForWhite);

		viewConfig.setThreadsForBlack(threadsForBlack);
		playerEngineForBlack.setThreads(threadsForBlack);

		
		viewConfig.setHashSizeForBlack(hashSizeForBlack);
		playerEngineForBlack.setHashSize(hashSizeForBlack);


		viewConfig.setContemptForBlack(contemptForBlack);
		playerEngineForBlack.setContempt(contemptForBlack);


		viewConfig.setMoveOverheadForBlack(moveOverheadForBlack);
		playerEngineForBlack.setMoveOverhead(moveOverheadForBlack);

		viewConfig.setUciEloForBlack(uciEloForBlack);
		playerEngineForBlack.setUciElo(uciEloForBlack);
		
		viewConfig.setStockfishDepthForBlack(stockfishDepthForBlack);
		playerEngineForBlack.setDepth(stockfishDepthForBlack);
		return "redirect:/?update=true";
	}
	
	/**
	 * Handles the POST request to update the settings. Applies the new settings and
	 * reloads the game if necessary.
	 *
	 * @param stockfishActive   The new value for the Stockfish activation setting.
	 * @param color             The selected color theme.
	 * @param timeForEachPlayer The new time allocated for each player.
	 * @param leftOffset        The new left offset for the chessboard.
	 * @param squareSize        The new size for each square on the chessboard.
	 * @param stockfishDepth    The new depth for Stockfish analysis.
	 * @return A redirect to the main view with updated settings.
	 * @throws Exception If any error occurs during the update.
	 */
	@PostMapping("/presentationSettings")
	protected String updatePresentationSettings(
			@RequestParam String color, 
			@RequestParam(defaultValue = "false") boolean capturedContainer,
			@RequestParam(defaultValue = "false") boolean silent, 
			@RequestParam int animationDuration, 
			@RequestParam int leftOffset,
			@RequestParam int squareSize ) {

		viewConfig.setLeftOffset(leftOffset); 
		viewConfig.setSquareSize(squareSize);
		viewConfig.setSilent(silent);
		
		viewConfig.setCapturedContainer(capturedContainer);
		
		
		viewConfig.setColor(Arrays.stream(Color.values()).filter(enumValue -> enumValue.name().equals(color))
				.findFirst().orElse(Color.GREEN));

		viewConfig.setAnimationDuration(animationDuration);
		
		return "redirect:/?update=true";
	}

}
