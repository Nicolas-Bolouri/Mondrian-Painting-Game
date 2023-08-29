package game;

import java.awt.Color;

public class PerimeterGoal extends Goal{

	public PerimeterGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		Color[][] flattenedBoard = board.flatten();
		int numRows = flattenedBoard.length;
		int numCols = flattenedBoard[0].length;
		int perimeterScore = 0;

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				if (i == 0 || i == numRows - 1 || j == 0 || j == numCols - 1) {
					if (flattenedBoard[i][j].equals(targetGoal)) {
						// Corner cells count twice
						if ((i == 0 || i == numRows - 1) && (j == 0 || j == numCols - 1)) {
							perimeterScore += 2;
						} else {
							perimeterScore++;
						}
					}
				}
			}
		}
		return perimeterScore;
	}


	@Override
	public String description() {
		return "Place the highest number of " + GameColors.colorToString(targetGoal) 
		+ " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
	}

}
