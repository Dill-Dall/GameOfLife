/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package model;

import model.BoardPack.Rule;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processing and parsing of RLE-files. This class is capable of reading and
 * parsing RLE-files. The RLE-file is read from a source with a
 * <code>Reader</code> in the method <code>convertToReader</code> and then
 * temporarily stored in an <code>ArrayList</code>. The different elements of
 * the <code>ArrayList</code> are extracted in the <code>decryptRLEFile</code>
 * and processed further from that point. The elements related to information
 * about the RLE-file are extracted with the <code>extractBoardInfo</code>
 * method, the rule set of the RLE-file is decoded in the
 * <code>parseRuleString</code> method and the pattern of the RLE-file is
 * processed in the <code>fillBoardArray</code>-method where the pattern is
 * stored in the <code>byte[][]</code>, <code>rleArray</code> for further usage.
 *
 * @author M.S.Olsen
 */
public class FileHandler {

    /**
     * The temporary <code>byte[][]</code>, holding the pattern of the RLE-file
     * after method <code>fillBoardArray</code>.
     */
    private byte[][] rleArray;

    /**
     * Information about the author of the RLE-file. Assigned a value in the
     * <code>extractBoardInfo</code>-method.
     */
    private String authors = "";

    /**
     * Information about the title of the RLE-file. Assigned a value in the
     * <code>extractBoardInfo</code>-method.
     */
    private String boardTitle = "";

    /**
     * Information about the rule set of the RLE-file. Assigned a value in the
     * <code>extractBoardInfo</code>-method.
     */
    private String ruleSet = "";

    /**
     * The <code>ArrayList</code> containing board information.
     */
    private final List<String> headerList = new ArrayList();

    /**
     * The <code>Matcher</code> used to extract information.
     */
    private static Matcher matcher;

    /**
     * The number of bounding rows in the pattern.
     */
    private int boundingRows;

    /**
     * The number of bounding columns in the pattern.
     */
    private int boundingColumns;

    /**
     * Converts a File into a Reader object. Uses method readBoard to extract
     * content of File given as an argument. The exceptions from this method are
     * thrown to the caller.
     *
     * @param f the File that is to be converted to a Reader object
     * @throws IOException if it occur errors related to creating a FileReader
     * from the File
     * @throws model.PatternFormatException exception related to the format of
     * the RLE-file
     */
    public void convertToReader(File f) throws IOException, PatternFormatException {
        readBoard(new FileReader(f));
    }

    /**
     * Transmits all the text in file into an ArrayList which then is decrypted.
     * The ArrayList <code>stringsInRle</code> is used as a parameter to method
     * <code>decryptRLEFile</code> where the contents of the file is parsed into
     * a rule set and the pattern is parsed into the <code>rleArray</code> which
     * is in turn placed as the board in a <code>Board</code>-object.
     *
     * @param r the <code>Reader</code>-object containing the file
     * @throws IOException exceptions related to errors in the process of
     * reading a file
     * @throws PatternFormatException exceptions related to errors in the format
     * of the RLE-file
     * @see decryptRLEFile extracts information from the RLE-file
     */
    private void readBoard(Reader r) throws IOException, PatternFormatException {
        List<String> stringsInRle;
        stringsInRle = new ArrayList();

        try (BufferedReader br = new BufferedReader(r)) {
            String line;

            while ((line = br.readLine()) != null) {
                stringsInRle.add(line);
            }
            // closes the buffered reader
            br.close();

            decryptRLEFile(stringsInRle);
        }
    }

