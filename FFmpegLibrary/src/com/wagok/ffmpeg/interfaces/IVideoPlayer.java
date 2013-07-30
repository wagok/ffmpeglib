package com.wagok.ffmpeg.interfaces;

public interface IVideoPlayer {

    public interface Listener {
        public void tick(long mCurrentTimeUs, long mVideoDurationUs, boolean isFinished);
    }

    public void play();

    public void pause();

    public void stop();

    public void seek(long milliseconds);

    public void seek(int frameNumber);

    public Info getInfo();

    public void setListener(Listener lsnr);
}
