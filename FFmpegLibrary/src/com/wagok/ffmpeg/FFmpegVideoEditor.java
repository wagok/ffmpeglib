package com.wagok.ffmpeg;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;
import com.wagok.ffmpeg.ffmpeg.*;
import com.wagok.ffmpeg.interfaces.AbstractVideoEditor;
import com.wagok.ffmpeg.interfaces.IVideoEditor;
import com.wagok.ffmpeg.interfaces.Info;
import com.wagok.ffmpeg.interfaces.area.IPlayArea;

/**
 * Created with IntelliJ IDEA.
 * User: wlad
 * Date: 6/25/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class FFmpegVideoEditor  extends AbstractVideoEditor implements IVideoEditor, FFmpegListener{

    FFmpegPlayer mMpegPlayer;
    Listener lsnr;
    Activity activity;
    Info info;


    public FFmpegVideoEditor(String fileName, FFmpegSurfaceView view, Activity act) {
        this.activity = act;
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

        return info;
    }

    @Override
    public void setListener(Listener lsnr) {
         this.lsnr = lsnr;
    }


    @Override
    public void onFFDataSourceLoaded(FFmpegError err, FFmpegStreamInfo[] streams) {
        //To change body of implemented methods use File | Settings | File Templates.
        Log.i("player", "here");
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
       //Log.i("Player", "Time tick " + mCurrentTimeUs);
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
        Toast.makeText(activity, "Video saved", Toast.LENGTH_LONG).show();
    }

    @Override
    public Bitmap getFrame(long milliseconds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Bitmap getFrame(int frameNumber) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Bitmap[] getFrames(long[] milliseconds) {
        return new Bitmap[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Bitmap[] getFrames(int[] frameNumber) {
        return new Bitmap[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setStartTime(long milliseconds) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setEndTime(long milliseconds) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setStartFrame(int frameNumber) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setEndFrame(int frameNumber) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void play(IPlayArea[] areas) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public String save(String fileName) throws VideoException {
        mMpegPlayer.save(fileName);
        return fileName;
    }

    @Override
    public void command(String[] cmdLine) {
        mMpegPlayer.ffmpegCMD(cmdLine);
    }


}
