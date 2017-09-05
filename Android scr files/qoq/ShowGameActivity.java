package com.example.martinstromolsen.goq;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Model.GoLBoard;

/**
 * Activity that is present when the board is displaying. Receives an <code>Intent</code> from the
 * <code>MainActivity</code> and handles further processing of the received information.
 *
 * Source:
 * https://developer.android.com/training/basics/firstapp/index.html
 */
public class ShowGameActivity extends AppCompatActivity {

    private ValueAnimator valueAnimator;
    private SeekBar zoomBar;
    private SeekBar speedBar;
    private TextView speed;
    private TextView zoom;
    private Button startButton;

    private long animationDuration = 2000;
    private final long BASESPEED = 2000;
    private static final int REQUEST_PHOTO = 1;
    private static float compressFactor = 0.03f;

    private static ImageConverter imageConverter;
    private GameViewer gv;
    private File image;

    private String imagePath;

    private static byte[][] byteArray;

    private static final CharSequence[] compressFactors = {"1% (less details -> faster)", "2%", "3%", "4%", "10% (more details -> slower)"};

    /**
     * Method called when <code>ShowGameActivity.java</code> is created. Initializes the
     * <code>GameViewer</code> and can use three different techniques to
     * place a <code>DynamicGBoard</code> or <code>StaticGBoard</code> in the
     * <code>GameViewer</code>:
     *
     * 1. Retrieving a String from <code>MainActivity</code> through method
     * <code>getStringExtra</code> and thereafter use this to create an instance of either
     * <code>DynamicGBoard</code> or <code>StaticGBoard</code> which is then placed in the
     * <code>GameViewer</code>
     *
     * 2. Retrieving a <code>GBoard</code> object through method <code>getSerializableExtra</code>
     * and thereafter cast this Serializable value into a <code>GBoard</code> and then place that
     * object into <code>GameViewer</code>
     *
     * 3. Retrieving a String from <code>MainActivity</code> which contains the absolute path to a
     * <code>GBoard</code> object and then use <code>FileInputStream</code> and
     * <code>ObjectInputStream</code> to read the filepath and through
     * <code>ObjectInputStream</code> read the the object which is assigned to reference variable
     * <code>GBoard</code> and then placed into <code>GameViewer</code>
     *
     * After retrieving <code>GBoard</code>, methods <code>startSetup</code> and
     * <code>displayZoomMessage</code> are called as a part of the initialization process.
     *
     * Source for ObjectInputStream and FileInputStream:
     * https://developer.android.com/reference/java/io/ObjectInputStream.html
     *
     * https://www.tutorialspoint.com/java/io/objectinputstream_readobject.htm
     *
     * @param savedInstanceState
     * @see #startSetup() initializing SeekBars
     * @see #displayZoomMessage() displaying <code>Toast</code> about zoom-properties
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_game);

        gv = (GameViewer) findViewById(R.id.game);
        Intent intent = getIntent();

        //Using String from MainActivity
//        String QRCode = intent.getStringExtra(MainActivity.STRING_INPUT);
//        gv.setGBoard(new DynamicGBoard(QRCode));

        //Using serializable object GBoard
        GoLBoard goLBoard = (GoLBoard) intent.getSerializableExtra(MainActivity.STRING_INPUT);
        gv.setGBoard(goLBoard);


        //Using ObjectInputStream
//        GBoard gBoard=null;
//        try
//        {
//            FileInputStream fileInputStream = new FileInputStream(intent.getStringExtra(MainActivity.STRING_INPUT));
//            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
//            gBoard = (DynamicGBoard) objectInputStream.readObject();
//            objectInputStream.close();
//            fileInputStream.close();
//
//            File gBoardFile = new File(intent.getStringExtra(MainActivity.STRING_INPUT));
//            gBoardFile.delete();
//        } catch (IOException ioe)
//        {
//            ioe.printStackTrace();
//        } catch (ClassNotFoundException cne)
//        {
//            cne.printStackTrace();
//        }
//        gv.setGBoard(gBoard);

        startSetup();
        displayZoomMessage();
    }

    /**
     * Initializes the <code>speedBar</code> and <code>zoomBar</code>, running their respective
     * setup-method. The <code>TextView</code> for each <code>SeekBar</code> is also initialized.
     * @see #speedBarSetup()
     * @see #zoomBarSetup()
     */
    private void startSetup()
    {
        speedBar = (SeekBar) findViewById(R.id.speedBar);
        speed = (TextView) findViewById(R.id.textView);
        speedBarSetup();

        zoomBar = (SeekBar) findViewById(R.id.zoomBar);
        zoom = (TextView) findViewById(R.id.textView2);
        zoomBarSetup();

        startButton = (Button) findViewById(R.id.startButton);
    }

