package project1;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class finderMap {
    String[][] map;
    int[][] visited;    
    boolean finished = false;
    completeRoute[] completed = new completeRoute[100];
    int routeCount;
    completeRoute cheapest = null;
    int cheapestCost = Integer.MAX_VALUE;
    int[] entrance = new int[2];

    private int[][] bestVisited;
    private int bestMoves;
     
    public finderMap(String map){
        if (map == null || map.length() == 0) {
            throw new IllegalArgumentException("The maze is empty.");
        }
        //getting sizes and fixing formatting
        String[] rows = map.split("\n");
        int columns = rows[0].length();

        if (columns == 0) {
            throw new IllegalArgumentException(
                    "The maze has an empty row.");
        }

        //setting up arrays
        this.map = new String[rows.length][columns];
        this.visited = new int[rows.length][columns];
        int starts = 0;
        int exits = 0;

        //parsing map
        for(int i = 0; i < rows.length; i++){
            if (rows[i].length() != columns) {
                throw new IllegalArgumentException(
                        "Every row must have the same number of characters.");
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

    private boolean getPath(int x, int y){
        if (!inBounds(x,y) || map[x][y].equalsIgnoreCase("x")){
            return false;
        }
        return true;
    }
    public int getVisited(int x, int y){
        if(!inBounds(x,y)){
            return 0;
        }
        return this.visited[x][y];
    }
    public boolean setVisited(int row, int col, int visitNum){
        if(!getPath(row,col) || visitNum < 0){
            return false;
        }
        visited[row][col] = visitNum;
        return true;
    }

    public int getCost(int x, int y){

        if (!inBounds(x,y) || map[x][y].equalsIgnoreCase("x") || map[x][y].equals("p")){
            return -1;
        }
        if(map[x][y].equals("#") || map[x][y].equals("!") ){
            return 0;
        }
        return Integer.parseInt(map[x][y], 16);
    }
    public int getTotalCost() {
        if (cheapest == null) {
            return 0;
        }
        return cheapestCost;
    }

    public int getTotalMoves() {
        if (cheapest == null) {
            return 0;
        }
        return bestMoves;
    }

    private int[][] copyVisited() {
        int[][] copy = new int[visited.length][];
        for (int i = 0; i < visited.length; i++) {
            copy[i] = visited[i].clone();
        }
        return copy;
    }

    private void saveRoute(int currentCost, int totalMoves) {
        int[][] lastVisited = copyVisited();
        completeRoute solution = new completeRoute(map, lastVisited);

        if (routeCount == completed.length) {
            completeRoute[] larger = new completeRoute[completed.length * 2];
            for (int i = 0; i < routeCount; i++) {
                larger[i] = completed[i];
            }
            completed = larger;
        }

        completed[routeCount] = solution;
        routeCount++;

        if (cheapest == null || currentCost < cheapestCost) {
            cheapestCost = currentCost;
            cheapest = solution;
            bestVisited = lastVisited;
            bestMoves = totalMoves;
        }
    }

    public boolean solve() {
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

        finished = true;
        return true;
    }

    // Explores every route without revisiting a square in the current route.
    private void floodFill(int row, int col, int move, int currentCost) {
        if (!getPath(row, col)) {
            return;
        }

        if (getVisited(row, col) != 0) {
            return;
        }

        setVisited(row, col, move);
        currentCost += getCost(row, col);

        if (map[row][col].equals("!")) {
            saveRoute(currentCost, move - 1);
        } else {
            floodFill(row, col-1, move +1, currentCost);
            floodFill(row, col+1, move+1, currentCost);
            floodFill(row-1, col, move +1, currentCost);
            floodFill(row+1, col, move+1, currentCost);

        }

        setVisited(row, col, 0);
    }

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