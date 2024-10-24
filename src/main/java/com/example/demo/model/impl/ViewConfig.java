package com.example.demo.model.impl;

import com.example.demo.model.Config;

import demo.chess.definitions.Color;
import demo.chess.definitions.engines.Engine;

/**
 * Implementation of the {@link com.example.demo.model.Config} interface
 * representing the view configuration settings.
 */
public class ViewConfig implements Config {

	// presets
	private Color color = Color.GREEN;
	private boolean showArrows = false;
	private boolean showEvaluation = false;
	private boolean showUciEngineLines = false;
	private boolean capturedContainer = false;
	private boolean silent = false;

	private boolean shortAlgebraicNotation = true;
	
	private boolean uciEngineActive = true;
	private int timeForEachPlayer = 300;
	private int incrementForWhite = 2;
	private int incrementForBlack = 2;
	private int updateIntervall = 1;
	private int animationDuration = 100;

	private String evaluationEngine = Engine.STOCKFISH_16.toString();
	private String playerEngineForWhite = Engine.STOCKFISH_16.toString();
	private String playerEngineForBlack = Engine.STOCKFISH_16.toString();

	private int uciEngineDepthForEvaluationEngine = 10;
	private int multiPVForEvaluationEngine = 3;

	private int uciEngineDepthForWhite = 0;
	private int uciEngineDepthForBlack = 0;

	private int threadsForWhite = 1;
	private int hashSizeForWhite = 1024;
	private int contemptForWhite = 99;
	private int moveOverheadForWhite = 5;
	private int uciEloForWhite = 0;

	private int threadsForBlack = 1;
	private int hashSizeForBlack = 1024;
	private int contemptForBlack = 99;
	private int moveOverheadForBlack = 5;
	private int uciEloForBlack = 0;

	private int leftOffset = 450;
	private int squareSize = 65;
	private int topBarHeight = 40;
	private int moveListWidth = 400;

	private int evalWidth;

	private int chessBoardOffset;
	private int uciEngineMoveListTop;
	private int uciEngineMoveListLeft;
	private int clockSize;

	private int moveListTop;
	private int moveListLeft;

	private int capturedPiecesLeft;
	private int capturedPiecesTop;
	private int capturedContainerWidth;
	private int capturedContainerHeight;
	private boolean isFlipped = false;

	/**
	 * @return the capturedPiecesLeft
	 */
	@Override
	public int getCapturedPiecesLeft() {
		return capturedPiecesLeft;
	}

	/**
	 * @param capturedPiecesLeft the capturedPiecesLeft to set
	 */
	@Override
	public void setCapturedPiecesLeft(int capturedPiecesLeft) {
		this.capturedPiecesLeft = capturedPiecesLeft;
	}

	/**
	 * @return the capturedPiecesTop
	 */
	@Override
	public int getCapturedPiecesTop() {
		return capturedPiecesTop;
	}

	/**
	 * @param capturedPiecesTop the capturedPiecesTop to set
	 */
	@Override
	public void setCapturedPiecesTop(int capturedPiecesTop) {
		this.capturedPiecesTop = capturedPiecesTop;
	}

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
	public int getCapturedContainerWidth() {
		return capturedContainerWidth;
	}

	@Override
	public void setCapturedContainerWidth(int capturedContainerWidth) {
		this.capturedContainerWidth = capturedContainerWidth;
	}

	@Override
	public int getCapturedContainerHeight() {
		return capturedContainerHeight;
	}

	@Override
	public void setCapturedContainerHeight(int capturedContainerHeight) {
		this.capturedContainerHeight = capturedContainerHeight;
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
	public boolean isCapturedContainer() {
		return capturedContainer;
	}

	@Override
	public void setCapturedContainer(boolean capturedContainer) {
		this.capturedContainer = capturedContainer;
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
