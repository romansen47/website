package com.example.demo.controller.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.controller.api.ChessApiResponse;
import com.example.demo.controller.helper.ApiControllerHelper;
import com.example.demo.controller.helper.impl.ChessHelper;
import com.example.demo.elements.KEY;
import com.example.demo.model.DisplayedField;
import com.example.demo.model.DisplayedPiece;

import demo.chess.definitions.Color;
import demo.chess.definitions.PieceType;
import demo.chess.definitions.board.Board;
import demo.chess.definitions.engines.EngineConfig;
import demo.chess.definitions.engines.PlayerEngine;
import demo.chess.definitions.engines.impl.EvaluationUciEngine;
import demo.chess.definitions.engines.impl.NoMoveFoundException;
import demo.chess.definitions.engines.impl.PlayerUciEngine;
import demo.chess.definitions.fields.Field;
import demo.chess.definitions.moves.Castling;
import demo.chess.definitions.moves.EnPassant;
import demo.chess.definitions.moves.Move;
import demo.chess.definitions.moves.Promotion;
import demo.chess.definitions.pieces.Piece;
import demo.chess.definitions.pieces.impl.Rook;
import demo.chess.game.Game;
import demo.chess.save.GameSaver;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The `ChessApiController` class provides RESTful endpoints for managing and
 * interacting with a chess game. It handles various game operations, such as
 * retrieving possible moves, responding to user interactions with pieces and
 * fields, and managing game state and engine interactions. Additionally, it
 * offers file operations for saving, loading, and downloading games.
 *
 * Key functionalities include: - **Move Validation**: Retrieves and validates
 * possible moves for selected pieces, allowing user interactions to highlight
 * valid moves and enforce rules like castling, en passant, and promotion. -
 * **Piece and Field Interaction**: Responds to piece and field clicks,
 * determining if moves are valid and applying them while updating the game
 * state accordingly. - **Engine Management**: Integrates UCI chess engines for
 * move suggestions and evaluations, updating clients with engine-provided moves
 * and handling special moves. - **Game State Persistence**: Enables saving and
 * loading of game states to/from files, allowing users to resume gameplay or
 * review previous games. - **Move List and Evaluation**: Provides UI support by
 * exposing move lists and evaluation data for client display, including the
 * generation of evaluation bars and best-move highlights. - **WebSocket
 * Communication**: Sends real-time updates to clients on clocks, move lists,
 * and game states, ensuring synchronization across user interfaces.
 *
 * This controller serves as a bridge between the frontend and backend, managing
 * interactions and game logic for a responsive and interactive chess
 * application, with extensive support for UCI engine evaluation and move
 * analysis.
 */
@RestController
@RequestMapping("/api/game")
public class ChessApiController extends ControllerTemplate {

	/**
	 * Logger instance for capturing and recording runtime information, errors, and
	 * diagnostic messages within the `ChessApiController`.
	 */
	protected static final Logger logger = LogManager.getLogger();

	/**
	 * Helper class that provides various utility methods for handling API
	 * operations, such as validating game state, managing game interactions, and
	 * processing requests.
	 */
	@Autowired
	private ApiControllerHelper helper;

	/**
	 * Stores the currently selected chess piece as a string representation,
	 * typically used to track the piece selected by the user for interaction on the
	 * chessboard.
	 */
	protected String selectedPiece;

	/**
	 * Represents the field on the chessboard that is currently selected by the
	 * user. Used to determine valid moves and manage board interactions for the
	 * selected piece.
	 */
	protected Field selectedField = null;

	/**
	 * Holds a list of valid promotion moves, enabling the application to provide
	 * promotion options when a pawn reaches the last rank.
	 */
	List<Promotion> validPromotions = new ArrayList<>();

	@Override
	@PostConstruct
	public void setup() throws Exception {
		super.setup();
		put(KEY.ENGINE_MATCH, false);
		put(KEY.REGULAR, true);
		put(KEY.UCI_ENGINE_EVALUATION, 0.5d);
		put(KEY.UCI_ENGINE_MOVELIST, new ArrayList<>());
	}

	/**
	 * Retrieves the possible moves for a piece on the clicked field. This method
	 * processes POST requests to get all valid target fields where the piece can
	 * move.
	 *
	 * @param field The chess board field where the piece is located.
	 * @return A `ChessApiResponse` containing a list of possible target fields.
	 * @throws Exception If there are issues accessing the possible moves.
	 */
	@PostMapping("/getPossibleMoves")
	@ResponseBody
	@SuppressWarnings("unchecked")
	protected ChessApiResponse<List<String>> getPossibleMoves(String field) throws Exception {
		List<DisplayedField> allfields = (List<DisplayedField>) get(KEY.FIELDS);
		Field clickedField = null;
		for (DisplayedField f : allfields) {
			if (f.getField().toString().equals(field)) {
				clickedField = f.getField();
			}
		}
		Piece piece = clickedField.getPiece();
		List<String> fields = new ArrayList<>();
		Game chessGame = (Game) get(KEY.CHESSGAME);
		for (Move move : chessGame.getPlayer().getValidMoves(chessGame)) {
			if (move.getSource().getPiece().equals(piece)) {
				fields.add(move.getTarget().getName());
			}
		}
		return new ChessApiResponse<>(true, fields);
	}

