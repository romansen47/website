package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.example.demo.elements.impl.AttributesImpl;
import com.example.demo.model.Config;
import com.example.demo.model.impl.ViewConfig;

import demo.chess.admin.impl.ChessAdmin;
import demo.chess.definitions.Color;
import demo.chess.definitions.board.impl.ChessBoard;
import demo.chess.definitions.engines.Engine;
import demo.chess.definitions.engines.PlayerEngine;
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
 * This class initializes the Spring Boot application and provides configuration
 * beans.
 * </p>
 */
@SpringBootApplication
public class App extends ChessAdmin implements AppAdmin {

	private static final Logger logger = LogManager.getLogger(App.class);

	/**
	 * Bean for the view configuration.
	 *
	 * @return a new instance of {@link ViewConfig}
	 */
	@Bean
	public Config viewConfig() {
		return new ViewConfig();
	}

	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setPrefix("classpath:/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCacheable(true);
		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setEnableSpringELCompiler(true);
		return templateEngine;
	}

	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		viewResolver.setOrder(1);
		return viewResolver;
	}

	/**
	 * Main method to run the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		try {
			SpringApplication.run(App.class, args);
		} catch (org.springframework.context.ApplicationContextException e) {
			logger.info("Another instance seems to be running. Exiting");
			System.exit(0);
		}
	}

	@Override
	@Bean
	@Scope("prototype")
	public Game chessGame(int time) throws Exception {
		MoveList moveList = new MoveListImpl();
		return new ChessGame(new ChessBoard(), new WhitePlayerImpl(moveList, "ChessGame"),
				new BlackPlayerImpl(moveList, "ChessGame"), moveList, this, time);
	}

	@Bean(name = "attributes")
	@Override
	public Attributes attributes() {
		Attributes attributes = new AttributesImpl();
		attributes.put("elements", new ArrayList<>());
		attributes.put("fields", new ArrayList<>());
		return attributes;
	}
	
	@Bean
	@Override
	public Map<Engine, PlayerEngine> playerEngines(){
		Map<Engine, PlayerEngine> engines = new HashMap<>();
		try {
			engines.put(Engine.STOCKFISH, new PlayerUciEngine("/usr/games/stockfish") {
				@Override
				public String toString() {
					return Engine.STOCKFISH.toString();
				}
			});
		} catch (Exception e) {
			logger.info("Failed to create player engine stockfish 16"); 
		}
		try {
			engines.put(Engine.GNUCHESS, new PlayerUciEngine("/usr/games/gnuchessu") {
				@Override
				public String toString() {
					return Engine.GNUCHESS.toString();
				}
			});
		} catch (Exception e) {
			logger.info("Failed to create player engine gnuchess"); 
		}
		return engines;
	}

}
