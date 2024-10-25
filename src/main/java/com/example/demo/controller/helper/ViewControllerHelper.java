package com.example.demo.controller.helper;

import org.springframework.ui.Model;

import demo.chess.definitions.engines.EvaluationEngine;
import demo.chess.game.Game;

public interface ViewControllerHelper {

	void addAttributes(String color, String whiteTimeString, String blackTimeString, Model model);

	void createNewFields();

	void setUnsetViewVariables(EvaluationEngine evaluationEngine);

	void setupEngineConfigurations();

	void createNewPiecesFromExistingPieces(Game chessGame);

}
