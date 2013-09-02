/*
 * MainActivity.java
 * Copyright (c) 2012 Jacek Marchwicki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ffmpegtest;

import android.app.Activity;

import android.os.*;

import android.view.Window;

import com.wagok.ffmpeg.FFmpegVideoEditor;


import java.io.File;

public class MainVideoActivity extends Activity  {



    private FFmpegVideoEditor mMpegEditor;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

        File videoFile = new File(Environment.getExternalStorageDirectory(),
                "Download/my_vid.mp4");

        File videoFileTrimmed = new File(Environment.getExternalStorageDirectory(),
                "Download/my_ttr.mp4");

        File videoFileFrame = new File(Environment.getExternalStorageDirectory(),
                "Download/m_out.jpg");

        //String[] cmdLine = {"ffmpeg", "-i", url, "-vcodec", "copy", "-ss", "00:00:00", "-t", "00:00:04", url+"trimmed.mp4"}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi

        /*
        File videoFile1 = new File(Environment.getExternalStorageDirectory(),
                "Download/my_4673.mp4");
        String url1 = "file://" + videoFile1.getAbsolutePath();


        File videoFile2 = new File(Environment.getExternalStorageDirectory(),
                "Download/my_vid.mp4");
        String url2 = "file://" + videoFile2.getAbsolutePath();

        File videoFile3 = new File(Environment.getExternalStorageDirectory(),
                "Download/my_result.mp4");
        String url3 = "file://" + videoFile3.getAbsolutePath();

        String[] cmdLine = {"ffmpeg",  "-i", "concat:my_4673.mp4|my_vid.mp4",  "-vcodec", "copy", url3}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi

       */


        mMpegEditor = new FFmpegVideoEditor();


        mMpegEditor.trimVideo(videoFile, "00:00:01.250", "00:00:15.500", videoFileTrimmed);

       // mMpegEditor.getFrame(videoFile, "00:00:01", videoFileFrame);
        //  mMpegEditor.joinVideo(videoFile, videoFileTrimmed, videoFileFrame, this);
	}



}