    /**
     * Converts the <code>stringsInRle</code> to a single String,
     * <code>rleString</code>. The information stored in the header of the
     * RLE-file is extracted through <code>extractBoardInfo</code> and placed
     * into respective information containers. If rule set is found in the
     * RLE-file this is assigned to the variable <code>ruleSet</code> and if no
     * rule set is found, a <code>PatternFormatException</code> is thrown to
     * notify the user. Information about the board is also extracted and sent
     * as a parameter to method <code>fillBoardArray</code> that creates a
     * <code>byte[][]</code> which is set as the board in a <code>Board</code>
     * -object.
     *
     * @param stringsInRle the contents of the RLE-file as a ArrayList
     * @throws PatternFormatException thrown if there are format errors in the
     * RLE-file
     * @see #extractBoardInfo(String)
     */
    protected void decryptRLEFile(List<String> stringsInRle) throws PatternFormatException {

        //Creates a StringBuilder
        StringBuilder stringBuild = new StringBuilder();

        for (String element : stringsInRle) {
            stringBuild.append("\n").append(element);
        }

        String rleString = stringBuild.toString();

        extractBoardInfo(rleString);

        //PRE-01052017
        //matcher = Pattern.compile("[R|r]ulestring|[R|r]ule\\s*[=|:]\\s*(\\w*\\s*"
        //        + "\\d*[\\s|\\/]\\w*\\s*\\d*)+[\\n|,|\\s]").matcher(rleString);
        matcher = Pattern.compile("(?i)(?:[Rr]ulestring|[Rr]ule|[Rr]ules|[Rr]ule\\s*set)\\s*[=:]\\s*(\\w*\\s*"
                + "\\d*[\\s\\/]\\w*\\s*\\d*)?[\\n|,|\\s]").matcher(rleString);

        if (matcher.find() && matcher.group(1) != null) {
            ruleSet = matcher.group(1);
            Rule.setRuleString(ruleSet);
            parseRuleString(matcher.group(1).replaceAll("\\s", ""));
        } else {
            throw new PatternFormatException("Cannot find rule/rulestring in RLE-file");
        }

        //BoardSetup
        String boardRLESetup = "";
        matcher = Pattern.compile("([.]|[\\n*][$A-Za-z0-9\\n*]*)[!]").matcher(rleString);
        if (matcher.find()) {
            boardRLESetup = matcher.group(1);
        } else {
            throw new PatternFormatException("Cannot find board data in RLE-file");
        }

        //X from RLE-file
        matcher = Pattern.compile("[x|X]\\s*[=]\\s*(\\d+)").matcher(rleString);

        if (matcher.find()) {
            boundingColumns = Integer.parseInt(matcher.group(1));
        } else {
            throw new PatternFormatException("Cannot find bounding columns in RLE-file");
        }

        //Y from RLE-file
        matcher = Pattern.compile("[y|Y]\\s*[=]\\s*(\\d+)").matcher(rleString);

        if (matcher.find()) {
            boundingRows = Integer.parseInt(matcher.group(1));
        } else {
            throw new PatternFormatException("Cannot find bounding rows in RLE-file");
        }

        String[] RLEArray = boardRLESetup.split("[$]+");

        if (RLEArray.length > boundingRows) {
            throw new PatternFormatException("Cannot fill board from RLE-file, data from board is not complying with bounding rows");
        } else {
            fillBoardArray(RLEArray, boundingRows, boundingColumns);
        }
    }

    /**
     * Extracts board information from the RLE-file. The method uses regular
     * expressions to extract information from lines of text marked with an
     * information tag and places these lines of text into different information
     * containers if the RLE-file contains this meta information.
     *
     * @param rleString the <code>String</code> representing the board in
     * <code>String</code>-format
     */
    protected void extractBoardInfo(String rleString) {
        matcher = Pattern.compile("#N\\s*(.+?)\\n").matcher(rleString);

        while (matcher.find()) {
            headerList.add("Name: " + matcher.group(1));
            boardTitle = matcher.group(1);
        }

        matcher = Pattern.compile("#C\\s*(.+?)\\n").matcher(rleString);

        while (matcher.find()) {
            headerList.add("Comment: " + matcher.group(1));
        }

        matcher = Pattern.compile("#O\\s*(.+?)\\n").matcher(rleString);

        while (matcher.find()) {
            headerList.add("Author: " + matcher.group(1));
            authors = matcher.group(1);
        }

        matcher = Pattern.compile("#R\\s*([-]*\\d+\\s*[-]*\\d*)+\\s*\\n").matcher(rleString);

        while (matcher.find()) {
            headerList.add("Top-left coordinates: " + matcher.group(1));
        }
    }

