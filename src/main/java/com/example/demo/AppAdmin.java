package com.example.demo;

import java.util.Map;

import com.example.demo.elements.Attributes;

import demo.chess.admin.Admin;
import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.definitions.engines.PlayerEngine;
import demo.chess.game.DummyGame;

/**
 * The `AppAdmin` interface extends the base `Admin` interface, adding
 * additional administrative functionalities specific to the chess application.
 *
 * <p>
 * This interface is designed to manage chess game instances, game
 * configurations, and chess engines within the application, providing an
 * organized structure for handling game state, player interactions, and
 * evaluations.
 * </p>
 *
 * <p>
 * It defines methods to retrieve and configure chess engines, access attributes
 * used across the application, and provide necessary instances of chess games
 * either for play or for simulation purposes.
 * </p>
 */
public interface AppAdmin extends Admin {

	/**
	 * Retrieves a map of all available player engines. Each entry in the map
	 * associates an engine name (as the key) with a specific instance of a
	 * {@link PlayerEngine} implementation.
	 *
	 * <p>
	 * This allows for dynamic selection and configuration of chess engines
	 * available to players.
	 * </p>
	 *
	 * @return a map of player engine names to their respective {@link PlayerEngine}
	 *         instances
	 */
	Map<String, PlayerEngine> playerEngines();

	/**
	 * Provides an instance of {@link Attributes}, which serves as a centralized
	 * store for application-wide attributes and settings. This attribute map allows
	 * key-value pairs to be dynamically managed across the application.
	 *
	 * @return the {@link Attributes} instance containing application settings and
	 *         values
	 */
	Attributes attributes();

	/**
	 * Retrieves a map of all available evaluation engines used to analyze and
	 * evaluate chess positions. Each entry in the map associates an engine name (as
	 * the key) with a specific instance of an {@link EvaluationEngine}.
	 *
	 * <p>
	 * Evaluation engines are utilized to provide feedback on game states,
	 * suggesting optimal moves or evaluating positions based on various criteria.
	 * </p>
	 *
	 * @return a map of evaluation engine names to their respective
	 *         {@link EvaluationEngine} instances
	 * @throws Exception if an error occurs while retrieving evaluation engines
	 */
	Map<String, EvaluationEngine> evaluationEngines() throws Exception;

	/**
	 * Returns dummy game with no validation.
	 *
	 * @return the dummy game instance
	 */
	DummyGame dummyGame();
}
