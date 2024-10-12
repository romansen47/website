package com.example.demo.controller.helper;

import org.springframework.ui.Model;

public interface ViewControllerHelper {

	void addAttributes(String color, String whiteTimeString, String blackTimeString, Model model);

	void createNewFields();

	void setUnsetVariables();

}
