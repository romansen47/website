package com.example.demo.model.impl;

import com.example.demo.model.Config;

import demo.chess.definitions.Color;
import demo.chess.definitions.engines.Engine;

/**
 * Implementation of the {@link com.example.demo.model.Config} interface
 * representing the view configuration settings.
 */
public class ViewConfig implements Config {

	/** Default color theme for the chessboard. */
	private Color color = Color.GREEN;

	/** Indicates whether arrows should be shown for moves on the board. */
	private boolean showArrows = false;

	/** Indicates whether evaluation scores should be displayed on the board. */
	private boolean showEvaluation = false;

	/**
	 * Indicates whether lines from the UCI engine's suggestions should be shown.
	 */
	private boolean showUciEngineLines = false;

	/** If true, disables sound effects in the application. */
	private boolean silent = false;

	/** Determines if the board displays moves using short algebraic notation. */
	private boolean shortAlgebraicNotation = true;

	/** Activates or deactivates the UCI engine in the application. */
	private boolean uciEngineActive = true;

	/** The total time allocated for each player in seconds. */
	private int timeForEachPlayer = 300;

	/** Increment in seconds added to the white player’s clock after each move. */
	private int incrementForWhite = 2;

	/** Increment in seconds added to the black player’s clock after each move. */
	private int incrementForBlack = 2;

	/** Additional time in seconds given to each player at the start of the game. */
	private int additionalTime = 0;

	/** Interval in seconds at which the UCI engine updates its evaluation. */
	private int updateIntervall = 2;

	/** Duration in milliseconds for animations, such as piece movements. */
	private int animationDuration = 500;

	/** Specifies the name of the engine used for evaluation purposes. */
	private String evaluationEngine = Engine.STOCKFISH_16.toString();

	/** Specifies the UCI engine for the white player. */
	private String playerEngineForWhite = Engine.STOCKFISH_16.toString();

	/** Specifies the UCI engine for the black player. */
	private String playerEngineForBlack = Engine.STOCKFISH_16.toString();

	/** Depth setting for the evaluation engine’s analysis. */
	private int uciEngineDepthForEvaluationEngine = 1;

	/**
	 * MultiPV setting for the evaluation engine, determining how many top moves to
	 * analyze.
	 */
	private int multiPVForEvaluationEngine = 3;

	/** Depth setting for the UCI engine’s analysis for the white player. */
	private int uciEngineDepthForWhite = 0;

	/** Depth setting for the UCI engine’s analysis for the black player. */
	private int uciEngineDepthForBlack = 0;

	/** Number of threads used by the white player's engine. */
	private int threadsForWhite = 8;

	/** Hash size in MB allocated for the white player's engine. */
	private int hashSizeForWhite = 1024;

	/**
	 * Contempt factor for the white player's engine, influencing move selection.
	 */
	private int contemptForWhite = 99;

	/** Milliseconds added as move overhead for the white player. */
	private int moveOverheadForWhite = 0;

	/** Elo rating setting for the white player's UCI engine. */
	private int uciEloForWhite = 0;

	/** Number of threads used by the black player's engine. */
	private int threadsForBlack = 8;

	/** Hash size in MB allocated for the black player's engine. */
	private int hashSizeForBlack = 1024;

	/**
	 * Contempt factor for the black player's engine, influencing move selection.
	 */
	private int contemptForBlack = 99;

	/** Milliseconds added as move overhead for the black player. */
	private int moveOverheadForBlack = 0;

	/** Elo rating setting for the black player's UCI engine. */
	private int uciEloForBlack = 0;

	/** Left offset in pixels for positioning the chessboard on the screen. */
	private int leftOffset = 275;

	/** Size of each square on the chessboard in pixels. */
	private int squareSize = 75;

	/** Height of the top bar in pixels. */
	private int topBarHeight = 40;

	/** Width of the move list display area in pixels. */
	private int moveListWidth = 250;

	/** Width of the evaluation display area in pixels. */
	private int evalWidth;

	/** Vertical offset in pixels for positioning the chessboard. */
	private int chessBoardOffset;

	/** Vertical position in pixels for the UCI engine's move list. */
	private int uciEngineMoveListTop;

	/** Horizontal position in pixels for the UCI engine's move list. */
	private int uciEngineMoveListLeft;

	/** Size of the clocks displayed on the screen in pixels. */
	private int clockSize;

	/** Vertical position in pixels for the move list display. */
	private int moveListTop;

	/** Horizontal position in pixels for the move list display. */
	private int moveListLeft;

	/** If true, the board is flipped so the black side is at the bottom. */
	private boolean isFlipped = false;

	/**
	 * @param evalWidth the evalWidth to set
	 */
	@Override
	public void setEvalWidth(int evalWidth) {
		this.evalWidth = evalWidth;
	}

	@Override
	public boolean isSilent() {
		return silent;
	}

	@Override
	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public boolean isUciEngineActive() {
		return uciEngineActive;
	}