	/**
	 * Handles POST requests when a piece is clicked by the user. This method
	 * returns the list of valid target fields for the selected piece.
	 *
	 * @param id The ID of the clicked piece.
	 * @return A response entity containing a list of target fields as strings.
	 * @throws Exception if s.t. wrong happens
	 */
	@PostMapping("/onPieceClicked")
	@ResponseBody
	@SuppressWarnings("unchecked")
	protected ChessApiResponse<List<String>> onPieceClicked(@RequestParam int id) throws Exception {

		Game chessGame = (Game) get(KEY.CHESSGAME);

		List<DisplayedPiece> elements = (List<DisplayedPiece>) get(KEY.ELEMENTS);
		if (!helper.checkForGameState(chessGame, getEvaluationEngine())) {
			return new ChessApiResponse<>(true, new ArrayList<>());
		}
		if (!helper.isHumanAlowedToInteract(chessGame, viewConfig.isUciEngineActive())) {
			String engine = !viewConfig.getIsFlipped() ? viewConfig.getPlayerEngineForBlack()
					: viewConfig.getPlayerEngineForWhite();
			this.webSocketService.sendMessage("Engine " + engine + " is thinking!");
			return new ChessApiResponse<>(true, new ArrayList<>());
		}
		// Get the piece corresponding to the clicked element
		Piece selectedPiece = elements.get(id).getPiece();

		// Get the color of the current player
		Color currentPlayerColor = chessGame.getPlayer().getColor();
		// If no field is selected and the piece belongs to the current player
		if (selectedField == null && selectedPiece.getColor() == currentPlayerColor
				&& null != selectedPiece.getField()) {
			// If the selected piece has valid source fields
			if (helper.getSourceFields().stream().map(Field::getPiece).distinct().toList().contains(selectedPiece)) {
				selectedField = selectedPiece.getField();
				List<String> fields = new ArrayList<>();
				// Get all valid target fields for this piece
				for (Move move : chessGame.getPlayer().getValidMoves(chessGame)) {
					if (move.getSource().getPiece().equals(selectedPiece)) {
						fields.add(move.getTarget().getName());
					}
				}
				return new ChessApiResponse<>(true, fields);
			}
		} else if (selectedField != null) {
			// If a field is already selected, attempt to make the move
			Field targetField = selectedPiece.getField();
			String selectedFieldName = selectedField.getName();
			List<Move> moveList = chessGame.getPlayer().getValidMoves(chessGame);
			Move chessMove = null;
			// Find the move that matches the selected source and target fields
			for (Move move : moveList) {
				if (move.getSource().getName().equals(selectedFieldName) && move.getTarget().equals(targetField)) {
					chessMove = move;
				}
			}
			// If a valid move was found, apply it
			if (chessMove != null) {
				selectedField = null;
				if (chessMove instanceof Castling) {
					// Handle castling moves
					Castling castling = (Castling) chessMove;
					final Rook rook = castling.getRook();
					Field newKingField;
					Field newRookField;
					int rank = castling.getPiece().getColor().equals(Color.BLACK) ? 8 : 1;
					if (rook.getField().getFile() == 1) {
						newKingField = chessGame.getChessBoard().getField(3, rank);
						newRookField = chessGame.getChessBoard().getField(4, rank);
					} else {
						newKingField = chessGame.getChessBoard().getField(7, rank);
						newRookField = chessGame.getChessBoard().getField(6, rank);
					}
					List<String> answer = List.of("castling", castling.getPiece().getField().getName(),
							newKingField.getName(), rook.getField().getName(), newRookField.getName());
					applyMove(chessMove);
					return new ChessApiResponse<>(true, answer);
				} else if (chessMove instanceof Promotion) {
					helper.setValidPromotions(moveList, chessMove, this.validPromotions);
					return new ChessApiResponse<>(true, List.of("promotion-capture", targetField.getName(),
							targetField.getName(), selectedFieldName));
				} else if (selectedPiece.getColor() != currentPlayerColor) {
					// Handle capturing moves
					applyMove(chessMove);
					return new ChessApiResponse<>(true,
							List.of("capture", targetField.getName(), targetField.getName(), selectedFieldName));
				} else if (!(selectedPiece.getColor() == currentPlayerColor)) {
					List<String> answer = new ArrayList<>();
					answer.add("wrong-color");
					answer.add(selectedField.toString());
					selectedField = null;
					return new ChessApiResponse<>(true, answer);
				} else {
					// Handle regular moves
					applyMove(chessMove);
					return new ChessApiResponse<>(true, List.of(selectedFieldName, targetField.getName()));
				}

			} else {
				if (!(selectedPiece.getColor() == currentPlayerColor)) {
					List<String> answer = new ArrayList<>();
					answer.add("wrong-color");
					answer.add(selectedField.toString());
					selectedField = null;
					return new ChessApiResponse<>(true, answer);
				}
				if (selectedField.toString().equals(selectedPiece.getField().toString())) {
					List<String> answer = new ArrayList<>();
					answer.add("unhighlight-field");
					answer.add(selectedField.toString());
					selectedField = null;
					return new ChessApiResponse<>(true, answer);
				}
				if (helper.getSourceFields().stream().map(Field::getPiece).distinct().toList()
						.contains(selectedPiece)) {
					List<String> answer = new ArrayList<>();
					answer.add("unhighlight-old-highlight-new-field-and-mark-possible-moves");
					answer.add(selectedField.toString());
					answer.add(selectedPiece.getField().toString());
					selectedField = selectedPiece.getField();
					return new ChessApiResponse<>(true, answer);
				}
			}
		}
		selectedField = null;
		return new ChessApiResponse<>(true, new ArrayList<>());
	}

