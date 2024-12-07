package com.example.demo.controller.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.AppAdmin;

import demo.chess.admin.Admin;
import demo.chess.definitions.engines.EngineConfig;
import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.definitions.engines.impl.NoMoveFoundException;
import demo.chess.definitions.fields.Field;
import demo.chess.definitions.moves.Move;
import demo.chess.definitions.moves.Promotion;
import demo.chess.definitions.pieces.Piece;
import demo.chess.game.Game;

/**
 * The `ApiControllerHelper` interface provides essential methods for managing
 * and validating game interactions, move analysis, and UI updates for a chess
 * application. It defines functionality for move validation, promotion
 * handling, and interaction with the evaluation engine, as well as utility
 * methods for UI and gameplay state management.
 *
 * Key responsibilities of this interface include: - Retrieving possible moves,
 * source fields, and target fields based on the current game state to
 * facilitate move validation and availability display. - Managing valid
 * promotions for a given piece to ensure valid move options. - Calculating
 * ratios for evaluation bars to visually display engine evaluations. -
 * Converting screen coordinates to chessboard positions for accurate
 * interaction mapping. - Managing communication with the evaluation engine to
 * fetch move evaluations and remove duplicates from move lists. - Checking the
 * game state against expected outcomes and controlling access for human
 * interactions based on engine activity. - Sending signals to reload or reset
 * the application as needed.
 *
 * Implementations of this interface enable a responsive and interactive chess
 * application, bridging between backend chess logic and frontend UI updates,
 * while ensuring accurate game and engine state synchronization.
 */
@Component
public interface ApiControllerHelper {

	/**
	 * Retrieves the source fields that a piece can be moved from in the current
	 * game state.
	 *
	 * @return A list of source fields available for moves.
	 * @throws NoMoveFoundException if no valid moves are found.
	 * @throws IOException          if an I/O error occurs during retrieval.
	 */
	List<Field> getSourceFields() throws NoMoveFoundException, IOException;

	/**
	 * Retrieves the target fields that a piece can be moved to in the current game
	 * state.
	 *
	 * @return A list of target fields available for moves.
	 * @throws NoMoveFoundException if no valid moves are found.
	 * @throws IOException          if an I/O error occurs during retrieval.
	 */
	List<Field> getTargetFields() throws NoMoveFoundException, IOException;

	/**
	 * Retrieves a list of all possible moves in the current game state.
	 *
	 * @return A list of valid moves.
	 * @throws NoMoveFoundException if no valid moves are found.
	 * @throws IOException          if an I/O error occurs during retrieval.
	 */
	List<Move> getPossibleMoves() throws NoMoveFoundException, IOException;

	/**
	 * Sets the valid promotion moves for a piece, based on available moves and the
	 * selected move.
	 *
	 * @param moveList        The list of potential moves.
	 * @param chessMove       The specific move chosen for promotion.
	 * @param validPromotions A list to store valid promotions.
	 */
	void setValidPromotions(List<Move> moveList, Move chessMove, List<Promotion> validPromotions);

	/**
	 * Calculates the ratio for evaluation bars based on a given evaluation score.
	 *
	 * @param eval The evaluation score.
	 * @return The calculated ratio for the evaluation bars.
	 */
	double getRatioEvalBars(double eval);

	/**
	 * Converts screen coordinates (top, left) to chess board file and rank.
	 *
	 * @param top  The top screen coordinate.
	 * @param left The left screen coordinate.
	 * @return An array containing the file and rank.
	 */
	int[] fileRankFor(int top, int left);

	/**
	 * Retrieves the Unicode symbol for a specified chess piece.
	 *
	 * @param piece The chess piece.
	 * @return The Unicode symbol representing the piece.
	 */
	String getUnicodeSymbol(Piece piece);

	/**
	 * Retrieves the list of moves made in the game along with their corresponding
	 * symbols.
	 *
	 * @return A list of moves with Unicode symbols.
	 */
	List<String> getMoveListWithSymbols();

	/**
	 * Sends a signal to reload the application, typically for updating the
	 * frontend.
	 */
	void sendReloadSignal();

	/**
	 * Removes duplicate entries from a list of moves based on the move's string
	 * representation.
	 *
	 * @param list The list of moves with evaluations.
	 * @return A list without duplicate moves.
	 */
	List<Pair<Pair<Double, Integer>, String>> removeDuplicatesByString(List<Pair<Pair<Double, Integer>, String>> list);

	/**
	 * Resets the application state, restoring it to its initial configuration.
	 *
	 * @return A string confirmation of the reset operation.
	 * @throws Exception if an error occurs during reset.
	 */
	String reset() throws Exception;

	/**
	 * Retrieves the move list from the evaluation engine, including evaluations for
	 * each move.
	 *
	 * @param evaluationEngine The evaluation engine providing move analysis.
	 * @return A list of moves with evaluations.
	 * @throws Exception if an error occurs during retrieval.
	 */
	List<String> getEvaluationEngineMoveList(EvaluationEngine evaluationEngine) throws Exception;

	/**
	 * Checks the game state to confirm it aligns with expectations for the given
	 * engine.
	 *
	 * @param chessGame        The current game state.
	 * @param evaluationEngine The evaluation engine assessing the game.
	 * @return True if the game state matches expectations; false otherwise.
	 * @throws Exception if an error occurs during the check.
	 */
	boolean checkForGameState(Game chessGame, EvaluationEngine evaluationEngine) throws Exception;

	/**
	 * Determines if a human player is allowed to interact with the game at the
	 * current state.
	 *
	 * @param chessGame       The current game state.
	 * @param uciEngineActive Whether a UCI engine is actively controlling a player.
	 * @return True if human interaction is allowed; false otherwise.
	 */
	boolean isHumanAlowedToInteract(Game chessGame, boolean uciEngineActive);

	String createToolTipForConfig(EngineConfig config);

	List<String> convertToSan(List<String> evalMoveList, Admin admin) throws Exception;

	List<Move> convertToCoordinateRepresentation(MultipartFile file, AppAdmin admin) throws FileNotFoundException, IOException, NoMoveFoundException;

	/**
	 * Returns a single character representing the specified chess piece. Uppercase
	 * letters denote white pieces, and lowercase letters denote black pieces.
	 *
	 * @param piece The piece to represent.
	 * @return A single character representing the piece.
	 */
	char getPieceRepresentation(Piece piece);

	/**
	 * Creates a 64-character string representing the current state of the
	 * chessboard. Each character corresponds to a square on the board, with pieces
	 * represented by standard abbreviations (e.g., 'P' for white pawn, 'p' for
	 * black pawn). Empty squares are represented by a placeholder character.
	 *
	 * @param chessGame The current chess game from which the board state is
	 *                  extracted.
	 * @return A 64-character string representing the board state.
	 */
	String createPositionAsString(Game chessGame);

	void createPositionsAsString(Game chessGame, AppAdmin admin) throws NoMoveFoundException, IOException;
}
