/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package model;

import controller.GoLFXController;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class contains the functions to let the user choose the songs
 * to be played while the game is performed to improve the user experience.
 * 
 * @author N.Nanthawisit
 */
public class Sound {


    private SourceDataLine sourceDataLine;
    private byte[] storeSound;
    private byte[] combined;

    private final GoLFXController GC = new GoLFXController();
    private Clip clip = null;
    private File soundFile;

//---------------------------MUSIC ACTION-------------------------------------//
    /**
     * Method for playing a song.
     *
     */
    public void playMusic() {

        try {

            if (soundFile.exists()) {
                AudioInputStream sound = AudioSystem.getAudioInputStream(soundFile);

                clip = AudioSystem.getClip();
                clip.open(sound);
                clip.start();
                clip.loop(9);
            } else {
                GC.errorMessage("File not found: " + soundFile.getAbsolutePath());
            }

        } catch (UnsupportedAudioFileException e) {
            GC.errorMessage("Unsupported Audio File: " + e);
        } catch (IOException e) {
            GC.errorMessage("Input/Output Error: " + e);
        } catch (LineUnavailableException e) {
            GC.errorMessage("Line Unavailable Exception Error: " + e);
        }

    }

    /**
     * Pauses the <code>clip</code>. Accomplished by calling the method
     * <code>stop</code> of object <code>clip</code>.
     *
     * @see #clip
     */
    public void pauseMusic() {
        clip.stop();
        clip.flush();
        clip.setFramePosition(0);
    }

    /**
     * Method for checking if the <code>clip</code> is playing.
     *
     * @return true if the clip is playing and false if the clip is empty.
     */
    public boolean isPlaying() {
        boolean songStatus = true;
        if (clip == null) {
            songStatus = false;
        } else {
            songStatus = true;
        }

        return songStatus;
    }

    /**
     * This method provided the other class to get the <code>clip</code>/song
     * from Sound class.
     *
     * @return clip is the song that are playing at the moment.
     */
    public Clip getClip() {

        return clip;
    }

    /**
     * Assign the path of the chosen song to <code>soundFile</code>.
     *
     * @param Path - the path of the chosen song.
     */
    public void setFile(String Path) {
        this.soundFile = new File(Path);
    }

//---------------------------GENERATE SOUND-------------------------------------//
    /**
     * Method for writing audio data.
     *
     * @see #sourceDataLine writes audio bytes.
     * @see #storeSound store the value of piano tone, via sinus function.
     */
    public void writeData() {

        for (int k = 0; k < storeSound.length; k++) {
            sourceDataLine.write(combined, 1, k);
        }
    }

    /**
     * Method that manipulates the frequency of the wave. The sound is a
     * representation of a wave sound, via the sinus function.
     *
     * @see #storeSound store the value of piano tone, via sinus function.
     */
    public void chords() {

        double[] chords = new double[4]; // Sine function
        int[] tones = {440, 493, 262, 294};
        storeSound = new byte[4]; //A4, B4, C4, D4 (piano tones)
        combined = new byte[16];
        byte valueOfMixedSound = 0;

        for (int i = 0; i < (float) 44100; i++) {
            
            //store sound in term of sinus function f(x) = sin(c*x)
            for (int r = 0; r < chords.length; r++) {
                chords[r] = (i / ((float) 44100 / tones[r]) * 2.0 * Math.PI);
                storeSound[r] = (byte) (Math.sin(chords[r]) * 20);
            }

            //mix the value of every elements in storeSound array 
            if (i > 1) {
                int a = 0;
                for (int j = 0; j < storeSound.length; j++) {
                    for (int u = 0; u < 16; u++) {
                        valueOfMixedSound = (byte) (storeSound[a] + storeSound[j]);
                        combined[u] = valueOfMixedSound;

                    }
                    a++;
                }
            }

            writeData();
        }
    }

}