    /**
     * Processing of the rulestring parsed from the RLE-file. This method checks
     * if the rulestring obtained from <code>decryptRLEFile</code> contains
     * B|b|S|s and assign <code>bValue</code> and <code>sValue</code> their
     * respective values. If however the rulestring is in the format
     * \\d*[\\/]\\d*, then the parser-method will interpret this in the form
     * survival counts/birth counts as is the norm. The parameter
     * <code>ruleStringTemp</code> should be either on the form: "B[number of
     * required neighbor cells to change state from 0 to 1]/S[number of required
     * neighbor cells to maintain state 1 if state already is 1]" or on the
     * form: "[number of required neighbor cells to maintain state 1]/[number of
     * required neighbor cells for a cell to change state from 0 to 1]". The
     * method also sets the rule set through the static method in the Rule
     * class, Rule.setRules.
     *
     * @param ruleStringTemp - the rulestring retrieved from the RLE-file
     * @see #decryptRLEFile(java.util.List)
     */
    public static void parseRuleString(String ruleStringTemp) {
        //Assumes that rulestrings without letters are on the form survival/birth
        //Finding letters in rulestring
        matcher = Pattern.compile("[B|S|b|s]").matcher(ruleStringTemp);
        String bValue = "";
        String sValue = "";

        if (matcher.find()) {
            matcher = Pattern.compile("[B|b]([0-8]*)").matcher(ruleStringTemp);
            if (matcher.find()) {
                bValue = matcher.group(1);
            }

            matcher = Pattern.compile("[S|s]([0-8]*)").matcher(ruleStringTemp);
            if (matcher.find()) {
                sValue = matcher.group(1);
            }

        } else {

            matcher = Pattern.compile("(\\d*)[\\/](\\d*)").matcher(ruleStringTemp);

            if (matcher.find()) {
                sValue = matcher.group(1);
                bValue = matcher.group(2);
            }
        }
        //Sets the rules for current RLE-file 
        Rule.setRules(bValue, sValue);
    }

    /**
     * Gets the author(s) of the RLE-file.
     *
     * @return the author(s), <code>authors</code>
     */
    public String getAuthors() {
        return authors;
    }

    /**
     * Gets the title of the board.
     *
     * @return the board title, <code>boardTitle</code>
     */
    public String getBoardTitle() {
        return boardTitle;
    }

    /**
     * Gets the <code>ruleSet</code> which represents the current rule set from
     * the RLE-file.
     *
     * @return the <code>ruleSet</code>
     */
    public String getRuleSet() {
        return ruleSet;
    }

    /**
     * Gets the <code>String</code>-representation of the
     * <code>headerList</code>.
     *
     * @return the String-representation of the <code>headerList</code>
     */
    public String getHeaderList() {
        return headerList.toString().replace('[', ' ').replace(']', ' ').replaceAll(",", "\n");
    }

    /**
     * Creates an URL-object and uses method <code>readBord</code> with an
     * <code>InputStreamReader</code> to create an
     * <code>InputStreamReader</code> object.
     *
     * @param url the URL directing to a RLE-file
     * @throws IOException thrown if there are errors reading from the
     * <code>url</code>
     * @throws model.PatternFormatException thrown if there are errors related
     * to the format of the pattern
     */
    public void readGameBoardFromURL(String url) throws IOException, PatternFormatException {
        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();
        readBoard(new InputStreamReader(conn.getInputStream()));
    }

