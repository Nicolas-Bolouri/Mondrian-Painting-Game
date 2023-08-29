package game;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {

	private int xCoord;
	private int yCoord;
	private int size; // height/width of the square
	private int level; // the root (outer most block) is at level 0
	private int maxDepth;
	private Color color;
	private Block[] children; // {UR, UL, LL, LR}

	public static Random gen = new Random(4);


	/*
	 * These two constructors are here for testing purposes.
	 */
	public Block() {
	}

	public Block(int x, int y, int size, int lvl, int maxD, Color c, Block[] subBlocks) {
		this.xCoord = x;
		this.yCoord = y;
		this.size = size;
		this.level = lvl;
		this.maxDepth = maxD;
		this.color = c;
		this.children = subBlocks;
	}

	/*
	 * Creates a random block given its level and a max depth.
	 *
	 * xCoord, yCoord, size, and highlighted should not be initialized
	 * (i.e. they will all be initialized by default)
	 */
	public Block(int lvl, int maxDepth) {
		this.level = lvl;
		this.maxDepth = maxDepth;
		this.children = new Block[0];
		this.color = null;

		if (level < maxDepth) {
			double randDouble = gen.nextDouble();
			if (randDouble < Math.exp(-0.25 * level)) {
				children = new Block[4];
				for (int i = 0; i < 4; i++) {
					children[i] = new Block(level + 1, maxDepth);
				}
			} else {
				color = GameColors.BLOCK_COLORS[gen.nextInt(GameColors.BLOCK_COLORS.length)];
			}
		} else {
			color = GameColors.BLOCK_COLORS[gen.nextInt(GameColors.BLOCK_COLORS.length)];
		}
	}

	/*
	 * Updates size and position for the block and all of its sub-blocks, while
	 * ensuring consistency between the attributes and the relationship of the
	 * blocks.
	 *
	 *  The size is the height and width of the block. (xCoord, yCoord) are the
	 *  coordinates of the top left corner of the block.
	 */
	public void updateSizeAndPosition(int size, int xCoord, int yCoord) {
		if (size < 0 || !isValidSize(size, this.level)) {
			throw new IllegalArgumentException("Invalid size: " + size);
		}

		this.size = size;
		this.xCoord = xCoord;
		this.yCoord = yCoord;

		if (children.length != 0) {
			int halfSize = size / 2;
			children[0].updateSizeAndPosition(halfSize, xCoord + halfSize, yCoord);
			children[1].updateSizeAndPosition(halfSize, xCoord, yCoord);
			children[2].updateSizeAndPosition(halfSize, xCoord, yCoord + halfSize);
			children[3].updateSizeAndPosition(halfSize, xCoord + halfSize, yCoord + halfSize);
		}
	}

	private boolean isValidSize(int size, int lvl) {
		if (maxDepth == lvl) {
			return true;
		} else {
			int halfSize = size / 2;
			return ((isValidSize(halfSize, lvl + 1)) && (size % 2 == 0));
		}
	}


 
	/*
  	* Returns a List of blocks to be drawn to get a graphical representation of this block.
  	* 
  	* This includes, for each undivided Block:
  	* - one BlockToDraw in the color of the block
  	* - another one in the FRAME_COLOR and stroke thickness 3
  	* 
  	* Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
  	*  
  	* The order in which the blocks to draw appear in the list does NOT matter.
  	*/
	public ArrayList<BlockToDraw> getBlocksToDraw() {
		ArrayList<BlockToDraw> blocksToDraw = new ArrayList<>();
		getBlocksToDrawHelper(this, blocksToDraw);
		return blocksToDraw;
	}

	private void getBlocksToDrawHelper(Block block, ArrayList<BlockToDraw> blocksToDraw) {
		if (block.children.length == 0) {
			// Add colored BlockToDraw
			blocksToDraw.add(new BlockToDraw(block.color, block.xCoord, block.yCoord, block.size, 0));

			// Add frame BlockToDraw
			blocksToDraw.add(new BlockToDraw(GameColors.FRAME_COLOR, block.xCoord, block.yCoord, block.size, 3));
		} else {
			for (Block subBlock : block.children) {
				getBlocksToDrawHelper(subBlock, blocksToDraw);
			}
		}
	}

	/*
	 * This method is provided and you should NOT modify it. 
	 */
	public BlockToDraw getHighlightedFrame() {
		return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
	}
 
 
 
	/*
	 * Return the Block within this Block that includes the given location
	 * and is at the given level. If the level specified is lower than 
	 * the lowest block at the specified location, then return the block 
	 * at the location with the closest level value.
	 * 
	 * The location is specified by its (x, y) coordinates. The lvl indicates 
	 * the level of the desired Block. Note that if a Block includes the location
	 * (x, y), and that Block is subdivided, then one of its sub-Blocks will 
	 * contain the location (x, y) too. This is why we need lvl to identify 
	 * which Block should be returned. 
	 * 
	 * Input validation: 
	 * - this.level <= lvl <= maxDepth (if not throw exception)
	 * - if (x,y) is not within this Block, return null.
	 */
	public Block getSelectedBlock(int x, int y, int lvl) {
		if (lvl < this.level || lvl > this.maxDepth) {
			throw new IllegalArgumentException("Invalid level provided. Provided level: " + lvl +
					", Current block level: " + this.level +
					", Max depth: " + this.maxDepth);
		}

		if (x < this.xCoord || x >= this.xCoord + this.size || y < this.yCoord || y >= this.yCoord + this.size) {
			return null;
		}

		if (this.children.length == 0 || this.level == lvl) {
			return this;
		}

		for (Block subBlock : this.children) {
			Block foundBlock = subBlock.getSelectedBlock(x, y, lvl);
			if (foundBlock != null) {
				return foundBlock;
			}
		}

		throw new IllegalStateException("Unexpected state in getSelectedBlock()");
	}



	/*
	 * Swaps the child Blocks of this Block. 
	 * If input is 1, swap vertically. If 0, swap horizontally. 
	 * If this Block has no children, do nothing. The swap 
	 * should be propagate, effectively implementing a reflection
	 * over the x-axis or over the y-axis.
	 * 
	 */
	public void reflect(int direction) {
		if (direction != 0 && direction != 1) {
			throw new IllegalArgumentException("Invalid direction provided.");
		}

		int sizeUpdate = this.size;
		int xCoordUpdate = this.xCoord;
		int yCoordUpdate = this.yCoord;

		reflectRecursively(direction);

		this.updateSizeAndPosition(sizeUpdate, xCoordUpdate, yCoordUpdate);
	}

	private void reflectRecursively(int direction) {
		if (this.children.length != 0) {
			if (direction == 0) { // Reflect over the x-axis
				this.children = new Block[]{this.children[3], this.children[2], this.children[1], this.children[0]};
			} else { // Reflect over the y-axis
				this.children = new Block[]{this.children[1], this.children[0], this.children[3], this.children[2]};
			}

			for (int i = 0; i < this.children.length; i++) {
				this.children[i].reflectRecursively(direction);
			}
		}
	}
 
	/*
	 * Rotate this Block and all its descendants. 
	 * If the input is 1, rotate clockwise. If 0, rotate 
	 * counterclockwise. If this Block has no children, do nothing.
	 */
	public void rotate(int direction) {
		if (direction != 0 && direction != 1) {
			throw new IllegalArgumentException("Invalid direction provided.");
		}

		int sizeUpdate = this.size;
		int xCoordUpdate = this.xCoord;
		int yCoordUpdate = this.yCoord;

		rotateRecursively(direction);

		this.updateSizeAndPosition(sizeUpdate, xCoordUpdate, yCoordUpdate);
	}

	private void rotateRecursively(int direction) {
		if (this.children.length != 0) {
			if (direction == 0) { // Counter-clockwise
				this.children = new Block[]{this.children[3], this.children[0], this.children[1], this.children[2]};
			} else { // Clockwise
				this.children = new Block[]{this.children[1], this.children[2], this.children[3], this.children[0]};
			}

			for (int i = 0; i < this.children.length; i++) {
				this.children[i].rotateRecursively(direction);
			}
		}
	}

	/*
	 * Smash this Block.
	 * 
	 * If this Block can be smashed,
	 * randomly generate four new children Blocks for it.  
	 * (If it already had children Blocks, discard them.)
	 * Ensure that the invariants of the Blocks remain satisfied.
	 * 
	 * A Block can be smashed iff it is not the top-level Block 
	 * and it is not already at the level of the maximum depth.
	 * 
	 * Return True if this Block was smashed and False otherwise.
	 * 
	 */
	public boolean smash() {
		if (this.level >= maxDepth || this.level == 0){
			return false;
		}
		Block topRight = new Block(this.level + 1, this.maxDepth);
		Block topLeft = new Block(this.level + 1, this.maxDepth);
		Block bottomLeft = new Block(this.level + 1, this.maxDepth);
		Block bottomRight = new Block(this.level + 1, this.maxDepth);

		this.children = new Block[] {topRight, topLeft, bottomLeft, bottomRight};
		this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
		return true;
	}
 
 
	/*
	 * Return a two-dimensional array representing this Block as rows and columns of unit cells.
	 * 
	 * Return and array arr where, arr[i] represents the unit cells in row i, 
	 * arr[i][j] is the color of unit cell in row i and column j.
	 * 
	 * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
	 */
	public Color[][] flatten() {
		int cellWorth = (int) (Math.pow(2, this.maxDepth - this.level));
		Color[][] flattened = new Color[cellWorth][cellWorth];

		if (this.children.length == 0) {
			for (int i = 0; i < cellWorth; i++) {
				for (int j = 0; j < cellWorth; j++) {
					flattened[i][j] = this.color;
				}
			}

		} else if (cellWorth == 1) {
			flattened[0][0] = this.color;

		} else {
			for (int z = 0; z < 4; z++) {
				Color[][] tempFlattened = this.children[z].flatten();
				merge(flattened, tempFlattened, z, cellWorth);
			}
		}
		return flattened;
	}

	public void merge(Color[][] master, Color[][] slave, int childIndex, int cellWorth) {
		int halfWorth = (cellWorth / 2);
		if (childIndex == 0) {
			for (int i = 0; i < halfWorth; i++) {
				for (int j = 0; j < halfWorth; j++) {
					master[i][j + halfWorth] = slave[i][j];
				}
			}
		} else if (childIndex == 1) {
			for (int i = 0; i < halfWorth; i++) {
				for (int j = 0; j < halfWorth; j++) {
					master[i][j] = slave[i][j];
				}
			}

		} else if (childIndex == 2) {
			for (int i = 0; i < halfWorth; i++) {
				for (int j = 0; j < halfWorth; j++) {
					master[i + halfWorth][j] = slave[i][j];
				}
			}

		} else if (childIndex == 3) {
			for (int i = 0; i < halfWorth; i++) {
				for (int j = 0; j < halfWorth; j++) {
					master[i + halfWorth][j + halfWorth] = slave[i][j];
				}
			}
		}
	}


	// These two get methods have been provided. Do NOT modify them. 
	public int getMaxDepth() {
		return this.maxDepth;
	}
 
	public int getLevel() {
		return this.level;
	}


	/*
	 * The next 5 methods are needed to get a text representation of a block. 
	 * You can use them for debugging. You can modify these methods if you wish.
	 */
	public String toString() {
		return String.format("pos=(%d,%d), size=%d, level=%d", this.xCoord, this.yCoord, this.size, this.level);
	}

	public void printBlock() {
		this.printBlockIndented(0);
	}

	private void printBlockIndented(int indentation) {
		String indent = "";
		for (int i=0; i<indentation; i++) {
			indent += "\t";
		}

		if (this.children.length == 0) {
			// it's a leaf. Print the color!
			String colorInfo = GameColors.colorToString(this.color) + ", ";
			System.out.println(indent + colorInfo + this);   
		} 
		else {
			System.out.println(indent + this);
			for (Block b : this.children)
				b.printBlockIndented(indentation + 1);
		}
	}
 
	private static void coloredPrint(String message, Color color) {
		System.out.print(GameColors.colorToANSIColor(color));
		System.out.print(message);
		System.out.print(GameColors.colorToANSIColor(Color.WHITE));
	}

	public void printColoredBlock(){
		Color[][] colorArray = this.flatten();
		for (Color[] colors : colorArray) {
			for (Color value : colors) {
				String colorName = GameColors.colorToString(value).toUpperCase();
				if(colorName.length() == 0){
					colorName = "\u2588";
				}
				else{
					colorName = colorName.substring(0, 1);
				}
				coloredPrint(colorName, value);
			}
			System.out.println();
		}
	}

	// FOR TESTING
	public static void main(String[] args) {
		Block blockDepth2 = new Block(0,3);
		blockDepth2.printColoredBlock();
	}

}