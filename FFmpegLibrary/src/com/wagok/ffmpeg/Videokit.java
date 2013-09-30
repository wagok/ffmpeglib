package com.wagok.ffmpeg;

/**
 * Created with IntelliJ IDEA.
 * User: wlad
 * Date: 8/6/13
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public final class Videokit {

    String libPath;

    static {
        // System.loadLibrary("videokit");
        System.loadLibrary("ffmpeginvoke");
    }

    public Videokit(String module) {
        libPath = "/data/data/" + module + "/lib/libvideokit.so";
    }

    public void run(String[] args) {
        invoke(libPath, args);
    }

    public native void invoke(String path, String[] args);

}