	@Override
	public void setUciEngineActive(boolean isUciEngineActive) {
		this.uciEngineActive = isUciEngineActive;
	}

	@Override
	public int getTimeForEachPlayer() {
		return timeForEachPlayer;
	}

	@Override
	public void setTimeForEachPlayer(int timeForEachPlayer) {
		this.timeForEachPlayer = timeForEachPlayer;
	}

	@Override
	public int getAdditionalTime() {
		return additionalTime;
	}

	@Override
	public void setAdditionalTime(int additionalTime) {
		this.additionalTime = additionalTime;
	}

	@Override
	public int getLeftOffset() {
		return leftOffset;
	}

	@Override
	public void setLeftOffset(int leftOffset) {
		this.leftOffset = leftOffset;
	}

	@Override
	public int getChessBoardOffset() {
		return chessBoardOffset;
	}

	@Override
	public void setChessBoardOffset(int chessBoardOffset) {
		this.chessBoardOffset = chessBoardOffset;
	}

	@Override
	public int getTopBarHeight() {
		return topBarHeight;
	}

	@Override
	public void setTopBarHeight(int topBarHight) {
		this.topBarHeight = topBarHight;
	}

	@Override
	public int getSquareSize() {
		return squareSize;
	}

	@Override
	public void setSquareSize(int squareSize) {
		this.squareSize = squareSize;
	}

	@Override
	public int getMoveListTop() {
		return moveListTop;
	}

	@Override
	public void setMoveListTop(int moveListTop) {
		this.moveListTop = moveListTop;
	}

	@Override
	public int getMoveListLeft() {
		return moveListLeft;
	}

	@Override
	public void setMoveListLeft(int moveListLeft) {
		this.moveListLeft = moveListLeft;
	}

	@Override
	public String getEvaluationEngine() {
		return evaluationEngine;
	}

	@Override
	public void setEvaluationEngine(String evaluationEngine) {
		this.evaluationEngine = evaluationEngine;
	}

	@Override
	public String getPlayerEngineForWhite() {
		return playerEngineForWhite;
	}

	@Override
	public void setPlayerEngineForWhite(String playerEngineForWhite) {
		this.playerEngineForWhite = playerEngineForWhite;
	}

	@Override
	public String getPlayerEngineForBlack() {
		return playerEngineForBlack;
	}

	@Override
	public void setPlayerEngineForBlack(String playerEngineForBlack) {
		this.playerEngineForBlack = playerEngineForBlack;
	}

	/**
	 * @return the uciEngineMoveListLeft
	 */
	@Override
	public int getUciEngineMoveListLeft() {
		return uciEngineMoveListLeft;
	}

	/**
	 * @param uciEngineMoveListLeft the uciEngineMoveListLeft to set
	 */
	@Override
	public void setUciEngineMoveListLeft(int uciEngineMoveListLeft) {
		this.uciEngineMoveListLeft = uciEngineMoveListLeft;
	}

	/**
	 * @return the uciEngineMoveListTop
	 */
	@Override
	public int getUciEngineMoveListTop() {
		return uciEngineMoveListTop;
	}

	/**
	 * @param uciEngineMoveListTop the uciEngineMoveListTop to set
	 */
	@Override
	public void setUciEngineMoveListTop(int uciEngineMoveListTop) {
		this.uciEngineMoveListTop = uciEngineMoveListTop;
	}

	@Override
	public int getEvalWidth() {
		return evalWidth;
	}

	/**
	 * @return the moveListWidth
	 */
	@Override
	public int getMoveListWidth() {
		return moveListWidth;
	}

	/**
	 * @param moveListWidth the moveListWidth to set
	 */
	@Override
	public void setMoveListWidth(int moveListWidth) {
		this.moveListWidth = moveListWidth;
	}

	@Override
	public int getClockSize() {
		return clockSize;
	}

	@Override
	public void setClockSize(int clockSize) {
		this.clockSize = clockSize;
	}

	/**
	 * @return the showArrows
	 */
	@Override
	public boolean isShowArrows() {
		return showArrows;
	}

	/**
	 * @param showArrows the showArrows to set
	 */
	@Override
	public void setShowArrows(boolean showArrows) {
		this.showArrows = showArrows;
	}

	/**
	 * @return the showEvaluation
	 */
	@Override
	public boolean isShowEvaluation() {
		return showEvaluation;
	}

	/**
	 * @param showEvaluation the showEvaluation to set
	 */
	@Override
	public void setShowEvaluation(boolean showEvaluation) {
		this.showEvaluation = showEvaluation;
	}

	/**
	 * @return the showUciEngineLines
	 */
	@Override
	public boolean isShowUciEngineLines() {
		return showUciEngineLines;
	}

	/**
	 * @param showUciEngineLines the showUciEngineLines to set
	 */
	@Override
	public void setShowUciEngineLines(boolean showUciEngineLines) {
		this.showUciEngineLines = showUciEngineLines;
	}

