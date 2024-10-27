package com.example.demo.controller.helper;

import java.io.IOException;
import java.util.Map;

import org.springframework.ui.Model;

import demo.chess.definitions.engines.ChessEngine;
import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.definitions.engines.PlayerEngine;
import demo.chess.game.Game;

public interface ViewControllerHelper {

	void addModelAttributes(String color, String whiteTimeString, String blackTimeString, Model model);

	void createNewFields();

	void setUnsetViewVariables(EvaluationEngine evaluationEngine);

	void setupEngineConfigurations();

	void createNewPiecesFromExistingPieces(Game chessGame);

	void createShutdownHooks(Map<String, ? extends ChessEngine> playerEngines);

	void seupClocks(Game chessGame);

	void saveGame(String string, Game chessGame)throws IOException;

	void updateUciEngineSettings(int uciEngineDepthForWhite, int threadsForWhite, int hashSizeForWhite,
			int contemptForWhite, int moveOverheadForWhite, int uciEloForWhite, int uciEngineDepthForBlack,
			int threadsForBlack, int hashSizeForBlack, int contemptForBlack, int moveOverheadForBlack,
			int uciEloForBlack, String selectedEngineForWhite, String selectedEngineForBlack, Map<String, PlayerEngine> playerEngines);


}
