package com.wagok.ffmpeg;


import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created with IntelliJ IDEA.
 * User: wlad
 * Date: 6/25/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class FFmpegVideoEditor {

    protected Videokit vk = null;

    public void getInfo() {

        return;
    }

    public void trimVideo(String videoFile, String startTime, String duration, String destinationFile) throws FFmpegVideoEditorException {


        String[] cmdLine = {"ffmpeg", "-ss", startTime, "-i", "file://" + videoFile, "-vcodec", "copy", "-t", duration, "-strict", "-2", "file://" + destinationFile}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi
        command(cmdLine);
    }

    public void joinVideo(String[] files, String tarjeta, String destinationFile, boolean codecCopy, Context ctx) throws FFmpegVideoEditorException {


        //String tarjeta = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
        Log.d("MyVideo", tarjeta);
        File file = new File(tarjeta + "/templist.txt");

        try {
            OutputStreamWriter escritor = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            for (String item : files) {
                escritor.write("file " + "'file://" + item + "'" + "\n");
            }
            escritor.flush();
            escritor.close();

        } catch (Exception e) {
        }

        String[] cmdLine;
        if (codecCopy) {
            cmdLine = new String[]{"ffmpeg", "-f", "concat", "-i", file.getAbsolutePath(), "-vcodec", "copy", "-strict", "-2", "file://" + destinationFile}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi
        } else {
            cmdLine = new String[]{"ffmpeg", "-f", "concat", "-i", file.getAbsolutePath(), "-strict", "-2", "file://" + destinationFile}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi
        }

        command(cmdLine);

    }

    public void rotateVideo(String filePathFrom, RotationHint hint, String filePathTo) throws FFmpegVideoEditorException {
        String[] cmdLine = {"ffmpeg", "-i", filePathFrom, "-vf", "transpose=" + hint.getParam(), "-strict", "-2", filePathTo};

        command(cmdLine);
    }

    public void mirroredFlip(String filePathFrom, String filePathTo) throws FFmpegVideoEditorException {
        String[] cmdLine = {"ffmpeg", "-i", filePathFrom, "-vf", "hflip,vflip", "-map", "0", "-strict", "-2", filePathTo};

        command(cmdLine);
    }

    public void simpleFFmpegProcessing(String filePathFrom, String filePathTo) throws FFmpegVideoEditorException {
        String[] cmdLine = {"ffmpeg", "-i", filePathFrom, "-map", "0", "-strict", "-2", filePathTo};

        command(cmdLine);
    }

    public void setMetaData(String filePathFrom, String metadataName, String metadataValue, String filePathTo) throws FFmpegVideoEditorException {
        String[] cmd = {
                "ffmpeg", "-i", filePathFrom, "-codec", "copy", "-strict", "-2", "-metadata:s:v:0", metadataName + "=" + metadataValue, filePathTo
        };

        command(cmd);
    }

    @Deprecated
    public void getFrames(String videoFile, String startTime, int seconds, int framesPerSecond, String jpgSize, String hint, String destinationFile) throws FFmpegVideoEditorException {
        String[] cmdLine;
        if (hint == null || hint.isEmpty()) {
            cmdLine = new String[]{"ffmpeg",
                    "-ss", startTime,
                    "-i", "file://" + videoFile,
                    "-f", "image2",
                    "-t", Integer.toString(seconds),
                    "-r", Integer.toString(framesPerSecond),
                    "-s", jpgSize,
                    "file://" + destinationFile};
        } else {
            cmdLine = new String[]{"ffmpeg",
                    "-ss", startTime,
                    "-i", "file://" + videoFile,
                    "-f", "image2",
                    "-t", Integer.toString(seconds),
                    "-r", Integer.toString(framesPerSecond),
                    "-s", jpgSize,
                    "-vf", hint,
                    "file://" + destinationFile};
        }
        command(cmdLine);
    }


    public void getFrames(String videoFile, String startTime, int seconds, int framesPerSecond, String hint, String destinationFile) throws FFmpegVideoEditorException {
        String[] cmdLine;
        if (hint == null || hint.isEmpty()) {
            cmdLine = new String[]{"ffmpeg",
                    "-ss", startTime,
                    "-i", "file://" + videoFile,
                    "-f", "image2",
                    "-t", Integer.toString(seconds),
                    "-r", Integer.toString(framesPerSecond),
                    "file://" + destinationFile};
        } else {
            cmdLine = new String[]{"ffmpeg",
                    "-ss", startTime,
                    "-i", "file://" + videoFile,
                    "-f", "image2",
                    "-t", Integer.toString(seconds),
                    "-r", Integer.toString(framesPerSecond),
                    "-vf", hint,
                    "file://" + destinationFile};
        }
        command(cmdLine);
    }

    public void getFrame(File videoFile, String startTime, File destinationFile, String hint) throws FFmpegVideoEditorException {
        if (hint == null) {
            String[] cmdLine = {"ffmpeg", "-y", "-ss", startTime, "-i", "file://" + videoFile.getAbsolutePath(), "-f", "image2", "-vframes", "1", "file://" + destinationFile.getAbsolutePath()}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi
            command(cmdLine);
        } else {
            String[] cmdLine = {
                    "ffmpeg",
                    "-y",
                    "-ss", startTime,
                    "-i", "file://" + videoFile.getAbsolutePath(),
                    "-f", "image2",
                    "-vframes", "1",
                    "-vf", hint,
                    "file://" + destinationFile.getAbsolutePath()};
            String res = "";
            for (String c : cmdLine) {
                res += c + " ";
                Log.d("DEBUG_VIDEO", c);
            }
            Log.d("DEBUG_VIDEO", res);
            command(cmdLine);
        }

        //String[] cmdLine = {"ffmpeg",  "-ss", startTime, "-i", "file://" + videoFile.getAbsolutePath(),  "-r", "1",   "file://" + destinationFile.getAbsolutePath()}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi


    }


    public void command(String[] cmdLine) throws FFmpegVideoEditorException {
        vk = new Videokit("com.brabble");
        try {
            vk.run(cmdLine);
        } catch (Exception e) {
//            Log.d("Videokit", e.getMessage());
            throw new FFmpegVideoEditorException("Videokit: " + e.getMessage());
        }
    }

    public void terminate() {
        if (vk != null) {
            vk.stopNative();
//           Log.d("Videokit", "Stop native called");
        }
    }


    public static String getTransposeSettings(int[] transposeSettings) {
        String res = "";
        if (transposeSettings != null && transposeSettings.length != 0) {
            for (int i = 0; i < transposeSettings.length; i++) {
                if (!res.isEmpty()) {
                    res += ",";
                }
                res += "transpose=" + transposeSettings[i];
            }
        }
        Log.d("DEBUG_", res);
        return res;
    }
}
