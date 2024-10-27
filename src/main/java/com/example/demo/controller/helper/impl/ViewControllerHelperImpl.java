package com.example.demo.controller.helper.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.example.demo.controller.helper.ViewControllerHelper;
import com.example.demo.elements.KEY;
import com.example.demo.model.DisplayedField;
import com.example.demo.model.DisplayedPiece;
import com.example.demo.model.impl.DisplayedChessField;
import com.example.demo.model.impl.DisplayedChessPiece;

import demo.chess.definitions.Color;
import demo.chess.definitions.engines.ChessEngine;
import demo.chess.definitions.engines.EngineConfig;
import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.definitions.engines.PlayerEngine;
import demo.chess.definitions.engines.UciEngineConfig;
import demo.chess.definitions.pieces.Piece;
import demo.chess.definitions.states.State;
import demo.chess.game.Game;
import demo.chess.save.GameSaver;

@Component
public class ViewControllerHelperImpl extends ChessHelper  implements ViewControllerHelper {

	protected static final Logger logger = LogManager.getLogger();

	/**
	 * Adds attributes to the model for rendering the chessboard view.
	 *
	 * @param color           the color theme for the chessboard
	 * @param whiteTimeString the time left for the white player in "MM:SS" format
	 * @param blackTimeString the time left for the black player in "MM:SS" format
	 * @param model           the model to add attributes to
	 */
	@Override
	public void addModelAttributes(String color, String whiteTimeString, String blackTimeString, Model model) {

		model.addAttribute("whiteTime", whiteTimeString);
		model.addAttribute("blackTime", blackTimeString);

		model.addAttribute("elements", (get(KEY.ELEMENTS)));
		model.addAttribute("fields", (get(KEY.FIELDS)));

		model.addAttribute("animationDuration", viewConfig.getAnimationDuration());
		model.addAttribute("topBarHeight", viewConfig.getTopBarHeight());

		model.addAttribute("chessboardOffsetX", viewConfig.getLeftOffset());
		model.addAttribute("chessboardOffsetY", viewConfig.getChessBoardOffset());
		model.addAttribute("squareSize", viewConfig.getSquareSize());
		model.addAttribute("color", color);
		model.addAttribute("moveListTop", viewConfig.getMoveListTop());
		model.addAttribute("moveListLeft", viewConfig.getMoveListLeft());
		model.addAttribute("moveListWidth", viewConfig.getMoveListWidth());

		model.addAttribute("uciEngineMoveListTop", viewConfig.getUciEngineMoveListTop());
		model.addAttribute("uciEngineMoveListLeft", viewConfig.getUciEngineMoveListLeft());
		model.addAttribute("uciEngineMoveListWidth", 800); // (((EngineConfig) get("engineConfigEval")).getDepth() + 1)
															// * 6 * 10);
		model.addAttribute("chessboardSize", 8 * viewConfig.getSquareSize());
		model.addAttribute("piecesOffset", viewConfig.getLeftOffset());
		model.addAttribute("clocksLeft", viewConfig.getLeftOffset());
		model.addAttribute("clocksTop", 8 * viewConfig.getSquareSize() + viewConfig.getChessBoardOffset());
		model.addAttribute("clockWidth", 8 * viewConfig.getSquareSize());
		model.addAttribute("evaluationEngine", get(KEY.EVALUATION_ENGINE));
		model.addAttribute("updateIntervall", viewConfig.getUpdateIntervall() * 1000);
		model.addAttribute("silent", viewConfig.isSilent());

		model.addAttribute("showEvaluation", viewConfig.isShowEvaluation());
		model.addAttribute("evalLeft", 8 * viewConfig.getSquareSize() + viewConfig.getLeftOffset());
		model.addAttribute("evalWidth", viewConfig.getEvalWidth());

		model.addAttribute("showUciEngineLines", viewConfig.isShowUciEngineLines());

		model.addAttribute("showArrows", viewConfig.isShowArrows());
		model.addAttribute("uciEngineActive", viewConfig.isUciEngineActive());

		double evaluation = (double) get(KEY.UCI_ENGINE_EVALUATION);
		model.addAttribute("uciEngineDepthForEvaluation", viewConfig.getUciEngineDepthForEvaluationEngine());
		model.addAttribute("stockFishEvaluation", getRatioEvalBars(evaluation));

		model.addAttribute("heightOfBlackEval", 8 * viewConfig.getSquareSize() * (1 - getRatioEvalBars(evaluation)));
		model.addAttribute("heightOfWhiteEval", 8 * viewConfig.getSquareSize());

		model.addAttribute("clockHeight", viewConfig.getClockSize());
		model.addAttribute("clockFontSize", viewConfig.getClockSize() / 2);

		setupEngineConfigurations();

	}

