package com.example.demo.model;

import demo.chess.definitions.Color;

/**
 * Interface representing the configuration settings for the application.
 * <p>
 * This interface provides methods to get and set various configuration settings
 * such as the time allocated for each player, whether the Stockfish engine is
 * active, and various layout settings for the chessboard and UI elements.
 * </p>
 */
public interface Config {

	/**
	 * Gets the time allocated for each player in seconds.
	 *
	 * @return the time for each player in seconds
	 */
	int getTimeForEachPlayer();

	/**
	 * Checks if the Stockfish engine is active.
	 *
	 * @return true if the Stockfish engine is active, false otherwise
	 */
	boolean isStockfishActive();

	/**
	 * Sets whether the Stockfish engine is active.
	 *
	 * @param isStockfishActive true to activate the Stockfish engine, false to
	 *                          deactivate it
	 */
	void setStockfishActive(boolean isStockfishActive);

	/**
	 * Gets the left offset for the chessboard.
	 *
	 * @return the left offset in pixels
	 */
	int getLeftOffset();

	/**
	 * Sets the left offset for the chessboard.
	 *
	 * @param leftOffset the left offset in pixels
	 */
	void setLeftOffset(int leftOffset);

	/**
	 * Sets the color theme of the chessboard.
	 *
	 * @param color the color theme to set
	 */
	void setColor(Color color);

	/**
	 * Gets the color theme of the chessboard.
	 *
	 * @return the current color theme
	 */
	Color getColor();

	/**
	 * Sets the size of each square on the chessboard.
	 *
	 * @param squareSize the size of each square in pixels
	 */
	void setSquareSize(int squareSize);

	/**
	 * Gets the size of each square on the chessboard.
	 *
	 * @return the size of each square in pixels
	 */
	int getSquareSize();

	/**
	 * Sets the height of the top bar.
	 *
	 * @param topBarHight the height of the top bar in pixels
	 */
	void setTopBarHeight(int topBarHight);

	/**
	 * Gets the height of the top bar.
	 *
	 * @return the height of the top bar in pixels
	 */
	int getTopBarHeight();

	/**
	 * Sets the offset of the chessboard from the top.
	 *
	 * @param chessBoardOffset the offset of the chessboard from the top in pixels
	 */
	void setChessBoardOffset(int chessBoardOffset);

	/**
	 * Gets the offset of the chessboard from the top.
	 *
	 * @return the offset of the chessboard from the top in pixels
	 */
	int getChessBoardOffset();

	/**
	 * Gets the top position of the move list.
	 *
	 * @return the top position of the move list in pixels
	 */
	int getMoveListTop();

	/**
	 * Sets the top position of the move list.
	 *
	 * @param moveListTop the top position of the move list in pixels
	 */
	void setMoveListTop(int moveListTop);

	/**
	 * Gets the left position of the move list.
	 *
	 * @return the left position of the move list in pixels
	 */
	int getMoveListLeft();

	/**
	 * Sets the left position of the move list.
	 *
	 * @param moveListLeft the left position of the move list in pixels
	 */
	void setMoveListLeft(int moveListLeft);

	void setTimeForEachPlayer(int timeForEachPlayer);

	void setStockfishMoveListTop(int top);

	void setStockfishMoveListLeft(int left);

	int getStockfishMoveListLeft();

	int getStockfishMoveListTop();

	int getEvalWidth();

	int getStockfishDepthForEvaluationEngine();

	void setStockfishDepthForEvaluationEngine(int stockfishDepthForEvaluationEngine);

	int getMoveListWidth();

	void setMoveListWidth(int moveListWidth);

	int getCapturedContainerWidth();

	void setCapturedContainerWidth(int moveListWidth);

	/**
	 * @return the capturedPiecesLeft
	 */
	int getCapturedPiecesLeft();

	/**
	 * @param capturedPiecesLeft the capturedPiecesLeft to set
	 */
	void setCapturedPiecesLeft(int capturedPiecesLeft);

	/**
	 * @return the capturedPiecesTop
	 */
	int getCapturedPiecesTop();

	/**
	 * @param capturedPiecesTop the capturedPiecesTop to set
	 */
	void setCapturedPiecesTop(int capturedPiecesTop);

	/**
	 * @param evalWidth the evalWidth to set
	 */
	void setEvalWidth(int evalWidth);

	int getClockSize();

	void setClockSize(int clockSize);

	int getCapturedContainerHeight();

	void setCapturedContainerHeight(int capturedContainerHeight);

	/**
	 * @return the showArrows
	 */
	boolean isShowArrows();

	/**
	 * @param showArrows the showArrows to set
	 */
	void setShowArrows(boolean showArrows);

	/**
	 * @return the showEvaluation
	 */
	boolean isShowEvaluation();

	/**
	 * @param showEvaluation the showEvaluation to set
	 */
	void setShowEvaluation(boolean showEvaluation);

	/**
	 * @return the showStockfishLines
	 */
	boolean isShowStockfishLines();

	/**
	 * @param showStockfishLines the showStockfishLines to set
	 */
	void setShowStockfishLines(boolean showStockfishLines);

	int getStockfishDepthForWhite();

	void setStockfishDepthForWhite(int stockfishDepthForWhite);

	int getStockfishDepthForBlack();

	void setStockfishDepthForBlack(int stockfishDepthForBlack);

	int getMultiPVForEvaluationEngine();

	void setMultiPVForEvaluationEngine(int multiPVForEvaluationEngine);

	int getThreadsForWhite();

	int getThreadsForBlack();

	void setThreadsForWhite(int threads);

	void setThreadsForBlack(int threads);

	int getHashSizeForWhite();

	int getHashSizeForBlack();

	void setHashSizeForWhite(int hashSize);

	void setHashSizeForBlack(int hashSize);

	int getMoveOverheadForWhite();

	int getMoveOverheadForBlack();

	void setMoveOverheadForWhite(int moveOverhead);

	void setMoveOverheadForBlack(int moveOverhead);

	int getContemptForWhite();

	int getContemptForBlack();

	void setContemptForWhite(int contempt);

	void setContemptForBlack(int contempt);

	int getUciEloForWhite();

	int getUciEloForBlack();

	void setUciEloForWhite(int uciElo);

	void setUciEloForBlack(int uciElo);

	int getUpdateIntervall();

	void setUpdateIntervall(int updateIntervall);

	void setIsFlipped(boolean isFlippe);

	boolean getIsFlipped();

	int getAnimationDuration();

	void setAnimationDuration(int animationDuration);

	boolean isCapturedContainer();

	void setCapturedContainer(boolean capturedContainer);

	int getIncrementForWhite();

	int getIncrementForBlack();

	void setIncrementForWhite(int incrementForWhite);

	void setIncrementForBlack(int incrementForBlack);

	boolean isSilent();

	void setSilent(boolean silent);

}