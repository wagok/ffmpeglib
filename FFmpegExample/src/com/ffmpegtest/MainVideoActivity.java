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
        String url = "file://" + videoFile.getAbsolutePath();

        String[] cmdLine = {"ffmpeg", "-i", url, "-vcodec", "copy", "-ss", "00:00:00", "-t", "00:00:04", url+"trimmed.mp4"}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi

        mMpegEditor = new FFmpegVideoEditor();

        mMpegEditor.command(cmdLine);


	}



}
