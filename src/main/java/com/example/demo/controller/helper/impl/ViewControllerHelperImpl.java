package com.example.demo.controller.helper.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.tuple.Pair;
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

/**
 * The `ViewControllerHelperImpl` class implements the `ViewControllerHelper`
 * interface, offering methods to set up and manage the visual representation
 * and configuration of a chess game within the application. This component is
 * responsible for synchronizing the frontend model with backend game data,
 * allowing efficient updates to the user interface.
 *
 * Key functionalities provided by this helper class include: - Configuring the
 * chessboard and related UI elements (like evaluation bars, clocks, and move
 * lists). - Managing engine configurations for players and evaluation engines.
 * - Creating and displaying pieces and fields based on the game state and user
 * settings. - Setting up shutdown hooks for engines to ensure proper resource
 * management. - Implementing and managing clocks with increment options and
 * game-over messages. - Saving and loading games to/from external storage.
 *
 * This helper utilizes the `ChessHelper` superclass for common chess
 * operations, and integrates settings from the `viewConfig` to control various
 * UI parameters. It also interacts with the WebSocket service for real-time UI
 * updates.
 */
@Component
public class ViewControllerHelperImpl extends ChessHelper implements ViewControllerHelper {

	/** Logger instance for capturing error details related to this exception. */
	protected static final Logger logger = LogManager.getLogger();

