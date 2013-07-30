package com.wagok.ffmpeg.interfaces;

import android.app.Activity;
import com.wagok.ffmpeg.ffmpeg.FFmpegSurfaceView;
import com.wagok.ffmpeg.FFmpegVideoEditor;
import com.wagok.ffmpeg.FFmpegVideoPlayer;

import java.io.File;

public class VideoPlayerFactory {

    public static IVideoPlayer creteVideoPlayer(String fileName, FFmpegSurfaceView view, Activity act) {
        if (fileName == null || fileName.isEmpty()) {
            throw  new IllegalArgumentException("File Name can not be empty");
        }
        if (view == null) {
            throw  new IllegalArgumentException("View does not initialized");
        }
        File f = new File(fileName);
        if (!f.exists()) {
         //   throw  new IllegalArgumentException("File with name '" + fileName + "' does not exist");
        }


        return new FFmpegVideoPlayer(fileName, view, act);
    }

    public static IVideoEditor creteVideoEditor(String fileName, FFmpegSurfaceView view, Activity act) {
        if (fileName == null || fileName.isEmpty()) {
            throw  new IllegalArgumentException("File Name can not be empty");
        }
        if (view == null) {
            throw  new IllegalArgumentException("View does not initialized");
        }
        File f = new File(fileName);
        if (!f.exists()) {
           // throw  new IllegalArgumentException("File with name '" + fileName + "' does not exist");
        }

        return new FFmpegVideoEditor(fileName, view, act);
    }

    //
    private VideoPlayerFactory() {}
}
