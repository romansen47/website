package com.example.demo.controller.helper.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.ImagePath;
import com.example.demo.controller.helper.ViewControllerHelper;
import com.example.demo.elements.Attributes;
import com.example.demo.elements.KEY;
import com.example.demo.model.Config;
import com.example.demo.websockets.WebSocketService;

import demo.chess.definitions.Color;
import demo.chess.definitions.PieceType;

public abstract class ChessHelper{

	@Autowired
	protected Config viewConfig;

	@Autowired
	protected Attributes attributes;

	@Autowired
	protected WebSocketService webSocketService;
	
	protected void put(KEY key, Object o) {
		attributes.put(key, o);
	}

	protected Object get(KEY key) {
		return attributes.get(key);
	}

	public String getImagePath(Color color, PieceType pieceType) {
		String imagePath;
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
		default:
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