	@Override
	public int getUciEngineDepthForEvaluationEngine() {
		return uciEngineDepthForEvaluationEngine;
	}

	@Override
	public void setUciEngineDepthForEvaluationEngine(int uciEngineDepthForEvaluationEngine) {
		this.uciEngineDepthForEvaluationEngine = uciEngineDepthForEvaluationEngine;
	}

	@Override
	public int getUciEngineDepthForWhite() {
		return uciEngineDepthForWhite;
	}

	@Override
	public void setUciEngineDepthForWhite(int depthForWhite) {
		this.uciEngineDepthForWhite = depthForWhite;
	}

	@Override
	public int getUciEngineDepthForBlack() {
		return uciEngineDepthForBlack;
	}

	@Override
	public void setUciEngineDepthForBlack(int depthForBlack) {
		this.uciEngineDepthForBlack = depthForBlack;
	}

	@Override
	public int getThreadsForWhite() {
		return threadsForWhite;
	}

	@Override
	public void setThreadsForWhite(int threads) {
		this.threadsForWhite = threads;
	}

	@Override
	public int getHashSizeForWhite() {
		return hashSizeForWhite;
	}

	@Override
	public void setHashSizeForWhite(int hashSize) {
		this.hashSizeForWhite = hashSize;
	}

	@Override
	public int getMultiPVForEvaluationEngine() {
		return multiPVForEvaluationEngine;
	}

	@Override
	public void setMultiPVForEvaluationEngine(int multiPVForEvaluationEngine) {
		this.multiPVForEvaluationEngine = multiPVForEvaluationEngine;
	}

	@Override
	public int getMoveOverheadForWhite() {
		return moveOverheadForWhite;
	}

	@Override
	public void setMoveOverheadForWhite(int moveOverhead) {
		this.moveOverheadForWhite = moveOverhead;
	}

	@Override
	public int getContemptForWhite() {
		return contemptForWhite;
	}

	@Override
	public void setContemptForWhite(int contempt) {
		this.contemptForWhite = contempt;
	}

	@Override
	public int getUciEloForWhite() {
		return uciEloForWhite;
	}

	@Override
	public void setUciEloForWhite(int uciElo) {
		this.uciEloForWhite = uciElo;
	}

	/**
	 * @return the updateIntervall
	 */
	@Override
	public int getUpdateIntervall() {
		return updateIntervall;
	}

	/**
	 * @param updateIntervall the updateIntervall to set
	 */
	@Override
	public void setUpdateIntervall(int updateIntervall) {
		this.updateIntervall = updateIntervall;
	}

	@Override
	public int getThreadsForBlack() {
		return threadsForBlack;
	}

	@Override
	public void setThreadsForBlack(int threadsForBlack) {
		this.threadsForBlack = threadsForBlack;
	}

	@Override
	public int getHashSizeForBlack() {
		return hashSizeForBlack;
	}

	@Override
	public void setHashSizeForBlack(int hashSizeForBlack) {
		this.hashSizeForBlack = hashSizeForBlack;
	}

	@Override
	public int getContemptForBlack() {
		return contemptForBlack;
	}

	@Override
	public void setContemptForBlack(int contemptForBlack) {
		this.contemptForBlack = contemptForBlack;
	}

	@Override
	public int getMoveOverheadForBlack() {
		return moveOverheadForBlack;
	}

	@Override
	public void setMoveOverheadForBlack(int moveOverheadForBlack) {
		this.moveOverheadForBlack = moveOverheadForBlack;
	}

	@Override
	public int getUciEloForBlack() {
		return uciEloForBlack;
	}

	@Override
	public void setUciEloForBlack(int uciEloForBlack) {
		this.uciEloForBlack = uciEloForBlack;
	}

	@Override
	public boolean getIsFlipped() {
		return isFlipped;
	}

	@Override
	public void setIsFlipped(boolean isFlipped) {
		this.isFlipped = isFlipped;
	}

	@Override
	public int getAnimationDuration() {
		return animationDuration;
	}

	@Override
	public void setAnimationDuration(int animationDuration) {
		this.animationDuration = animationDuration;
	}

	@Override
	public int getIncrementForWhite() {
		return incrementForWhite;
	}

	@Override
	public void setIncrementForWhite(int incrementForWhite) {
		this.incrementForWhite = incrementForWhite;
	}

	@Override
	public int getIncrementForBlack() {
		return incrementForBlack;
	}

	@Override
	public void setIncrementForBlack(int incrementForBlack) {
		this.incrementForBlack = incrementForBlack;
	}

	@Override
	public boolean isShortAlgebraicNotation() {
		return shortAlgebraicNotation;
	}

	@Override
	public void setShortAlgebraicNotation(boolean shortAlgebraicNotation) {
		this.shortAlgebraicNotation = shortAlgebraicNotation;
	}

}
