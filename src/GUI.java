import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

public class GUI extends JFrame implements ActionListener {

    /** Height of the game frame. */
    private static final int DEFAULT_HEIGHT = 575;
    /** Width of the game frame. */
    private static final int DEFAULT_WIDTH = 1220;
    /** Width of a briefcase. */
    private static final int CASE_WIDTH = 66;
    /** Height of a briefcase. */
    private static final int CASE_HEIGHT = 98;
    /** Row (y coord) of the upper left corner of the first case. */
    private static final int LAYOUT_TOP = 60;
    /** Column (x coord) of the upper left corner of the first case. */
    private static final int LAYOUT_LEFT = 200;
    /** Distance between the upper left x coords of
     *  two horizontally adjacent cases. */
    private static final int LAYOUT_WIDTH_INC = 90;
    /** Distance between the upper left y coords of
     *  two vertically adjacent cases. */
    private static final int LAYOUT_HEIGHT_INC = 100;
    /** Width of a money value plate. */
    private static final int VALUES_WIDTH = 145;
    /** Height of a money value plate. */
    private static final int VALUES_HEIGHT = 28;
    /** y coord of the first money value plate. */
    private static final int VALUES_TOP = 60;
    /** x coord of the first money value plate. */
    private static final int VALUES_LEFT = 30;
    /** Distance between the x coords of two money value
     *  plates on opposite sides of the screen.
     */
    private static final int VALUES_WIDTH_INC = 1000;
    /** Distance between the tops of money value plates */
    private static final int VALUES_HEIGHT_INC = 30;
    /** y coord of the instruction label. */
    private static final int INSTRUCTION_LABEL_TOP = 30;
    /** y coord of the statistics labels. */
    private static int STATISTICS_LABELS_TOP = 470;
    /** y coord of the banker offer label. */
    private static final int LABEL_TOP = 60;
    /** x coord of briefcase field's right border. */
    private static final int LABEL_LEFT = LAYOUT_LEFT + 7 * LAYOUT_WIDTH_INC;
    /** Distance between the tops of labels. */
    private static final int LABEL_HEIGHT_INC = 35;
    /** y coord of the "Deal" button. */
    private static final int BUTTON_TOP = LABEL_TOP + LABEL_HEIGHT_INC;
    /** x coord of the "Deal" button. */
    private static final int BUTTON_LEFT = LABEL_LEFT + (DEFAULT_WIDTH - LAYOUT_LEFT - LABEL_LEFT - 100) / 2;
    /** Distance between the tops of buttons. */
    private static final int BUTTON_HEIGHT_INC = 50;

    /** The game (Briefcases subclass). */
    private Briefcases game;

    /** The main panel containing the game components. */
    private JPanel panel;
    /** The briefcase displays. */
    private JLabel[] displayCases;
    /** The coordinates of the briefcase displays. */
    private Point[] caseCoords;
    /** The money value displays. */
    private JLabel[] displayValues;
    /** The coordinates of the money value displays. */
    private Point[] valueCoords;
    /** The instruction messages. */
    private JLabel instructionsMsg;
    /** The banker's offer. */
    private JLabel offerMsg;
    /** The previous offers. */
    private JLabel previousOffersMsg;
    /** The user's total statistics. */
    private JLabel statisticsMsg;
    /** The user's current statistics. */
    private JLabel currentStatisticsMsg;
    /** The Deal button. */
    private JButton dealButton;
    /** The No Deal button. */
    private JButton noDealButton;
    /** The Restart button. */
    private JButton restartButton;
    /** The Close button. */
    private JButton closeButton;

    /** ith element is 0, 1, 2, or 3 if case #i is closed, open, chosen, or taken. */
    private int[] selections;
    /** ith element is true if the case with value #i has been opened */
    private boolean[] valueTaken;
    /** Flag that tracks if the user has kept the first case. */
    private boolean keepFlag = true;
    /** Flag that tracks if the game has ended. */
    private boolean gameOver = false;
    /** ArrayList to track previous offers. */
    private ArrayList<Double> offers;
    /** DecimalFormat to format cash values in the briefcases. */
    private final DecimalFormat briefcaseFormatter = new DecimalFormat("#,###.##");
    /** DecimalFormat to format cash values. */
    private final DecimalFormat formatter = new DecimalFormat("#,##0.00");

