package com.example.demo.controller.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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
import com.example.demo.model.DisplayedField;
import com.example.demo.model.DisplayedPiece;

import demo.chess.definitions.Color;
import demo.chess.definitions.PieceType;
import demo.chess.definitions.engines.PlayerEngine;
import demo.chess.definitions.engines.impl.NoMoveFoundException;
import demo.chess.definitions.fields.Field;
import demo.chess.definitions.moves.Castling;
import demo.chess.definitions.moves.EnPassant;
import demo.chess.definitions.moves.Move;
import demo.chess.definitions.moves.Promotion;
import demo.chess.definitions.pieces.Piece;
import demo.chess.definitions.pieces.impl.Rook;
import demo.chess.definitions.states.State;
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
		put("engineMatch", false);
		put("regular", true);
		put("stockfishEvaluation", 0.5d);
		put("stockfishMoveList", new ArrayList<>());
	}

	@PostMapping("/getPossibleMoves")
	@ResponseBody
	protected ChessApiResponse<List<String>> getPossibleMoves(String field) throws Exception {
		List<DisplayedField> allfields = (List<DisplayedField>) get("fields");
		Field clickedField = null;
		for (DisplayedField f : allfields) {
			if (f.getField().toString().equals(field)) {
				clickedField = f.getField();
			}
		}
		Piece piece = clickedField.getPiece();
		List<String> fields = new ArrayList<>();
		for (Move move : ((Game) get("chessGame")).getPlayer().getValidMoves(((Game) get("chessGame")))) {
			if (move.getSource().getPiece().equals(piece)) {
				fields.add(move.getTarget().getName());
			}
		}
		return new ChessApiResponse<>(true, fields);
	}

	private boolean checkForGameState(Game chessGame) throws Exception {

		if (chessGame.getState() != null) {
			String message = "";
			long white;
			long black;
			if (chessGame.getState().equals(State.BLACK_MATED)) {
				message = "White won by checkmate!";
			}
			if (chessGame.getState().equals(State.WHITE_MATED)) {
				message = "Black won by checkmate!";
			}
			if (chessGame.getState().equals(State.STALEMATE)) {
				message = "Game ended in a stalemate!";
			}
			if (chessGame.getState().equals(State.LOST_ON_TIME)) {
				white = chessGame.getTimeForEachPlayer() * 1000
						- chessGame.getWhitePlayer().getChessClock().getTime(TimeUnit.MILLISECONDS);
				black = chessGame.getTimeForEachPlayer() * 1000
						- chessGame.getBlackPlayer().getChessClock().getTime(TimeUnit.MILLISECONDS);
				message = " lost on time!";
				if (white < 0) {
					message = "White" + message;
				} else if (black < 0) {
					message = "Black" + message;
				}
			}
			if (chessGame.getState().equals(State.WHITE_RESIGNED)) {
				message = "White resigned!";
			}
			if (chessGame.getState().equals(State.BLACK_RESIGNED)) {
				message = "Black resigned!";
			}
			webSocketService.sendMessage(message);
			return false;
		}
		return true;
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
	protected ChessApiResponse<List<String>> onPieceClicked(@RequestParam int id) throws Exception {

		if (!checkForGameState(((Game) get("chessGame")))) {
			return new ChessApiResponse<>(true, new ArrayList<>());
		}
		// Get the piece corresponding to the clicked element
		Piece selectedPiece = ((List<DisplayedPiece>) get("elements")).get(id).getPiece();

		// Get the color of the current player
		Color currentPlayerColor = ((Game) get("chessGame")).getPlayer().getColor();
		// If no field is selected and the piece belongs to the current player
		if (selectedField == null && selectedPiece.getColor() == currentPlayerColor
				&& null != selectedPiece.getField()) {
			// If the selected piece has valid source fields
			if (helper.getSourceFields().stream().map(Field::getPiece).distinct().toList().contains(selectedPiece)) {
				selectedField = selectedPiece.getField();
				List<String> fields = new ArrayList<>();
				// Get all valid target fields for this piece
				for (Move move : ((Game) get("chessGame")).getPlayer().getValidMoves(((Game) get("chessGame")))) {
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
			List<Move> moveList = ((Game) get("chessGame")).getPlayer().getValidMoves(((Game) get("chessGame")));
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
						newKingField = ((Game) get("chessGame")).getChessBoard().getField(3, rank);
						newRookField = ((Game) get("chessGame")).getChessBoard().getField(4, rank);
					} else {
						newKingField = ((Game) get("chessGame")).getChessBoard().getField(7, rank);
						newRookField = ((Game) get("chessGame")).getChessBoard().getField(6, rank);
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
	protected ChessApiResponse<List<String>> onFieldClicked(@RequestParam int id) throws Exception {
		if (!checkForGameState(((Game) get("chessGame")))) {
			return new ChessApiResponse<>(true, new ArrayList<>());
		}
		Field fieldClickedOn = ((List<DisplayedField>) get("fields")).get(id).getField();
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
			List<Move> moveList = ((Game) get("chessGame")).getPlayer().getValidMoves(((Game) get("chessGame")));
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
			Optional<DisplayedPiece> element = ((List<DisplayedPiece>) get("elements")).stream()
					.filter(el -> el.getPiece().equals(finalProm.getPiece())).findFirst();
			if (element.isPresent()) {
				Color color = promotionTmp.getPiece().getColor();
				PieceType type = promotionTmp.getPromotedPiece().getType();
				element.get().setPiece(promotionTmp.getPromotedPiece());
				String imagePath = getImagePath(color, promotionTmp.getPiece().getType());
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
		saver.saveGame(((Game) get("chessGame")).getMoveList(), path);
	}

	/**
	 * Handles POST requests to load a saved game state from a file.
	 *
	 * @throws Exception If any error occurs during loading.
	 */
	@PostMapping("/load-game")
	@ResponseBody
	protected void loadGame() throws Exception {
		this.reset();
		loadGame("save-game.txt");
		helper.sendReloadSignal();
	}

	@GetMapping("/download-game")
	public ResponseEntity<InputStreamResource> downloadGame() throws IOException {
		// Simulate generating the game file (replace with your real logic)
		String gameData = "";
		if (((Game) get("chessGame")).getMoveList().size() == 1) {
			gameData = ((Game) get("chessGame")).getMoveList().get(0).toString();
		} else if (((Game) get("chessGame")).getMoveList().size() == 2) {
			gameData = ((Game) get("chessGame")).getMoveList().get(0).toString() + "\n"
					+ ((Game) get("chessGame")).getMoveList().get(1).toString();
		} else {
			gameData = ((Game) get("chessGame")).getMoveList().get(0).toString();
			for (int i = 1; i < ((Game) get("chessGame")).getMoveList().size(); i++) {
				gameData = gameData + "\n" + ((Game) get("chessGame")).getMoveList().get(i);
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
			// Read the file content (replace this with your actual logic for loading the
			// game)
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
	 * Handles GET requests to retrieve the list of moves made during the game.
	 *
	 * @return A list of strings representing the moves.
	 */
	@GetMapping("/moveList")
	@ResponseBody
	public ChessApiResponse<String> getMoveList() {
		List<Move> moves = ((Game) get("chessGame")).getMoveList();
		StringBuilder moveListHtml = new StringBuilder();

		if ((boolean) get("engineMatch")) {
			moveListHtml.append("<div>Stockfish (depth " + viewConfig.getStockfishDepthForWhite() + ") - Stockfish (depth "
					+ viewConfig.getStockfishDepthForBlack() + ")</div>");
		} else {
			moveListHtml.append("<div>Player vs Stockfish</div>");
		}
		moveListHtml.append("<hr/>");

		for (int i = 0; i < moves.size(); i += 2) {
			moveListHtml.append("<div style='display: flex;'>");

			// Füge den ersten Zug (Weiß) hinzu mit fester Breite
			moveListHtml.append("<span style='width: 1px; display: inline-block; margin-left: 20px;'>")
					.append(i / 2 + 1).append("</span>")
					.append("<span style='width: 40px; display: inline-block; margin-left: 20px;'>").append(" : 	")
					.append("</span>").append("<span style='width: 60px; display: inline-block;'>")
					.append(helper.getUnicodeSymbol(moves.get(i).getPiece())) // Schachsymbol für die Figur
					.append(" ").append(moves.get(i).toString()).append("</span>");

			// Falls ein zweiter Zug (Schwarz) vorhanden ist, füge ihn hinzu
			if (i + 1 < moves.size()) {
				moveListHtml.append("<span style='width: 60px; display: inline-block; margin-left: 20px;'>")
						.append(helper.getUnicodeSymbol(moves.get(i + 1).getPiece())) // Schachsymbol für die Figur
						.append(" 		").append(moves.get(i + 1).toString()).append("</span>");
			}

			moveListHtml.append("</div>");
		}

		return new ChessApiResponse<>(true, moveListHtml.toString()); // Rückgabe des HTML-Strings
	}

	/**
	 * Handles GET requests to retrieve the list of moves suggested by the Stockfish
	 * engine.
	 *
	 * @return A list of strings representing the Stockfish suggestions.
	 * @throws Exception If any error occurs during retrieval.
	 */
	@GetMapping("/stockfishMoveList")
	@ResponseBody
	protected ChessApiResponse<List<String>> getStockFishMoveList() throws Exception {
		return new ChessApiResponse<>(true,
//				helper.getStockFishMoveList(((List<Pair<Double, String>>)get("stockfishMoveList"))));
				helper.getStockFishMoveList());
	}

	/**
	 * Gets the Stockfish evaluation score.
	 *
	 * @return the Stockfish evaluation score
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@GetMapping("/stockfishEvaluation")
	@ResponseBody
	protected ChessApiResponse<Double> getStockfishEvaluation()
			throws IOException, InterruptedException, ExecutionException {
		Game chessGame = (Game) get("chessGame");
		List<Pair<Double, String>> bestLines = evaluationEngine.getBestLines(chessGame);
		double eval;
		if (bestLines.isEmpty()) {
			eval = (double) get("stockfishEvaluation");
		} else {
			eval = bestLines.get(0).getLeft();
			put("stockfishEvaluation", eval);
		}
		return new ChessApiResponse<>(true, helper.getRatioEvalBars(eval));
	}

	/**
	 * Handles POST requests to update the position of the Stockfish move list on
	 * the screen.
	 *
	 * @param top  The new top position (in pixels).
	 * @param left The new left position (in pixels).
	 */
	@PostMapping("/updateStockfishMoveListPosition")
	@ResponseBody
	protected void updateStockfishMoveListPosition(@RequestParam int top, @RequestParam int left) {
		viewConfig.setStockfishMoveListTop(top);
		viewConfig.setStockfishMoveListLeft(left);
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
		// Zeit für beide Spieler berechnen
		int timeForEachPlayer = ((Game) get("chessGame")).getTimeForEachPlayer();
		int whiteTime = timeForEachPlayer
				- (int) ((Game) get("chessGame")).getWhitePlayer().getChessClock().getTime(TimeUnit.SECONDS);
		int blackTime = timeForEachPlayer
				- (int) ((Game) get("chessGame")).getBlackPlayer().getChessClock().getTime(TimeUnit.SECONDS);

		// Zeit in eine Map packen
		Map<String, Integer> timeMap = new HashMap<>();
		timeMap.put("whiteTime", Math.max(0, whiteTime)); // Stellt sicher, dass die Zeit nicht negativ ist
		timeMap.put("blackTime", Math.max(0, blackTime)); // Stellt sicher, dass die Zeit nicht negativ ist

		return new ChessApiResponse<>(true, timeMap);
	}

	@PostMapping("/updateCapturedPiecesPosition")
	public ChessApiResponse<String> updateCapturedPiecesPosition(@RequestParam int top, @RequestParam int left) {
		viewConfig.setCapturedPiecesTop(top); // Setze die Top-Position
		viewConfig.setCapturedPiecesLeft(left); // Setze die Left-Position
		return new ChessApiResponse<>(true, "Position updated");
	}

	@PostMapping("/checkStockfishPlayer")
	public ChessApiResponse<Map<String, Object>> checkStockfishPlayer() throws Exception {
		Map<String, Object> response = new HashMap<>();
		boolean stockfishActive = viewConfig.isStockfishActive();
		if (!checkForGameState((Game) get("chessGame"))) {
			return new ChessApiResponse<>(true, response);
		}
		if (stockfishActive && ((Game) get("chessGame")).getState() == null) {
			// Berechne den besten Zug von Stockfish
			PlayerEngine playerEngine = ((Game) get("chessGame")).getPlayer().getColor().equals(Color.WHITE)
					? playerEngineForWhite
					: playerEngineForBlack;

			Move move = playerEngine.getBestMove(((Game) get("chessGame")));

			if ((boolean) get("engineMatch")) {
				response.put("engineClash", true);
			}
			// Bereite die Antwort mit dem Stockfish-Zug für das Frontend vor
			response.put("stockfishActive", true);
			if (move instanceof Castling) {
				logger.info("stockfish applying castling: {}", move);
				response.put("type", "castling");
				response.put("rooksource", ((Castling) move).getRook().getField().toString());
				applyMove(move);
				response.put("rooktarget", ((Castling) move).getRook().getField().toString());
				response.put("move", move.toString());
			} else if (move instanceof EnPassant) {
				logger.info("stockfish applying enpassent: {}", move);
				response.put("type", "enpassant");
				response.put("slayed", ((EnPassant) move).getSlayedPiece().getField().toString());
				response.put("move", move.toString());
				applyMove(move);
			} else if (move instanceof Promotion) {
				logger.info("stockfish applying promotion: {}", move);
				response.put("type", "promotion");
				if (move.getTarget().getPiece() != null) {
					response.put("slayed", move.getTarget().toString());
				}
				response.put("move", move.toString());
				response.put("pieceType", ((Promotion) move).getPromotedPiece().getType().name().toLowerCase());
				response.put("color", ((Promotion) move).getPromotedPiece().getColor().toString().toLowerCase());
				applyMove(move);
			} else if (move.getTarget().getPiece() != null) {
				logger.info("stockfish applying regular slaying: {}", move);
				response.put("slayed", move.getTarget().toString());
				response.put("move", move.toString());
				applyMove(move);
			} else {
				logger.info("stockfish applying regular move: {}", move);
				response.put("move", move.toString());
				applyMove(move);
			}
			return new ChessApiResponse<>(true, response);

		} else {
			// Wenn Stockfish nicht aktiv ist, wird nur der Spieler gewechselt
			response.put("stockfishActive", false);
			return new ChessApiResponse<>(true, response);
		}
	}

	@GetMapping("/stockfishBestMove")
	protected ChessApiResponse<Map<String, String>> getBestMoveForArrow() throws Exception {
		String mv = "";
		while (mv.isBlank() || mv.equals("[]")) {
			if (!helper.getStockFishMoveList().isEmpty()) {
				String s = helper.getStockFishMoveList().get(0);
				double eval = Double.parseDouble(s.split(":")[0]);
				put("stockfishEvaluation", eval);
				mv = s.split(":")[1].split(" ")[1];
			} else {
				mv = evaluationEngine.getBestLines(((Game) get("chessGame"))).toString().split(" ")[0];
			}
		}
		Map<String, String> map = new LinkedHashMap<>();

//		logger.info("move: {}", mv);
		map.put("from", mv.substring(0, 2));
		map.put("to", mv.substring(2, 4));
		return new ChessApiResponse<>(true, map);
	}

	// following things may remain here and will not be extracted
	public void applyMove(Move move) throws Exception {
		Game chessGame = ((Game) get("chessGame"));
		if (!checkForGameState(chessGame)) {
			return;
		}
		chessGame.apply(move);
		if (viewConfig.isShowArrows() || viewConfig.isShowEvaluation() || viewConfig.isShowStockfishLines()) {
			((List<Pair<Double, String>>) get("stockfishMoveList")).clear();
			helper.getStockFishMoveList();
		}
		this.webSocketService.updateClocks();
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

}
