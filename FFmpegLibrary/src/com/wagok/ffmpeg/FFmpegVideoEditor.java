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

    public void trimVideo(File videoFile, String startTime, String duration, File destinationFile) {


        String[] cmdLine = {"ffmpeg", "-i", "file://" + videoFile.getAbsolutePath(), "-vcodec", "copy", "-ss", startTime, "-t", duration, "-strict", "-2",  "file://" + destinationFile.getAbsolutePath()}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi
        command(cmdLine);
    }

    public void joinVideo(File firstFile, File secondFile, File destinationFile, Context ctx)  {


       //         String[] cmdLine = {"ffmpeg",  "-i", "concat:"+ firstFile.getAbsolutePath()+"|"+secondFile.getAbsolutePath(),  "-vcodec", "copy", destinationFile.getAbsolutePath()}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi

        String[] data = {"file " + "'" + firstFile.getAbsolutePath() + "'" ,
                         "file " + "'" + secondFile.getAbsolutePath() + "'"};




        File tarjeta = Environment.getExternalStorageDirectory();
        File file = new File(tarjeta.getAbsolutePath()+"/Download/mylist.txt");
        try {
        OutputStreamWriter escritor = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            escritor.write("file " + "'" + firstFile.getAbsolutePath() + "'" + "\n" + "file " + "'" + secondFile.getAbsolutePath() + "'" + "\n");
            escritor.flush();
            escritor.close();

        } catch (Exception e) {
        }


        String[] cmdLine = {"ffmpeg", "-f", "concat",  "-i",  file.getAbsolutePath(),  "-vcodec", "copy", "-strict", "-2", destinationFile.getAbsolutePath()}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi
        //String[] cmdLine = {"ffmpeg", "-formats"};
        command(cmdLine);

    }

    public void getFrame(File videoFile, String startTime, File destinationFile) {
        String[] cmdLine = {"ffmpeg", "-i", "file://" + videoFile.getAbsolutePath(),  "-ss", startTime, "-f", "image2", "-vframes", "1",  "file://" + destinationFile.getAbsolutePath()}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi
        command(cmdLine);
    }


    public void command(String[] cmdLine) {
        Videokit vk = new Videokit();
        vk.run(cmdLine);
    }




}
