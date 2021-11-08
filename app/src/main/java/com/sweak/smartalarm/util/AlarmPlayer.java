package com.sweak.smartalarm.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

import java.io.IOException;

public class AlarmPlayer {

    private final MediaPlayer mMediaPlayer;
    private final Context mContext;

    public AlarmPlayer(Context context) {
        mMediaPlayer = new MediaPlayer();
        mContext = context;

        setAlarmPreparedListener();
    }

    public void setAlarmPreparedListener() {
        mMediaPlayer.setOnPreparedListener(MediaPlayer::start);
    }

    public void setAlarmTone(int alarmToneId) {
        try {
            mMediaPlayer.setDataSource(mContext, AlarmToneSelector.getAlarmToneUri(alarmToneId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareAlarmPlayer() {
        mMediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
        mMediaPlayer.setLooping(true);

        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareAlarmPreviewPlayer() {
        mMediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build());
        mMediaPlayer.setLooping(false);

        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startAlarm(int alarmToneId) {
        mMediaPlayer.reset();

        setAlarmTone(alarmToneId);
        prepareAlarmPlayer();
    }

    public void startPreview(int alarmToneId) {
        mMediaPlayer.reset();

        setAlarmTone(alarmToneId);
        prepareAlarmPreviewPlayer();
    }

    public void stop() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }
}
