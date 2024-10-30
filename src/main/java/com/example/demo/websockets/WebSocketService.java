package com.example.demo.websockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * WebSocketService provides a simplified API for sending WebSocket messages from
 * various parts of the application to the client. This service acts as a bridge
 * between application events and WebSocket communication, encapsulating the
 * underlying WebSocketHandler functionalities.
 *
 * <p>Through this service, other components can trigger client updates for actions
 * like reloading the interface, updating clocks, or sending messages, without needing
 * direct access to the WebSocketHandler.</p>
 */
@Service
public class WebSocketService {

    private final WebSocketHandler webSocketHandler;

    /**
     * Constructor to inject the WebSocketHandler dependency.
     *
     * @param webSocketHandler the WebSocket handler for direct message sending
     */
    @Autowired
    public WebSocketService(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * Sends a signal to the client to reload the UI. This is useful for refreshing
     * the interface when critical updates occur.
     */
    public void sendReloadSignal() {
        try {
            webSocketHandler.sendReloadSignal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a custom message to the client through WebSocket.
     *
     * @param message the message content to send
     */
    public void sendMessage(String message) {
        try {
            webSocketHandler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a trigger to the client to initiate the UCI engine's next move. This
     * supports engine-controlled chess moves in the application.
     */
    public void triggerUciEngineMove() {
        try {
            webSocketHandler.triggerUciEngineMove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an update message for the clocks on the client side. This method
     * helps keep the client clocks in sync with server-side time updates.
     */
    public void updateClocks() {
        try {
            webSocketHandler.updateClocks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an update message for the move list on the client side, allowing the
     * client to display the latest list of moves in real time.
     */
    public void updateMoveList() {
        try {
            webSocketHandler.updateMoveList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
}
