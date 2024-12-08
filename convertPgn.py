import chess.pgn

def pgn_to_uci_file(pgn_file, output_file):
    uci_moves = []

    # Öffnen der PGN-Datei
    with open(pgn_file, "r") as file:
        # Laden der ersten Partie aus der Datei
        game = chess.pgn.read_game(file)
        if game is None:
            print("Keine Partie in der PGN-Datei gefunden.")
            return

        # Erstelle ein Board-Objekt aus der Partie
        board = game.board()

        # Iteriere über die SAN-Züge in der Partie und wandle sie in UCI um
        for move in game.mainline_moves():
            uci_moves.append(move.uci())
            board.push(move)

    # Schreiben der UCI-Züge in die Ausgabedatei
    with open(output_file, "w") as out_file:
        for move in uci_moves:
            out_file.write(move + "\n")

    print(f"Die UCI-Züge wurden in {output_file} geschrieben.")

# Beispiel: PGN-Datei einlesen und UCI-Züge in test.txt schreiben
pgn_file = input("file to convert: ") # Ersetze dies durch deine PGN-Datei
output_file = "converted.txt"

pgn_to_uci_file(pgn_file, output_file)
