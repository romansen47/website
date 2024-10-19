package com.example.demo.controller.helper;

import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;

import demo.chess.definitions.engines.Engine;
import demo.chess.definitions.engines.EvaluationEngine;

public interface ViewControllerHelper {

	void addAttributes(String color, String whiteTimeString, String blackTimeString, Model model);

	void createNewFields();

	void setUnsetViewVariables(EvaluationEngine evaluationEngine);

	void setupEngineConfigurations();

}
