package com.example.demo.controller.helper;

import org.springframework.ui.Model;

import demo.chess.definitions.engines.EvaluationEngine;

public interface ViewControllerHelper {

	void addAttributes(String color, String whiteTimeString, String blackTimeString, Model model, EvaluationEngine evaluationEngine);

	void createNewFields();

	void setUnsetViewVariables(EvaluationEngine evaluationEngine);

	void setupEngineConfigurations();

}