	/**
	 * Handles POST requests when a field on the board is clicked by the user. This
	 * method checks if a move can be made to the clicked field.
	 *
	 * @param id The ID of the clicked field.
	 * @return A response entity containing the result of the move attempt.
	 * @throws Exception If any error occurs during processing.
	 */
	@PostMapping("/onFieldClicked")
	@ResponseBody
	@SuppressWarnings("unchecked")
	protected ChessApiResponse<List<String>> onFieldClicked(@RequestParam int id) throws Exception {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		if (!helper.checkForGameState(chessGame, getEvaluationEngine())
				|| !helper.isHumanAlowedToInteract(chessGame, viewConfig.isUciEngineActive())) {
			return new ChessApiResponse<>(false, new ArrayList<>());
		}
		Field fieldClickedOn = ((List<DisplayedField>) get(KEY.FIELDS)).get(id).getField();
		List<String> answer = new ArrayList<>();
		if (selectedField == null) {
			return new ChessApiResponse<>(true, answer);
		}
		List<Field> possibleFields = helper.getTargetFields();
		if (selectedField == fieldClickedOn) {
			selectedField = null;
			return new ChessApiResponse<>(true, answer);
		}
		if (possibleFields.contains(fieldClickedOn)) {
			String selectedFieldName = selectedField.getName();
			List<Move> moveList = chessGame.getPlayer().getValidMoves(chessGame);
			Move chessMove = null;
			for (Move move : moveList) {
				if (move.getSource().getName().equals(selectedFieldName) && move.getTarget().equals(fieldClickedOn)) {
					chessMove = move;
				}
			}
			if (chessMove == null) {
				answer.add("unhighlight-field");
				answer.add(selectedField.toString());
				selectedField = null;
				return new ChessApiResponse<>(true, answer);
			}

			if (chessMove instanceof Promotion) {
				helper.setValidPromotions(moveList, chessMove, this.validPromotions);
				answer.add("promotion");
				answer.add(selectedFieldName);
				answer.add(fieldClickedOn.getName());
				selectedField = null;
				return new ChessApiResponse<>(true, answer);
			}

			applyMove(chessMove);

			if (chessMove instanceof EnPassant) {
				EnPassant enPassantMove = (EnPassant) chessMove;
				Field slayedPawnField = enPassantMove.getSlayedPiece().getField();
				String slayedPawnFieldName = slayedPawnField.getName();
				answer.add("en-passant");
				answer.add(selectedFieldName);
				answer.add(fieldClickedOn.getName());
				answer.add(slayedPawnFieldName);
				selectedField = null;
				return new ChessApiResponse<>(true, answer);
			}
			selectedField = null;
			answer.add(selectedFieldName);
			return new ChessApiResponse<>(true, answer);
		} else {
			selectedField = null;
			return new ChessApiResponse<>(true, answer);
		}
	}

