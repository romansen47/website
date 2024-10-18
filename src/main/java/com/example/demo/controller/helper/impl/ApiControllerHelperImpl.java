package com.example.demo.controller.helper.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.controller.helper.ApiControllerHelper;
import com.example.demo.controller.impl.ChessApiController;
import com.example.demo.elements.Attributes;
import com.example.demo.model.Config;
import com.example.demo.websockets.WebSocketService;

import demo.chess.definitions.engines.EngineConfig;
import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.definitions.engines.PlayerEngine;
import demo.chess.definitions.engines.impl.NoMoveFoundException;
import demo.chess.definitions.fields.Field;
import demo.chess.definitions.moves.Move;
import demo.chess.definitions.moves.Promotion;
import demo.chess.definitions.pieces.Piece;
import demo.chess.game.Game;

@Component
public class ApiControllerHelperImpl implements ApiControllerHelper {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(ChessApiController.class);

	@Autowired
	protected WebSocketService webSocketService;

	@Autowired
	protected Config viewConfig;

	@Autowired
	protected Attributes attributes;

	/**
	 * Retrieves a list of source fields for all possible moves.
	 *
	 * @return A list of source fields.
	 * @throws NoMoveFoundException If no valid moves are found.
	 * @throws IOException          If an I/O error occurs.
	 */
	@Override
	public List<Field> getSourceFields() throws NoMoveFoundException, IOException {
		return getPossibleMoves().stream().map(Move::getSource).distinct().toList();
	}

	/**
	 * Retrieves a list of target fields for all possible moves.
	 *
	 * @return A list of target fields.
	 * @throws NoMoveFoundException If no valid moves are found.
	 * @throws IOException          If an I/O error occurs.
	 */
	@Override
	public List<Field> getTargetFields() throws NoMoveFoundException, IOException {
		return getPossibleMoves().stream().map(Move::getTarget).distinct().toList();
	}

	/**
	 * Retrieves a list of possible moves for the current player.
	 *
	 * @return A list of valid moves.
	 * @throws NoMoveFoundException If no valid moves are found.
	 * @throws IOException          If an I/O error occurs.
	 */
	@Override
	public List<Move> getPossibleMoves() throws NoMoveFoundException, IOException {
		if (((Game) get("chessGame")).getState() == null) {
			return ((Game) get("chessGame")).getPlayer().getValidMoves(((Game) get("chessGame")));
		}
		return Collections.emptyList();
	}

	@Override
	public void setValidPromotions(List<Move> moveList, Move chessMove, List<Promotion> validPromotions) {
		validPromotions.clear();
		for (Move move : moveList) {
			if (move instanceof Promotion && move.getSource().equals(chessMove.getSource())
					&& move.getTarget().equals(chessMove.getTarget())) {
				validPromotions.add((Promotion) move);
			}
		}
	}

	/**
	 * Calculates the ratio for the evaluation bars based on the evaluation score.
	 *
	 * @param eval the evaluation score
	 * @return the ratio for the evaluation bars
	 */
	@Override
	public double getRatioEvalBars(double eval) {
		double ans = 0.5 + Math.atan(Math.tan(Math.PI / 10d) * eval) / Math.PI;
		return ans;
	}

	/**
	 * Converts the top and left pixel positions to chessboard file and rank.
	 *
	 * @param top  the top pixel position
	 * @param left the left pixel position
	 * @return an array containing the file and rank
	 */
	@Override
	public int[] fileRankFor(int top, int left) {
		int[] answer = new int[2];
		answer[0] = 8 - top / viewConfig.getSquareSize();
		answer[1] = 1 + (left - viewConfig.getLeftOffset()) / viewConfig.getSquareSize();
		return answer;
	}

	@Override
	public String getUnicodeSymbol(Piece piece) {
		if (piece == null) {
			return "";
		}
		switch (piece.getColor()) {
		case WHITE:
			switch (piece.getType()) {
			case KING:
				return "♔";
			case QUEEN:
				return "♕";
			case ROOK:
				return "♖";
			case BISHOP:
				return "♗";
			case KNIGHT:
				return "♘";
			case PAWN:
				return "♙";
			default:
				return "";
			}
		case BLACK:
			switch (piece.getType()) {
			case KING:
				return "♚";
			case QUEEN:
				return "♛";
			case ROOK:
				return "♜";
			case BISHOP:
				return "♝";
			case KNIGHT:
				return "♞";
			case PAWN:
				return "♟";
			default:
				return "";
			}
		default:
			return "";
		}
	}

	@Override
	public List<String> getMoveListWithSymbols() {
		List<String> moveListWithSymbols = new ArrayList<>();
		for (Move move : ((Game) get("chessGame")).getMoveList()) {
			String moveDescription = getUnicodeSymbol(move.getPiece()) + " " + move.toString();
			moveListWithSymbols.add(moveDescription);
		}
		return moveListWithSymbols;
	}

	/**
	 * Sends a reload signal to the client to reload the page. This is typically
	 * done using WebSocket or Server-Sent Events (SSE).
	 */
	@Override
	public void sendReloadSignal() {
		try {
			webSocketService.sendReloadSignal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Pair<Double, String>> removeDuplicatesByString(List<Pair<Double, String>> list) {
		Set<String> seenStrings = new LinkedHashSet<>();
		List<Pair<Double, String>> uniqueList = new ArrayList<>();

		for (Pair<Double, String> pair : list) {
			if (seenStrings.add(pair.getValue())) {
				uniqueList.add(pair);
			}
		}

		List<Pair<Double, String>> finalList = new ArrayList<>();
		finalList.addAll(uniqueList);

		for (Pair<Double, String> pair : uniqueList) {
			for (Pair<Double, String> otherPair : uniqueList) {
				if (pair.getRight().contains(otherPair.getRight()) && !pair.getRight().equals(otherPair.getRight())) {
					finalList.remove(otherPair);
				}
			}
		}

		return finalList;
	}

	@Override
	public List<String> getEvaluationEngineMoveList(EvaluationEngine evaluationEngine) throws Exception {

		List<Pair<Double, String>> uniqueList = new ArrayList<>();
		uniqueList.addAll(evaluationEngine.getBestLines((Game) get("chessGame"), (EngineConfig) get("engineConfigEval")));
		List<Pair<Double, String>> copyOfUciEngineMoveList = removeDuplicatesByString(uniqueList);

		List<String> answer = new ArrayList<>();

		// Holen Sie sich die besten Züge von UciEngine
		List<Pair<Double, String>> bestLines = new ArrayList<>(copyOfUciEngineMoveList);// engine.getBestLines(chessGame,
		// viewConfig.getUciEngineDepth());

		for (Pair<Double, String> line : bestLines) {
			answer.add(line.getLeft() + " : " + line.getRight()); // Bewertung : Zugsequenz
		}
		attributes.put("uciEngineMoveList", answer);
		return answer;
	}

	@Override
	public String reset() throws Exception {
		return "redirect:/";
	}

	private Object get(String s) {
		return attributes.get(s);
	}

}
