package com.example.demo;

import com.example.demo.elements.Attributes;

import demo.chess.admin.Admin;
import demo.chess.definitions.engines.PlayerEngine;

public interface AppAdmin extends Admin {

	PlayerEngine playerEngineForWhite() throws Exception;

	PlayerEngine playerEngineForBlack() throws Exception;

	Attributes attributes();
}