	/**
	 * Selects a promotion piece based on user input and applies the promotion move.
	 * This method is typically invoked when a pawn reaches the promotion rank, and
	 * the user selects the piece for promotion (e.g., Queen).
	 *
	 * @param pieceSelection The selected piece for promotion.
	 * @param response       The HTTP response object.
	 * @return A `ChessApiResponse` containing the promotion details.
	 * @throws Exception If no valid promotion move is found or there is an error
	 *                   during promotion.
	 */
	@PostMapping("/selectPiece")
	@ResponseBody
	protected ChessApiResponse<List<String>> selectPiece(@RequestBody PieceSelection pieceSelection,
			HttpServletResponse response) throws Exception {
		selectedPiece = pieceSelection.getPiece();
		Promotion promotionTmp = null;
		for (Promotion move : validPromotions) {
			if (move.getPromotedPiece().getType().toString().toLowerCase().equals(selectedPiece)) {
				applyMove(move);
				promotionTmp = move;
			}
		}
		if (promotionTmp != null) {
			final Promotion finalProm = promotionTmp;
			@SuppressWarnings("unchecked")
			Optional<DisplayedPiece> element = ((List<DisplayedPiece>) get(KEY.ELEMENTS)).stream()
					.filter(el -> el.getPiece().equals(finalProm.getPiece())).findFirst();
			if (element.isPresent()) {
				Color color = promotionTmp.getPiece().getColor();
				PieceType type = promotionTmp.getPromotedPiece().getType();
				element.get().setPiece(promotionTmp.getPromotedPiece());
				String imagePath = ((ChessHelper) helper).getImagePath(color, promotionTmp.getPiece().getType());
				element.get().setImagePath(imagePath);
				List<String> answer = new ArrayList<>();
				answer.add(selectedPiece.toLowerCase());
				answer.add(promotionTmp.getSource().getName());
				answer.add(promotionTmp.getTarget().getName());
				answer.add(type.toString().toLowerCase());
				answer.add(color.toString().toLowerCase());

				return new ChessApiResponse<>(true, answer);
			}
			throw new Exception("No element found...");
		}
		throw new NoMoveFoundException("no promotions found");
	}

	/**
	 * Handles POST requests to save the current game state to a file.
	 * <p>
	 * This method attempts to save the game to a predefined file (`save-game.txt`).
	 * If the save operation is successful, it returns an HTTP 200 response with a
	 * success message. If an error occurs during the file-saving process, it
	 * returns an HTTP 500 response with an error message.
	 * </p>
	 *
	 * @return a `ResponseEntity` with a message indicating success or failure of
	 *         the save operation
	 */
	@PostMapping("/save-game")
	@ResponseBody
	public ResponseEntity<String> saveGame() {
		try {
			saveGame("save-game.txt");
			return ResponseEntity.ok("Game saved successfully");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving game");
		}
	}

	/**
	 * Helper method to save the game state to a specified file path.
	 *
	 * @param path The file path to save the game data.
	 * @throws IOException If an I/O error occurs while saving.
	 */
	protected void saveGame(String path) throws IOException {
		GameSaver saver = new GameSaver();
		saver.saveGame(((Game) get(KEY.CHESSGAME)).getMoveList(), path);
	}

	/**
	 * Handles POST requests to load a saved game state from a file.
	 *
	 * @throws Exception If any error occurs during loading.
	 */
	@PostMapping("/load-game")
	@ResponseBody
	protected void loadGame() throws Exception {
		reset();
		setup();
		loadGame("save-game.txt");
		helper.sendReloadSignal();
	}

	/**
	 * Downloads the game moves as a file for the user to save locally.
	 *
	 * @return A `ResponseEntity` containing the file with moves in text format.
	 * @throws IOException If an error occurs while preparing the file.
	 */
	@GetMapping("/download-game")
	public ResponseEntity<InputStreamResource> downloadGame() throws IOException {

		Game chessGame = (Game) get(KEY.CHESSGAME);

		String gameData = "";
		if (chessGame.getMoveList().size() == 1) {
			gameData = chessGame.getMoveList().get(0).toString();
		} else if (chessGame.getMoveList().size() == 2) {
			gameData = chessGame.getMoveList().get(0).toString() + "\n" + chessGame.getMoveList().get(1).toString();
		} else {
			gameData = chessGame.getMoveList().get(0).toString();
			for (int i = 1; i < chessGame.getMoveList().size(); i++) {
				gameData = gameData + "\n" + chessGame.getMoveList().get(i);
			}
		}
		logger.info("uploading {}", gameData);
		ByteArrayInputStream bis = new ByteArrayInputStream(gameData.getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=chessgame.txt");

		return ResponseEntity.ok().headers(headers).contentLength(gameData.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(bis));
	}

	/**
	 * Uploads a saved game file from the client to the server, which is then loaded
	 * into the game for resuming or reviewing previous moves.
	 *
	 * @param file The file containing the saved game state.
	 * @return A `ResponseEntity` confirming successful load or detailing any error.
	 * @throws Exception If an error occurs during the file upload or load process.
	 */
	@PostMapping("/upload-game")
	public ResponseEntity<String> uploadGame(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file selected.");
		}

		try {
			FileWriter fw = new FileWriter("save-game.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(new String(file.getBytes()));
			bw.flush();
			bw.close();
			fw.close();

			loadGame();
			return ResponseEntity.ok("Game loaded successfully");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading game");
		}
	}

	/**
	 * Imports a UCI chess engine from an uploaded file, adds it to the available
	 * engines, and makes it executable for use in the game.
	 *
	 * @param file The file containing the engine executable.
	 * @return A `ResponseEntity` confirming successful import or detailing any
	 *         error.
	 * @throws Exception If an error occurs during file upload or import.
	 */
	@PostMapping("/import-Engine")
	public ResponseEntity<String> importEngine(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file selected.");
		}

		try {
			String name = System.getProperty("user.dir") + "/" + file.getOriginalFilename();

			FileOutputStream fos = new FileOutputStream(name);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			bos.write(file.getBytes());
			bos.flush();
			bos.close();
			fos.close();

			new File(name).setExecutable(true);

			evaluationEngines.put(file.getOriginalFilename(), new EvaluationUciEngine(name) {
				@Override
				public String toString() {
					return file.getOriginalFilename();
				}
			});
			playerEngines.put(file.getOriginalFilename(), new PlayerUciEngine(name) {
				@Override
				public String toString() {
					return file.getOriginalFilename();
				}
			});
			return ResponseEntity.ok("Game loaded successfully");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading game");
		}
	}

