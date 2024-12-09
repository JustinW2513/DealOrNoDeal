/**
 * Title: <code>Deal Or No Deal Game</code><br>
 * Author: <code>Justin Wu</code><br>
 * Date: <code>7 December 2024</code><br>
 * Adapted from: <code>Arrays Assignment</code><br>
 * Originally verified by: <code>Neil Wan</code>
 */

public class Main {

    /**
     * Plays the GUI for the game "Deal or No Deal".
     * @param args can be used to make a game with a custom board.
     */
    public static void main(String[] args) {

        // Array with Doubles with the possible values in the briefcases
        Double[] arr;

        // Use default values if no arguments are given
        if (args.length == 0) {
            arr = new Double[]{0.01, 1.0, 5.0, 10.0, 25.0, 50.0, 75.0, 100.0, 200.0, 300.0, 400.0, 500.0, 750.0,
                    1000.0, 5000.0, 10000.0, 25000.0, 50000.0, 75000.0, 100000.0, 200000.0, 300000.0, 400000.0,
                    500000.0, 750000.0, 1000000.0};
        } else {

            // Ensure there is not only one argument
            if (args.length == 1) {
                sendError("Two or more or no arguments expected.");
            }

            // Create array with appropriate length
            arr = new Double[args.length];

            // Initialize array with arguments
            for (int i = 0; i < args.length; i++) {

                // Ensure a number is given with a try catch statement
                try {

                    // Add the number to the array
                    arr[i] = Double.parseDouble(args[i]);

                    // Ensure it is not a negative number
                    if (arr[i] < 0.0) {
                        sendError(args[i] + " is a negative number.");
                    }
                } catch (Exception e) {
                    sendError(args[i] + " is not a number.");
                }
            }
        }

        // Make a new Briefcases object that will contain the game
        Briefcases briefcases = new Briefcases(arr);

        // Make a new GUI object to render the game in briefcases
        GUI gui = new GUI(briefcases);

        // Display the game with the GUI
        gui.displayGame();
    }

    /**
     * Prints an error to the console.
     * @param message is the error message.
     */
    static void sendError(String message) {
        System.out.println(message);
        System.out.println("\0");
        System.exit(0);
    }
}