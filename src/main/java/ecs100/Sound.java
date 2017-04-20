/**
 * Part of the Java SoundBox project, an attempt to bring a simple and intuitive sound library to the ecs100 (UI)
 * library in use at the Victoria University of Wellingtion Engineering and Computer Science Faculty.
 * 
 * (c) Elliot Wilde, 2017
 * Created 2017-03-18
 * 
 * Use it as you will, edit it as you will, just put your name, the edit date, and what you did in this text block (be smart about it)
 * I hold no responsibility or liability for anything related to this library.
 * 
 * I am not working with Alastair on this, so most of the variables and methods should be properly spelt
 */

package ecs100;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
//import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JComponent;



/**
 * Simple sound oject. <p>
 * Created with a .wav file (sorry, no .mp3). <p>
 * Playback started with {@link #play}, paused with {@link #pause}, and stopped with {@link #stop}. <br>
 * 
 * @author Elliot Wilde <br>
 * Created 2017-03-18
 *
 */
public class Sound {

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Sound)) {
			return false;
		}
		Sound other = (Sound) obj;
		if (file == null) {
			if (other.file != null) {
				return false;
			}
		} else if (!file.equals(other.file)) {
			return false;
		}
		return true;
	}

	private File file;
	private Clip clip;
	private FloatControl volCtrl;
	private BooleanControl muteCtrl;
	
	private boolean playing;
	
	/**
	 * Creates a sound object. Takes a {@link File} object. <p>
	 * <i> filepath </i> must refer to a valid .wav file, or other such file that can be decoded by the Java standard
	 * libraries, so no .mp3 files. <p> 
	 * Will throw an {@link IOException} if the file doesn't exist, and will 'convert' a {@link UnsupportedAudioFileException}  to IOException in the case that the file is not decodable.<br>
	 * In the annoying case that Java is unable to do sound stuff, a {@link LineUnavailableException} will be thrown. This is bad. Or maybe you don't have a sound device?
	 * @param filepath Path to .wav file
	 * @throws IOException If file doesn't exist, or isn't .wav
	 */
	public Sound(File filepath) throws IOException {
		this.file = filepath;
		try {
			this.clip = AudioSystem.getClip();
			AudioInputStream ais = AudioSystem.getAudioInputStream(this.file);
			this.clip.open(ais);
		} catch (LineUnavailableException  e) {
			// catch and throw higher, but convert to unchecked because hopefully these will be uncommon
			throw new RuntimeException(e);
		} catch (UnsupportedAudioFileException e) {
			// convert the UnsupportedAudioFileException to IOException, so that only one thing needs to be caught
			throw new IOException(e);
		} catch (IOException e) {
//			new IOexc
			throw e;
		}
		
		this.volCtrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		this.muteCtrl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
		
		this.playing = false;
	}
	
	
	/**
	 * Creates a sound object. Takes a {@link File} object. <p>
	 * <i> filepath </i> must refer to a valid .wav file, or other such file that can be decoded by the Java standard
	 * libraries, so no .mp3 files. <br> 
	 * <i> volume </i> is a value in the range 0.0 to 1.0. This is how 'loud' the sound should be <p>
	 * Will throw an {@link IOException} if the file doesn't exist, and will 'convert' a {@link UnsupportedAudioFileException}  to IOException in the case that the file is not decodable.<br>
	 * In the annoying case that Java is unable to do sound stuff, a {@link LineUnavailableException} will be thrown. This is bad. Or maybe you don't have a sound device?
	 * @param filepath Path to .wav file
	 * @param volume How loud to sound
	 * @throws IOException If file doesn't exist, or isn't .wav
	 * 
	 * @see #setVolume(float)
	 */
	public Sound(File filepath, float volume) throws IOException {
		this(filepath);
		this.setVolume(volume);
	}
	
	/**
	 * Creates a sound object. Takes a {@link File} object. <p>
	 * <i> filepath </i> must refer to a valid .wav file, or other such file that can be decoded by the Java standard
	 * libraries, so no .mp3 files. <br> 
	 * <i> listener </i> is a {@link LineListener}, or a lambda in the form "(event) -> { /* code goes here *&#47; &nbsp;}".
	 *  See {@link #addLineListener(LineListener)} for slightly more useful information <p>
	 * Will throw an {@link IOException} if the file doesn't exist, and will 'convert' a {@link UnsupportedAudioFileException}  to IOException in the case that the file is not decodable.<br>
	 * In the annoying case that Java is unable to do sound stuff, a {@link LineUnavailableException} will be thrown. This is bad. Or maybe you don't have a sound device?
	 * @param filepath Path to .wav file
	 * @param listener LineListener object, for listening. spooky.
	 * @throws IOException If file doesn't exist, or isn't .wav
	 * 
	 * @see #addLineListener(LineListener)
	 */
	public Sound(File filepath, LineListener listener) throws IOException {
		this(filepath);
		this.clip.addLineListener(listener);
	}
	
	/**
	 * Creates a sound object. Takes a {@link File} object. <p>
	 * <i> filepath </i> must refer to a valid .wav file, or other such file that can be decoded by the Java standard
	 * libraries, so no .mp3 files. <br> 
	 * <i> listener </i> is a {@link LineListener}, or a lambda in the form "(event) -> { /* code goes here *&#47; &nbsp;}".
	 *  See {@link #addLineListener(LineListener)} for slightly more useful information <br>
	 * <i> volume </i> is a value in the range 0.0 to 1.0. This is how 'loud' the sound should be <p>
	 * Will throw an {@link IOException} if the file doesn't exist, and will 'convert' a {@link UnsupportedAudioFileException}  to IOException in the case that the file is not decodable.<br>
	 * In the annoying case that Java is unable to do sound stuff, a {@link LineUnavailableException} will be thrown. This is bad. Or maybe you don't have a sound device?
	 * @param filepath Path to .wav file
	 * @param listener LineListener object, for listening. spooky.
	 * @param volume How loud to sound
	 * @throws IOException If file doesn't exist, or isn't .wav
	 * 
	 * @see #setVolume(float)
	 * @see #addLineListener(LineListener)
	 */
	public Sound(File filepath, LineListener listener, float volume) throws IOException {
		this(filepath);
		this.clip.addLineListener(listener);
		setVolume(volume);
	}
	
	/**
	 * Adds a {@link LineListener} to the underlying {@link Clip} object. <p>
	 * If you don't want to go to the effort of creating a LineListener, you can provide a lambda: <br>
	 * (event) -> { /* code goes here *&#47; &nbsp;} <br>
	 * Where <i>event</i> is a {@link LineEvent} object. <br>
	 * The type of event can be retrieved with {@link LineEvent#getType()}, which will return one of four {@link LineEvent.Type Type} objects.
	 * <ul>
	 * <li> {@link LineEvent.Type#START}
	 * <li> {@link LineEvent.Type#STOP}
	 * <li> {@link LineEvent.Type#OPEN}
	 * <li> {@link LineEvent.Type#CLOSE}
	 * </ul>
	 * @param listener
	 */
	public void addLineListener(LineListener listener){
		if (this.clip == null) return;
		this.clip.addLineListener(listener);
	}
	
	/**
	 * Begins playing the sound.
	 * @see #pause()
	 * @see #stop()
	 * @see #playing()
	 */
	public void play(){ // take some kind of parameter? loop, etc
		if (this.clip == null) return;
		this.clip.start();
		this.playing = true;
	}
	
	/**
	 * Returns whether or not the sound is playing
	 * @see #play()
	 * @see #pause()
	 * @see #stop()
	 */
	public boolean playing(){
		return playing;
	}
	
	/**
	 * Stops playing the sound, and returns the position to the beginning of the sound. <br>
	 * If you don't want the sound to return to the start, see {@link #pause() pause}.
	 * @see #play()
	 * @see #pause()
	 * @see #playing()
	 */
	public void stop(){
		if (this.clip == null) return;
		clip.stop();
		clip.flush();
		clip.setFramePosition(0);
		this.playing = false;
	}
	
	/**
	 * Pauses playing the sound, leaving the playback position where it was paused. <br>
	 * If you want the sound to playback from the start, see {@link #stop() stop}.
	 * @see #play()
	 * @see #stop()
	 * @see #playing()
	 */
	public void pause(){
		if (this.clip == null) return;
		clip.stop();
		this.playing = false;
	}

	/**
	 * Sets the gain of the sound in decibels. <br>
	 * 0 dB is no gain<br>
	 * <0 dB makes it quieter<br>
	 * >0 dB makes it louder<p>
	 * You should probably use {@link #setVolume(float) setVolume} instead.
	 * @param newGain
	 * @see #setVolume(float)
	 * @see #getGain()
	 * @see #getVolume()
	 */
	public void setGain(float newGain){
		if (this.clip == null) return;
		volCtrl.setValue(Math.min(newGain, volCtrl.getMaximum()));
	}
	
	/**
	 * Gets the gain of the sound in decibels
	 * @return The gain
	 * @see #setGain(float)
	 * @see #setVolume(float)
	 * @see #getVolume()
	 */
	public float getGain(){
		if (this.clip == null) return 0;
		return volCtrl.getValue();
	}
	
	/**
	 * Sets the Volume using a 0.0-1.0 scale, where 0.0 is silent, and 1 is unmodified. <br>
	 * Values higher than 1.0 will result in the sound getting louder <p>
	 * Values higher than FloatControl.getMaximum() will be limited to that value
	 * @param vol The new Volume
	 * @see #getVolume()
	 * @see #setGain(float)
	 * @see #getGain()
	 */
	public void setVolume(float vol){
		if (this.clip == null) return;
		// get the maximum volume as a linear value
		float maxVolume = (float) Math.pow(10f, volCtrl.getMaximum() / 20f);
		// check the bounds
//		if (vol <0f || vol > maxVolume){
			vol = Math.max(0f, Math.min(vol, maxVolume));
//			throw new IllegalArgumentException("volume bad:" + vol);
//		}
		volCtrl.setValue(20f * (float) Math.log10(vol));
	}
	
	/**
	 * Gets the Volume using a 0.0-1.0 scale, where 0.0 is silent, and 1.0 is unmodified.
	 * @return The volume
	 * @see #setVolume(float)
	 * @see #setGain(float)
	 * @see #getGain()
	 */
	public float getVolume(){
		if (this.clip == null) return 0;
		return (float) Math.pow(10f, volCtrl.getValue());

	}
	
	/**
	 * Unmutes the sound.
	 * @see Sound#mute()
	 */
	public void unMute(){
		if (this.clip == null) return;
		muteCtrl.setValue(false);
	}
	
	/**
	 * Mutes the sound.<br>
	 * Does not halt playback.
	 * @see Sound#unMute()
	 */
	public void mute(){
		if (this.clip == null) return;
		muteCtrl.setValue(true);
	}
	
	/**
	 * Gets the underlying {@link Clip} instance, which is responsible for all the sounds. <p>
	 * This method is roughly equivalent to the public field {@link ecs100.UI#canvas} in the ecs100 library, which can be used to get the 
	 * {@link JComponent} object being used by the UI as a canvas. 
	 * @return
	 */
	public Clip getClip(){
		return this.clip;
	}
	
	
	/**
	 * Do this when you are done.<br>
	 * Don't try and do anything with the sound afterwards
	 */
	public void close(){
		this.clip.close();
	}
	
	public File getFile(){
		return this.file;
//		return copy
	}
	
	
	
}
