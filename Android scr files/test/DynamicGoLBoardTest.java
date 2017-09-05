package Model;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by MartinStromOlsen on 05/05/2017.
 */
public class DynamicGoLBoardTest {


    @Test
    public void setNextGeneration() throws Exception {
        byte[][] board = {{0, 0, 0, 0}, {0, 1, 1, 0}, {0, 0, 1, 0}, {0, 0, 0, 0}};

        DynamicGoLBoard instance = new DynamicGoLBoard(board);
        instance.setNextGeneration();

        ArrayList<ArrayList<Byte>> expected = new ArrayList<>();

        for(int r = 0; r < 4; r++)
        {
            expected.add(new ArrayList<Byte>());
            for(int c = 0; c < 4; c++)
            {
                if(r == 1 & c == 1 || r == 1 & c ==2 || r == 2 & c == 2 || r == 2 & c == 1)
                {
                    expected.get(r).add(c, (byte)1);
                } else {
                    expected.get(r).add(c, (byte)0);
                }
            }
        }

        ArrayList<ArrayList<Byte>> actual = instance.getBoardList();
        for(int i = 0; i < actual.size(); i++)
        {
            for(int k = 0; k < actual.get(0).size(); k++) {
                assertEquals(actual.get(i).get(k), expected.get(i).get(k));
            }
        }

        instance.setNextGeneration();
        ArrayList<ArrayList<Byte>> actualSecond = instance.getBoardList();
        for(int i = 0; i < actual.size(); i++)
        {
            for(int k = 0; k < actual.get(0).size(); k++) {
                assertEquals(actualSecond.get(i).get(k), expected.get(i).get(k));
            }
        }

        byte[][] board2 = {{0, 0, 0, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
        DynamicGoLBoard instance2 = new DynamicGoLBoard(board2);
        instance2.setNextGeneration();

        ArrayList<ArrayList<Byte>> actualThird = instance2.getBoardList();
        ArrayList<ArrayList<Byte>> expected2 = new ArrayList<>();

        for(int r = 0; r < 5; r++)
        {
            expected2.add(new ArrayList<Byte>());
            for(int c = 0; c < 5; c++)
            {
                if(r == 1 & c == 2 || r == 2 & c ==2 || r == 3 & c == 2)
                {
                    expected2.get(r).add(c, (byte)1);
                } else {
                    expected2.get(r).add(c, (byte)0);
                }
            }
        }

        for(int i = 0; i < actualThird.size(); i++)
        {
            for(int k = 0; k < actualThird.get(0).size(); k++) {
                assertEquals(actualThird.get(i).get(k), expected2.get(i).get(k));
            }
        }


    }

}