	@Override
	public void addModelAttributes(String color, String whiteTimeString, String blackTimeString, Model model, Map<String, EvaluationEngine> evaluationEngines) {

		model.addAttribute("evaluationEngines", evaluationEngines);
		model.addAttribute("showChart", get(KEY.SHOW_CHART));
		model.addAttribute("whiteTime", whiteTimeString);
		model.addAttribute("blackTime", blackTimeString);

		model.addAttribute("elements", (get(KEY.ELEMENTS)));
		model.addAttribute("fields", (get(KEY.FIELDS)));

		model.addAttribute("uciThreadsForEvaluationEngine", viewConfig.getThreadsForEvaluationEngine());
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

	@Override
	public void setUnsetViewVariables(EvaluationEngine evaluationEngine) {

		put(KEY.UCI_ENGINE_EVALUATION, 0.5d);
		put(KEY.REGULAR, !viewConfig.getIsFlipped());
		if (get(KEY.ENGINE_CONFIG_EVAL) == null) {
			put(KEY.ENGINE_CONFIG_EVAL, new UciEngineConfig());
		}
		if (get(KEY.ENGINE_CONFIG_FOR_WHITE) == null) {
			put(KEY.ENGINE_CONFIG_FOR_WHITE, new UciEngineConfig());
		}
		if (get(KEY.ENGINE_CONFIG_FOR_BLACK) == null) {
			put(KEY.ENGINE_CONFIG_FOR_BLACK, new UciEngineConfig());
		}

		int leftOffset = viewConfig.getLeftOffset();
		int squareSize = viewConfig.getSquareSize();
		int topBarHeight = viewConfig.getTopBarHeight();

		int chessBoardOffset = topBarHeight;
		viewConfig.setChessBoardOffset(chessBoardOffset);

		int clockSize = squareSize / 2;
		viewConfig.setClockSize(clockSize);

		int evalWidth = 20;
		viewConfig.setEvalWidth(evalWidth);

		int moveListLeft = 0; // leftOffset - moveListWidth - chessBoardOffset;
		viewConfig.setMoveListLeft(moveListLeft);

		int moveListTop = topBarHeight;
		viewConfig.setMoveListTop(moveListTop);

		viewConfig.setUciEngineMoveListTop(moveListTop);

		int uciEngineMoveListLeft = 8 * squareSize + leftOffset + evalWidth + chessBoardOffset;
		viewConfig.setUciEngineMoveListLeft(uciEngineMoveListLeft);

		((EngineConfig) get(KEY.ENGINE_CONFIG_EVAL)).setDepth(viewConfig.getUciEngineDepthForEvaluationEngine());

	}

	@Override
	public void setupEngineConfigurations() {

		EngineConfig engineConfigForWhite = ((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE));
		engineConfigForWhite.setThreads(viewConfig.getThreadsForWhite());
		engineConfigForWhite.setContempt(viewConfig.getContemptForWhite());
		engineConfigForWhite.setDepth(viewConfig.getUciEngineDepthForWhite());
		engineConfigForWhite.setHashSize(viewConfig.getHashSizeForWhite());
		engineConfigForWhite.setMoveOverhead(viewConfig.getMoveOverheadForWhite());
		engineConfigForWhite.setUciElo(viewConfig.getUciEloForWhite());

		EngineConfig engineConfigForBlack = ((EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK));
		engineConfigForBlack.setThreads(viewConfig.getThreadsForBlack());
		engineConfigForBlack.setContempt(viewConfig.getContemptForBlack());
		engineConfigForBlack.setDepth(viewConfig.getUciEngineDepthForBlack());
		engineConfigForBlack.setHashSize(viewConfig.getHashSizeForBlack());
		engineConfigForBlack.setMoveOverhead(viewConfig.getMoveOverheadForBlack());
		engineConfigForBlack.setUciElo(viewConfig.getUciEloForBlack());

		((EngineConfig) get(KEY.ENGINE_CONFIG_EVAL)).setMultiPV(viewConfig.getMultiPVForEvaluationEngine());
		((EngineConfig) get(KEY.ENGINE_CONFIG_EVAL)).setThreads(viewConfig.getThreadsForEvaluationEngine());
		((EngineConfig) get(KEY.ENGINE_CONFIG_EVAL)).setDepth(viewConfig.getUciEngineDepthForEvaluationEngine());
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
				String type = entry.getValue() instanceof EvaluationEngine ? "evaluation" : "player";
				if (entry.getValue() instanceof EvaluationEngine) {
					logger.info("Shutting down {} engine {}", type, entry.getValue());
				}
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
			int uciEloForBlack, String selectedEngineForWhite, String selectedEngineForBlack,
			Map<String, PlayerEngine> playerEngines) {

		put(KEY.PLAYER_ENGINE_FOR_WHITE, playerEngines.get(selectedEngineForWhite));
		viewConfig.setPlayerEngineForWhite(selectedEngineForWhite);

		EngineConfig configForWhite = (EngineConfig) get(KEY.ENGINE_CONFIG_FOR_WHITE);
		EngineConfig configForBlack = (EngineConfig) get(KEY.ENGINE_CONFIG_FOR_BLACK);

		viewConfig.setThreadsForWhite(threadsForWhite);
		configForWhite.setThreads(threadsForWhite);

		viewConfig.setHashSizeForWhite(hashSizeForWhite);
		configForWhite.setHashSize(hashSizeForWhite);

		viewConfig.setContemptForWhite(contemptForWhite);
		configForWhite.setContempt(contemptForWhite);

		viewConfig.setUciEloForWhite(uciEloForWhite);
		configForWhite.setUciElo(uciEloForWhite);

		viewConfig.setMoveOverheadForWhite(moveOverheadForWhite);
		configForWhite.setMoveOverhead(moveOverheadForWhite);

		viewConfig.setUciEngineDepthForWhite(uciEngineDepthForWhite);
		configForWhite.setDepth(uciEngineDepthForWhite);

		put(KEY.PLAYER_ENGINE_FOR_BLACK, playerEngines.get(selectedEngineForBlack));
		viewConfig.setPlayerEngineForBlack(selectedEngineForBlack);

		viewConfig.setThreadsForBlack(threadsForBlack);
		configForBlack.setThreads(threadsForBlack);

		viewConfig.setHashSizeForBlack(hashSizeForBlack);
		configForBlack.setHashSize(hashSizeForBlack);

		viewConfig.setContemptForBlack(contemptForBlack);
		configForBlack.setContempt(contemptForBlack);

		viewConfig.setMoveOverheadForBlack(moveOverheadForBlack);
		configForBlack.setMoveOverhead(moveOverheadForBlack);

		viewConfig.setUciEloForBlack(uciEloForBlack);
		configForBlack.setUciElo(uciEloForBlack);

		viewConfig.setUciEngineDepthForBlack(uciEngineDepthForBlack);
		configForBlack.setDepth(uciEngineDepthForBlack);

		put(KEY.ENGINE_CONFIG_FOR_WHITE, configForWhite);
		put(KEY.ENGINE_CONFIG_FOR_BLACK, configForBlack);

	}

	@Override
	public void downloadGameAnalysis() {
	    try {
	        @SuppressWarnings("unchecked")
	        Map<String, List<Pair<Pair<Double, Integer>, String>>> engineLines =
	                (Map<String, List<Pair<Pair<Double, Integer>, String>>>) get(KEY.ENGINE_ANALYSIS);

	        // Map nach den Schlüsseln sortieren
	        var sortedKeys = engineLines.keySet().stream()
	                .sorted(Comparator.reverseOrder())
	                .toList();

	        var documentFactory = DocumentBuilderFactory.newInstance();
	        var documentBuilder = documentFactory.newDocumentBuilder();
	        var document = documentBuilder.newDocument();

	        var root = document.createElement("gameAnalysis");
	        document.appendChild(root);

	        for (String position : sortedKeys) {
	            List<Pair<Pair<Double, Integer>, String>> evaluations = engineLines.get(position);

	            Collections.sort(evaluations, Comparator.comparingInt(evaluation -> evaluation.getValue().length()));

	            String lastMove = position.trim().substring(position.lastIndexOf(" ") + 1).split("]")[0];

	            var positionTag = document.createElement("position");
	            positionTag.setAttribute("position", position);
	            positionTag.setAttribute("move", lastMove);
	            root.appendChild(positionTag);

	            for (Pair<Pair<Double, Integer>, String> evaluation : evaluations) {
	                Pair<Double, Integer> evalData = evaluation.getKey();
	                String line = evaluation.getValue();

	                var moveTag = document.createElement("variant");
	                moveTag.setAttribute("evaluation", String.valueOf(evalData.getLeft()));
	                moveTag.setAttribute("depth", String.valueOf(evalData.getRight()));
	                moveTag.setAttribute("line", line);

	                positionTag.appendChild(moveTag);
	            }
	        }

	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        DOMSource domSource = new DOMSource(document);
	        StreamResult streamResult = new StreamResult(new File("test.xml"));

	        transformer.transform(domSource, streamResult);

	        System.out.println("Die Datei wurde in test.xml gespeichert.");
	    } catch (Exception e) {
	        logger.info(e);
	    }
	}
}
