package com.example.demo.websockets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

	private static final Logger logger = LogManager.getLogger(WebSocketHandler.class);

	private static WebSocketSession session;

	@Override
	public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
		WebSocketHandler.session = session;
		logger.info("WebSocket connection established with session ID: " + session.getId());
	}

	public void sendReloadSignal() throws Exception {
		if (session != null && session.isOpen()) {
			logger.info("Sending reload signal to WebSocket session ID: " + session.getId());
			session.sendMessage(new TextMessage(WS_MESSAGE.RELOAD.toString()));
		} else {
			logger.info("WebSocket session is not open or null.");
		}
	}

	public void sendMessage(String message) throws Exception {
		if (session != null && session.isOpen()) {
			session.sendMessage(new TextMessage(WS_MESSAGE.MESSAGE.toString() + message));
		} else {
			logger.info("WebSocket session is not open or null.");
		}
	}

	public void updateClocks() throws Exception {
		if (session != null && session.isOpen()) {
			session.sendMessage(new TextMessage(WS_MESSAGE.CLOCKS.toString()));
		} else {
			logger.info("WebSocket session is not open or null.");
		}
	}
	
	public void triggerStockfishMove() throws Exception {
		if (session != null && session.isOpen()) {
			session.sendMessage(new TextMessage(WS_MESSAGE.TRIGGERSTOCKFISHMOVE.toString()));
		} else {
			logger.info("WebSocket session is not open or null.");
		}
	}
	
	public static enum WS_MESSAGE{
		RELOAD, MESSAGE, CLOCKS, TRIGGERSTOCKFISHMOVE;
	}

}
