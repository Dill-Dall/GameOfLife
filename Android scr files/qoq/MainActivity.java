package com.example.martinstromolsen.goq;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import Model.DynamicGoLBoard;

/**
 * The main activity that receives a <code>String</code> as input.
 *
 * Source for the <code>MainActivity</code>:
 * https://developer.android.com/training/basics/firstapp/index.html
 */
public class MainActivity extends AppCompatActivity {

    public static final String STRING_INPUT = "com.example.martinstromolsen.INPUT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Method being called when <code>button</code> is being pushed. There are (at least) three
     * alternative ways of transferring data to the next activity:
     * 1. Fetching the <code>String</code> <code>userInput</code> from <code>editText</code>.
     * <code>userInput</code> is used in the constructor of a new <code>DynamicGBoard</code>
     * that is sent with the <code>intent</code> because <code>DynamicGBoard</code>
     * produces a serializable object.
     *
     * 2. The <code>String</code> containing the <code>userInput</code> could also be sent directly
     * with the <code>intent</code> as a <code>String</code> and then used to instantiate
     * <code>StaticGBoard</code> or <code>DynamicGBoard</code> in <code>ShowGameActivity.java</code>.
     *
     * 3. The <code>String</code> containing the <code>userInput</code> could be used to instantiate
     * a <code>StaticGBoard</code> or <code>DynamicGBoard</code> in <code>MainActivity.java</code>
     * and then saved as a <code>File</code> using <code>FileOutputStream</code> and
     * <code>ObjectOutputStream</code> to save the <code>gBoardFile</code> and then retrieving it in
     * <code>ShowGameActivity.java</code>
     *
     * Source for ObjectInputStream and FileInputStream:
     * https://developer.android.com/reference/java/io/ObjectInputStream.html
     *
     * https://www.tutorialspoint.com/java/io/objectinputstream_readobject.htm
     * @param view the current view
     */
    public void convertInput(View view)
    {
        Intent intent = new Intent(this, ShowGameActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String userInput = editText.getText().toString();

        //Transferring with String
//        intent.putExtra(STRING_INPUT, userInput);

        //Transferring serializable in intent without writing to file
        //intent.putExtra(STRING_INPUT, new StaticGBoard(userInput));
        intent.putExtra(STRING_INPUT, new DynamicGoLBoard(userInput));

        //Transferring with FileOutputStream
//        String gBoardFileName = "gBoard.ser";
//        File gBoardFile = new File(this.getFilesDir(), gBoardFileName);
//
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(gBoardFile.getAbsolutePath());
//            ObjectOutputStream writeOutput = new ObjectOutputStream(fileOutputStream);
//
//            writeOutput.writeObject(new DynamicGBoard(userInput));
//            writeOutput.close();
//            fileOutputStream.close();
//        } catch (IOException ioe)
//        {
//            ioe.printStackTrace();
//        }
//        intent.putExtra(STRING_INPUT, gBoardFile.getAbsolutePath());

        startActivity(intent);
    }

}
