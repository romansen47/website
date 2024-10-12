package com.example.demo.controller.helper;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import demo.chess.definitions.engines.impl.NoMoveFoundException;
import demo.chess.definitions.fields.Field;
import demo.chess.definitions.moves.Move;
import demo.chess.definitions.moves.Promotion;
import demo.chess.definitions.pieces.Piece;

@Component
public interface ApiControllerHelper {

	List<Field> getSourceFields() throws NoMoveFoundException, IOException;

	List<Field> getTargetFields() throws NoMoveFoundException, IOException;

	List<Move> getPossibleMoves() throws NoMoveFoundException, IOException;

	void setValidPromotions(List<Move> moveList, Move chessMove, List<Promotion> validPromotions);

	double getRatioEvalBars(double eval);

	int[] fileRankFor(int top, int left);

	String getUnicodeSymbol(Piece piece);

	List<String> getMoveListWithSymbols();

	void sendReloadSignal();

	List<Pair<Double, String>> removeDuplicatesByString(List<Pair<Double, String>> list);

	String reset() throws Exception;

	List<String> getStockFishMoveList() throws Exception;
}
