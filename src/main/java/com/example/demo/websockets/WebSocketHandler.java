package com.example.demo.websockets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocketHandler is responsible for handling WebSocket sessions and providing methods
 * to send specific messages to the client through WebSocket. This handler enables the server
 * to communicate with the client in real time by sending signals or updates as needed.
 *
 * <p>This class includes methods for establishing WebSocket connections, managing sessions,
 * and sending messages to update the frontend UI, such as reloading the interface or updating
 * the chess game elements (clocks, move list, etc.).
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {

	 /** Logger instance for logging WebSocket events and errors. */
    private static final Logger logger = LogManager.getLogger(WebSocketHandler.class);

    /**
     * Stores the active WebSocket session.
     * <p>
     * Declared as static to ensure that only one active session is maintained at a time,
     * enabling consistent handling of WebSocket messages and connection status.
     * </p>
     */
    private static WebSocketSession session;

    /**
     * Invoked after a WebSocket connection is established. Sets the session and logs the
     * session ID.
     *
     * @param session the active WebSocket session
     * @throws Exception if session establishment fails
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        WebSocketHandler.session = session;
        logger.info("WebSocket connection established with session ID: " + session.getId());
    }

    /**
     * Sends a reload signal to the client through WebSocket to trigger an interface refresh.
     * Verifies that the session is open before sending the message.
     *
     * @throws Exception if sending the message fails
     */
    public void sendReloadSignal() throws Exception {
        if (session != null && session.isOpen()) {
            logger.info("Sending reload signal to WebSocket session ID: " + session.getId());
            session.sendMessage(new TextMessage(WS_MESSAGE.RELOAD.toString()));
        } else {
            logger.info("WebSocket session is not open or null.");
        }
    }

    /**
     * Sends a custom message to the client. Prepends the message type to the message content.
     *
     * @param message the message to send
     * @throws Exception if sending the message fails
     */
    public void sendMessage(String message) throws Exception {
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(WS_MESSAGE.MESSAGE.toString() + message));
        } else {
            logger.info("WebSocket session is not open or null.");
        }
    }

    /**
     * Sends a message to update the clocks in the client UI. Checks if the session is open.
     *
     * @throws Exception if sending the message fails
     */
    public void updateClocks() throws Exception {
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(WS_MESSAGE.CLOCKS.toString()));
        } else {
            logger.info("WebSocket session is not open or null.");
        }
    }

    /**
     * Sends a message to update the move list displayed on the client side.
     *
     * @throws Exception if sending the message fails
     */
    public void updateMoveList() throws Exception {
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(WS_MESSAGE.MOVELIST.toString()));
        } else {
            logger.info("WebSocket session is not open or null.");
        }
    }

    /**
     * Sends a signal to trigger the UCI engine's next move on the client side.
     *
     * @throws Exception if sending the message fails
     */
    public void triggerUciEngineMove() throws Exception {
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(WS_MESSAGE.TRIGGERSTOCKFISHMOVE.toString()));
        } else {
            logger.info("WebSocket session is not open or null.");
        }
    } 

    /**
     * Enumeration representing the types of WebSocket messages used for
     * communication with the client.
     * <p>
     * Each constant in this enumeration corresponds to a specific message type,
     * indicating various actions or updates that can be triggered on the client
     * side, such as reloading the interface, updating clocks, or triggering engine
     * moves.
     * </p>
     */
    public static enum WS_MESSAGE {

        /**
         * Signals the client to reload the interface.
         */
        RELOAD,

        /**
         * Generic message type for sending custom messages to the client.
         */
        MESSAGE,

        /**
         * Updates the clocks displayed in the client UI.
         */
        CLOCKS,

        /**
         * Triggers a move by the UCI engine, updating the game state on the client.
         */
        TRIGGERSTOCKFISHMOVE,

        /**
         * Updates the move list displayed in the client UI.
         */
        MOVELIST;
    }

}
