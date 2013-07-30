package com.wagok.ffmpeg;

/**
 * Created with IntelliJ IDEA.
 * User: wlad
 * Date: 5/24/13
 * Time: 5:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class Myffmpeg {

    private long nativeHandle;
    public Myffmpeg() {
        initialise();
    }
    public native int setDecodeFile(String filePath);
    public native int setEncodeFile(String filePath);
   // public native int startEncodeVideo(int width, int height);
    public native int setNextFrame(Object bm);
    public native int finishEncode();

    public native void initialise();
   // public native int startDecodeVideo();
    public native int stopDecode();
    public native int getNextFrame(Object bm);
    public native void destroy();
   // public native int muxing(String filePath);
    @Override
    protected void finalize() {
       destroy();
   }
}
