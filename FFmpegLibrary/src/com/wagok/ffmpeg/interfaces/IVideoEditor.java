package com.wagok.ffmpeg.interfaces;

import android.graphics.Bitmap;
import com.wagok.ffmpeg.VideoException;
import com.wagok.ffmpeg.interfaces.area.IPlayArea;

public interface IVideoEditor extends IVideoPlayer {

    public Bitmap getFrame(long milliseconds);

    public Bitmap getFrame(int frameNumber);

    public Bitmap[] getFrames(long[] milliseconds);

    public Bitmap[] getFrames(int[] frameNumber);

    public void setStartTime(long milliseconds);

    public void setEndTime(long milliseconds);

    public void setStartFrame(int frameNumber);

    public void setEndFrame(int frameNumber);

    public void play(IPlayArea[] areas);

    public String save() throws VideoException;

    public String save(String fileName) throws VideoException;

    public void command(String[] cmdLine);
}
