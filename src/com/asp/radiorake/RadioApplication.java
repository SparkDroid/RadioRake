package com.asp.radiorake;

import android.app.Application;
import android.media.MediaPlayer;

public class RadioApplication extends Application {

    private RadioDetails _playingStation;
    private RadioDetails _recordingStation;
    private MediaPlayer _mediaPlayer;
    private String _playingStatus;
    private String _recordingStatus;
    private boolean _exitFlag;
    private boolean _buffering = false;
    private PlayingFile _playingFileDetails;
    private int _playingType;
    private LastPlayedFile _lastPlayedFile;

    public static final int StartPlayingRadio = 1;
    public static final int StartPlayingFile = 2;
    public static final int PausePlaying = 3;
    public static final int ResumePlaying = 4;
    public static final int StopPlaying = 5;

    public static final int PlayingFile = 1;
    public static final int PlayingStream = 2;

    public MediaPlayer getMediaPlayer() {
        return _mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this._mediaPlayer = mediaPlayer;
    }

    public RadioDetails getPlayingStation() {
        return _playingStation;
    }

    public void setPlayingStation(RadioDetails playingStation) {
        _playingStation = playingStation;
    }

    public RadioDetails getRecordingStation() {
        return _recordingStation;
    }

    public void setRecordingStation(RadioDetails recordingStation) {
        _recordingStation = recordingStation;
    }

    public String getPlayingStatus() {
        return _playingStatus == null ? "" : _playingStatus;
    }

    public void setPlayingStatus(String playingStatus) {
        this._playingStatus = playingStatus;
    }

    public String getRecordingStatus() {
        return _recordingStatus == null ? "" : _recordingStatus;
    }

    public void setRecordingStatus(String recordingStatus) {
        this._recordingStatus = recordingStatus;
    }

    public boolean getExitFlag() {
        return _exitFlag;
    }

    public void setExitFlag(boolean exitFlag) {
        _exitFlag = exitFlag;
    }

    public boolean isBuffering() {
        return _buffering;
    }

    public void setBuffering(boolean buffering) {
        this._buffering = buffering;
    }

    public void setPlayingFileDetails(PlayingFile playingFile) {
        _playingFileDetails = playingFile;
    }

    public PlayingFile getPlayingFileDetails() {
        return _playingFileDetails;
    }

    public int getPlayingType() {
        return _playingType;
    }

    public void setPlayingType(int playingType) {
        _playingType = playingType;
    }

    public void setLastPlayedFile(LastPlayedFile lastPlayedFile) {
        _lastPlayedFile = lastPlayedFile;
    }

    public LastPlayedFile getLastPlayedFile() {
        return _lastPlayedFile;
    }
}
