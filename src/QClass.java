/**
 * Q learning
 * Build a maze generator to generate randomized maps
 * Author: William Writer
 * Collaborator(s): trent ishan
 * Collaboration: we helped eachother figure out what was wrong and how to fix our problems
 * Date: 4/19/22
 * On My Honor, I confirm that I followed all collaboration policy guidelines, and that the work I am submitting is my own: WW
 **/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class QClass {
    // Copied from the http://technobium.com/reinforcement-learning-q-learning-java/
    private Maze m;

    private final double alpha = 0.1; // Learning rate
    private final double gamma = 0.9; // Eagerness - 0 looks in the near future, 1 looks in the distant future

    private final int mazeWidth = 5;
    private final int mazeHeight = 5;
    private final int statesCount = mazeHeight * mazeWidth;

    private final int reward = 100;
    private final int penalty = -10;

    private char[][] readMaze;  // read from file
    private int[][] R;       // Reward lookup
    private double[][] QLearn;    // Q learning



    public static void main(String args[]) { // driver that is calling upon methods to test functionaltiy
        QClass qc = new QClass();

        qc.init();
        qc.calculateQ();
        qc.printQ();
        qc.printPolicy();

    }

    public void init() {
        //initialize maze and call function to create the maze and move it to the txt file
        m = new Maze();
        m.buildMaze();

        File file = new File("src/Maze.txt"); //allows to be pushed to my Maze.txt file

        R = new int[statesCount][statesCount]; // initializing instance data
        QLearn = new double[statesCount][statesCount];// initializing instance data
        readMaze = new char[mazeHeight][mazeWidth];// initializing instance data


        try (FileInputStream fis = new FileInputStream(file)) {

            int i = 0;
            int j = 0;

            int contains;

            // Read the maze from the input file
            while ((contains = fis.read()) != -1) {
                char c = (char) contains;
                if (c != '0' && c != 'F' && c != 'X') {
                    continue;
                }
                readMaze[i][j] = c;
                j++;
                if (j == mazeWidth) {
                    j = 0;
                    i++;
                }
            }

            // We will navigate through R
            for (int k = 0; k < statesCount; k++) {

                // We will cycle through i and j, so we need to translate k into i and j
                i = k / mazeWidth;
                j = k - i * mazeWidth;

                // Fill in the reward matrix with -1
                for (int s = 0; s < statesCount; s++) {
                    R[k][s] = -1;
                }

                // If not in final state or a wall try moving in all directions in the maze
                if (readMaze[i][j] != 'F') {

                    // Try to move left in the maze
                    int goLeft = j - 1;
                    if (goLeft >= 0) {
                        int target = i * mazeWidth + goLeft;
                        if (readMaze[i][goLeft] == '0') {
                            R[k][target] = 0;
                        } else if (readMaze[i][goLeft] == 'F') {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }

                    // Try to move right in the maze
                    int goRight = j + 1;
                    if (goRight < mazeWidth) {
                        int target = i * mazeWidth + goRight;
                        if (readMaze[i][goRight] == '0') {
                            R[k][target] = 0;
                        } else if (readMaze[i][goRight] == 'F') {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }

                    // Try to move up in the maze
                    int goUp = i - 1;
                    if (goUp >= 0) {
                        int target = goUp * mazeWidth + j;
                        if (readMaze[goUp][j] == '0') {
                            R[k][target] = 0;
                        } else if (readMaze[goUp][j] == 'F') {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }

                    // Try to move down in the maze
                    int goDown = i + 1;
                    if (goDown < mazeHeight) {
                        int target = goDown * mazeWidth + j;
                        if (readMaze[goDown][j] == '0') {
                            R[k][target] = 0;
                        } else if (readMaze[goDown][j] == 'F') {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }
                }
            }
            initializeQ();
            printR(R);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Set Q values to R values
    void initializeQ()
    {
        for (int i = 0; i < statesCount; i++){
            for(int j = 0; j < statesCount; j++){
                QLearn[i][j] = (double)R[i][j];
            }
        }
    }
    // Used for debug
    void printR(int[][] matrix) {
        System.out.printf("%25s", "States: ");
        for (int i = 0; i <= 8; i++) {
            System.out.printf("%4s", i);
        }
        System.out.println();

        for (int i = 0; i < statesCount; i++) {
            System.out.print("Possible states from " + i + " :[");
            for (int j = 0; j < statesCount; j++) {
                System.out.printf("%4s", matrix[i][j]);
            }
            System.out.println("]");
        }
    }

    void calculateQ() { // copied from source above
        Random rand = new Random();

        for (int i = 0; i < 1000; i++) { // Train cycles
            // Select random initial state
            int crtState = rand.nextInt(statesCount);

            while (!isFinalState(crtState)) {
                int[] actionsFromCurrentState = possibleActionsFromState(crtState);

                // Pick a random action from the ones possible
                int index = rand.nextInt(actionsFromCurrentState.length);
                int nextState = actionsFromCurrentState[index];

                // Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
                double q = QLearn[crtState][nextState];
                double maxQ = maxQ(nextState);
                int r = R[crtState][nextState];

                double value = q + alpha * (r + gamma * maxQ - q);
                QLearn[crtState][nextState] = value;

                crtState = nextState;
            }
        }
    }

    boolean isFinalState(int state) {
        int i = state / mazeWidth;
        int j = state - i * mazeWidth;

        return readMaze[i][j] == 'F';
    }

    int[] possibleActionsFromState(int state) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < statesCount; i++) {
            if (R[state][i] != -1) {
                result.add(i);
            }
        }

        return result.stream().mapToInt(i -> i).toArray();
    }

    double maxQ(int nextState) {
        int[] actionsFromState = possibleActionsFromState(nextState);
        //the learning rate and eagerness will keep the W value above the lowest reward
        double maxValue = -10;
        for (int nextAction : actionsFromState) {
            double value = QLearn[nextState][nextAction];

            if (value > maxValue)
                maxValue = value;
        }
        return maxValue;
    }

    void printPolicy() { //copied from source
        System.out.println("\nPrint policy");
        for (int i = 0; i < statesCount; i++) {
            System.out.println("From state " + i + " goto state " + getPolicyFromState(i));
        }
    }

    int getPolicyFromState(int state) { // copied from source
        int[] actionsFromState = possibleActionsFromState(state);

        double maxValue = Double.MIN_VALUE;
        int policyGotoState = state;

        // Pick to move to the state that has the maximum Q value
        for (int nextState : actionsFromState) {
            double value = QLearn[state][nextState];

            if (value > maxValue) {
                maxValue = value;
                policyGotoState = nextState;
            }
        }
        return policyGotoState;
    }

    void printQ() { // actually prints out the info in the q solution
        //string to store q matrix in
        String s = "Q matrix" + "\n";
        System.out.println("Q matrix");
        for (int i = 0; i < QLearn.length; i++) {
            System.out.print("From state " + i + ":  ");
            s += "From state" + i + ": ";
            for (int j = 0; j < QLearn[i].length; j++) {
                System.out.printf("%6.2f ", (QLearn[i][j]));
                s += String.format("%6.2f ", (QLearn[i][j]));
            }
            s += "\n";
            System.out.println();
        }
        //add the string to the Qsolution file through a try catch
        try {
            FileWriter myWriter = new FileWriter("src/QSolution.txt");
            myWriter.write(s);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}