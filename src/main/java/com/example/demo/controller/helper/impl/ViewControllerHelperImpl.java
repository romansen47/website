package com.example.demo.controller.helper.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.example.demo.controller.helper.ViewControllerHelper;
import com.example.demo.elements.Attributes;
import com.example.demo.model.Config;
import com.example.demo.model.DisplayedField;
import com.example.demo.model.impl.DisplayedChessField;

import demo.chess.definitions.Color;
import demo.chess.definitions.engines.EngineConfig;
import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.game.Game;

@Component
public class ViewControllerHelperImpl implements ViewControllerHelper {

	@Autowired
	protected Config viewConfig;

	@Autowired
	protected Attributes attributes;

	/**
	 * Adds attributes to the model for rendering the chessboard view.
	 *
	 * @param color           the color theme for the chessboard
	 * @param whiteTimeString the time left for the white player in "MM:SS" format
	 * @param blackTimeString the time left for the black player in "MM:SS" format
	 * @param model           the model to add attributes to
	 */
	@Override
	public void addAttributes(String color, String whiteTimeString, String blackTimeString, Model model) {

		model.addAttribute("whiteTime", whiteTimeString);
		model.addAttribute("blackTime", blackTimeString);

		model.addAttribute("fields", (get("fields")));
		model.addAttribute("elements", (get("elements")));

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
		model.addAttribute("capturedTop", viewConfig.getCapturedPiecesTop());
		model.addAttribute("capturedLeft", viewConfig.getCapturedPiecesLeft());
		model.addAttribute("capturedWidth", viewConfig.getCapturedContainerWidth());
		model.addAttribute("capturedHeight", viewConfig.getCapturedContainerHeight());
		model.addAttribute("piecesOffset", viewConfig.getLeftOffset());
		model.addAttribute("clocksLeft", viewConfig.getLeftOffset());
		model.addAttribute("clocksTop", 8 * viewConfig.getSquareSize() + viewConfig.getChessBoardOffset());
		model.addAttribute("clockWidth", 8 * viewConfig.getSquareSize());
		model.addAttribute("evaluationEngine", get("evaluationEngine"));
		model.addAttribute("updateIntervall", viewConfig.getUpdateIntervall() * 1000);
		model.addAttribute("silent", viewConfig.isSilent());

		model.addAttribute("showEvaluation", viewConfig.isShowEvaluation());
		model.addAttribute("evalLeft", 8 * viewConfig.getSquareSize() + viewConfig.getLeftOffset());
		model.addAttribute("evalWidth", viewConfig.getEvalWidth());

		model.addAttribute("showUciEngineLines", viewConfig.isShowUciEngineLines());

		model.addAttribute("showArrows", viewConfig.isShowArrows());
		model.addAttribute("capturedContainer", viewConfig.isCapturedContainer());
		model.addAttribute("uciEngineActive", viewConfig.isUciEngineActive());

		double evaluation = (double) get("uciEngineEvaluation");
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
		if ((boolean) get("regular")) {
			for (int file = 0; file < 8; file++) {
				for (int row = 0; row < 8; row++) {
					Color color = (row + file) % 2 == 0 ? Color.WHITE : Color.BLACK;
					((List<DisplayedField>) get("fields"))
							.add(new DisplayedChessField(color, viewConfig.getSquareSize(), viewConfig.getSquareSize(),
									viewConfig.getSquareSize() * row, viewConfig.getSquareSize() * file,
									((Game) get("chessGame")).getChessBoard().getField(file + 1, 8 - row)));
				}
			}
		} else {
			for (int file = 0; file < 8; file++) {
				for (int row = 0; row < 8; row++) {
					Color color = (row + file) % 2 == 0 ? Color.WHITE : Color.BLACK;
					((List<DisplayedField>) get("fields"))
							.add(new DisplayedChessField(color, viewConfig.getSquareSize(), viewConfig.getSquareSize(),
									viewConfig.getSquareSize() * (7 - row), viewConfig.getSquareSize() * (7 - file),
									((Game) get("chessGame")).getChessBoard().getField(file + 1, 8 - row)));
				}
			}
		}
	}

	@Override
	public void setUnsetViewVariables(EvaluationEngine evaluationEngine) {

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

		int uciEngineMoveListTop = topBarHeight;
		viewConfig.setUciEngineMoveListTop(uciEngineMoveListTop);

		int captureContainerLeft = leftOffset - 2;
		viewConfig.setCapturedPiecesLeft(captureContainerLeft);

		int captureContainerTop = topBarHeight + 8 * squareSize + clockSize + chessBoardOffset;
		viewConfig.setCapturedPiecesTop(captureContainerTop);

		int captureContainerWidth = 8 * squareSize + 4;
		viewConfig.setCapturedContainerWidth(captureContainerWidth);

		int captureContainerHeight = 4 * squareSize;
		viewConfig.setCapturedContainerHeight(captureContainerHeight);

		((EngineConfig) get("engineConfigEval")).setDepth(viewConfig.getUciEngineDepthForEvaluationEngine());
	}

	private Object get(String s) {
		return attributes.get(s);
	}

	@Override
	public void setupEngineConfigurations() {
		((EngineConfig) get("engineConfigForWhite")).setThreads(viewConfig.getThreadsForWhite());
		((EngineConfig) get("engineConfigForWhite")).setContempt(viewConfig.getContemptForWhite());
		((EngineConfig) get("engineConfigForWhite")).setDepth(viewConfig.getUciEngineDepthForWhite());
		((EngineConfig) get("engineConfigForWhite")).setHashSize(viewConfig.getHashSizeForWhite());
		((EngineConfig) get("engineConfigForWhite")).setMoveOverhead(viewConfig.getMoveOverheadForWhite());
		((EngineConfig) get("engineConfigForWhite")).setUciElo(viewConfig.getUciEloForWhite());

		((EngineConfig) get("engineConfigForBlack")).setThreads(viewConfig.getThreadsForBlack());
		((EngineConfig) get("engineConfigForBlack")).setContempt(viewConfig.getContemptForBlack());
		((EngineConfig) get("engineConfigForBlack")).setDepth(viewConfig.getUciEngineDepthForBlack());
		((EngineConfig) get("engineConfigForBlack")).setHashSize(viewConfig.getHashSizeForBlack());
		((EngineConfig) get("engineConfigForBlack")).setMoveOverhead(viewConfig.getMoveOverheadForBlack());
		((EngineConfig) get("engineConfigForBlack")).setUciElo(viewConfig.getUciEloForBlack());

		((EngineConfig) get("engineConfigEval")).setMultiPV(viewConfig.getMultiPVForEvaluationEngine());
		((EngineConfig) get("engineConfigEval")).setDepth(viewConfig.getUciEngineDepthForEvaluationEngine());
	}

}
