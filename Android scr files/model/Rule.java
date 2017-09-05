package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

    /**
     * Created by MartinStromOlsen on 22/03/2017.
     */

/**
 * Class adapted from the main project containing the rule set logic.
 */
public class Rule
    {
        private static Matcher matcher;

        private static int[] birthArray;
        private static int[] surviveArray;

        /**
         * Parses the rule string obtained from a .RLE-file. The method checks if the rule string
         * contain letters (B, S, b or s) and then extracts the numbers following the letter. The
         * numbers are then assigned to <code>bValue</code> (numbers following B/b) or
         * <code>sValue</code> (numbers following S/s). If the rule string does not contain letters,
         * it is assumed to be on the form "survival/birth" and therefore assigns the number
         * preceding the "/" to <code>sValue</code> and the number following the "/" to
         * <code>bValue</code>. The method calls method <code>setRuleArrays</code> with
         * <code>bValue</code> and <code>sValue</code> as parameters. At most, there should be two
         * letters in the rule string and it is assumed to be either two or zero.
         * @param ruleStringTemp the rule string from a .RLE-file
         * @see #setRuleArrays(String, String)
         */
        protected static void parseRuleString(String ruleStringTemp)
        {

            //Finding letters in rulestring
            matcher = Pattern.compile("[B|S|b|s]").matcher(ruleStringTemp);
            String bValue = "";
            String sValue = "";

            if (matcher.find())
            {
                matcher = Pattern.compile("[B|b]([0-8]*)").matcher(ruleStringTemp);
                if (matcher.find()) {
                    bValue = matcher.group(1);
                }

                matcher = Pattern.compile("[S|s]([0-8]*)").matcher(ruleStringTemp);
                if (matcher.find())
                {
                    sValue = matcher.group(1);
                }

            }
            else
            {
                matcher = Pattern.compile("(\\d*)[\\/](\\d*)").matcher(ruleStringTemp);

                if (matcher.find())
                {
                    sValue = matcher.group(1);
                    bValue = matcher.group(2);
                }
            }

            //Set rules for current .rle
            setRuleArrays(bValue, sValue);
        }

        /**
         * Method that splits the Strings <code>bValue</code> and <code>sValue</code> into
         * <code>int[][]</code> containing their separate values. Iterating through the Strings,
         * char-by-char, to extract the number using static method <code>Character.digit</code>.
         * @param bValue String containing birth-values
         * @param sValue String containing survival-values
         */
        private static void setRuleArrays(String bValue, String sValue)
        {
            birthArray = new int[bValue.length()];
            surviveArray = new int[sValue.length()];

            for(int i = 0; i < birthArray.length; i++)
            {
                birthArray[i] = Character.digit(bValue.charAt(i), 10);
            }

            for(int j = 0; j < surviveArray.length; j++)
            {
                surviveArray[j] = Character.digit(sValue.charAt(j), 10);
            }
        }

        /**
         * Method evaluating the state of the current cell and its number of neighbors to determine
         * the if the cell state should change. The method iterates through the
         * <code>birthArray</code> if the state of the current cell == 0 to determine if it should
         * change the state to 1. The method iteraters through the <code>surviveArray</code> if the
         * state the current cell == 1 to determine if it should change the state to 0.
         * @param cellState state of the current cell
         * @param numNeighbours number of neighbors, determined by the neighbor-board
         * @return the new cell state
         */
        protected static byte rleRules(int cellState, int numNeighbours)
        {
            int returnState = 0;
            int numB = 0;
            int numS = 0;

            if(cellState == 0)
            {
                for(int i = 0; i < birthArray.length; i++)
                {
                    numB = birthArray[i];
                    if(numNeighbours == numB)
                    {
                        returnState = 1;
                        break;
                    }

                }

                if(numNeighbours != numB)
                {
                    returnState = 0;

                }
            }
            else if(cellState == 1)
            {

                for(int i = 0; i < surviveArray.length; i++)
                {
                    numS = surviveArray[i];
                    if(numNeighbours == numS)
                    {
                        returnState = 1;
                        break;
                    }
                }
                    if(numNeighbours != numS)
                    {
                        returnState = 0;
                    }
            }

            return (byte)returnState;
        }
    }


