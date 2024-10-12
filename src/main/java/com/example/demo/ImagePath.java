package com.example.demo;

/**
 * Enum representing the paths to image files for chess pieces.
 */
public enum ImagePath {

	/** Path to the image of the white rook piece. */
	WHITE_ROOK("/white_rook.png"),
	/** Path to the image of the white knight piece. */
	WHITE_KNIGHT("/white_knight.png"),
	/** Path to the image of the white bishop piece. */
	WHITE_BISHOP("/white_bishop.png"),
	/** Path to the image of the white queen piece. */
	WHITE_QUEEN("/white_queen.png"),
	/** Path to the image of the white king piece. */
	WHITE_KING("/white_king.png"),
	/** Path to the image of the white pawn piece. */
	WHITE_PAWN("/white_pawn.png"),

	/** Path to the image of the black rook piece. */
	BLACK_ROOK("/black_rook.png"),
	/** Path to the image of the black knight piece. */
	BLACK_KNIGHT("/black_knight.png"),
	/** Path to the image of the black bishop piece. */
	BLACK_BISHOP("/black_bishop.png"),
	/** Path to the image of the black queen piece. */
	BLACK_QUEEN("/black_queen.png"),
	/** Path to the image of the black king piece. */
	BLACK_KING("/black_king.png"),
	/** Path to the image of the black pawn piece. */
	BLACK_PAWN("/black_pawn.png");

	/** The file path to the image. */
	public final String path;

	/**
	 * Constructs an ImagePath enum with the specified file path.
	 *
	 * @param label the file path to the image
	 */
	private ImagePath(String label) {
		this.path = label;
	}
}
