# Deal Or No Deal Game

## Overview

This is an application to play the famous American reality TV show, "Deal or No Deal".
When run, a JFrame will show, with the game board, including panels, briefcases, and offers.

### Instructions

A JFrame with a grid of briefcases will show, with each briefcase numbered from one to twenty-six.
At the top, there are instructions, stating what to do such as initially choosing a case to keep to the end.
On the very left and right, there are panels with the values that could still be in the briefcases.
A briefcase is chosen by clicking it on the screen, displaying the money value inside, and the panels on the side will update accordingly.
When a deal is made by the banker, it shows up on the right side with the label "Banker's Offer: [Offer]".
"[Offer]" will either be "No Offer" when a deal is not to made, or the number, such as "$19,652.63".
There are two buttons below with either "Deal" or "No Deal".
These are your options, and you can choose to accept the banker's offer or to decline and keep playing.
If you decline, the offer will show up below the buttons under "Previous Offers:".
If you accept, you get the money and your stats will update.
Playing to the end when there are only two briefcases left, your last turn will let you swap briefcases, giving you a final decision to make.
After choosing a briefcase, your stats will update, and the instructions label will display the cash amount in the other briefcase.
After the game ends, you have the option to restart or exit.

### Statistics

The stats are calculated with the amount the user won, and the amount they could have won.
The amount the user could have won is calculated by comparing the money won to either the money in the original briefcase if the game ended due to a deal,
or the amount in the other briefcase if played to the last two briefcases.
Total statistics are calculated with the sum of all the previous statistics for amount user won, and amount user could have won.

The current round's statistics are displayed in the bottom left corner.
Total statistics of the session are displayed in the right hand corner.

## Running The Program

You can run the `.jar` from the commandline using:

    java -jar DealOrNoDeal.jar

****

## Specifications

* Title: Deal Or No Deal Game
* Author: Justin Wu
* Date: 7 December 2024
* Adapted from: ICS3U1 - Arrays Assignment
* Originally verified by: Neil Wan
* Made for: ICS3U1 - CPT
