package project1;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class finderMap {
    char[][] map;
    boolean[][] visited; 
    int[] entrance = new int[2];
    
    public finderMap(String map){
        if (map == null || map.length() == 0) {
            throw new IllegalArgumentException("The maze is empty.");
        }
        String[] rows = map.split("\n");
        int columns = rows[0].length();
        int starts = 0;
        int exits = 0;

        if (columns == 0) {
            throw new IllegalArgumentException("The maze has an empty row.");
        }
        this.map = new char[rows.length][columns];
        this.visited = new boolean[rows.length][columns];
        
        for(int i = 0; i < rows.length; i++){
            if (rows[i].length() != columns) {
                throw new IllegalArgumentException("Every row must have the same number of characters.");
            }
            char[] line = rows[i].toCharArray();
            for(int j = 0; j < line.length; j++){
                if(line[j] != '#' && line[j] != '!' && line[j] != 'x' && line[j] != 'p'&&line[j] != '\n' ){
                    System.err.println("Map must not contain characters other than: #, !, x, or p");
                }
                if(line[j] == '#'){
                    entrance[0] = i;
                    entrance[1] = j;
                    starts++;
                }
                if(line[j] == '!'){
                    exits++;
                }
                this.visited[i][j] = false;
                
                this.map[i][j] = line[j];
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
        if (!inBounds(row,col) || map[row][col] == 'x' || map[row][col] == 'X'){
            return false;
        }
        return true;
    }
    public boolean getVisited(int row, int col){
        if(!inBounds(row,col)){
            return false;
        }
        return visited[row][col];
    }
    public void setVisited(int x, int y){
        if(this.visited[x][y] != true){
                this.visited[x][y] = true;
        }
    }
    public void getString(){
        int size = map.length;
        char[][] current = new char[size][map[0].length];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < map[i].length; j++){
                if(map[i][j] == 'x' || map[i][j] =='X'){
                    current[i][j] = 'x';
                }
                if(map[i][j] == 'p' && visited[i][j] == true){
                    current[i][j] = 'v';
                }
                if(map[i][j] == 'p' && visited[i][j] != true){
                    current[i][j] = 'p';
                }
                if(map[i][j] == '!' && visited[i][j] == true){
                    current[i][j] = 'v';
                }
                if(map[i][j] == '!' && visited[i][j] !=true){
                    current[i][j] = map[i][j];
                }
                if(map[i][j] == '#' && visited[i][j] == true){
                    current[i][j] = 'v';
                }
                if(map[i][j] == '#' && visited[i][j] != true){
                    current[i][j] = map[i][j];
                }
            }
        }
        for (int i = 0; i < size; i++) { 
            for (int j = 0; j < map[i].length; j++) { 
                System.out.print(current[i][j]);
            }
            System.out.println(); 
        }
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
        testmap.getString();
        System.out.println();
        //not allowed on example
        testmap.setVisited(1,2);
        testmap.setVisited(3,4);
        //valid path on example
        testmap.setVisited(1,5);
        testmap.setVisited(2,1);
        testmap.setVisited(2,2);
        testmap.getString();
    }
}