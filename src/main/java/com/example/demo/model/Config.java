package com.example.demo.model;

import demo.chess.definitions.Color;

/**
 * Interface representing the configuration settings for the chess application.
 * <p>
 * This interface provides methods to get and set various configuration settings
 * such as the time allocated for each player, whether the UCI engine is active,
 * and various layout settings for the chessboard and UI elements.
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
	 * Sets the time allocated for each player in seconds.
	 *
	 * @param timeForEachPlayer the time in seconds for each player
	 */
	void setTimeForEachPlayer(int timeForEachPlayer);

	/**
	 * Checks if the UCI (Universal Chess Interface) engine is active.
	 *
	 * @return true if the UCI engine is active, false otherwise
	 */
	boolean isUciEngineActive();

	/**
	 * Sets whether the UCI engine should be active.
	 *
	 * @param isUciEngineActive true to activate the UCI engine, false to deactivate
	 *                          it
	 */
	void setUciEngineActive(boolean isUciEngineActive);

	/**
	 * Gets the left offset for the chessboard in pixels.
	 *
	 * @return the left offset in pixels
	 */
	int getLeftOffset();

	/**
	 * Sets the left offset for the chessboard in pixels.
	 *
	 * @param leftOffset the left offset in pixels
	 */
	void setLeftOffset(int leftOffset);

	/**
	 * Gets the color theme for the chessboard.
	 *
	 * @return the current {@link Color} theme
	 */
	Color getColor();

	/**
	 * Sets the color theme of the chessboard.
	 *
	 * @param color the {@link Color} theme to set
	 */
	void setColor(Color color);

	/**
	 * Gets the size of each square on the chessboard in pixels.
	 *
	 * @return the square size in pixels
	 */
	int getSquareSize();

	/**
	 * Sets the size of each square on the chessboard.
	 *
	 * @param squareSize the size in pixels
	 */
	void setSquareSize(int squareSize);

	/**
	 * Gets the height of the top bar in pixels.
	 *
	 * @return the height of the top bar
	 */
	int getTopBarHeight();

	/**
	 * Sets the height of the top bar.
	 *
	 * @param topBarHeight the height in pixels
	 */
	void setTopBarHeight(int topBarHeight);

	/**
	 * Gets the top offset for the chessboard in pixels.
	 *
	 * @return the top offset in pixels
	 */
	int getChessBoardOffset();

	/**
	 * Sets the top offset for the chessboard.
	 *
	 * @param chessBoardOffset the top offset in pixels
	 */
	void setChessBoardOffset(int chessBoardOffset);

	/**
	 * Gets the top position for the move list panel in pixels.
	 *
	 * @return the top position of the move list
	 */
	int getMoveListTop();

	/**
	 * Sets the top position for the move list panel.
	 *
	 * @param moveListTop the top position in pixels
	 */
	void setMoveListTop(int moveListTop);

	/**
	 * Gets the left position for the move list panel in pixels.
	 *
	 * @return the left position of the move list
	 */
	int getMoveListLeft();

	/**
	 * Sets the left position for the move list panel.
	 *
	 * @param moveListLeft the left position in pixels
	 */
	void setMoveListLeft(int moveListLeft);

	/**
	 * Gets the left position for the UCI engine move list in pixels.
	 *
	 * @return the left position of the UCI engine move list
	 */
	int getUciEngineMoveListLeft();

	/**
	 * Sets the left position for the UCI engine move list.
	 *
	 * @param left the left position in pixels
	 */
	void setUciEngineMoveListLeft(int left);

	/**
	 * Gets the top position for the UCI engine move list in pixels.
	 *
	 * @return the top position of the UCI engine move list
	 */
	int getUciEngineMoveListTop();

	/**
	 * Sets the top position for the UCI engine move list.
	 *
	 * @param top the top position in pixels
	 */
	void setUciEngineMoveListTop(int top);

	/**
	 * Gets the width of the evaluation bar in pixels.
	 *
	 * @return the width of the evaluation bar
	 */
	int getEvalWidth();

	/**
	 * Sets the width of the evaluation bar.
	 *
	 * @param evalWidth the width in pixels
	 */
	void setEvalWidth(int evalWidth);

	/**
	 * Gets the size of the clock display in pixels.
	 *
	 * @return the clock size
	 */
	int getClockSize();

	/**
	 * Sets the size of the clock display.
	 *
	 * @param clockSize the size in pixels
	 */
	void setClockSize(int clockSize);

	/**
	 * Checks if arrows showing move history are enabled.
	 *
	 * @return true if arrows are shown, false otherwise
	 */
	boolean isShowArrows();

	/**
	 * Enables or disables arrows showing move history.
	 *
	 * @param showArrows true to show arrows, false to hide
	 */
	void setShowArrows(boolean showArrows);

	/**
	 * Checks if the evaluation bar is displayed.
	 *
	 * @return true if evaluation is shown, false otherwise
	 */
	boolean isShowEvaluation();

	/**
	 * Sets whether to display the evaluation bar.
	 *
	 * @param showEvaluation true to show, false to hide
	 */
	void setShowEvaluation(boolean showEvaluation);

	/**
	 * Checks if UCI engine move suggestions are displayed.
	 *
	 * @return true if UCI engine lines are shown, false otherwise
	 */
	boolean isShowUciEngineLines();

	/**
	 * Sets whether to display UCI engine move suggestions.
	 *
	 * @param showUciEngineLines true to show, false to hide
	 */
	void setShowUciEngineLines(boolean showUciEngineLines);

	/**
	 * Gets the depth setting for the UCI engine for the white player.
	 *
	 * @return the depth for the white player’s UCI engine
	 */
	int getUciEngineDepthForWhite();

	/**
	 * Sets the depth setting for the UCI engine for the white player.
	 *
	 * @param uciEngineDepthForWhite the depth for the white player’s UCI engine
	 */
	void setUciEngineDepthForWhite(int uciEngineDepthForWhite);

	/**
	 * Gets the depth setting for the UCI engine for the black player.
	 *
	 * @return the depth for the black player’s UCI engine
	 */
	int getUciEngineDepthForBlack();

	/**
	 * Sets the depth setting for the UCI engine for the black player.
	 *
	 * @param uciEngineDepthForBlack the depth for the black player’s UCI engine
	 */
	void setUciEngineDepthForBlack(int uciEngineDepthForBlack);

	/**
	 * Gets the number of multi-principal variations for the evaluation engine.
	 *
	 * @return the number of multi-principal variations
	 */
	int getMultiPVForEvaluationEngine();

	/**
	 * Sets the number of multi-principal variations for the evaluation engine.
	 *
	 * @param multiPVForEvaluationEngine the number of multi-principal variations
	 */
	void setMultiPVForEvaluationEngine(int multiPVForEvaluationEngine);

	/**
	 * Gets the number of threads used by the UCI engine for the white player.
	 *
	 * @return the number of threads for the white player’s UCI engine
	 */
	int getThreadsForWhite();

	/**
	 * Sets the number of threads for the UCI engine used by the white player.
	 *
	 * @param threads the number of threads for the white player’s UCI engine
	 */
	void setThreadsForWhite(int threads);

	/**
	 * Gets the number of threads used by the UCI engine for the black player.
	 *
	 * @return the number of threads for the black player’s UCI engine
	 */
	int getThreadsForBlack();

	/**
	 * Sets the number of threads for the UCI engine used by the black player.
	 *
	 * @param threads the number of threads for the black player’s UCI engine
	 */
	void setThreadsForBlack(int threads);

	/**
	 * Gets the hash size for the UCI engine for the white player in megabytes.
	 *
	 * @return the hash size for the white player’s UCI engine
	 */
	int getHashSizeForWhite();

	/**
	 * Sets the hash size for the UCI engine for the white player in megabytes.
	 *
	 * @param hashSize the hash size for the white player’s UCI engine
	 */
	void setHashSizeForWhite(int hashSize);

	/**
	 * Gets the hash size for the UCI engine for the black player in megabytes.
	 *
	 * @return the hash size for the black player’s UCI engine
	 */
	int getHashSizeForBlack();

	/**
	 * Sets the hash size for the UCI engine for the black player in megabytes.
	 *
	 * @param hashSize the hash size for the black player’s UCI engine
	 */
	void setHashSizeForBlack(int hashSize);

	/**
	 * Gets the move overhead in milliseconds for the white player.
	 *
	 * @return the move overhead for the white player
	 */
	int getMoveOverheadForWhite();

	/**
	 * Sets the move overhead in milliseconds for the white player.
	 *
	 * @param moveOverhead the move overhead for the white player
	 */
	void setMoveOverheadForWhite(int moveOverhead);

	/**
	 * Gets the move overhead in milliseconds for the black player.
	 *
	 * @return the move overhead for the black player
	 */
	int getMoveOverheadForBlack();

	/**
	 * Sets the move overhead in milliseconds for the black player.
	 *
	 * @param moveOverhead the move overhead for the black player
	 */
	void setMoveOverheadForBlack(int moveOverhead);

	/**
	 * Gets the contempt factor for the white player’s UCI engine.
	 *
	 * @return the contempt factor for the white player
	 */
	int getContemptForWhite();

	/**
	 * Sets the contempt factor for the white player’s UCI engine.
	 *
	 * @param contempt the contempt factor for the white player
	 */
	void setContemptForWhite(int contempt);

	/**
	 * Gets the contempt factor for the black player’s UCI engine.
	 *
	 * @return the contempt factor for the black player
	 */
	int getContemptForBlack();

	/**
	 * Sets the contempt factor for the black player’s UCI engine.
	 *
	 * @param contempt the contempt factor for the black player
	 */
	void setContemptForBlack(int contempt);

	/**
	 * Gets the UCI Elo rating for the white player’s engine.
	 *
	 * @return the UCI Elo rating for the white player
	 */
	int getUciEloForWhite();

	/**
	 * Sets the UCI Elo rating for the white player’s engine.
	 *
	 * @param uciElo the Elo rating for the white player’s engine
	 */
	void setUciEloForWhite(int uciElo);

	/**
	 * Gets the UCI Elo rating for the black player’s engine.
	 *
	 * @return the UCI Elo rating for the black player
	 */
	int getUciEloForBlack();

	/**
	 * Sets the UCI Elo rating for the black player’s engine.
	 *
	 * @param uciElo the Elo rating for the black player’s engine
	 */
	void setUciEloForBlack(int uciElo);

	/**
	 * Gets the update interval in milliseconds for UI updates.
	 *
	 * @return the update interval in milliseconds
	 */
	int getUpdateIntervall();

	/**
	 * Sets the update interval in milliseconds for UI updates.
	 *
	 * @param updateIntervall the update interval in milliseconds
	 */
	void setUpdateIntervall(int updateIntervall);

	/**
	 * Sets whether the board should be flipped.
	 *
	 * @param isFlipped true to flip the board, false otherwise
	 */
	void setIsFlipped(boolean isFlipped);

	/**
	 * Checks if the board orientation is flipped.
	 *
	 * @return true if the board is flipped, false otherwise
	 */
	boolean getIsFlipped();

	/**
	 * Gets the animation duration for moves in milliseconds.
	 *
	 * @return the animation duration in milliseconds
	 */
	int getAnimationDuration();

	/**
	 * Sets the animation duration for moves in milliseconds.
	 *
	 * @param animationDuration the animation duration in milliseconds
	 */
	void setAnimationDuration(int animationDuration);

	/**
	 * Gets the increment in seconds added to each move for the white player.
	 *
	 * @return the increment for the white player in seconds
	 */
	int getIncrementForWhite();

	/**
	 * Sets the increment in seconds added to each move for the white player.
	 *
	 * @param incrementForWhite the increment for the white player in seconds
	 */
	void setIncrementForWhite(int incrementForWhite);

	/**
	 * Gets the increment in seconds added to each move for the black player.
	 *
	 * @return the increment for the black player in seconds
	 */
	int getIncrementForBlack();

	/**
	 * Sets the increment in seconds added to each move for the black player.
	 *
	 * @param incrementForBlack the increment for the black player in seconds
	 */
	void setIncrementForBlack(int incrementForBlack);

	/**
	 * Checks if silent mode is enabled, suppressing sound notifications.
	 *
	 * @return true if silent mode is enabled, false otherwise
	 */
	boolean isSilent();

	/**
	 * Sets the silent mode, suppressing sound notifications if true.
	 *
	 * @param silent true to enable silent mode, false to disable
	 */
	void setSilent(boolean silent);

	/**
	 * Gets the evaluation engine's identifier.
	 *
	 * @return the identifier for the evaluation engine
	 */
	String getEvaluationEngine();

	/**
	 * Sets the evaluation engine's identifier.
	 *
	 * @param evaluationEngine the identifier for the evaluation engine
	 */
	void setEvaluationEngine(String evaluationEngine);

	/**
	 * Gets the identifier for the white player’s engine.
	 *
	 * @return the identifier for the white player’s engine
	 */
	String getPlayerEngineForWhite();

	/**
	 * Sets the identifier for the white player’s engine.
	 *
	 * @param playerEngineForWhite the identifier for the white player’s engine
	 */
	void setPlayerEngineForWhite(String playerEngineForWhite);

	/**
	 * Gets the identifier for the black player’s engine.
	 *
	 * @return the identifier for the black player’s engine
	 */
	String getPlayerEngineForBlack();

	/**
	 * Sets the identifier for the black player’s engine.
	 *
	 * @param playerEngineForBlack the identifier for the black player’s engine
	 */
	void setPlayerEngineForBlack(String playerEngineForBlack);

	/**
	 * Checks if short algebraic notation is enabled for move display.
	 *
	 * @return true if short algebraic notation is enabled, false otherwise
	 */
	boolean isShortAlgebraicNotation();

	/**
	 * Sets whether to use short algebraic notation for move display.
	 *
	 * @param shortAlgebraicNotation true to use short notation, false to use full
	 */
	void setShortAlgebraicNotation(boolean shortAlgebraicNotation);

	/**
	 * Gets the additional time for each player in seconds.
	 *
	 * @return the additional time in seconds
	 */
	int getAdditionalTime();

	/**
	 * Sets the additional time for each player in seconds.
	 *
	 * @param additionalTime the additional time in seconds
	 */
	void setAdditionalTime(int additionalTime);

	/**
	 * Gets the width of the move list display area in pixels.
	 *
	 * @return the width of the move list area in pixels
	 */
	int getMoveListWidth();

	/**
	 * Sets the width of the move list display area in pixels.
	 *
	 * @param moveListWidth the width in pixels for the move list area
	 */
	void setMoveListWidth(int moveListWidth);

	/**
	 * Gets the depth setting for the UCI evaluation engine, which determines how
	 * many moves ahead the engine evaluates during analysis.
	 *
	 * @return the depth setting for the evaluation engine
	 */
	int getUciEngineDepthForEvaluationEngine();

	/**
	 * Sets the depth for the UCI evaluation engine, influencing the extent of
	 * analysis performed by the engine.
	 *
	 * @param uciEngineDepthForEvaluationEngine the desired depth setting
	 */
	void setUciEngineDepthForEvaluationEngine(int uciEngineDepthForEvaluationEngine);

	void setThreadsForEvaluationEngine(int threads);

	int getThreadsForEvaluationEngine();
}