	/**
	 * Handles GET requests to retrieve the players
	 *
	 * @return The current players
	 * @throws IOException          if s.t. goes wrong
	 * @throws NoMoveFoundException if no move is found
	 */
	@GetMapping("/getPlayers")
	@ResponseBody
	public ChessApiResponse<Map<String, String>> getPlayers() throws NoMoveFoundException, IOException {
		Map<String, String> players = new HashMap<>();
		if (!((Boolean) get(KEY.ENGINE_MATCH))) {
			if (((Boolean) get(KEY.REGULAR))) {
				players.put("white", "Player");
				players.put("whitetooltip", "The human player");
				players.put("black", get(KEY.PLAYER_ENGINE_FOR_BLACK).toString());
				players.put("blacktooltip",
						helper.createToolTipForConfig((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)));
			} else {
				players.put("black", "Player");
				players.put("blacktooltip", "The human player");
				players.put("white", get(KEY.PLAYER_ENGINE_FOR_WHITE).toString());
				players.put("whitetooltip",
						helper.createToolTipForConfig((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)));
			}
		} else {
			players.put("white", get(KEY.PLAYER_ENGINE_FOR_WHITE).toString());
			players.put("whitetooltip", helper.createToolTipForConfig((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)));
			players.put("black", get(KEY.PLAYER_ENGINE_FOR_BLACK).toString());
			players.put("blacktooltip", helper.createToolTipForConfig((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)));
		}
		return new ChessApiResponse<>(true, players);
	}

	/**
	 * Handles GET requests to retrieve the players
	 *
	 * @return The current players
	 * @throws IOException          if s.t. goes wrong
	 * @throws NoMoveFoundException if no move is found
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/getPositionStrings")
	@ResponseBody
	public ChessApiResponse<List<String>> getPositionStrings() throws NoMoveFoundException, IOException {
		return new ChessApiResponse<>(true, (List<String>) get(KEY.POSITIONS_AS_STRINGS));
	}
	 
	@SuppressWarnings("unchecked")
	@GetMapping("/getEvaluationProfile")
	@ResponseBody
	public ChessApiResponse<List<Double>> getEvaluationProfile() throws NoMoveFoundException, IOException {
		Map<String, List<Pair<Double, String>>> engineLines = (Map<String, List<Pair<Double, String>>>) get(KEY.ENGINE_ANALYSIS);
		List<Double> profile = new ArrayList<>(); 
		for (int i = 0; i < engineLines.size(); i++) {
			double val = 0.5;
			for (Entry<String, List<Pair<Double, String>>> entry:engineLines.entrySet()) {
				if (entry.getKey().split(" ").length == i) {
					val = -entry.getValue().get(0).getKey();
				}				
			}
			profile.add(Math.max(Math.min(val, 10), -10));
		}
		return new ChessApiResponse<>(true, profile);
	}

	/**
	 * Handles GET requests to retrieve the list of moves made during the game.
	 *
	 * @return A list of strings representing the moves.
	 * @throws IOException          if s.t. goes wrong
	 * @throws NoMoveFoundException if no move is found
	 */
	@GetMapping("/moveList")
	@ResponseBody
	public ChessApiResponse<List<String>> getMoveList() throws NoMoveFoundException, IOException {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		List<String> moves = chessGame.getSanMoveList();
		return new ChessApiResponse<>(true, moves);
	}

	@GetMapping("/positionsStringsForEvaluationEngine")
	@ResponseBody
	public ChessApiResponse<List<String>> positionsStringsForEvaluationEngine()
			throws NoMoveFoundException, IOException {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		List<String> moves = chessGame.getSanMoveList();
		return new ChessApiResponse<>(true, moves);
	}

