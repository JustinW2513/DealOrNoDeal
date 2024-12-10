import java.text.DecimalFormat;
import java.util.*;

public class Briefcases {

    /** Array with all possible cash values. */
    public Double[] cashValues;
    /** ArrayList to contain the briefcases' cash during the game. */
    private ArrayList<Double> briefcases;
    /** The chosen briefcase's index */
    private int chosen;
    /** Keeps track of the stages of the game (every decision made by the user). */
    private int opened;
    /** Array with the stages to make a deal or no deal. */
    private int[] stages;
    /** Total money in the cases on the game board. */
    private double total;
    /** Total number of cases on the game board. */
    private int cnt;
    /** Flag that tracks if the game has ended. */
    private boolean done;
    /** Current earnings from a round. */
    private double earnings = 0.0;
    /** Current possible earnings from a round. */
    private double possible = 0.0;
    /** Total earnings from all games. */
    private double totalEarnings = 0.0;
    /** Total possible earnings from all games */
    private double totalPossible = 0.0;
    /** DecimalFormat to format cash values. */
    private final DecimalFormat formatter = new DecimalFormat("#,##0.00");

    /**
     * Initialize the <code>Briefcases</code> game object.
     * @param moneyValues an array with the possible cash values in the cases.
     */
    public Briefcases(Double[] moneyValues) {
        if (moneyValues.length < 2) {
            throw new IllegalArgumentException("There must be at least 2 money values.");
        }
        cashValues = moneyValues;
        Arrays.sort(cashValues);
        newGame();
        initStages();
    }

    /**
     * Resets/Initializes the variables for one game.
     */
    public void newGame() {
        briefcases = new ArrayList<>(Arrays.asList(cashValues));
        Collections.shuffle(briefcases, new Random());
        chosen = -1;
        opened = -1;
        done = false;
        total = 0.0;
        for (double cash : cashValues) {
            total += cash;
        }
        cnt = briefcases.size();
    }

    /**
     * Accesses the total number of briefcases possible.
     * @return the number of briefcases.
     */
    public int size() {
        return briefcases.size();
    }

    /**
     * Accesses the cash in a case on the board.
     * @param c is the index of the briefcase.
     * @return the cash in case <code>c</code> on the board.
     */
    public double cashIn(int c) {
        return briefcases.get(c);
    }

    /**
     * Gets the instruction at any particular stage of the game.
     * @return a String with the instruction.
     */
    public String getInstruction() {
        if (done) {
            return "The chosen briefcase had $" + formatter.format(briefcases.get(chosen)) + ".";
        } else if (opened == -1) {
            return "Choose a briefcase to keep to the end.";
        } else if (opened <= (briefcases.size() + stages.length - 3)) {
            if (dealOrNoDeal()) return "Deal or No Deal?";
            int n = calculateInstruction();
            return (n > 1)? String.format("Take %d cases.", n) : "Take 1 case.";
        } else if (opened == (briefcases.size() + stages.length) - 2) {
            return "Which case will you choose? Your case or the final case?";
        } else if (opened == (briefcases.size() + stages.length - 1)) {
            for (double cash : briefcases) {
                if (cash != -1.0 && cash != earnings) return "The other briefcase had $" + formatter.format(cash) + ".";
            }
        }
        return "";
    }

    /**
     * Initialize the <code>stages</code> array.
     */
    public void initStages() {
        int roundCases = (int) (-1 * ((1 - Math.sqrt(1 + 8 * (briefcases.size() - 2))) / 2));
        int extra = briefcases.size() - 2 - (roundCases * (roundCases + 1) / 2);
        stages = new int[roundCases + extra];
        for (int i = 0, n = roundCases; i < stages.length; i++, n--) {
            if (i == 0) stages[i] = n;
            else if (n > 0) stages[i] = n + stages[i - 1] + 1;
            else stages[i] = stages[i - 1] + 2;
        }
    }

    /**
     * Accesses the length of the <code>stages</code>
     * array (number of times a deal is offered).
     * @return the number of times a deal is offered.
     */
    public int numStages() {
        return stages.length;
    }

    /**
     * Calculates the number of cases to be removed during a regular round.
     * @return the number of cases to be removed.
     */
    public int calculateInstruction() {
        for (int i = 0; i < stages.length; i++) {
            if (opened < stages[i]) return stages[i] - opened;
        }
        return -1;
    }

    /**
     * Registers the chosen briefcase.
     * @param c the index of the chosen briefcase.
     */
    public void caseChosen(int c) {
        chosen = c;
        opened++;
    }

    /**
     * Increments the game stage.
     */
    public void caseOpened() {
        opened++;
    }

    /**
     * Updates multiple cases at a time.
     * @param cases array of case indices.
     */
    public void updateCases(int[] cases) {
        for (int i = 0; i < cases.length; i++) {
            if (cases[i] == 3) {
                if (briefcases.get(i) != -1.0) {
                    total -= briefcases.get(i);
                    cnt--;
                }
                briefcases.set(i, -1.0);
            }
        }
    }

    /**
     * Check if it is time for a deal to occur.
     * @return true if a deal is to happen.
     */
    public boolean dealOrNoDeal(){
        for (int i : stages) {
            if (opened == i) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates the banker's offer.
     * @return the offer.
     */
    public double getOffer() {

        // Calculate the maximum value
        double max = 0.0;
        for (double cash : briefcases) {
            if (cash > max) max = cash;
        }

        // Samuel D. Bradley's Formula - https://commcognition.blogspot.com/2007/06/deal-or-no-deal-bankers-formula.html
        double value = 12275.30 + (0.748 * (total / cnt)) - (2714.74 * cnt) - (0.040 * max) + (0.0000006986 * Math.pow((total/cnt), 2)) + (32.623 * Math.pow(cnt, 2));

        // Return an offer
        if (value > 0 && !(value > max)) return value;
        else return total / cnt;
    }

    /**
     * Handles a deal being taken.
     */
    public void tookDeal() {
        earnings = getOffer();
        possible = Math.max(getOffer(), briefcases.get(chosen));
        totalEarnings += earnings;
        totalPossible += possible;
        done = true;
    }

    /**
     * Checks if the game is a number of steps from ending.
     * @param option the number of steps from ending.
     * @return true if the game is <code>option</code> steps from ending.
     */
    public boolean isDone(int option) {
        return opened == (briefcases.size() + stages.length) - option;
    }

    /**
     * Handles the last case being taken.
     * @param c index of the last case.
     */
    public void updateLastCase(int c) {
        earnings = briefcases.get(c);
        for (double cash : briefcases) {
            if (cash != -1.0 && cash != earnings) possible = Math.max(earnings, cash);
        }
        totalEarnings += earnings;
        totalPossible += possible;
    }

    /**
     * Accesses the statistics.
     * @return an array with the four statistics.
     */
    public double[] getStatistics() {
        return new double[] {earnings, possible, totalEarnings, totalPossible};
    }

    /**
     * Update total statistics with values from previous sessions.
     * @param stats an array with the total earnings and possible earnings from previous sessions
     */
    public void updateStatistics(double[] stats) {
        totalEarnings += stats[0];
        totalPossible += stats[1];
    }
}
