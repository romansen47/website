package com.example.demo.controller.helper.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.ImagePath;
import com.example.demo.elements.Attributes;
import com.example.demo.elements.KEY;
import com.example.demo.model.Config;
import com.example.demo.websockets.WebSocketService;

import demo.chess.definitions.Color;
import demo.chess.definitions.PieceType;

/**
 * Abstract helper class for chess-related utilities and shared logic. This
 * class provides methods to manage chess piece images and game attributes. It
 * also facilitates access to configurations and WebSocket services.
 */
public abstract class ChessHelper {

	/**
	 * Configuration settings related to the view, used to manage and access various
	 * display settings, such as board orientation, square size, and visual
	 * preferences for the chess application.
	 */
	@Autowired
	protected Config viewConfig;

	/**
	 * Attribute manager that facilitates managing game-specific data using
	 * predefined keys. This provides access to core elements, field data, and
	 * various states required across the game lifecycle.
	 */
	@Autowired
	protected Attributes attributes;

	/**
	 * WebSocket service used for handling real-time communication and updates with
	 * the frontend. This service is essential for synchronizing the chessboard,
	 * clocks, and game states across different user interfaces.
	 */
	@Autowired
	protected WebSocketService webSocketService;

	/**
	 * Stores an attribute in the shared attribute map.
	 *
	 * @param key The unique key to identify the attribute
	 * @param o   The value to store
	 */
	protected void put(KEY key, Object o) {
		attributes.put(key, o);
	}

	/**
	 * Retrieves an attribute from the shared attribute map.
	 *
	 * @param key The unique key identifying the attribute
	 * @return The stored attribute value, or null if it does not exist
	 */
	protected Object get(KEY key) {
		return attributes.get(key);
	}

	/**
	 * Gets the image path for a chess piece based on its color and type.
	 *
	 * @param color     The color of the piece (e.g., WHITE or BLACK)
	 * @param pieceType The type of the piece (e.g., KING, QUEEN, ROOK, etc.)
	 * @return The file path of the image representing the piece
	 */
	public String getImagePath(Color color, PieceType pieceType) {
		String imagePath;

		// Determine the correct image path based on the piece color and type
		switch (color) {
		case WHITE:
			switch (pieceType) {
			case KING:
				imagePath = ImagePath.WHITE_KING.path;
				break;
			case QUEEN:
				imagePath = ImagePath.WHITE_QUEEN.path;
				break;
			case ROOK:
				imagePath = ImagePath.WHITE_ROOK.path;
				break;
			case KNIGHT:
				imagePath = ImagePath.WHITE_KNIGHT.path;
				break;
			case BISHOP:
				imagePath = ImagePath.WHITE_BISHOP.path;
				break;
			default:
				imagePath = ImagePath.WHITE_PAWN.path;
				break;
			}
			break;

		default: // BLACK
			switch (pieceType) {
			case KING:
				imagePath = ImagePath.BLACK_KING.path;
				break;
			case QUEEN:
				imagePath = ImagePath.BLACK_QUEEN.path;
				break;
			case ROOK:
				imagePath = ImagePath.BLACK_ROOK.path;
				break;
			case KNIGHT:
				imagePath = ImagePath.BLACK_KNIGHT.path;
				break;
			case BISHOP:
				imagePath = ImagePath.BLACK_BISHOP.path;
				break;
			default:
				imagePath = ImagePath.BLACK_PAWN.path;
				break;
			}
		}

		return imagePath;
	}
}
