package game;

import java.awt.Color;

public class BlobGoal extends Goal{

	public BlobGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		Color[][] flattenedBoard = board.flatten();
		int numRows = flattenedBoard.length;
		int numCols = flattenedBoard[0].length;
		boolean[][] visited = new boolean[numRows][numCols];

		int maxBlobSize = 0;

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				if (!visited[i][j] && flattenedBoard[i][j].equals(targetGoal)) {
					int blobSize = undiscoveredBlobSize(i, j, flattenedBoard, visited);
					maxBlobSize = Math.max(maxBlobSize, blobSize);
				}
			}
		}

		return maxBlobSize;
	}


	@Override
	public String description() {
		return "Create the largest connected blob of " + GameColors.colorToString(targetGoal) 
		+ " blocks, anywhere within the block";
	}


	public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
		if (i < 0 || i >= unitCells.length || j < 0 || j >= unitCells[0].length || visited[i][j] || !unitCells[i][j].equals(targetGoal)) {
			return 0;
		}

		visited[i][j] = true;
		int blobSize = 1;

		blobSize += undiscoveredBlobSize(i - 1, j, unitCells, visited);
		blobSize += undiscoveredBlobSize(i + 1, j, unitCells, visited);
		blobSize += undiscoveredBlobSize(i, j - 1, unitCells, visited);
		blobSize += undiscoveredBlobSize(i, j + 1, unitCells, visited);

		return blobSize;
	}

}
