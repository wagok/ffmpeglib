package com.wagok.ffmpeg;


import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

        File file = new File(Environment.getExternalStorageDirectory(),
                "Download/mylist.txt");

        if (!file.exists()) {
            try {
            file.createNewFile();
            } catch (Exception e) {

            }
        }

        FileOutputStream writer = null;
        try {
            writer = ctx.openFileOutput(file.getName(), Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        for (String string: data){
            try {
                writer.write(string.getBytes());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //String[] cmdLine = {"ffmpeg", "-f", "concat",  "-i",  file.getAbsolutePath(),  "-vcodec", "copy", destinationFile.getAbsolutePath()}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi
        String[] cmdLine = {"ffmpeg", "-formats"};
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
