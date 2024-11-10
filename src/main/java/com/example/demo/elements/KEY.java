package com.example.demo.elements;

/**
 * The `KEY` enum defines a set of constants for identifying and accessing
 * various attributes within the application's attribute storage, such as the
 * `Attributes` map or other data containers. These keys standardize references
 * to specific configurations, game elements, and engines, enabling centralized,
 * type-safe management of game state and settings.
 */
public enum KEY {

	/**
	 * Represents a collection of displayable chess pieces or UI components, such as
	 * instances of `DisplayedPiece`. Used to manage the set of visible chess pieces
	 * and their respective states on the UI.
	 */
	ELEMENTS,

	/**
	 * Represents the chessboard's fields or tiles, including individual squares and
	 * their properties (e.g., color, position, and occupied state). Used to access
	 * and manipulate the chessboard layout.
	 */
	FIELDS,

	/**
	 * Refers to the UCI (Universal Chess Interface) engine assigned to the white
	 * player, responsible for generating moves for white in cases of AI or
	 * engine-driven play.
	 */
	PLAYER_ENGINE_FOR_WHITE,

	/**
	 * Contains configuration settings specific to the white player's engine.
	 * Settings may include search depth, evaluation preferences, or computation
	 * resources.
	 */
	ENGINE_CONFIG_FOR_WHITE,

	/**
	 * Contains configuration settings specific to the black player's engine.
	 * Supports custom settings for each player’s engine, similar to
	 * `ENGINE_CONFIG_FOR_WHITE`.
	 */
	ENGINE_CONFIG_FOR_BLACK,

	/**
	 * Refers to the UCI or player engine assigned to the black player. Generates
	 * moves for black when the game is set for engine-driven gameplay or analysis.
	 */
	PLAYER_ENGINE_FOR_BLACK,

	/**
	 * Indicates whether the game is set up in a standard (non-variant) mode,
	 * following traditional chess rules. Often a boolean flag for rule enforcement
	 * and game mechanics.
	 */
	REGULAR,

	/**
	 * Refers to the main `Game` object that maintains the state and data of the
	 * current chess game, including game state, move history, and player
	 * information.
	 */
	CHESSGAME,

	/**
	 * Indicates if an engine-vs-engine match is currently in progress. Used to
	 * toggle between different game modes, such as human-vs-engine,
	 * engine-vs-engine, or human-vs-human.
	 */
	ENGINE_MATCH,

	/**
	 * Represents configuration settings for the evaluation engine, an engine that
	 * analyzes board positions and assesses move quality without necessarily
	 * playing moves.
	 */
	ENGINE_CONFIG_EVAL,

	/**
	 * Refers to the specific evaluation engine instance used to analyze the board
	 * state and provide insights on positional advantages or disadvantages.
	 */
	EVALUATION_ENGINE,

	/**
	 * Holds the most recent evaluation score provided by a UCI-compatible engine
	 * for a given board position, often a numerical value representing advantage
	 * (positive for white, negative for black).
	 */
	UCI_ENGINE_EVALUATION,

	/**
	 * Stores a list of moves suggested by the UCI engine, useful for displaying
	 * potential lines of play or best moves based on the engine's analysis.
	 */
	UCI_ENGINE_MOVELIST,

	/**
	 * Stores a list of Strings used in frontend.
	 */
	POSITIONS_AS_STRINGS;

}
