package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import com.example.demo.elements.Attributes;
import com.example.demo.elements.KEY;
import com.example.demo.elements.impl.AttributesImpl;
import com.example.demo.model.Config;
import com.example.demo.model.impl.ViewConfig;

import demo.chess.admin.impl.ChessAdmin;
import demo.chess.definitions.board.impl.ChessBoard;
import demo.chess.definitions.engines.Engine;
import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.definitions.engines.PlayerEngine;
import demo.chess.definitions.engines.impl.EvaluationUciEngine;
import demo.chess.definitions.engines.impl.PlayerUciEngine;
import demo.chess.definitions.moves.MoveList;
import demo.chess.definitions.moves.impl.MoveListImpl;
import demo.chess.definitions.players.impl.BlackPlayerImpl;
import demo.chess.definitions.players.impl.WhitePlayerImpl;
import demo.chess.game.Game;
import demo.chess.game.impl.ChessGame;

/**
 * Main application class for the Chess application.
 * <p>
 * The `App` class serves as the entry point for the chess application. It
 * extends `ChessAdmin` and implements `AppAdmin`, allowing it to provide
 * essential services and beans for managing chess games, configuring view
 * settings, and initializing chess engines.
 * </p>
 *
 * <p>
 * This class configures the Spring Boot application and provides a range of
 * beans for application-wide use, including template resolvers, chess game
 * instances, and UCI chess engines. It ensures the application's configuration
 * is managed in a modular, Spring-compliant way, supporting the application's
 * view, logic, and chess game instances.
 * </p>
 */
@SpringBootApplication
public class App extends ChessAdmin implements AppAdmin {

	private static final Logger logger = LogManager.getLogger(App.class);

	/**
	 * Bean for the view configuration, providing display and layout settings for
	 * the application's views.
	 *
	 * @return a new instance of {@link ViewConfig} that holds view configuration
	 *         data
	 */
	@Bean
	public Config viewConfig() {
		return new ViewConfig();
	}

	/**
	 * Bean to resolve templates for Thymeleaf. Configures the template resolver
	 * with the template path, suffix, mode, and caching settings.
	 *
	 * @return a {@link SpringResourceTemplateResolver} for Thymeleaf templates
	 */
	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setPrefix("classpath:/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCacheable(true);
		return templateResolver;
	}

	/**
	 * Bean for the Thymeleaf template engine. Sets the previously defined template
	 * resolver and enables Spring EL compiler support.
	 *
	 * @return a configured {@link SpringTemplateEngine} instance
	 */
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setEnableSpringELCompiler(true);
		return templateEngine;
	}

	/**
	 * Bean for configuring the Thymeleaf view resolver. It binds the Thymeleaf
	 * template engine to the view resolver and sets its order of execution.
	 *
	 * @return a {@link ThymeleafViewResolver} for rendering views
	 */
	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		viewResolver.setOrder(1);
		return viewResolver;
	}

	/**
	 * Main method to start the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	/**
	 * Bean for creating a new chess game instance. Configures the chess game with
	 * the specified time, players, and chess board, and returns it for use in
	 * gameplay and simulation.
	 *
	 * @param time the time control setting for the game
	 * @return a new {@link ChessGame} instance
	 * @throws Exception if an error occurs during game initialization
	 */
	@Override
	@Bean
	@Scope("prototype")
	public Game chessGame(int time) throws Exception {
		MoveList moveList = new MoveListImpl();
		return new ChessGame(new ChessBoard(), new WhitePlayerImpl(moveList, "ChessGame"),
				new BlackPlayerImpl(moveList, "ChessGame"), moveList, this, time);
	}

	/**
	 * Bean to initialize and provide application attributes. Populates the
	 * attribute map with essential data like elements and fields, allowing
	 * components across the application to store and retrieve attribute values.
	 *
	 * @return a new {@link Attributes} instance containing initial attribute data
	 */
	@Bean(name = "attributes")
	@Override
	public Attributes attributes() {
		Attributes attributes = new AttributesImpl();
		attributes.put(KEY.ELEMENTS, new ArrayList<>());
		attributes.put(KEY.FIELDS, new ArrayList<>());
		return attributes;
	}

	/**
	 * Bean for configuring and providing evaluation engines. Each engine in
	 * {@link Engine} is instantiated and added to a map, allowing the application
	 * to retrieve and use different evaluation engines for move analysis.
	 *
	 * @return a map of engine names to their respective {@link EvaluationEngine}
	 *         instances
	 */
	@Bean
	@Override
	public Map<String, EvaluationEngine> evaluationEngines() {
		Map<String, EvaluationEngine> engines = new HashMap<>();
		for (Engine engine : Engine.values()) {
			try {
				engines.put(engine.toString(), new EvaluationUciEngine("/usr/games/" + engine.path()) {
					@Override
					public String toString() {
						return engine.toString();
					}
				});
			} catch (Exception e) {
				logger.info("Failed to create evaluation engine {}", engine);
			}
		}
		return engines;
	}

	/**
	 * Bean for configuring and providing player engines. Each engine in
	 * {@link Engine} is instantiated and added to a map, allowing the application
	 * to retrieve and use different player engines for simulating AI moves.
	 *
	 * @return a map of engine names to their respective {@link PlayerEngine}
	 *         instances
	 */
	@Bean
	@Override
	public Map<String, PlayerEngine> playerEngines() {
		Map<String, PlayerEngine> engines = new HashMap<>();
		for (Engine engine : Engine.values()) {
			try {
				engines.put(engine.toString(), new PlayerUciEngine("/usr/games/" + engine.path()) {
					@Override
					public String toString() {
						return engine.toString();
					}
				});
			} catch (Exception e) {
				logger.info("Failed to create player engine {}", engine);
			}
		}
		return engines;
	}

}
