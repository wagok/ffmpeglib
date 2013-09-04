package com.wagok.ffmpeg;


import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: wlad
 * Date: 6/25/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class FFmpegVideoEditor   {

    public void getInfo() {

        return;
    }

    public void trimVideo(String videoFile, String startTime, String duration, String destinationFile) {


        String[] cmdLine = {"ffmpeg",  "-ss", startTime, "-i", "file://" + videoFile, "-vcodec", "copy", "-t", duration, "-strict", "-2",  "file://" + destinationFile}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi
        command(cmdLine);
    }

    public void joinVideo(String[] files, String destinationFile, Context ctx)  {



        String tarjeta = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
        Log.d("MyVideo", tarjeta);
        File file = new File(tarjeta+"/templist.txt");

        try {
        OutputStreamWriter escritor = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            for(String item : files)  {
                escritor.write("file " + "'file://" + item + "'" + "\n" );
            }
            escritor.flush();
            escritor.close();

        } catch (Exception e) {
        }


        String[] cmdLine = {"ffmpeg", "-f", "concat",  "-i",  file.getAbsolutePath(),  "-vcodec", "copy", "-strict", "-2", "file://" + destinationFile}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi

        command(cmdLine);

    }

    public void getFrames(String videoFile, String startTime, int seconds, int framesPerSecond, String jpgSize, String destinationFile) {
        String[] cmdLine = {"ffmpeg",
                            "-ss", startTime,
                            "-i", "file://" + videoFile,
                            "-f", "image2",
                            "-t", Integer.toString(seconds),
                            "-r", Integer.toString(framesPerSecond),
                            "-s", jpgSize,
                            "file://" + destinationFile }; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi
        command(cmdLine);
    }

    public void getFrame(File videoFile, String startTime, File destinationFile) {
        String[] cmdLine = {"ffmpeg", "-ss", startTime, "-i", "file://" + videoFile.getAbsolutePath(),   "-f", "image2", "-vframes", "1",  "file://" + destinationFile.getAbsolutePath()}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi
        //String[] cmdLine = {"ffmpeg",  "-ss", startTime, "-i", "file://" + videoFile.getAbsolutePath(),  "-r", "1",   "file://" + destinationFile.getAbsolutePath()}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi

        command(cmdLine);
    }

    public void command(String[] cmdLine) {
        Videokit vk = new Videokit("com.ffmpegtest");
        vk.run(cmdLine);
    }




}
