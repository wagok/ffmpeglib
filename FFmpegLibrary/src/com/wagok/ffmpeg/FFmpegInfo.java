package com.wagok.ffmpeg;

import com.wagok.ffmpeg.interfaces.Info;

/**
 * Created with IntelliJ IDEA.
 * User: wlad
 * Date: 7/10/13
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class FFmpegInfo extends Info {
     protected int video_bit_rate;
     protected int audio_bit_rate;
     protected int video_frame_rate;
     protected int video_width;
     protected int video_height;
     protected int video_gop_size;

    public int getVideo_bit_rate() {
        return video_bit_rate;
    }

    public int getAudio_bit_rate() {
        return audio_bit_rate;
    }

    public int getVideo_frame_rate() {
        return video_frame_rate;
    }

    public int getVideo_width() {
        return video_width;
    }

    public int getVideo_height() {
        return video_height;
    }

    public int getVideo_gop_size() {
        return video_gop_size;
    }
}
