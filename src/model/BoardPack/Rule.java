/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package model.BoardPack;

/**
 * Processing of operations related to the current rule set. The class extracts
 * the values which should be used for the birth rule and the survival rule,
 * assigns the numeric value(s) for the birth rule to the <code>int[]</code>
 * <code>birthArray</code> and the numeric value(s) for the survival rule to the
 * <code>int[]</code> <code>surviveArray</code>. The <code>birthArray</code> and
 * the <code>surviveArray</code> is further used to determine the new cell state
 * for a cell by using the number of neighbors and the current cell state.
 * 
 * @author M.S.Olsen, T.Dahll
 */
public class Rule {

    /**
     * The <code>int[]</code> representation of the "birth"-rules. Contains the
     * number of cells that is required for a cell with cell state = 0 to become
     * a cell with cell state = 1. The default value is the 3 (Conway's rules).
     */
    private static int[] birthArray = {3};

    /**
     * The <code>int[]</code> representation of the "survive"-rules. Contains
     * the number of cells that is required for a cell with cell state = 1 to
     * become a cell with cell state = 1. The default value is the 2 and 3
     * (Conway's rules).
     */
    private static int[] surviveArray = {2, 3};

    /**
     * The <code>String</code>-representation of the rule set.
     */
    private static String ruleString = "B3/S23";

    /**
     * Returns the current <code>ruleString</code>.
     *
     * @return the ruleString representing the rule set of the current board
     */
    public static String getRuleString() {
        return ruleString;
    }

    /**
     * Convert a <code>String</code> to an <code>int[]</code> for later use in
     * <code>rleRules</code>.
     *
     * @param rule is the rulestring, which represents the rule set
     * @return the <code>int[][]</code>-representation of the birth rule or the
     * survival rule
     */
    @Deprecated
    public static int[] toIntArray(String rule) {
        int[] intArray = new int[String.valueOf(rule).length()];

        for (int i = 0; i < rule.length(); i++) {
            intArray[i] = Character.digit(rule.charAt(i), 10);
        }

        int[] ruleArray = intArray;

        return ruleArray;
    }

    /**
     * Tests the cell with its <code>cellState</code> with the corresponding
     * number of neighbors, <code>numNeighbours</code>. The return value is
     * either 0 or 1, determined by the four variables <code>cellState</code>,
     * <code>numNeighbours</code>, <code>birthArray</code> and
     * <code>surviveArray</code>.
     *
     * Example of usage: B36/S23 (B for birth and S for survival). If a dead
     * cell(0) has 3 or 6 neighbors: turns alive (0 becomes 1). If not 3 or 6:
     * still a dead cell (0).
     *
     * If an alive cell has 2 or 3 neighbors: still alive (1). If not 2 or 3:
     * the alive cell dies (1 becomes 0).
     *
     * @param cellState the state of the current cell (0 or 1)
     * @param numNeighbours the number of neighbors of the current cell [0, 8]
     * @return return the new cell state (0 or 1)
     */
     public static byte rleRules(int cellState, int numNeighbours){
		
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
        } else if(cellState == 1)
        {
            
            for(int i = 0; i < surviveArray.length; i++)
            { 

                numS = surviveArray[i];
                if (numNeighbours == numS) {
                    returnState = 1;
                    break;
                }
            }
            if (numNeighbours != numS) {
                returnState = 0;

            }
        }
        return (byte) returnState;
    }

    /**
     * Setting a new <code>ruleString</code>. The String represents a rule set
     * on the form "Bx/Sx" or "s/b" which is used to extract the variables
     * representing the rules.
     *
     * @param aRuleString the ruleString to set
     */
    public static void setRuleString(String aRuleString) {
        ruleString = aRuleString;
    }

    /**
     * Sets the birth variable and the survive variable from their respective
     * Strings, <code>bValue</code> and <code>sValue</code> respectively. This
     * method places the integer values of parameters into the respective
     * <code>int[]</code> for further usage.
     *
     * @param bValue the birth rule (the number of neighbors needed for a 0 to 1
     * transition)
     * @param sValue the survival rule (the number of neighbors needed for 1 to
     * 1 transition)
     */
    public static void setRules(String bValue, String sValue) {
        birthArray = new int[bValue.length()];
        surviveArray = new int[sValue.length()];

        for (int i = 0; i < birthArray.length; i++) {
            birthArray[i] = Character.digit(bValue.charAt(i), 10);
        }

        for (int j = 0; j < surviveArray.length; j++) {
            surviveArray[j] = Character.digit(sValue.charAt(j), 10);
        }

    }

    /**
     * Gets the <code>birthArray</code>. The <code>birthArray</code> is assigned
     * values in method <code>setRules</code>.
     *
     * @return birthArray the <code>int[]</code> containing the birth rules
     */
    public static int[] getBirthArray() {
        return birthArray;
    }

    /**
     * Gets the <code>surviveArray</code>. The <code>surviveArray</code> is
     * assigned values in method <code>setRules</code>.
     *
     * @return birthArray the <code>int[]</code> containing the survival rules
     */
    public static int[] getSurviveArray() {
        return surviveArray;
    }

}
