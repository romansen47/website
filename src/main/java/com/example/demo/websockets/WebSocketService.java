package com.example.demo.websockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

	private final WebSocketHandler webSocketHandler;

	@Autowired
	public WebSocketService(WebSocketHandler webSocketHandler) {
		this.webSocketHandler = webSocketHandler;
	}

	public void sendReloadSignal() {
		try {
			webSocketHandler.sendReloadSignal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		try {
			webSocketHandler.sendMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void triggerUciEngineMove() {
		try {
			webSocketHandler.triggerUciEngineMove();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateClocks() {
		try {
			webSocketHandler.updateClocks();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