	/**
	 * Handles GET requests to retrieve the list of moves suggested by the UciEngine
	 * engine.
	 *
	 * @return A list of strings representing the UciEngine suggestions.
	 * @throws Exception If any error occurs during retrieval.
	 */
	@GetMapping("/uciEngineMoveList")
	@ResponseBody
	protected synchronized ChessApiResponse<List<String>> getStockFishMoveList() throws Exception {
		List<String> evalMoveList = helper.getEvaluationEngineMoveList(this.getEvaluationEngine());
		List<String> evalSanMoveList = helper.convertToSan(evalMoveList, admin);
		return new ChessApiResponse<>(true, evalSanMoveList);
	}

	/**
	 * Gets the UciEngine evaluation score.
	 *
	 * @return the UciEngine evaluation score
	 * @throws ExecutionException   if s.t. in future task goes wrong
	 * @throws InterruptedException if s.t. in future task goes wrong
	 * @throws IOException          if s.t. in future task goes wrong
	 */
	@GetMapping("/uciEngineEvaluation")
	@ResponseBody
	protected ChessApiResponse<Double> getUciEngineEvaluation()
			throws IOException, InterruptedException, ExecutionException {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		if (chessGame.getState() != null) {
			new ChessApiResponse<>(false, 0.5d);
		}
		List<Pair<Double, String>> bestLines = getEvaluationEngine().getBestLines(chessGame,
				(EngineConfig) get(KEY.ENGINE_CONFIG_EVAL));
		double eval;
		if (bestLines.isEmpty()) {
			eval = (double) get(KEY.UCI_ENGINE_EVALUATION);
		} else {
			eval = bestLines.get(0).getLeft();
			put(KEY.UCI_ENGINE_EVALUATION, eval);
		}
		return new ChessApiResponse<>(true, helper.getRatioEvalBars(eval));
	}

	/**
	 * Handles POST requests to update the position of the UciEngine move list on
	 * the screen.
	 *
	 * @param top  The new top position (in pixels).
	 * @param left The new left position (in pixels).
	 */
	@PostMapping("/updateUciEngineMoveListPosition")
	@ResponseBody
	protected void updateUciEngineMoveListPosition(@RequestParam int top, @RequestParam int left) {
		viewConfig.setUciEngineMoveListTop(top);
		viewConfig.setUciEngineMoveListLeft(left);
	}

	/**
	 * Handles POST requests to update the position of the move list on the screen.
	 *
	 * @param top  The new top position (in pixels).
	 * @param left The new left position (in pixels).
	 */
	@PostMapping("/updateMoveListPosition")
	@ResponseBody
	protected void updateMoveListPosition(@RequestParam int top, @RequestParam int left) {
		viewConfig.setMoveListTop(top);
		viewConfig.setMoveListLeft(left);
	}

	/**
	 * Retrieves the remaining time for each player in the current game.
	 * <p>
	 * This endpoint calculates the time left for both the white and black players
	 * based on the total time allocated to each player and the elapsed time tracked
	 * by each player's clock. The remaining time is returned as a map with keys
	 * "whiteTime" and "blackTime", each representing the remaining time in seconds
	 * for the respective player.
	 * </p>
	 *
	 * @return a `ChessApiResponse` containing a map with the remaining time for
	 *         both players in seconds
	 */
	@GetMapping("/currentTime")
	@ResponseBody
	public ChessApiResponse<Map<String, Integer>> getCurrentTime() {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		int timeForEachPlayer = chessGame.getTimeForEachPlayer();
		int whiteTime = timeForEachPlayer - (int) chessGame.getWhitePlayer().getChessClock().getTime(TimeUnit.SECONDS);
		int blackTime = timeForEachPlayer - (int) chessGame.getBlackPlayer().getChessClock().getTime(TimeUnit.SECONDS);

		Map<String, Integer> timeMap = new HashMap<>();
		timeMap.put("whiteTime", Math.max(0, whiteTime));
		timeMap.put("blackTime", Math.max(0, blackTime));

		return new ChessApiResponse<>(true, timeMap);
	}

	/**
	 * Updates the position of captured pieces on the display, as specified by the
	 * provided screen coordinates.
	 *
	 * @param top  The new top position (in pixels).
	 * @param left The new left position (in pixels).
	 * @return A `ChessApiResponse` confirming the position update.
	 */
	@PostMapping("/updateCapturedPiecesPosition")
	public ChessApiResponse<String> updateCapturedPiecesPosition(@RequestParam int top, @RequestParam int left) {
		return new ChessApiResponse<>(true, "Position updated");
	}

