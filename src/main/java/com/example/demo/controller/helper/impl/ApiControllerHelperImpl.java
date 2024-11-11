package com.example.demo.controller.helper.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.example.demo.AppAdmin;
import com.example.demo.controller.helper.ApiControllerHelper;
import com.example.demo.controller.impl.ChessApiController;
import com.example.demo.elements.KEY;

import demo.chess.admin.Admin;
import demo.chess.definitions.engines.EngineConfig;
import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.definitions.engines.impl.NoMoveFoundException;
import demo.chess.definitions.fields.Field;
import demo.chess.definitions.moves.Move;
import demo.chess.definitions.moves.Promotion;
import demo.chess.definitions.pieces.Piece;
import demo.chess.definitions.states.State;
import demo.chess.game.Game;

/**
 * The `ApiControllerHelperImpl` class implements the `ApiControllerHelper`
 * interface, providing utility methods to facilitate interaction with the chess
 * game logic in the `ChessApiController`. This implementation enables
 * controller operations for handling moves, board state, evaluation results,
 * and user interface interactions.
 *
 * Core functionalities include: - Retrieving possible moves and fields for a
 * selected piece. - Managing promotion move options for pawns. - Converting
 * chess moves into symbolic representation for display. - Providing
 * calculations for evaluation bars based on engine evaluations. - Communicating
 * game state updates and reset signals through WebSocket messages. - Filtering
 * duplicate engine suggestions and preparing move lists for evaluation. -
 * Checking game states (e.g., checkmate, stalemate) and sending corresponding
 * messages. - Verifying if human interaction is allowed based on current player
 * status.
 *
 * This helper class operates as a Spring component, allowing it to be injected
 * where needed and providing easy access to shared game configuration and
 * helper methods from the `ChessHelper` superclass.
 */
@Component
public class ApiControllerHelperImpl extends ChessHelper implements ApiControllerHelper {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(ChessApiController.class);

	@Override
	public List<Field> getSourceFields() throws NoMoveFoundException, IOException {
		return getPossibleMoves().stream().map(Move::getSource).distinct().toList();
	}

	@Override
	public List<Field> getTargetFields() throws NoMoveFoundException, IOException {
		return getPossibleMoves().stream().map(Move::getTarget).distinct().toList();
	}

	@Override
	public List<Move> getPossibleMoves() throws NoMoveFoundException, IOException {
		Game chessGame = (Game) get(KEY.CHESSGAME);
		if (chessGame.getState() == null) {
			return chessGame.getPlayer().getValidMoves(chessGame);
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

	@Override
	public double getRatioEvalBars(double eval) {
		double ans = 0.5 + Math.atan(Math.tan(Math.PI / 10d) * eval) / Math.PI;
		return ans;
	}

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
		for (Move move : ((Game) get(KEY.CHESSGAME)).getMoveList()) {
			String moveDescription = getUnicodeSymbol(move.getPiece()) + " " + move.toString();
			moveListWithSymbols.add(moveDescription);
		}
		return moveListWithSymbols;
	}

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
		uniqueList.addAll(
				evaluationEngine.getBestLines((Game) get(KEY.CHESSGAME), (EngineConfig) get(KEY.ENGINE_CONFIG_EVAL)));
		List<Pair<Double, String>> copyOfUciEngineMoveList = removeDuplicatesByString(uniqueList);

		List<String> answer = new ArrayList<>();

		// Holen Sie sich die besten Züge von UciEngine
		List<Pair<Double, String>> bestLines = new ArrayList<>(copyOfUciEngineMoveList);// engine.getBestLines(chessGame,
		// viewConfig.getUciEngineDepth());

		for (Pair<Double, String> line : bestLines) {
			answer.add(line.getLeft() + " : " + line.getRight()); // Bewertung : Zugsequenz
		}
		attributes.put(KEY.UCI_ENGINE_MOVELIST, answer);
		return answer;
	}

	@Override
	public boolean checkForGameState(Game chessGame, EvaluationEngine evaluationEngine) throws Exception {
		if (chessGame.getState() != null) {
			if (evaluationEngine != null) {
				evaluationEngine.stopEvaluation();
			}
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
			if (message.isEmpty() && chessGame.getState() != null) {
				message = chessGame.getState().getLabel();
			}
			webSocketService.sendMessage(message);
			return false;
		}
		return true;
	}

	@Override
	public String createToolTipForConfig(EngineConfig config) {
		String tooltip = "Depth: " + config.getDepth() + "\n" + "Contempt: " + config.getContempt() + "\n"
				+ "Hashsize: " + config.getHashSize() + "\n" + "Threads: " + config.getThreads() + "\n" + "UCI Elo: "
				+ config.getUciElo() + "\n" + "MultiPV: " + config.getMultiPV();
		return tooltip;
	}

	@Override
	public String reset() throws Exception {
		return "redirect:/";
	}

	@Override
	public boolean isHumanAlowedToInteract(Game chessGame, boolean uciEngineActive) {
		boolean humanPlaysWhite = !viewConfig.getIsFlipped() ? true : false;
		if (uciEngineActive && (humanPlaysWhite && chessGame.getMoveList().size() % 2 == 1
				|| !humanPlaysWhite && chessGame.getMoveList().size() % 2 == 0)) {
			return false;
		}
		return true;
	}

	@Override
	public List<String> convertToSan(List<String> evalMoveList, Admin admin) throws Exception {
		List<Move> chessGameMoveList = ((Game) get(KEY.CHESSGAME)).getMoveList();
		List<String> sanMoveList = new ArrayList<>();
		Game tmpGame;
		Move moveToExecute;
		try {
			for (String moves : evalMoveList) {
				String[] movesAsArray = moves.split(" ");
				String prefix = movesAsArray[0] + " " + movesAsArray[1];
				tmpGame = ((AppAdmin)admin).dummyChessGame();
				for (Move move : chessGameMoveList) {
					moveToExecute = null;
					for (Move tmpMove : tmpGame.getPlayer().getValidMoves(tmpGame)) {
						if (tmpMove.toString().equals(move.toString())) {
							moveToExecute = tmpMove;
							break;
						}
					}
					tmpGame.apply(moveToExecute);
				}
				for (int i = 2; i < movesAsArray.length; i++) {
					moveToExecute = null;
					for (Move tmpMove : tmpGame.getPlayer().getValidMoves(tmpGame)) {
						if (tmpMove.toString().equals(movesAsArray[i])) {
							moveToExecute = tmpMove;
							break;
						}
					}
					tmpGame.apply(moveToExecute);
				}
				String answer = "";
				for (int i = chessGameMoveList.size(); i < tmpGame.getSanMoveList().size(); i++) {
					answer += tmpGame.getSanMoveList().get(i) + " ";
				}
				sanMoveList.add(prefix + answer);
			}
		} catch (java.util.ConcurrentModificationException e) {
			logger.debug("Abortet movelist transformation due to comodification");
		}
		return sanMoveList;
	}
}
