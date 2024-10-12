package com.example.demo.model.impl;

import com.example.demo.model.Config;

import demo.chess.definitions.Color;

/**
 * Implementation of the {@link com.example.demo.model.Config} interface
 * representing the view configuration settings.
 */
public class ViewConfig implements Config {

	// presets
	private Color color = Color.GREEN;
	private boolean showArrows = false;
	private boolean showEvaluation = false;
	private boolean showStockfishLines = false;
	private boolean capturedContainer = false;
	private boolean silent = false;

	private boolean stockfishActive = true;
	private int timeForEachPlayer = 300;
	private int incrementForWhite = 1;
	private int incrementForBlack = 1;
	private int updateIntervall = 1;
	private int animationDuration = 100;

	private int stockfishDepthForEvaluationEngine = 15;
	private int multiPVForEvaluationEngine = 10;

	private int depthForWhite = 18;
	private int depthForBlack = 18;

	private int threadsForWhite = 2;
	private int hashSizeForWhite = 1024;
	private int contemptForWhite = 0;
	private int moveOverheadForWhite = 5;
	private int uciEloForWhite = 2200;

	private int threadsForBlack = 2;
	private int hashSizeForBlack = 1024;
	private int contemptForBlack = 0;
	private int moveOverheadForBlack = 5;
	private int uciEloForBlack = 2200;

	private int leftOffset = 350;
	private int squareSize = 60;
	private int topBarHeight = 40;
	private int moveListWidth = 300;

	private int evalWidth;

	private int chessBoardOffset;
	private int stockfishMoveListTop;
	private int stockfishMoveListLeft;
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
	public boolean isStockfishActive() {
		return stockfishActive;
	}

	@Override
	public void setStockfishActive(boolean isStockfishActive) {
		this.stockfishActive = isStockfishActive;
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

	/**
	 * @return the stockfishMoveListLeft
	 */
	@Override
	public int getStockfishMoveListLeft() {
		return stockfishMoveListLeft;
	}

	/**
	 * @param stockfishMoveListLeft the stockfishMoveListLeft to set
	 */
	@Override
	public void setStockfishMoveListLeft(int stockfishMoveListLeft) {
		this.stockfishMoveListLeft = stockfishMoveListLeft;
	}

	/**
	 * @return the stockfishMoveListTop
	 */
	@Override
	public int getStockfishMoveListTop() {
		return stockfishMoveListTop;
	}

	/**
	 * @param stockfishMoveListTop the stockfishMoveListTop to set
	 */
	@Override
	public void setStockfishMoveListTop(int stockfishMoveListTop) {
		this.stockfishMoveListTop = stockfishMoveListTop;
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
	 * @return the showStockfishLines
	 */
	@Override
	public boolean isShowStockfishLines() {
		return showStockfishLines;
	}

	/**
	 * @param showStockfishLines the showStockfishLines to set
	 */
	@Override
	public void setShowStockfishLines(boolean showStockfishLines) {
		this.showStockfishLines = showStockfishLines;
	}

	@Override
	public int getStockfishDepthForEvaluationEngine() {
		return stockfishDepthForEvaluationEngine;
	}

	@Override
	public void setStockfishDepthForEvaluationEngine(int stockfishDepthForEvaluationEngine) {
		this.stockfishDepthForEvaluationEngine = stockfishDepthForEvaluationEngine;
	}

	@Override
	public int getStockfishDepthForWhite() {
		return depthForWhite;
	}

	@Override
	public void setStockfishDepthForWhite(int depthForWhite) {
		this.depthForWhite = depthForWhite;
	}

	@Override
	public int getStockfishDepthForBlack() {
		return depthForBlack;
	}

	@Override
	public void setStockfishDepthForBlack(int depthForBlack) {
		this.depthForBlack = depthForBlack;
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
}