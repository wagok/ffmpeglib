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
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.*;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.wagok.ffmpeg.VideoException;
import com.wagok.ffmpeg.ffmpeg.FFmpegSurfaceView;
import com.wagok.ffmpeg.interfaces.IVideoEditor;
import com.wagok.ffmpeg.interfaces.VideoPlayerFactory;

import java.io.File;

public class MainVideoActivity extends Activity  {



    private IVideoEditor mMpegPlayer;

	private View mVideoView, mVideoView1, mVideoView2, mVideoView3;

    Bitmap bim = Bitmap.createBitmap(640, 360, Bitmap.Config.ARGB_8888);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DITHER);

		super.onCreate(savedInstanceState);

		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		this.getWindow().setBackgroundDrawable(null);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		this.setContentView(R.layout.main_video_surfaceview);



		mVideoView = this.findViewById(R.id.video_view);

        File videoFile = new File(Environment.getExternalStorageDirectory(),
                "Download/my_vid.mp4");
        String url = "file://" + videoFile.getAbsolutePath();

        String[] cmdLine = {"-i", "Download/my_vid.mp4", "-vcodec", "copy", "-ss", "00:00:00", "-t", "00:00:04", "Download/trimmed_video.mp4"}; // ffmpeg -i video.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:00:04 trimmed_video.avi

		mMpegPlayer = VideoPlayerFactory.creteVideoEditor(url, (FFmpegSurfaceView) mVideoView, this);
        mMpegPlayer.command(cmdLine);
        File videoFileOut = new File(Environment.getExternalStorageDirectory(),
                "Download/out_out_big.mp4");
        url = "file://" + videoFileOut.getAbsolutePath();

        /*
        try {
        mMpegPlayer.save(url);

        } catch (VideoException e) {

        }
        */
        mMpegPlayer.play();

	}



}