    /**
     * Creates and displays a <code>Toast</code> with information regarding the zoom-function.
     */
    private void displayZoomMessage()
    {
        CharSequence message = "Manual zoom disables dynamic zoom";
        int toastDuration = Toast.LENGTH_SHORT;
        Toast.makeText(this, message, toastDuration).show();
    }

    /**
     * Initializes the <code>ValueAnimator</code> if it is not already initialized. The duration of
     * the animation is set to the <code>BASESPEED</code> and the method
     * <code>setNextGeneration</code> in <code>GameViewer</code> is called, this method sets the
     * next generation for the <code>GBoard</code> and draws the board through the
     * <code>invalidate</code>-method in <code>GameViewer</code>. If <code>valueAnimator</code>
     * already exists, the method pauses the animation unless it is already paused and starts the
     * animation if it is paused. If the build version of the OS is below KITKAT, the
     * <code>valueAnimator</code> is instead cancelled in order to stop the animation and
     * instantiated in the odd-numbered method calls.
     *
     * Source for animation:
     * https://developer.android.com/reference/android/animation/ValueAnimator.html
     * @see #valueAnimator
     * @see #BASESPEED
     */
    private void animationHandling()
    {
        if(valueAnimator == null) {
            valueAnimator = new ValueAnimator().ofInt(1);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    gv.setNextGeneration();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {
                    gv.setNextGeneration();
                }
            });