	/**
	 * Calculates the ratio for the evaluation bars based on the evaluation score.
	 *
	 * @param eval the evaluation score
	 * @return the ratio for the evaluation bars
	 */
	public double getRatioEvalBars(double eval) {
		double ans = 0.5 + Math.atan(Math.tan(Math.PI / 10d) * eval) / Math.PI;
		return ans;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void createNewFields() {
		if ((boolean) get(KEY.REGULAR)) {
			for (int file = 0; file < 8; file++) {
				for (int row = 0; row < 8; row++) {
					Color color = (row + file) % 2 == 0 ? Color.WHITE : Color.BLACK;
					((List<DisplayedField>) get(KEY.FIELDS))
							.add(new DisplayedChessField(color, viewConfig.getSquareSize(), viewConfig.getSquareSize(),
									viewConfig.getSquareSize() * row, viewConfig.getSquareSize() * file,
									((Game) get(KEY.CHESSGAME)).getChessBoard().getField(file + 1, 8 - row)));
				}
			}
		} else {
			for (int file = 0; file < 8; file++) {
				for (int row = 0; row < 8; row++) {
					Color color = (row + file) % 2 == 0 ? Color.WHITE : Color.BLACK;
					((List<DisplayedField>) get(KEY.FIELDS))
							.add(new DisplayedChessField(color, viewConfig.getSquareSize(), viewConfig.getSquareSize(),
									viewConfig.getSquareSize() * (7 - row), viewConfig.getSquareSize() * (7 - file),
									((Game) get(KEY.CHESSGAME)).getChessBoard().getField(file + 1, 8 - row)));
				}
			}
		}
	}

	@Override
	public void setUnsetViewVariables(EvaluationEngine evaluationEngine) {


		put(KEY.UCI_ENGINE_EVALUATION, 0.5d);
		put(KEY.REGULAR, !viewConfig.getIsFlipped());
		put(KEY.ENGINE_CONFIG_EVAL, new UciEngineConfig());
		put(KEY.ENGINE_CONFIG_FOR_WHITE, new UciEngineConfig());
		put(KEY.ENGINE_CONFIG_FOR_BLACK, new UciEngineConfig());

		int leftOffset = viewConfig.getLeftOffset();
		int squareSize = viewConfig.getSquareSize();
		int topBarHeight = viewConfig.getTopBarHeight();
		int moveListWidth = viewConfig.getMoveListWidth();

		int chessBoardOffset = topBarHeight;
		viewConfig.setChessBoardOffset(chessBoardOffset);

		int clockSize = squareSize / 2;
		viewConfig.setClockSize(clockSize);

		int evalWidth = squareSize / 2;
		viewConfig.setEvalWidth(evalWidth);

		int moveListLeft = leftOffset - moveListWidth - chessBoardOffset;
		viewConfig.setMoveListLeft(moveListLeft);

		int moveListTop = topBarHeight;
		viewConfig.setMoveListTop(moveListTop);

		int uciEngineMoveListLeft = 8 * squareSize + leftOffset + evalWidth + chessBoardOffset;
		viewConfig.setUciEngineMoveListLeft(uciEngineMoveListLeft);

		int captureContainerHeight = 4 * squareSize;

		((EngineConfig) get(KEY.ENGINE_CONFIG_EVAL)).setDepth(viewConfig.getUciEngineDepthForEvaluationEngine());

//		((List<DisplayedPiece>) get(KEY.ELEMENTS)).clear();
//		((List<DisplayedField>) get(KEY.FIELDS)).clear();
	}

	@Override
	public void setupEngineConfigurations() {
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)).setThreads(viewConfig.getThreadsForWhite());
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)).setContempt(viewConfig.getContemptForWhite());
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)).setDepth(viewConfig.getUciEngineDepthForWhite());
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)).setHashSize(viewConfig.getHashSizeForWhite());
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)).setMoveOverhead(viewConfig.getMoveOverheadForWhite());
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)).setUciElo(viewConfig.getUciEloForWhite());

		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)).setThreads(viewConfig.getThreadsForBlack());
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)).setContempt(viewConfig.getContemptForBlack());
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)).setDepth(viewConfig.getUciEngineDepthForBlack());
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)).setHashSize(viewConfig.getHashSizeForBlack());
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)).setMoveOverhead(viewConfig.getMoveOverheadForBlack());
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)).setUciElo(viewConfig.getUciEloForBlack());

		((EngineConfig) get(KEY.ENGINE_CONFIG_EVAL)).setMultiPV(viewConfig.getMultiPVForEvaluationEngine());
		((EngineConfig) get(KEY.ENGINE_CONFIG_EVAL)).setDepth(viewConfig.getUciEngineDepthForEvaluationEngine());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createNewPiecesFromExistingPieces(Game chessGame) {
		((List<DisplayedPiece>) get(KEY.ELEMENTS)).clear();
		List<DisplayedPiece> newElements = new ArrayList<>();
		if ((boolean) get(KEY.REGULAR)) {
			for (Piece piece : chessGame.getWhitePlayer().getPieces()) {
				newElements.add(new DisplayedChessPiece(getImagePath(piece.getColor(), piece.getType()),
						viewConfig.getSquareSize(), viewConfig.getSquareSize(),
						(8 - piece.getField().getRank()) * viewConfig.getSquareSize(),
						(piece.getField().getFile() - 1) * viewConfig.getSquareSize() + viewConfig.getLeftOffset(),
						piece));
			}
			for (Piece piece : chessGame.getBlackPlayer().getPieces()) {
				newElements.add(new DisplayedChessPiece(getImagePath(piece.getColor(), piece.getType()),
						viewConfig.getSquareSize(), viewConfig.getSquareSize(),
						(8 - piece.getField().getRank()) * viewConfig.getSquareSize(),
						(piece.getField().getFile() - 1) * viewConfig.getSquareSize() + viewConfig.getLeftOffset(),
						piece));
			}
		} else {
			for (Piece piece : chessGame.getWhitePlayer().getPieces()) {
				newElements.add(new DisplayedChessPiece(getImagePath(piece.getColor(), piece.getType()),
						viewConfig.getSquareSize(), viewConfig.getSquareSize(),
						(piece.getField().getRank() - 1) * viewConfig.getSquareSize(),
						(8 - piece.getField().getFile()) * viewConfig.getSquareSize() + viewConfig.getLeftOffset(),
						piece));
			}
			for (Piece piece : chessGame.getBlackPlayer().getPieces()) {
				newElements.add(new DisplayedChessPiece(getImagePath(piece.getColor(), piece.getType()),
						viewConfig.getSquareSize(), viewConfig.getSquareSize(),
						(piece.getField().getRank() - 1) * viewConfig.getSquareSize(),
						(8 - piece.getField().getFile()) * viewConfig.getSquareSize() + viewConfig.getLeftOffset(),
						piece));
			}
		}
		((List<DisplayedPiece>) get(KEY.ELEMENTS)).addAll(newElements);
	}

	@Override
	public void createShutdownHooks(Map<String, ? extends ChessEngine> engines) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			engines.entrySet().forEach(entry -> {
				logger.info("Shutting down evaluation engine {}", entry.getValue());
				entry.getValue().close();
			});
		}));
	}

	@Override
	public void seupClocks(Game chessGame) {
		chessGame.getWhitePlayer().setupClock(viewConfig.getTimeForEachPlayer(), viewConfig.getIncrementForWhite(),
				() -> {
					chessGame.setState(State.LOST_ON_TIME);
					if (!chessGame.getWhitePlayer().getChessClock().isStopped()) {
						chessGame.getWhitePlayer().getChessClock().stop();
					}
					if (!chessGame.getBlackPlayer().getChessClock().isStopped()) {
						chessGame.getBlackPlayer().getChessClock().stop();
					}
					webSocketService.sendMessage("White lost on time!");
				});

		chessGame.getBlackPlayer().setupClock(viewConfig.getTimeForEachPlayer(), viewConfig.getIncrementForBlack(),
				() -> {
					chessGame.setState(State.LOST_ON_TIME);
					if (!chessGame.getWhitePlayer().getChessClock().isStopped()) {
						chessGame.getWhitePlayer().getChessClock().stop();
					}
					if (!chessGame.getBlackPlayer().getChessClock().isStopped()) {
						chessGame.getBlackPlayer().getChessClock().stop();
					}
					webSocketService.sendMessage("Black lost on time!");
				});
	} 

	@Override
	public void saveGame(String path, Game chessGame) throws IOException {
		GameSaver saver = new GameSaver();
		saver.saveGame(chessGame.getMoveList(), path);
	}

	@Override
	public void updateUciEngineSettings(int uciEngineDepthForWhite, int threadsForWhite, int hashSizeForWhite,
			int contemptForWhite, int moveOverheadForWhite, int uciEloForWhite, int uciEngineDepthForBlack,
			int threadsForBlack, int hashSizeForBlack, int contemptForBlack, int moveOverheadForBlack,
			int uciEloForBlack, String selectedEngineForWhite, String selectedEngineForBlack, Map<String, PlayerEngine> playerEngines) {

		
		put(KEY.PLAYER_ENGINE_FOR_WHITE, playerEngines.get(selectedEngineForWhite));
		viewConfig.setPlayerEngineForWhite(selectedEngineForWhite);

		viewConfig.setThreadsForWhite(threadsForWhite);
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)).setThreads(threadsForWhite);

		viewConfig.setHashSizeForWhite(hashSizeForWhite);
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)).setHashSize(hashSizeForWhite);

		viewConfig.setContemptForWhite(contemptForWhite);
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)).setContempt(contemptForWhite);

		viewConfig.setUciEloForWhite(uciEloForWhite);
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)).setUciElo(uciEloForWhite);

		viewConfig.setMoveOverheadForWhite(moveOverheadForWhite);
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)).setMoveOverhead(moveOverheadForWhite);

		viewConfig.setUciEngineDepthForWhite(uciEngineDepthForWhite);
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE)).setDepth(uciEngineDepthForWhite);


		put(KEY.PLAYER_ENGINE_FOR_BLACK, playerEngines.get(selectedEngineForBlack));
		viewConfig.setPlayerEngineForBlack(selectedEngineForBlack);

		viewConfig.setThreadsForBlack(threadsForBlack);
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)).setThreads(threadsForBlack);

		viewConfig.setHashSizeForBlack(hashSizeForBlack);
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)).setHashSize(hashSizeForBlack);

		viewConfig.setContemptForBlack(contemptForBlack);
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)).setContempt(contemptForBlack);

		viewConfig.setMoveOverheadForBlack(moveOverheadForBlack);
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)).setMoveOverhead(moveOverheadForBlack);

		viewConfig.setUciEloForBlack(uciEloForBlack);
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)).setUciElo(uciEloForBlack);

		viewConfig.setUciEngineDepthForBlack(uciEngineDepthForBlack);
		((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK)).setDepth(uciEngineDepthForBlack);

		
	}


}
