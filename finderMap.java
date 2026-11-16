package project1;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Project 1 finderMap class for reading, solving, and displaying mazes.
 *
 * @author Isabel Stec 
 * @author Eddy Perez
 * @version 1.0
 */
public class finderMap {
    //creating array to store given map (which contains cost of each spot)
    String[][] map;
    //array to store if a spot has been visited and which step it was
    int[][] visited;    
    int[] entrance = new int[2];

    //keeping track of routes taken and their costs
    completeRoute[] completed = new completeRoute[100];
    int routeCount;
    completeRoute cheapest = null;
    int cheapestCost = Integer.MAX_VALUE;
    private int[][] bestVisited;
    private int bestMoves;

    /**
     * finderMap constructor, parses the provided maze and stores into map array 
     *
     * @param map the maze to be solved
     * 
     */
    public finderMap(String map){
        if (map == null || map.length() == 0) {
            throw new IllegalArgumentException("The maze is empty.");
        }
        //getting sizes
        String[] rows = map.split("\n");
        int columns = rows[0].length();
        int starts = 0;
        int exits = 0;

        if (columns == 0) {
            throw new IllegalArgumentException("The maze has an empty row.");
        }

        //setting up arrays
        this.map = new String[rows.length][columns];
        this.visited = new int[rows.length][columns];

        //parsing map and taking note of the entrance and exti
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
                this.visited[i][j] = 0; //haven't gone anywhere so visited array contains 0
                this.map[i][j] = String.valueOf(line[j]);
            }
        }
        if(starts != 1 || exits != 1){
            throw new IllegalArgumentException("The maze must contain one # and one !.");
        }
    }
    /**
     * inBounds checks to see if the coordinates from the parameters are within the maze 
     *
     * @param row the row of the coordinate
     * @param col you'll never guess
     * @return true if the coordinates are within scope of the maze, false if not
     */
    private boolean inBounds(int row, int col) {
        return row >= 0 && row < this.map.length && col >= 0 && col < this.map[row].length;
    } 
    /**
     * getPath will tell you if coordinates are on the path or wall
     *
     * @param row the row of the coordinate
     * @param col the column of the coordinate
     * @return boolean based on if the spot is an x or a cost value
     */
    private boolean getPath(int row, int col){
        if (!inBounds(row,col) || map[row][col].equalsIgnoreCase("x")){
            return false;
        }
        return true;
    }
    /**
     * getVisited will tell you if you visited a certain spot
     *
     * @param row the row of the coordinate
     * @param col the column of the coordinate
     * @return either 0 if wall/out of bounds/not visited, step # if it was visited
     */
    public int getVisited(int row, int col){
        if(!inBounds(row,col)){
            return 0;
        }
        return this.visited[row][col];
    }
    /**
     * setVisited will change the spot on a map if it has been visited
     *
     * @param row the row of the coordinate
     * @param col the column of the coordinate
     * @param visitNum the step that is currently being taken
     * @return true once spot was able to be visited
     */
    public boolean setVisited(int row, int col, int visitNum){
        if(!getPath(row,col) || visitNum < 0){
            return false;
        }
        visited[row][col] = visitNum;
        return true;
    }
    /**
     * getCost checks the provided map (which still has the og cost values) for the cost of a given spot
     *
     * @param row the row of the coordinate
     * @param col the column of the coordinate
     * @return the cost of that spot on the map
     */
    public int getCost(int row, int col){
        if (!inBounds(row,col) || map[row][col].equalsIgnoreCase("x") ){
            return -1;
        }
        if(map[row][col].equals("#") || map[row][col].equals("!") || map[row][col].equals("p")){
            return 0;
        }
        return Integer.parseInt(map[row][col], 16);
    }
    /**
     * getTotalCost cheapest cost getter
     *
     * @return the cheapest cost found
     */
    public int getTotalCost() {
        if (cheapest == null) {
            return 0;
        }
        return cheapestCost;
    }
    /**
     * getTotalMoves returns the total number of moves in the best route
     *
     * @return bestMoves count
     */
    public int getTotalMoves() {
        if (cheapest == null) {
            return 0;
        }
        return bestMoves;
    }
    /**
     * copyVisited makes a copy of the visited array
     *
     * @return new copy of the visited array for new Routes that might not be the best
     */
    private int[][] copyVisited() {
        int[][] copy = new int[visited.length][];
        for (int i = 0; i < visited.length; i++) {
            copy[i] = visited[i].clone();
        }
        return copy;
    }
    /**
     * saveRoute saves a completed route and comapres cost with cheapest route found
     *
     * @param currentCost current cheapest route
     * @param totalMoves current move total
     * 
     */
    private void saveRoute(int currentCost, int totalMoves) {
        int[][] lastVisited = copyVisited();
        completeRoute solution = new completeRoute(map, lastVisited);

        //making sure route array still has room, if not increase size
        if (routeCount == completed.length) {
            completeRoute[] larger = new completeRoute[completed.length * 2];
            for (int i = 0; i < routeCount; i++) {
                larger[i] = completed[i];
            }
            completed = larger;
        }
        completed[routeCount] = solution;
        routeCount++;

        //if new completeRoute is better than previous, update values to hold the new ones
        if (cheapest == null || currentCost < cheapestCost) {
            cheapestCost = currentCost;
            cheapest = solution;
            bestVisited = lastVisited;
            bestMoves = totalMoves;
        }
    }
    /**
     * solve clears any old values from previous runs and then runs the floodFill method, saving thebest route
     *
     * @return true if solved
     */
    public boolean solve() {
        //clean up in aisle 4
        completed = new completeRoute[100];
        routeCount = 0;
        cheapest = null;
        cheapestCost = Integer.MAX_VALUE;
        bestVisited = null;
        bestMoves = 0;

        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited[i].length; j++) {
                visited[i][j] = 0;
            }
        }
        floodFill(entrance[0], entrance[1], 1, 0);

        if (cheapest == null) {
            return false;
        }
        // Copy the best saved route into the display array.
        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited[i].length; j++) {
                visited[i][j] = bestVisited[i][j];
            }
        }
        return true;
    }
    /**
     * floodFill recursively checks all routes, finish one and then backtack to check a ne one
     *
     * @param row the row of the coordinate
     * @param col the column of the coordinate
     * @param move the current move number we are on
     * @param currentCost the current cost of the route
     * 
     */
    private void floodFill(int row, int col, int move, int currentCost) {
        if (!getPath(row, col) || getVisited(row, col) != 0 ) {
            return;
        }

        setVisited(row, col, move);
        currentCost += getCost(row, col);

        if (map[row][col].equals("!")) { //got to the end
            saveRoute(currentCost, move - 1);
        
        } else {
            //what changes when going up, down, left, right
            int[][] directionSet = {{row-1,col}, {row+1, col}, {row, col -1}, {row, col + 1} };

            //compare cost of neighbor spots to sort them
            for(int i = 0; i < directionSet.length; i++){
                for(int j = i +1; j < directionSet.length;j++){

                    int neighbor1 = getCost(directionSet[i][0], directionSet[i][1]); 
                    int neighbor2 = getCost(directionSet[j][0], directionSet[j][1]);

                    //if not valid spot, aka x or out of bounds, give it a really high cost to avoid
                    if(neighbor1 == -1){
                        neighbor1 = Integer.MAX_VALUE;
                    }
                    if(neighbor2 == -1){
                        neighbor2 = Integer.MAX_VALUE;
                    }
                    //swap coordinats if neighbor 2 has a lower cost
                    if(neighbor2 < neighbor1){
                        int[] temp = directionSet[i];
                        directionSet[i] = directionSet[j];
                        directionSet[j] = temp;
                    }
                }
            }
            //recursively exploring each neighboring spot
            for(int i = 0; i < directionSet.length; i++){
                int nextRow = directionSet[i][0];
                int nextCol = directionSet[i][1];

                floodFill(nextRow, nextCol, move + 1, currentCost);
            }
        }
        //Allowing backtracking by marking this square as unvisited
        setVisited(row, col, 0);
    }
    /**
     * getString prints the maze with the cheapest route taken
     * 
     */
    public void getString(){
        int size = map.length;
        int sum = 0;
        String[][] current = new String[size][map[0].length];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < map[i].length; j++){
                if(!getPath(i,j)){
                    current[i][j] = map[i][j];
                }
                if(getPath(i,j) && visited[i][j] > 0){
                    current[i][j] = Integer.toString( visited[i][j]);
                    sum += getCost(i,j);
                }
                if(getPath(i,j) && visited[i][j] == 0){
                    current[i][j] = Integer.toString( visited[i][j]);
                }
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(current[i][j]);
            }
            System.out.println();
        }
        System.out.println("Total Cost: " + sum);

    }
    /**
     * main will create a finderMap based on given file and then solve if able
     * @param args commandLine file name containing maze 
     */
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
            System.exit(1);
        }
        finderMap testmap = new finderMap(test.toString());

        if (!testmap.solve()) {
            System.out.println("No route from # to ! was found.");
            return;
        }
        testmap.getString();
        System.out.println("Total Moves: " + testmap.getTotalMoves());
        //testmap.cheapest.printRoute();
    }
}
