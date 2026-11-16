package project1;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class finderMap {
    String[][] map;
    int[][] visited;    
    boolean finished = false;
    int[] entrance = new int[2];

    private int totalCost;
    private int totalMoves;
    
    public finderMap(String map){
        if (map == null || map.length() == 0) {
            throw new IllegalArgumentException("The maze is empty.");
        }
        String[] rows = map.split("\n");
        int columns = rows[0].length();
        int starts = 0;
        int exits = 0;

        this.map = new String[rows.length][columns];
        this.visited = new int[rows.length][columns];

        if (columns == 0) {
            throw new IllegalArgumentException("The maze has an empty row.");
        }
        
        for(int i = 0; i < rows.length; i++){
            if (rows[i].length() != columns) {
                throw new IllegalArgumentException("Every row must have the same number of characters.");
            }
            char[] line = rows[i].toCharArray();
            for(int j = 0; j < line.length; j++){
                
                if(line[j] == '#'){
                    entrance[0] = i;
                    entrance[1] = j;
                    starts++;
                }
                if(line[j] == '!'){
                    exits++;
                }
                this.visited[i][j] = 0;
                this.map[i][j] = String.valueOf(line[j]);
            }
        }
        if(starts != 1 || exits != 1){
            throw new IllegalArgumentException("The maze must contain one # and one !.");
        }
    }
    private boolean inBounds(int row, int col) {
        return row >= 0 && row < this.map.length && col >= 0 && col < this.map[row].length;
    } 
    private boolean getPath(int row, int col){
        if (!inBounds(row,col) || map[row][col].equalsIgnoreCase("x")){
            return false;
        }
        return true;
    }

    public int getVisited(int row, int col){
        if(!inBounds(row,col)){
            return 0;
        }
        return this.visited[row][col];
    }
    public int getCost(int row, int col) {
        if (!getPath(row, col)) {
            return -1;
        }
        if (map[row][col].equals("#") || map[row][col].equals("!")) {
            return 0;
        }
        return Integer.parseInt(map[row][col], 16);
    }
    public boolean solve() {
        this.totalCost = 0;
        this.totalMoves = 0;

        for (int y = 0; y < this.visited.length; y++) {
            for (int x = 0; x < this.visited[y].length; x++) {
                this.visited[y][x] = 0;
            }
        }
        return floodFill(entrance[0], entrance[1], 1, 0);
    }
    public boolean setVisited(int x, int y, int visitNum) {
        if (!getPath(x, y) || visitNum < 0) {
            return false;
        }

        this.visited[x][y] = visitNum;
        return true;
    }
    // Recursively searches for the exit and erases incorrect moves.
    private boolean floodFill(int row, int col, int visitNum, int currentCost) {
        if (!getPath(row, col)) {
            return false;
        }
        if (getVisited(row, col) != 0) {
            return false;
        }
        setVisited(row, col, visitNum);
        currentCost += getCost(row, col);

        if (this.map[row][col].equals("!")) {
            this.totalCost = currentCost;
            this.totalMoves = visitNum - 1;
            return true;
        }

        // Try up.
        if (floodFill(row - 1, col, visitNum + 1, currentCost)) {
            return true;
        }

        // Try down.
        if (floodFill(row + 1, col, visitNum + 1, currentCost)) {
            return true;
        }

        // Try left.
        if (floodFill(row, col -1, visitNum + 1, currentCost)) {
            return true;
        }

        // Try right.
        if (floodFill(row, col + 1, visitNum + 1, currentCost)) {
            return true;
        }

        // All directions failed, so erase this move.
        setVisited(row, col, 0);
        return false;
    }

    // Returns the maze with numbered steps on the successful route.
    public String getString() {
        StringBuilder result = new StringBuilder();

        int maxSteps = this.map.length * this.map[0].length;
        int width = Integer.toString(maxSteps).length() + 1;

        for (int y = 0; y < this.map.length; y++) {
            for (int x = 0; x < this.map[y].length; x++) {
                String square;

                if (this.visited[y][x] > 0) {
                    square = Integer.toString(this.visited[y][x]);
                } else {
                    square = this.map[y][x];
                }

                result.append(String.format("%" + width + "s", square));
            }

            if (y < this.map.length - 1) {
                result.append('\n');
            }
        }

        return result.toString();
    }

    public static void main(String[] args){
        if (args.length < 1) {
            System.out.println("Error: Please provide a file path.");
            System.exit(1); 
        }
        File file = new File(args[0]);
        StringBuilder test = new StringBuilder();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                test.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: The file '" + args[0] + "' could not be found.");
        }
        finderMap testmap = new finderMap(test.toString());

        if (testmap.solve()) {
            System.out.println(testmap.getString());
            System.out.println("Total Cost: " + testmap.totalCost);
            System.out.println("Total Moves: " + testmap.totalMoves);
        } else {
            System.out.println("No solution found.");
        }
    }
}
 
    