            valueAnimator.setDuration(animationDuration);
            valueAnimator.start();

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                if(valueAnimator.isPaused())
                {
                    valueAnimator.start();
                }
                else
                {
                    valueAnimator.pause();
                }

            } else
            {
                valueAnimator.cancel();
                valueAnimator = null;
            }
        }

    }

    /**
     * Called when the <code>startButton</code> is pushed. Alters the text of the button to indicate
     * if the animation is started or not. Calls <code>animationHandling</code> to create and then
     * pause/start the animation.
     * @param view the current view
     * @see #animationHandling()
     */
    public void startGame(View view)
    {
        if(startButton.getText().equals("Start game"))
        {
            startButton.setText("Stop game");
        }
        else
        {
            startButton.setText("Start game");
        }

        animationHandling();
    }

    /**
     * Setting up the <code>SeekBar</code> representing the speed-slider. Initialize the listener
     * and change the duration of the <code>valueAnimator</code> based on the value of the
     * speed-slider if the <code>valueAnimator</code> is not null.
     * @see #valueAnimator
     * @see #speedBar
     */
    private void speedBarSetup()
    {
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                animationDuration = BASESPEED - progress*15;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(valueAnimator != null)
                {
                    valueAnimator.setDuration(animationDuration);
                }
            }
        });
    }

    /**
     * Method called when <code>visualButton</code> is pushed. Creates an <code>AlertDialog</code>
     * which presents a list of <code>colors</code> to choose from and shows it. Based on the
     * choice, the <code>GameViewer</code>-object changes the color of the cells.
     *
     * Adapted from: https://developer.android.com/guide/topics/ui/dialogs.html
     * Adapted from: https://developer.android.com/guide/topics/resources/string-resource.html
     * @param view the current view
     */
    public void openVisual(View view) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(R.string.choose_cellColor);

        AlertDialog.Builder colorDialog = alertBuilder.setItems(R.array.color_name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                int[] colorChoice = getResources().getIntArray(R.array.cellColors);
                gv.setCellColor(colorChoice[which]);
            }

        });
        colorDialog.setCancelable(true);
        colorDialog.show();

    }

    /**
     * Sets up the zoom-bar by initializing a <code>SeekBar</code> and calling:
     * <code>GameViewer.setCellSize</code> to set the new cell size
     * <code>GameViewer.setFitZoom</code> to disable fitZoom and rather use the value from this
     * <code>seekBar</code>
     * <code>GameViewer.postInvalidate</code> to draw the board
     * @see #zoomBar
     */
    private void zoomBarSetup()
    {
        zoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                gv.getGoLBoard().setCellSize((progress+1));
                gv.getGoLBoard().setFitZoom(false);
                gv.postInvalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * Method called when <code>captureqrButton</code> is pushed. Builds an <code>AlertDialog</code>
     * which presents a list of compressing factors in which it is possible to choose one. The
     * <code>compressFactor</code> determines the size of the Bitmap-representation of the image and
     * is the percentage of width/height of the original size of the image.
     * When the "OK"-button is pushed, a photo intent is created in order to take the photo with the
     * camera.
     *
     * Source for methods related to taking pictures and permissions in AndroidManifest.xml:
     * https://developer.android.com/training/camera/photobasics.html
     * @param view the current view
     * @see #createPhotoIntent()
     */
    public void captureQR(View view)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        alertBuilder.setTitle(R.string.image_settings).setSingleChoiceItems(compressFactors, -1 , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(compressFactors[which].equals("1% (less details -> faster)"))
                {
                    compressFactor = 0.01f;
                } else if(compressFactors[which].equals("2%"))
                {
                    compressFactor = 0.02f;
                } else if(compressFactors[which].equals("3%"))
                {
                    compressFactor = 0.03f;
                } else if(compressFactors[which].equals("4%"))
                {
                    compressFactor = 0.04f;
                } else if(compressFactors[which].equals("10% (more details -> slower)")){
                    compressFactor = 0.10f;
                }
            }
        }).setPositiveButton(R.string.image_settings_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createPhotoIntent();
            }
        }).setNegativeButton(R.string.image_settings_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

    }

    /**
     *  Creates intent to take photo and a <code>File</code> to save the photo through the method
     *  <code>newImageFile</code>. If the creation of the <code>File</code> was successful, an <
     *  code>Uri</code> is created and passed with the intent to save the photo. This method also
     *  requires configuration in the
     *  AndroidManifest.xml of both the <code>FileProvider</code> and permission to write to storage
     *  (see source below).
     *
     *  Source for methods related to taking pictures and permissions in AndroidManifest.xml:
     *  https://developer.android.com/training/camera/photobasics.html
     *  @see #newImageFile()
     */
    private void createPhotoIntent()
    {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(photoIntent.resolveActivity(getPackageManager()) != null)
        {
            File image = null;
            try
            {
                image = newImageFile();
            }
            catch (IOException ioe)
            {
                CharSequence error = "Error creating image file";
                int toastDuration = Toast.LENGTH_SHORT;
                Toast.makeText(this, error, toastDuration).show();
                image = null;
            }

            if(image != null)
            {
                Uri imageUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", image);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(photoIntent, REQUEST_PHOTO);
            }
        }
    }

    /**
     * Creates a <code>File</code> to save the photo. Configures the filename and directory to save
     * the file, creates the file and assigns the absolute path to <code>imagePath</code>.
     *
     * Source for this method and permissions in AndroidManifest.xml:
     * https://developer.android.com/training/camera/photobasics.html
     * @return currentImage the <code>File</code> for the photo
     * @throws IOException exception being thrown if file could not be created
     * @see #createPhotoIntent()
     * @see #imagePath
     */
    private File newImageFile() throws IOException
    {
        String primaryKey = new SimpleDateFormat("ddMMyyyy-HHmm").format(new Date());
        String fileName = "TEMP"+primaryKey;
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File currentImage = File.createTempFile(fileName, ".jpg", directory);
        imagePath = currentImage.getAbsolutePath();
        return currentImage;
    }

    /**
     * Retrieves the intent from the photoIntent in <code>createPhotoIntent</code>. Creates a new
     * <code>ImageConverter</code> if one does not already exists, in order to extract the
     * <code>byte[][]</code> representing the "halftone"-version of the image, determined by the
     * image itself and the <code>compressFactor</code> The <code>File</code> containing the image
     * is then deleted after the <code>byte[][]</code> is extracted and the <code>byteArray</code>
     * is placed onto a <code>GBoard</code> to be represented in the <code>GameViewer</code> as a
     * board.
     *
     * Source for this method and permissions in AndroidManifest.xml:
     * https://developer.android.com/training/camera/photobasics.html
     * @param request
     * @param result
     * @param data
     * @see #byteArray
     * @see #imagePath
     * @see #compressFactor
     */
    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        if (request == REQUEST_PHOTO && result == RESULT_OK) {


            if(imageConverter==null)
            {
                imageConverter = new ImageConverter();
            }

            byteArray = imageConverter.generateByteArray(imagePath, compressFactor);

            File fileToDelete = new File(imagePath);
            fileToDelete.delete();

            gv.setGBoardFromImage(byteArray);
        }

    }

    /**
     * Disables the animation and sets a new text to the <code>startButton</code> when the built in
     * "back"-button/screen is locked is pushed or if the activity is paused.
     *
     * Source:
     * https://developer.android.com/guide/components/activities/activity-lifecycle.html
     */
    @Override
    public void onPause()
    {
        super.onPause();
        if(valueAnimator != null)
        {
            valueAnimator.cancel();
            valueAnimator = null;
            startButton.setText("Start game");
        }
    }

}