	/**
	 * Checks if the UCI engine is active and provides the next suggested move from
	 * the engine for the current player. If the UCI engine is active, it retrieves
	 * the best move, which may include castling, en passant, promotion, or a
	 * regular move. If the UCI engine is not active or if the game state does not
	 * allow moves, a response indicating that the engine is inactive is returned.
	 *
	 * @return A `ChessApiResponse` containing details about the UCI engine's
	 *         status, the type of move suggested, and additional move information
	 *         if applicable.
	 * @throws Exception If an error occurs during the move retrieval process.
	 */
	@PostMapping("/checkUciEnginePlayer")
	public ChessApiResponse<Map<String, Object>> checkUciEnginePlayer() throws Exception {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		Map<String, Object> response = new HashMap<>();
		boolean uciEngineActive = viewConfig.isUciEngineActive();
		if (!helper.checkForGameState(chessGame, getEvaluationEngine())) {
			return new ChessApiResponse<>(true, response);
		}
		if (uciEngineActive && chessGame.getState() == null) {
			Color color = getChessGame().getPlayer().getColor();
			PlayerEngine playerEngine = color.equals(Color.WHITE) ? ((PlayerEngine) get(KEY.PLAYER_ENGINE_FOR_WHITE))
					: ((PlayerEngine) get(KEY.PLAYER_ENGINE_FOR_BLACK));
			EngineConfig config = color.equals(Color.WHITE) ? (EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)
					: (EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK);

			Move move = playerEngine.getBestMove(chessGame, config);

			if ((boolean) get(KEY.ENGINE_MATCH)) {
				response.put("engineClash", true);
			}
			response.put("uciEngineActive", true);
			if (move instanceof Castling) {
				logger.info("{} suggesting castling: {}", playerEngine, move);
				response.put("type", "castling");
				response.put("rooksource", ((Castling) move).getRook().getField().toString());
				applyMove(move);
				response.put("rooktarget", ((Castling) move).getRook().getField().toString());
				response.put("move", move.toString());
			} else if (move instanceof EnPassant) {
				logger.info("{} suggesting enpassent: {}", playerEngine, move);
				response.put("type", "enpassant");
				response.put("slayed", ((EnPassant) move).getSlayedPiece().getField().toString());
				response.put("move", move.toString());
				applyMove(move);
			} else if (move instanceof Promotion) {
				logger.info("{} suggesting promotion: {}", playerEngine, move);
				response.put("type", "promotion");
				if (move.getTarget().getPiece() != null) {
					response.put("slayed", move.getTarget().toString());
				}
				response.put("move", move.toString());
				response.put("pieceType", ((Promotion) move).getPromotedPiece().getType().name().toLowerCase());
				response.put("color", ((Promotion) move).getPromotedPiece().getColor().toString().toLowerCase());
				applyMove(move);
			} else if (move.getTarget().getPiece() != null) {
				logger.info("{} suggesting regular slaying: {}", playerEngine, move);
				response.put("slayed", move.getTarget().toString());
				response.put("move", move.toString());
				applyMove(move);
			} else {
				logger.info("{} suggesting regular move: {}", playerEngine, move);
				response.put("move", move.toString());
				applyMove(move);
			}
			return new ChessApiResponse<>(true, response);

		} else {
			response.put("uciEngineActive", false);
			return new ChessApiResponse<>(true, response);
		}
	}

	/**
	 * Retrieves the best move suggested by the UCI engine for display as an arrow
	 * on the chessboard. This method ensures that a valid move is provided by the
	 * engine and formats it for use in the frontend. If the game state does not
	 * allow moves, an empty response is returned.
	 *
	 * @return A `ChessApiResponse` containing a map with "from" and "to" keys,
	 *         representing the start and end coordinates of the best move as
	 *         strings.
	 * @throws Exception If any error occurs during move retrieval from the engine.
	 */
	@GetMapping("/uciEngineBestMove")
	protected ChessApiResponse<Map<String, String>> getBestMoveForArrow() throws Exception {
		Map<String, String> map = new LinkedHashMap<>();
		Game chessGame = (Game) get(KEY.CHESSGAME);
		if (chessGame.getState() != null) {
			return new ChessApiResponse<>(false, map);
		}
		String mv = "";
		while (mv.isBlank() || mv.equals("[]")) {
			if (!helper.getEvaluationEngineMoveList(this.getEvaluationEngine()).isEmpty()) {
				String s = helper.getEvaluationEngineMoveList(this.getEvaluationEngine()).get(0);
				double eval = Double.parseDouble(s.split(":")[0]);
				put(KEY.UCI_ENGINE_EVALUATION, eval);
				mv = s.split(":")[1].split(" ")[1];
			} else {
				mv = getEvaluationEngine().getBestLines(chessGame, (EngineConfig) get(KEY.ENGINE_CONFIG_EVAL))
						.toString().split(" ")[0];
			}
		}

		map.put("from", mv.substring(0, 2));
		map.put("to", mv.substring(2, 4));
		return new ChessApiResponse<>(true, map);
	}

