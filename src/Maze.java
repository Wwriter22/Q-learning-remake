/**
 * Q learning
 * Build a maze generator to generate randomized maps
 * Author: William Writer
 * Collaborator(s): trent ishan
 * Collaboration: we helped eachother figure out what was wrong and how to fix our problems
 * Date: 4/19/22
 * On My Honor, I confirm that I followed all collaboration policy guidelines, and that the work I am submitting is my own: WW
 **/
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Maze {
    //create a random maze

    //instance data
    //random to create the destination, clear path, and roadblocks
    private Random r;
    //ints for width and height
    int w;
    int h;

    //default constructor
    public Maze(){
        r = new Random();
        w = 10;
        h = 10;
    }

    //method to create the maze
    public void buildMaze(){
        //random final destination
        int finalD = r.nextInt(25);

        //int to keep number of characters added
        int spacesAdd = 0;
        //string to store the characters
        String s = new String();
        //int to store random to determined if path is open or blocked
        int openOrBlocked;
        for(int i = 0; i< 25; i++){
            if(spacesAdd % 5 == 0 && spacesAdd != 0){
                s += "\n";
            }
            openOrBlocked = r.nextInt(3);
            //check if it is at the destination otherwise move to else if
            if(i == finalD){
                s+= "F";
                spacesAdd += 1;
            }else if(openOrBlocked == 0 || openOrBlocked == 1){
                s += "0";
                spacesAdd += 1;
            }else{
                s += "X";
                spacesAdd += 1;
            }
        }
        //adds the info to the maze.txt
        try {
            FileWriter myWriter = new FileWriter("src/Maze.txt");
            myWriter.write(s);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}