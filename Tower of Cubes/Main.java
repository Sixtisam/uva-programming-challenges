import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.stream.Collectors;

/**
 * 10051 - Tower of cubes
 * My tower of cubes solution uses the dynamic programming approach.
 * For all possible colors, the current highest tower with that color on top is memoized.
 * At the beginnin, each color has a max height of 0. With more cubes being added (with all their sides)
 * the tower for each color grows.
 * At the end, all towers are inspected and the biggest one is the solution, which is then output
 * 
 * @date 12.11.2019
 * @author Samuel Keusch
 */
public class Main {
	
	/**
	 * holds the cubes that are currently investigated
	 */
	public static int[][] cubes = null;
	
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			int counter = 1;
			StringBuilder sb = new StringBuilder(); // use StringBuilder to collect ALL output (its faster than System.out)
            while (reader.ready()) {
                int numOfCubes = Integer.parseInt(reader.readLine());
				if(numOfCubes == 0) {
					System.out.print(sb); // output everything
					return;
				};
				
				
				/// 2-dimension array for cubes: First dimension is the cube index, second dimension the 6 sides
                cubes = new int[numOfCubes][];
				// read in all the cubes with their 6 sides
                for (int i = 0; i < numOfCubes; i++) {
					String[] line = reader.readLine().trim().split(" ", 6);
					
					// read in all sides of the cube
					cubes[i] = new int[]{
						Integer.parseInt(line[0]),
						Integer.parseInt(line[1]),
						Integer.parseInt(line[2]),
						Integer.parseInt(line[3]),
						Integer.parseInt(line[4]),
						Integer.parseInt(line[5]),
					};
                }
				
				// between each testcase must be an empty line
				if(counter > 1){
					sb.append(System.lineSeparator());
				}
				sb.append("Case #" + counter++);
				sb.append(System.lineSeparator());
				sb.append(solve(cubes));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	/**
	 * Solves tower of cubes according to UVA description.
     */
	public static StringBuilder solve(int[][] cubes){
		// Holds for each color the maximum tower discovered so far.
		// - first dimension is
		// - [i] contains the highest tower so far for color i
		// - [][0][0] size of tower resp. index of roof element
		// - [][0][1] color of roof cube
		// - [][][0] size of tower
		// - [][][1] top side of cube
		// - starting at [2] the tower from bottom
		int[][][] colorTowers = new int[101][][];
		
		// For each cube, place it on all existing color towers where possible.
		// Only the highest tower for each color is kept.
		for(int i = cubes.length - 1; i >= 0; i--){
			stackCube(i, colorTowers);
		}
		
		// inspect all towers and get the highest one
		StringBuilder sb = new StringBuilder();
		int[][] highestTower = colorTowers[0];
		for(int i = 0; i < colorTowers.length; i++){
			if(colorTowers[i] != null && (highestTower == null || colorTowers[i][0][0] > highestTower[0][0])){
				highestTower = colorTowers[i];
			}
		}
		
		// transform highest tower into a string
		sb.append(highestTower[0][0]);
		sb.append(System.lineSeparator());
		for(int i = highestTower[0][0]; i > 0; i--){
			sb.append(highestTower[i][0] + 1);
			sb.append(" ");
			sb.append(sideName(highestTower[i][1]));
			sb.append(System.lineSeparator());
		}

		return sb;
	}
	
	/**
	 * For all 6 sides, the cube is placed onto the existing tower for that color.
	 * If the resulting tower is higher than the current tower of the new top color,
	 * that tower is being replace (this operation is done at the end of the function, because
	 * any other attempts must be played on the state of 'towers' before this call).
	 */
	public static void stackCube(int cubeIndex, int[][][] colorTowers){
		// all "attempts" must be made on the towers before the cube is placed on any tower.
		int[][][] tmpTowers = new int[101][][];
		// cube has 6 sides, so he can be placed onto 6 different towers
		for(int side = 0; side < 6; side++){
			int bottomColor = cubes[cubeIndex][side]; // bottom color of cube to stack
			int topSide = oppositeSide(side); // top side of cube to stack
			int topColor = cubes[cubeIndex][topSide]; // top color of cube
			int originHeight = colorTowers[bottomColor] == null ? 0 : colorTowers[bottomColor][0][0]; // height of tower on which the cube is placed
			
			// if there is no tower with top color 'topColor' or the existing color tower is smaller than the tower created by placing this cube onto it.
			// set the new tower to originTower + cube placed on top
			if((colorTowers[topColor] == null || colorTowers[topColor][0][0] < originHeight + 1) && 
				(tmpTowers[topColor] == null || tmpTowers[topColor][0][0] < originHeight + 1)){
				int[][] newTower = copyTower(colorTowers[bottomColor]); // copies or creates a new tower
				stackCubeSideOntoTower(newTower, cubeIndex, topSide); // place the cube onto the copied tower
				tmpTowers[topColor] = newTower; // buffer changed towers 
			}
		}
		// now we can move those towers, that exceed the old towers to the real towers array
		for(int i = 0; i < tmpTowers.length; i++){
			if(tmpTowers[i] != null){
				colorTowers[i] = tmpTowers[i];
			}
		}
	}
	
	/**
	 * Stacks a cube with a specified side onto the passed tower.
	 */
	public static void stackCubeSideOntoTower(int[][] tower, int cubeIndex, int side){
		// [0][0] keeps the roofIndex of the tower, therefor we need to increment it.
		int roofIndex = tower[0][0];
		tower[0][0] = roofIndex + 1; 
		// put the new roof cube onto the tower
		tower[roofIndex + 1] = new int[]{cubeIndex, side};
	}
	
	/**
	 * Copy or create a tower
	 */
	public static int[][] copyTower(int[][] tower){
		// if tower does not exist yet: Create new one
		if(tower == null){
			int[][] newTower = new int[1 + cubes.length][];
			newTower[0] = new int[]{0 /*roof index*/,-1}; // must be set to 0 to work properly
			return newTower;
		} else {
			// if tower already exists: Copy tower
			int[][] newTower = new int[1 + cubes.length][];
			for(int i = 0; i < newTower.length; i++){
				if(tower[i] != null){
					newTower[i] = new int[]{tower[i][0], tower[i][1]};
				}
			}

			return newTower;
		}
	}
	
	/**
	 * Returns the opposite side of a cube's side
	 */
	public static int oppositeSide(int side){
		switch(side){
			case 0: return 1;
			case 1: return 0;
			case 2: return 3;
			case 3: return 2;
			case 4: return 5;
			case 5: return 4;
			default: throw new RuntimeException("invalid side supplied");
		}
	}
	
	/** 
	 * Returns the name of a side
	 * 0 front
	 * 1 back
	 * 2 left
	 * 3 right
	 * 4 top
	 * 5 bottom
     */
	public static String sideName(int side){
		switch(side){
			case 0: return "front";
			case 1: return "back";
			case 2: return "left";
			case 3: return "right";
			case 4: return "top";
			case 5: return "bottom";
			default: throw new RuntimeException("invalid side supplied");
		}
	}
}