    /**
     * Initialize the GUI.
     * @param gameCases is a <code>Briefcases</code> subclass.
     */
    public GUI(Briefcases gameCases) {
        game = gameCases;
        selections = new int[game.size()];
        valueTaken = new boolean[game.size()];
        offers = new ArrayList<>();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                for (int i = 2; i <= 3; i++) {
                    System.out.println(game.getStatistics()[i]);
                }
                System.out.println("\0");
                System.exit(0);
            }
        });
        initDisplay();
        repaint();
    }

    /**
     * Run the game.
     */
    public void displayGame() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
            }
        });
    }

    /**
     * Draw the display.
     */
    public void repaint() {
        if (game.isDone(2)) {
            for (int i = 0; i < game.size(); i++) {
                if (selections[i] == 2) {
                    selections[i] = 0;
                    break;
                }
            }
        }

        for (int i = 0; i < game.size(); i++) {
            String caseImageFileName = imageFileName(selections[i]);
            URL imageURL = getClass().getResource(caseImageFileName);
            if (imageURL != null) {
                ImageIcon icon = new ImageIcon(imageURL);
                displayCases[i].setIcon(icon);
                if (selections[i] == 0) {
                    displayCases[i].setText(String.format("Case %d", i + 1));
                    displayCases[i].setForeground(Color.BLACK);
                    displayCases[i].setVisible(true);
                    displayCases[i].setFont(new Font("Dialog", Font.BOLD, 12));
                } else if (selections[i] == 1) {
                    if (game.cashIn(i) == Math.floor(game.cashIn(i))) {
                        displayCases[i].setText(String.format("$%s", briefcaseFormatter.format(game.cashIn(i))));
                    } else {
                        displayCases[i].setText(String.format("$%s", formatter.format(game.cashIn(i))));
                    }
                    displayCases[i].setForeground(Color.WHITE);
                    displayCases[i].setFont(new Font("Dialog", Font.PLAIN, 10));
                    game.caseOpened();
                    selections[i] = 3;
                } else if (selections[i] == 2) {
                    displayCases[i].setText("Chosen");
                    displayCases[i].setForeground(Color.BLACK);
                } else if (selections[i] == 3 && game.cashIn(i) == -1.0) {
                    displayCases[i].setVisible(false);
                }
                displayCases[i].setHorizontalTextPosition(JLabel.CENTER);
                displayCases[i].setVerticalTextPosition(JLabel.CENTER);
            } else {
                throw new RuntimeException("Could not find file: \"" + caseImageFileName + "\"");
            }
        }

        for (int i = 0; i < game.size(); i++) {
            String valueImageFileName = (valueTaken[i])? "Img/MoneyValuesPlateTaken.png" : "Img/MoneyValuesPlate.png";
            URL imageURL = getClass().getResource(valueImageFileName);
            if (imageURL != null) {
                ImageIcon icon = new ImageIcon(imageURL);
                displayValues[i].setIcon(icon);
                if (game.size() % 2 == 0) {
                    if (game.cashValues[i] == Math.floor(game.cashValues[i])) {
                        displayValues[(i * 2 + (i / (game.size() / 2))) % game.size()].setText("$" + briefcaseFormatter.format(game.cashValues[i]));
                    } else {
                        displayValues[(i * 2 + (i / (game.size() / 2))) % game.size()].setText("$" + formatter.format(game.cashValues[i]));
                    }
                } else {
                    if (game.cashValues[i] == Math.floor(game.cashValues[i])) {
                        displayValues[((i * 2 + i / (game.size() / 2 + 1)) % game.size()) - (i / (game.size() / 2 + 1))].setText("$" + briefcaseFormatter.format(game.cashValues[i]));
                    } else {
                        displayValues[((i * 2 + i / (game.size() / 2 + 1)) % game.size()) - (i / (game.size() / 2 + 1))].setText("$" + formatter.format(game.cashValues[i]));
                    }
                }
                displayValues[i].setHorizontalTextPosition(JLabel.CENTER);
                displayValues[i].setVerticalTextPosition(JLabel.CENTER);
            } else {
                throw new RuntimeException("Could not find file: \"" + valueImageFileName + "\"");
            }
        }

        instructionsMsg.setText(game.getInstruction());
        instructionsMsg.setBounds((int) ((LABEL_LEFT - VALUES_WIDTH) - instructionsMsg.getPreferredSize().getWidth()) / 2 + VALUES_WIDTH, INSTRUCTION_LABEL_TOP, 450, 30);

        offerMsg.setText("Banker's Offer: No Offer");
        offerMsg.setBounds((int) (LABEL_LEFT + (DEFAULT_WIDTH - LAYOUT_LEFT - LABEL_LEFT - offerMsg.getPreferredSize().getWidth()) / 2), LABEL_TOP, 250, 30);

        if (game.dealOrNoDeal()) {
            dealButton.setEnabled(true);
            noDealButton.setEnabled(true);
            game.updateCases(selections);
            offerMsg.setText(String.format("Banker's Offer: $%s", formatter.format(game.getOffer())));
            offerMsg.setBounds((int) (LABEL_LEFT + (DEFAULT_WIDTH - LAYOUT_LEFT - LABEL_LEFT - offerMsg.getPreferredSize().getWidth()) / 2), LABEL_TOP, 250, 30);
        } else {
            dealButton.setEnabled(false);
            noDealButton.setEnabled(false);
        }

        if (game.isDone(1) || gameOver) {
            restartButton.setEnabled(true);
            closeButton.setEnabled(true);
            double[] stats = game.getStatistics();
            currentStatisticsMsg.setText(String.format("<html>Current Statistics:<br>Earnings: $%s<br>Possible Earnings: $%s</html>", formatter.format(stats[0]), formatter.format(stats[1])));
            //currentStatisticsMsg.setBounds((int) ((LABEL_LEFT - VALUES_LEFT - VALUES_WIDTH) / 2.0 - currentStatisticsMsg.getPreferredSize().getWidth()) / 2, STATISTICS_LABELS_TOP, 300, 60);
            statisticsMsg.setText(String.format("<html>Total Statistics:<br>Earnings: $%s<br>Possible Earnings: $%s</html>", formatter.format(stats[2]), formatter.format(stats[3])));
            //statisticsMsg.setBounds((int) (((LABEL_LEFT - VALUES_LEFT - VALUES_WIDTH) / 2.0 - statisticsMsg.getPreferredSize().getWidth()) / 2 + (LABEL_LEFT - VALUES_LEFT - VALUES_WIDTH) / 2.0), STATISTICS_LABELS_TOP, 300, 60);
        } else {
            restartButton.setEnabled(false);
            closeButton.setEnabled(false);
        }

        pack();
        panel.repaint();
    }

    /**
     * Initialize the display.
     */
    private void initDisplay()	{

        // Initialize JPanel
        panel = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };

        // Set the title of the JFrame
        setTitle("Deal or No Deal");

        // Calculate number of rows of cases (7 cases per row) and adjust JFrame height if necessary
        int numRows = (game.size() + 6) / 7;
        int height = DEFAULT_HEIGHT;
        if (numRows > 4) {
            height += (numRows - 4) * LAYOUT_HEIGHT_INC;
        }

        // Set the JFrame and JPanel dimensions
        this.setSize(new Dimension(DEFAULT_WIDTH, height));
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(DEFAULT_WIDTH - 20, height - 20));

        // Initialize caseCoords using 7 cases per row
        caseCoords = new Point[game.size()];
        int x = LAYOUT_LEFT;
        int y = LAYOUT_TOP;
        for (int i = 0; i < caseCoords.length; i++) {
            caseCoords[i] = new Point(x, y);
            if (i % 7 == 6) {
                x = LAYOUT_LEFT;
                y += LAYOUT_HEIGHT_INC;
            } else {
                x += LAYOUT_WIDTH_INC;
            }
        }

        // Initialize displayCases and selections
        displayCases = new JLabel[game.size()];
        for (int i = 0; i < game.size(); i++) {
            displayCases[i] = new JLabel();
            panel.add(displayCases[i]);
            displayCases[i].setBounds(caseCoords[i].x, caseCoords[i].y, CASE_WIDTH, CASE_HEIGHT);
            displayCases[i].addMouseListener(new MyMouseListener());
            selections[i] = 0;
        }

        // Initialize valueCoords using 2 values per row
        valueCoords = new Point[game.size()];
        x = VALUES_LEFT;
        y = VALUES_TOP;
        for (int i = 0; i < valueCoords.length; i++) {
            valueCoords[i] = new Point(x, y);
            if (i % 2 == 1) {
                x = VALUES_LEFT;
                y += VALUES_HEIGHT_INC;
            } else {
                x += VALUES_WIDTH_INC;
            }
        }

        // Initialize displayValues and valueTaken
        displayValues = new JLabel[game.size()];
        for (int i = 0; i < game.size(); i++) {
            displayValues[i] = new JLabel();
            panel.add(displayValues[i]);
            displayValues[i].setBounds(valueCoords[i].x, valueCoords[i].y, VALUES_WIDTH, VALUES_HEIGHT);
            valueTaken[i] = false;
        }

        // Initialize the dealButton JButton
        dealButton = new JButton();
        dealButton.setText("Deal");
        panel.add(dealButton);
        dealButton.setBounds(BUTTON_LEFT, BUTTON_TOP, 100, 30);
        dealButton.addActionListener(this);

        // Initialize the noDealButton JButton
        noDealButton = new JButton();
        noDealButton.setText("No Deal");
        panel.add(noDealButton);
        noDealButton.setBounds(BUTTON_LEFT, BUTTON_TOP + BUTTON_HEIGHT_INC, 100, 30);
        noDealButton.addActionListener(this);

        // Initialize the restartButton JButton
        restartButton = new JButton();
        restartButton.setText("Restart");
        panel.add(restartButton);
        restartButton.setBounds(BUTTON_LEFT, BUTTON_TOP + BUTTON_HEIGHT_INC + 2 * LABEL_HEIGHT_INC + 150 + 12 * (game.numStages() - 9), 100, 30);
        restartButton.addActionListener(this);

        // Initialize the closeButton JButton
        closeButton = new JButton();
        closeButton.setText("Close");
        panel.add(closeButton);
        closeButton.setBounds(BUTTON_LEFT, BUTTON_TOP + 2 * BUTTON_HEIGHT_INC + 2 * LABEL_HEIGHT_INC + 150 + 12 * (game.numStages() - 9), 100, 30);
        closeButton.addActionListener(this);

        // Initialize the instructionsMsg JLabel
        instructionsMsg = new JLabel(game.getInstruction());
        panel.add(instructionsMsg);
        instructionsMsg.setBounds((int) ((LABEL_LEFT - VALUES_WIDTH) - instructionsMsg.getPreferredSize().getWidth()) / 2 + VALUES_WIDTH, INSTRUCTION_LABEL_TOP, 450, 30);
        instructionsMsg.setVisible(true);

        // Initialize the offerMsg JLabel
        offerMsg = new JLabel("Banker's Offer: No Offer");
        panel.add(offerMsg);
        offerMsg.setBounds((int) (LABEL_LEFT + (DEFAULT_WIDTH - LAYOUT_LEFT - LABEL_LEFT - offerMsg.getPreferredSize().getWidth()) / 2), LABEL_TOP, 250, 30);
        offerMsg.setVisible(true);

        // Initialize the previousOffersMsg JLabel
        previousOffersMsg = new JLabel("<html>Previous Offers:</html>");
        panel.add(previousOffersMsg);
        previousOffersMsg.setBounds((int) (LABEL_LEFT + (DEFAULT_WIDTH - LAYOUT_LEFT - LABEL_LEFT - previousOffersMsg.getPreferredSize().getWidth()) / 2), BUTTON_TOP + BUTTON_HEIGHT_INC + LABEL_HEIGHT_INC, 100, 180 + 12 * (game.numStages() - 9));
        previousOffersMsg.setVisible(true);

        // Initialize the STATISTICS_LABELS_TOP variable for the statistics JLabels
        STATISTICS_LABELS_TOP = Math.max(((game.size() + 6) / 7) * 100 + 2 * LABEL_HEIGHT_INC, (int) Math.ceil(game.size() / 2) * VALUES_HEIGHT_INC + VALUES_TOP);

        // Initialize the currentStatisticsMsg JLabel
        currentStatisticsMsg = new JLabel("<html>Current Statistics:<br>Earnings:<br>Possible Earnings:</html>");
        panel.add(currentStatisticsMsg);
        currentStatisticsMsg.setBounds((int) ((LABEL_LEFT - VALUES_WIDTH) / 2.0 - currentStatisticsMsg.getPreferredSize().getWidth()) / 2 + VALUES_WIDTH, STATISTICS_LABELS_TOP, 300, 60);
        currentStatisticsMsg.setVisible(true);

        // Initialize the statisticsMsg JLabel
        statisticsMsg = new JLabel("<html>Total Statistics:<br>Earnings:<br>Possible Earnings:</html>");
        panel.add(statisticsMsg);
        statisticsMsg.setBounds((int) (((LABEL_LEFT - VALUES_WIDTH) / 2.0 - statisticsMsg.getPreferredSize().getWidth()) / 2 + (LABEL_LEFT - VALUES_WIDTH) / 2.0 + VALUES_WIDTH), STATISTICS_LABELS_TOP, 300, 60);
        statisticsMsg.setVisible(true);

        // Add the JPanel with all the components to the JFrame
        pack();
        getContentPane().add(panel);
        panel.setVisible(true);
    }

    /**
     * Deal with the user clicking on something other than a button or a briefcase.
     */
    private void signalError() {
        Toolkit t = panel.getToolkit();
        t.beep();
    }

    /**
     * Returns the image that corresponds to the input selection.
     *
     * @param selection number that indicates if the briefcase is closed, open, or chosen
     * @return String representation of the image
     */
    private String imageFileName(int selection) {
        if (selection == 0) {
            return "Img/ClosedBriefcase.png";
        } else if (selection == 1 || selection == 3) {
            return "Img/OpenBriefcase.png";
        } else if (selection == 2) {
            return "Img/ChosenBriefcase.png";
        }
        return "";
    }

    /**
     * Returns a string of HTML wih items in the previous offers
     * list <code>offers</code>, seperated by line breaks.
     *
     * @param offers Double ArrayList with the previous offers
     * @return HTML String representation of the previous offers
     */
    private String generateOffersHTML(ArrayList<Double> offers) {
        String str = "<html>Previous Offers:";
        for (double cash : offers) {
            str += String.format("<br>$%s", formatter.format(cash));
        }
        str += "</html>";
        return str;
    }

    /**
     * Respond to a button click.
     * @param e the button click action event
     */
    public void actionPerformed(ActionEvent e) {
        // Use an if statement to get the source of the ActionEvent
        if (e.getSource().equals(dealButton)) {

            // Increase the stage of the game
            game.caseOpened();

            // Call tookDeal() to signal to the Briefcases class the end of the game by taking a deal
            game.tookDeal();

            // Set gameOver to true
            gameOver = true;

            // Repaint the game board
            repaint();
        } else if (e.getSource().equals(noDealButton)) {

            // Add the declined offer to the list of previous offers
            offers.add(game.getOffer());

            // Increase the stage of the game
            game.caseOpened();

            // Update the JLabel of the previous offers with the new offer
            previousOffersMsg.setText(generateOffersHTML(offers));
            previousOffersMsg.setBounds((int) (LABEL_LEFT + (DEFAULT_WIDTH - LAYOUT_LEFT - LABEL_LEFT - previousOffersMsg.getPreferredSize().getWidth()) / 2), BUTTON_TOP + BUTTON_HEIGHT_INC + LABEL_HEIGHT_INC, 100, 180 + 12 * (game.numStages() - 9));

            // Repaint the game board
            repaint();
        } else if (e.getSource().equals(restartButton)) {

            // Call newGame() to signal to the Briefcases class to reset the game
            game.newGame();

            // Reset all the flags, arrays, ArrayLists, and the previous offers JLabel (it isn't updated in repaint())
            keepFlag = true;
            gameOver = false;
            offers.clear();
            previousOffersMsg.setText(generateOffersHTML(offers));
            previousOffersMsg.setBounds((int) (LABEL_LEFT + (DEFAULT_WIDTH - LAYOUT_LEFT - LABEL_LEFT - previousOffersMsg.getPreferredSize().getWidth()) / 2), BUTTON_TOP + BUTTON_HEIGHT_INC + LABEL_HEIGHT_INC, 100, 180 + 12 * (game.numStages() - 9));
            Arrays.fill(selections, 0);
            Arrays.fill(valueTaken, false);

            // Repaint the game board
            repaint();
        } else if (e.getSource().equals(closeButton)) {

            // Close the window
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else {

            // Handle an ActionEvent from a different source
            signalError();
        }
    }

    /**
     * Receives and handles mouse clicks.  Other mouse events are ignored.
     */
    private class MyMouseListener implements MouseListener {

        /**
         * Handle a mouse click on a briefcase by changing its
         * selection value from 0 to 1 or choosing the first
         * case to keep for the end.
         * Each briefcase is represented as a label.
         * @param e the mouse event.
         */
        public void mouseClicked(MouseEvent e) {

            // Clicks will only register if the game is not over and a deal is not proposed
            if (!game.dealOrNoDeal() && !gameOver && !game.isDone(1)) {

                // Loop over the cases to find the source of the MouseEvent
                for (int i = 0; i < game.size(); i++) {
                    if (e.getSource().equals(displayCases[i])) {

                        // Check whether to record the first case or not
                        if (keepFlag) {

                            // Set the briefcase as the chosen case
                            selections[i] = 2;
                            game.caseChosen(i);

                            // Dry keepFlag to false
                            keepFlag = false;
                        } else if (selections[i] == 0) {

                            // Check if the game is at the point to choose between the two last cases
                            if (game.isDone(2)) {

                                // Set the briefcase to open
                                selections[i] = 1;

                                // Signal to the Briefcases class to signal the end of the game by depleting all the briefcases
                                game.updateLastCase(i);
                            } else {

                                // Set the briefcase to open
                                selections[i] = 1;

                                // Loop over the possible cash values to update the money value plates through the valueTaken array
                                for (int j = 0; j < game.size(); j++) {
                                    if (game.cashValues[j] == game.cashIn(i)) {
                                        if (game.size() % 2 == 0) {
                                            valueTaken[(j * 2 + (j / (game.size() / 2))) % game.size()] = true;
                                        } else {
                                            valueTaken[((j * 2 + j / (game.size() / 2 + 1)) % game.size()) - (j / (game.size() / 2 + 1))] = true;
                                        }
                                    }
                                }
                            }
                        }

                        // Repaint the game board
                        repaint();
                        return;
                    }
                }

                // Handle a click not on a briefcase
                signalError();
            }
        }

        /**
         * Ignore a mouse exited event.
         * @param e the mouse event.
         */
        public void mouseExited(MouseEvent e) {
        }

        /**
         * Ignore a mouse released event.
         * @param e the mouse event.
         */
        public void mouseReleased(MouseEvent e) {
        }

        /**
         * Ignore a mouse entered event.
         * @param e the mouse event.
         */
        public void mouseEntered(MouseEvent e) {
        }

        /**
         * Ignore a mouse pressed event.
         * @param e the mouse event.
         */
        public void mousePressed(MouseEvent e) {
        }
    }
}