    /**
     * Thomas Dahll rev 10.03 uses the contents from the decryption to create an
     * 2DArray
     *
     * @param x value of x in rle
     * @param y value of y in rle
     * @param rleAryStrng the string which describes the array
     * @return an array of 1's and 0's
     * @see model.FileHandler#decryptRLEFile(java.util.List)
     */
    @Deprecated
    public byte[][] rletoArray(int x, int y, String rleAryStrng) {
        byte[][] rleArray = new byte[y][x];

        int column = 0;
        int row = 0;
        int times = 1;
        int count = 1;

        for (int indeX = 0; indeX < rleAryStrng.length(); indeX++) {
            char ch = rleAryStrng.charAt(indeX);

            switch (ch) {

                case 'o':
                    count = 1;
                    times = 1;

                    if (indeX > 0 && Character.isDigit(rleAryStrng.charAt(indeX - count))) {
                        times = Character.getNumericValue(rleAryStrng.charAt(indeX - count));
                        count++;

                        while (Character.isDigit(rleAryStrng.charAt(indeX - count))) {
                            times += 10 * Character.getNumericValue(rleAryStrng.charAt(indeX - count));
                            count++;
                        }
                    }

                    for (int timex = 0; timex < times; ++timex) {
                        rleArray[row][column] = 1;
                        column++;
                    }
                    break;

                case 'b':
                    times = 1;
                    count = 1;

                    if (indeX > 0 && Character.isDigit(rleAryStrng.charAt(indeX - count))) {
                        times = Character.getNumericValue(rleAryStrng.charAt(indeX - count));
                        count++;

                        while (Character.isDigit(rleAryStrng.charAt(indeX - count))) {
                            times += 10 * Character.getNumericValue(rleAryStrng.charAt(indeX - count));
                            count++;
                        }
                    }

                    for (int timex = 0; timex < times; ++timex) {
                        rleArray[row][column] = 0;
                        column++;

                    }

                    break;
                case '$':

                    times = 1;
                    count = 1;

                    if (indeX > 0 && Character.isDigit(rleAryStrng.charAt(indeX - count))) {
                        times = Character.getNumericValue(rleAryStrng.charAt(indeX - count));
                        count++;

                        while (Character.isDigit(rleAryStrng.charAt(indeX - count))) {
                            times += 10 * Character.getNumericValue(rleAryStrng.charAt(indeX - count));
                            count++;
                        }
                    }

                    for (int timex = 0; timex < times; ++timex) {
                        column = 0;
                        row++;
                        if (times > 1) {
                            for (int i = 0; i < x; i++) {
                                rleArray[row][column] = 0;
                                column++;
                            }
                        }
                        column = 0;
                    }

                    break;

                default:
                    break;
            }
        }
        return rleArray;
    }

    /**
     * Uses the <code>arrayString</code> to code it into an RLE pattern. This
     * creates a <code>String</code>-representation of the RLE-file which in
     * turn can be written to an RLE-file and be read with RLE-decoders.
     *
     * @param arrayString the String representing the pattern in terms of 1s and
     * 0s (alive and dead respectively)
     * @return the RLE-board in <code>String</code> form
     */
    public static String encodeArray(String arrayString) {

        arrayString = arrayString.replace("\n", "$");
        arrayString = arrayString.replace("0", "b");
        arrayString = arrayString.replace("1", "o");

        StringBuilder rleString = new StringBuilder();

        for (int index = 0; index < arrayString.length(); index++) {

            int times = 1;

            if (index < arrayString.length() - 1) {
                while (arrayString.charAt(index) == arrayString.charAt(index + 1)) {
                    times++;
                    if (index < arrayString.length()) {
                        index++;
                    }
                }
            }

            if (times > 1) {
                rleString.append(Integer.toString(times));
                rleString.append(arrayString.charAt(index));
            } else {
                rleString.append(arrayString.charAt(index));
            }
        }

        rleString.replace(rleString.length() - 1, rleString.length(), "!");

        for (int n = 0; n < rleString.length(); n++) {
            if ((n % 60) == 0) {
                rleString.insert(n, "\n");
            }
        }
        return rleString.toString();
    }

    /**
     * Gets the <code>rleArray</code>. This contains the <code>byte[][]</code>
     * of the pattern in the RLE-file.
     *
     * @return the <code>byte[][]</code>-representation of the pattern in the
     * RLE-file
     */
    public byte[][] getRleArray() {
        return rleArray;
    }

