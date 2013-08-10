package com.wagok.ffmpeg;

/**
 * Created with IntelliJ IDEA.
 * User: wlad
 * Date: 8/6/13
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public final class Videokit {

    static {
        System.loadLibrary("videokit");
    }

    public native void run(String[] args);

}

