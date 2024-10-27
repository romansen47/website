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

@RestController
@RequestMapping("/api/game")
public class ChessApiController extends ControllerTemplate {

	protected static final Logger logger = LogManager.getLogger();

	@Autowired
	private ApiControllerHelper helper;

	protected String selectedPiece;

	protected Field selectedField = null;

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
	 * @throws Exception
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
			String engine = !viewConfig.getIsFlipped() ? viewConfig.getPlayerEngineForBlack() : viewConfig.getPlayerEngineForWhite();
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
		if (!helper.checkForGameState(chessGame,getEvaluationEngine()) || !helper.isHumanAlowedToInteract(chessGame, viewConfig.isUciEngineActive())) {
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
			Optional<DisplayedPiece> element = ((List<DisplayedPiece>) get(KEY.ELEMENTS)).stream()
					.filter(el -> el.getPiece().equals(finalProm.getPiece())).findFirst();
			if (element.isPresent()) {
				Color color = promotionTmp.getPiece().getColor();
				PieceType type = promotionTmp.getPromotedPiece().getType();
				element.get().setPiece(promotionTmp.getPromotedPiece());
				String imagePath = ((ChessHelper)helper).getImagePath(color, promotionTmp.getPiece().getType());
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
	 *
	 * @throws IOException If any I/O error occurs during saving.
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

	@GetMapping("/download-game")
	public ResponseEntity<InputStreamResource> downloadGame() throws IOException {

		Game chessGame = (Game) get(KEY.CHESSGAME);

		String gameData = "";
		if (chessGame.getMoveList().size() == 1) {
			gameData = chessGame.getMoveList().get(0).toString();
		} else if (chessGame.getMoveList().size() == 2) {
			gameData = chessGame.getMoveList().get(0).toString() + "\n"
					+ chessGame.getMoveList().get(1).toString();
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

	// Load game: Client uploads a game file to the server
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
	 * Handles GET requests to retrieve the list of moves made during the game.
	 *
	 * @return A list of strings representing the moves.
	 * @throws IOException
	 * @throws NoMoveFoundException
	 */
	@GetMapping("/moveList")
	@ResponseBody
	public ChessApiResponse<String> getMoveList() throws NoMoveFoundException, IOException {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		List<Move> moves = chessGame.getMoveList();
		StringBuilder moveListHtml = new StringBuilder();

		String prefixWhite = viewConfig.getUciEngineDepthForWhite() == 0
				? " (time " + viewConfig.getMoveOverheadForWhite() + "s)"
				: " (depth " + viewConfig.getUciEngineDepthForWhite() + ")";
		String prefixBlack = viewConfig.getUciEngineDepthForBlack() == 0
				? " (time " + viewConfig.getMoveOverheadForBlack() + "s)"
				: " (depth " + viewConfig.getUciEngineDepthForBlack() + ")";

		if ((boolean) get(KEY.ENGINE_MATCH)) {
			moveListHtml.append("<div><b>" + get(KEY.PLAYER_ENGINE_FOR_WHITE) + prefixWhite + "     vs     ")
					.append(get(KEY.PLAYER_ENGINE_FOR_BLACK) + prefixBlack + "</b></div>");
		} else if (!viewConfig.getIsFlipped()) {
			moveListHtml.append("<div><b>Player    vs    " + get(KEY.PLAYER_ENGINE_FOR_BLACK) + prefixBlack + "</b></div>");
		} else {
			moveListHtml.append("<div><b>" + get(KEY.PLAYER_ENGINE_FOR_BLACK) + prefixWhite + "    vs   Player</b></div>");
		}
		moveListHtml.append("<hr/>");

		for (int i = 0; i < moves.size(); i += 2) {
			moveListHtml.append("<div style='display: flex;'>");

			moveListHtml.append("<span style='width: 1px; display: inline-block; margin-left: 20px;'>")
					.append(i / 2 + 1).append("</span>")
					.append("<span style='width: 40px; display: inline-block; margin-left: 20px;'>").append(" : 	")
					.append("</span>").append("<span style='width: 60px; display: inline-block;'>");
			if (!viewConfig.isShortAlgebraicNotation()) {
				moveListHtml.append(helper.getUnicodeSymbol(moves.get(i).getPiece())).append(" ");
				moveListHtml.append(moves.get(i).toString()).append("</span>");
			} else {
				moveListHtml.append(chessGame.getSanMoveList().get(i));
				moveListHtml.append("</span>");
			}
			if (i + 1 < moves.size()) {
				if (!viewConfig.isShortAlgebraicNotation()) {
					moveListHtml.append("<span style='width: 60px; display: inline-block; margin-left: 20px;'>")
							.append(helper.getUnicodeSymbol(moves.get(i + 1).getPiece())).append(" 		")
							.append(moves.get(i + 1).toString()).append("</span>");
				} else {
					moveListHtml.append("<span style='width: 60px; display: inline-block; margin-left: 20px;'>")
							.append(chessGame.getSanMoveList().get(i + 1).toString()).append("</span>");
				}
			}

			moveListHtml.append("</div>");
		}

		return new ChessApiResponse<>(true, moveListHtml.toString());
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
	protected ChessApiResponse<List<String>> getStockFishMoveList() throws Exception {
		return new ChessApiResponse<>(true, helper.getEvaluationEngineMoveList(this.getEvaluationEngine()));
	}

	/**
	 * Gets the UciEngine evaluation score.
	 *
	 * @return the UciEngine evaluation score
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IOException
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

	@GetMapping("/currentTime")
	@ResponseBody
	public ChessApiResponse<Map<String, Integer>> getCurrentTime() {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		int timeForEachPlayer = chessGame.getTimeForEachPlayer();
		int whiteTime = timeForEachPlayer
				- (int) chessGame.getWhitePlayer().getChessClock().getTime(TimeUnit.SECONDS);
		int blackTime = timeForEachPlayer
				- (int) chessGame.getBlackPlayer().getChessClock().getTime(TimeUnit.SECONDS);

		Map<String, Integer> timeMap = new HashMap<>();
		timeMap.put("whiteTime", Math.max(0, whiteTime));
		timeMap.put("blackTime", Math.max(0, blackTime));

		return new ChessApiResponse<>(true, timeMap);
	}

	@PostMapping("/updateCapturedPiecesPosition")
	public ChessApiResponse<String> updateCapturedPiecesPosition(@RequestParam int top, @RequestParam int left) {
		return new ChessApiResponse<>(true, "Position updated");
	}

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
				logger.info("{} applying regular slaying: {}", playerEngine, move);
				response.put("slayed", move.getTarget().toString());
				response.put("move", move.toString());
				applyMove(move);
			} else {
				logger.info("{} applying regular move: {}", playerEngine, move);
				response.put("move", move.toString());
				applyMove(move);
			}
			return new ChessApiResponse<>(true, response);

		} else {
			response.put("uciEngineActive", false);
			return new ChessApiResponse<>(true, response);
		}
	}

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
	}

	protected static class PieceSelection {
		private String piece;

		public String getPiece() {
			return piece;
		}

		public void setPiece(String piece) {
			this.piece = piece;
		}
	}

	@Override
	protected String reset() throws Exception {
		return helper.reset();
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
