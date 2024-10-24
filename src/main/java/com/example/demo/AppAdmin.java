package com.example.demo;

import java.util.Map;

import com.example.demo.elements.Attributes;

import demo.chess.admin.Admin;
import demo.chess.definitions.engines.PlayerEngine;

public interface AppAdmin extends Admin {

	Map<String, PlayerEngine> playerEngines();

	Attributes attributes();
}