	/**
	 * Applies a specified move to the current game state. This method first checks
	 * if the game state allows for the move and, if so, updates the game with the
	 * new move. It also updates various UI components, including clocks and move
	 * lists, and refreshes evaluation or arrow visuals if configured to display
	 * them.
	 *
	 * @param move The move to apply to the game.
	 * @throws Exception If an error occurs during the move application or if the
	 *                   game state check fails.
	 */
	@SuppressWarnings("unchecked")
	public void applyMove(Move move) throws Exception {
		Game chessGame = ((Game) get(KEY.CHESSGAME));
		if (!helper.checkForGameState(chessGame, getEvaluationEngine())) {
			if (evaluationEngines.get(get(KEY.EVALUATION_ENGINE)) != null) {
				evaluationEngines.get(get(KEY.EVALUATION_ENGINE)).stopEvaluation();
			}
			return;
		}
		chessGame.apply(move);
		if (viewConfig.isShowArrows() || viewConfig.isShowEvaluation() || viewConfig.isShowUciEngineLines()) {
			((List<Pair<Double, String>>) get(KEY.UCI_ENGINE_MOVELIST)).clear();
			helper.getEvaluationEngineMoveList(this.getEvaluationEngine());
		}
		this.webSocketService.updateClocks();
		this.webSocketService.updateMoveList();
		String positionAsString = createPositionAsString(chessGame);
		((List<String>) get(KEY.POSITIONS_AS_STRINGS)).add(positionAsString);
	}

	/**
	 * Creates a 64-character string representing the current state of the
	 * chessboard. Each character corresponds to a square on the board, with pieces
	 * represented by standard abbreviations (e.g., 'P' for white pawn, 'p' for
	 * black pawn). Empty squares are represented by a placeholder character.
	 *
	 * @param chessGame The current chess game from which the board state is
	 *                  extracted.
	 * @return A 64-character string representing the board state.
	 */
	private String createPositionAsString(Game chessGame) {
		StringBuilder boardString = new StringBuilder(64);
		Board board = chessGame.getChessBoard(); // Retrieve the board object

		for (int rank = 8; rank > 0; rank--) { // Iterate over ranks from top (8) to bottom (1)
			for (int file = 1; file <= 8; file++) { // Iterate over files from left (a) to right (h)
				Field field = board.getField(file, rank); // Retrieve the field at (file, rank)
				Piece piece = field.getPiece();

				if (piece == null) {
					boardString.append('.'); // Placeholder for empty squares
				} else {
					boardString.append(getPieceRepresentation(piece));
				}
			}
		}
		return boardString.toString();
	}

	/**
	 * Returns a single character representing the specified chess piece. Uppercase
	 * letters denote white pieces, and lowercase letters denote black pieces.
	 *
	 * @param piece The piece to represent.
	 * @return A single character representing the piece.
	 */
	private char getPieceRepresentation(Piece piece) {
		char representation;
		switch (piece.getType()) {
		case PAWN:
			representation = 'P';
			break;
		case KNIGHT:
			representation = 'N';
			break;
		case BISHOP:
			representation = 'B';
			break;
		case ROOK:
			representation = 'R';
			break;
		case QUEEN:
			representation = 'Q';
			break;
		case KING:
			representation = 'K';
			break;
		default:
			representation = '.';
			break;
		}
		return piece.getColor() == Color.BLACK ? Character.toLowerCase(representation) : representation;
	}

	/**
	 * Resets the game state to its initial configuration. This method invokes the
	 * helper’s reset logic, ensuring that all relevant game components and
	 * configurations are restored to their default states.
	 *
	 * @return A string confirmation of the reset process.
	 * @throws Exception If any error occurs during the reset operation.
	 */
	@Override
	protected String reset() throws Exception { 
		put(KEY.ENGINE_MATCH, false);
		createNewGame();
		setup(); 
		return helper.reset();
	}

	/**
	 * Retrieves the logger instance associated with this controller. This logger
	 * can be used for debugging, information, or error logging within the
	 * controller.
	 *
	 * @return The logger instance for this class.
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

	/**
	 * The `PieceSelection` class represents a selected piece during a promotion in
	 * a chess game. This class holds information about the type of piece chosen by
	 * the user when a pawn is promoted.
	 *
	 * It includes: - A getter method to retrieve the selected piece type. - A
	 * setter method to set the piece type during promotion.
	 */
	protected static class PieceSelection {
		private String piece;

		/**
		 * Retrieves the type of the selected piece.
		 *
		 * @return A string representing the selected piece type, such as "queen" or
		 *         "knight".
		 */
		public String getPiece() {
			return piece;
		}

		/**
		 * Sets the type of the selected piece during a promotion.
		 *
		 * @param piece A string representing the type of piece chosen for promotion.
		 */
		public void setPiece(String piece) {
			this.piece = piece;
		}
	}

}