    /**
     * The method creates a temporary byte array, <code>rleArray</code> that
     * represents the board. The method is called from
     * <code>decryptRLEFile</code> and receives an <code>String[]</code>
     * -representation of the board parsed from the RLE-file and the minimum
     * number of rows and columns for the pattern to be correctly represented
     * (bounding rows and bounding columns). The parser follows the
     * recommendations from LifeWiki and treats b as 0 (dead) and o (and every
     * letter except from b and B) as 1 (alive). The binary representation of
     * the board is stored in <code>rleArray</code>.
     *
     * Source for the RLE-recommendations: http://conwaylife.com/wiki/RLE
     *
     * @param RLEArray - the rows from the RLE-file with letters b and o
     * indicating if cells have state 0 or 1
     * @param patternColumns - number of columns required to successfully create
     * pattern from RLE-file
     * @param patternRows - number of rows required to successfully create
     * pattern from RLE-file
     * @throws PatternFormatException if the board contains errors such as the
     * pattern being of greater size than the bounding restrictions
     */
    private void fillBoardArray(String[] RLEArray, int patternRows, int patternColumns) throws PatternFormatException {
        rleArray = new byte[patternRows + 2][patternColumns + 2];

        int bounding_rows = patternRows;
        int bounding_columns = patternColumns;
        int rowCounter = 1;
        int columnCounter = 1;

        Pattern value = Pattern.compile("([1-9]*[A-Za-z]{1})");
        Pattern value_sep = Pattern.compile("\\s*([1-9]+[0-9]*)*([A-Za-z]{1})\\s*");
        //Pattern value_sep = Pattern.compile("([1-9]+[0-9]*[$])*([1-9]+[0-9]*)*([A-Za-z]{1})");

        //Pattern to check for trailing digit in String array
        Pattern trailingDigit = Pattern.compile("[A-Za-z]*([1-9]+[0-9]*)$");

        List<String> liste;

        for (int i = 0; i < bounding_rows; i++) {
            //Checks for more rows with content
            if (i < RLEArray.length) {
                matcher = value_sep.matcher(RLEArray[i]);

                liste = new ArrayList();

                while (matcher.find()) {
                    liste.add(matcher.group(1));
                    liste.add(matcher.group(2));
                }

                for (int j = 0; j < liste.size(); j += 2) {

                    if (liste.get(j) == null) {
                        if (liste.get(j + 1).matches("[^B|^b]")) {
                            if (columnCounter <= patternColumns) {
                                rleArray[rowCounter][columnCounter] = 1;
                                columnCounter++;
                            } else {
                                throw new PatternFormatException("Cannot fill board from RLE-file, board not complying with bounding columns");
                            }
                        } else if (liste.get(j + 1).matches("[Bb]")) {
                            if (columnCounter <= patternColumns) {
                                rleArray[rowCounter][columnCounter] = 0;
                                columnCounter++;
                            } else {
                                throw new PatternFormatException("Cannot fill board from RLE-file, board not complying with bounding columns");
                            }
                        }

                    } else if (liste.get(j) != null) {
                        if (liste.get(j + 1).matches("[^B|^b]")) {
                            for (int k = 0; k < Integer.parseInt(liste.get(j)); k++) {
                                if (columnCounter <= patternColumns) {
                                    rleArray[rowCounter][columnCounter] = 1;
                                    columnCounter++;
                                } else {
                                    throw new PatternFormatException("Cannot fill board from RLE-file, board not complying with bounding columns");
                                }
                            }
                        } else if (liste.get(j + 1).matches("[Bb]")) {
                            for (int k = 0; k < Integer.parseInt(liste.get(j)); k++) {
                                if (columnCounter <= patternColumns) {
                                    rleArray[rowCounter][columnCounter] = 0;
                                    columnCounter++;
                                } else {
                                    throw new PatternFormatException("Cannot fill board from RLE-file, board not complying with bounding columns");
                                }
                            }
                        }
                    }
                }

                //Check for trailing digit, if found: add one row of zeros
                matcher = trailingDigit.matcher(RLEArray[i]);
                if (matcher.find()) {
                    rowCounter = rowCounter + (Integer.valueOf(matcher.group(1)) - 1);
                }
            }
            rowCounter++;
            columnCounter = 1;
        }
    }

}
