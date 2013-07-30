package com.wagok.ffmpeg;

import android.app.Activity;
import android.util.Log;
import com.wagok.ffmpeg.ffmpeg.*;
import com.wagok.ffmpeg.interfaces.IVideoPlayer;
import com.wagok.ffmpeg.interfaces.Info;

/**
 * Created with IntelliJ IDEA.
 * User: wlad
 * Date: 6/25/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class FFmpegVideoPlayer implements IVideoPlayer, FFmpegListener {

    FFmpegPlayer mMpegPlayer;
    Listener lsnr;
    Info info;


    public FFmpegVideoPlayer(String fileName, FFmpegSurfaceView view, Activity act) {

        mMpegPlayer = new FFmpegPlayer((FFmpegDisplay) view, act);
        mMpegPlayer.setMpegListener(this);
        mMpegPlayer.setDataSource(fileName);
    }

    @Override
    public void play() {
        mMpegPlayer.resume();
    }

    @Override
    public void pause() {
        mMpegPlayer.pause();
    }

    @Override
    public void stop() {
        mMpegPlayer.stop();
    }

    @Override
    public void seek(long milliseconds) {
        mMpegPlayer.seek(milliseconds * 1000);
    }

    @Override
    public void seek(int frameNumber) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Info getInfo() {
        //mMpegPlayer.getVideoDurationNative();
        return info;
    }

    @Override
    public void setListener(Listener lsnr) {
         this.lsnr = lsnr;
    }


    @Override
    public void onFFDataSourceLoaded(FFmpegError err, FFmpegStreamInfo[] streams) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onFFResume(NotPlayingException result) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onFFPause(NotPlayingException err) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onFFStop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onFFUpdateTime(long mCurrentTimeUs, long mVideoDurationUs, boolean isFinished) {
       Log.i("Player", "Time tick " + mCurrentTimeUs);
        if (this.lsnr != null) {
            this.lsnr.tick(mCurrentTimeUs, mVideoDurationUs, isFinished);
        }
    }

    @Override
    public void onFFSeeked(NotPlayingException result) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onFFSaved(NotPlayingException result) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
