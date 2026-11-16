package project1;
public class completeRoute {
    String [][] route;
    int completeCost;

    public completeRoute(String[][] map, int[][] visited){
        route = new String[map.length][map[0].length];
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[0].length; j++){
                if(map[i][j].equals("x")){
                    route[i][j] = map[i][j];
                } else {
                    route[i][j] = Integer.toString(visited[i][j]);
                }
                if(visited[i][j] != 0 && !map[i][j].equals("x") && !map[i][j].equals("!") && !map[i][j].equals("#") && !map[i][j].equals("p")){
                    completeCost += Integer.parseInt(map[i][j],16);
                } 
            }
        }
    }
    public int getCompleteCost(){
        return completeCost;
    }
    public void printRoute(){
        for(int i = 0; i < route.length; i++){
            for(int j = 0; j < route[0].length; j++){
                System.out.print(route[i][j]);
            }
            System.out.println();
        }
        System.out.println("Total cost: "+ completeCost);
    }
}
