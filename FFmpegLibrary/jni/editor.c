#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

#include <libavutil/mathematics.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>

#include <android/log.h>

#include "editor.h"



#define LOG_LEVEL 10
#define LOG_TAG "player.c"
#define LOGI(level, ...) if (level <= LOG_LEVEL) {__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__);}
#define LOGE(level, ...) if (level <= LOG_LEVEL + 10) {__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__);}
#define LOGW(level, ...) if (level <= LOG_LEVEL + 5) {__android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__);}

#define FALSE 0
#define TRUE (!(FALSE))



/* 5 seconds stream duration */
//#define STREAM_DURATION   20.0
#define STREAM_FRAME_RATE 24 /* 24 images/s */
#define STREAM_NB_FRAMES  ((int)(STREAM_DURATION * STREAM_FRAME_RATE))
#define STREAM_PIX_FMT    PIX_FMT_YUV420P /* default pix_fmt */

static int sws_flags = SWS_BICUBIC;

/**************************************************************/
/* audio output */

/*static float t, tincr, tincr2;
static int16_t *samples;
static int audio_input_frame_size;*/

/*
 * add an audio output stream
 */
static AVStream *add_audio_stream(AVFormatContext *oc, AVCodec **codec,
                                  enum AVCodecID codec_id, struct EncoderData * enc_data)
{
    AVCodecContext *c;
    AVStream *st;

    /* find the audio encoder */
    *codec = avcodec_find_encoder(codec_id);
    if (!(*codec)) {
        LOGE(1, "Could not find codec\n");
        exit(1);
    }

    st = avformat_new_stream(oc, *codec);
    if (!st) {
        LOGE(1, "Could not allocate stream\n");
        exit(1);
    }
    st->id = 1;

    c = st->codec;

    /* put sample parameters */
    c->sample_fmt  = enc_data->audio_sample_fmt; //AV_SAMPLE_FMT_S16;
    c->bit_rate    = enc_data->audio_bit_rate;
    c->sample_rate = enc_data->audio_sample_rate;
    c->channels    = enc_data->audio_channels;

    // some formats want stream headers to be separate
    if (oc->oformat->flags & AVFMT_GLOBALHEADER)
        c->flags |= CODEC_FLAG_GLOBAL_HEADER;

    return st;
}

static void open_audio(AVFormatContext *oc, AVCodec *codec, AVStream *st, struct EncoderData * enc_data)
{
    AVCodecContext *c;

    c = st->codec;
    c->strict_std_compliance = -2;

    /* open it */
    if (avcodec_open2(c, codec, NULL) < 0) {
        LOGE(1, "could not open codec\n");
        exit(1);
    }


    if (c->codec->capabilities & CODEC_CAP_VARIABLE_FRAME_SIZE)
        enc_data->audio_input_frame_size = 10000;
    else
        enc_data->audio_input_frame_size = c->frame_size;
    enc_data->samples = av_malloc(enc_data->audio_input_frame_size *
                        av_get_bytes_per_sample(c->sample_fmt) *
                        c->channels);
}


void write_audio_frame2(AVFormatContext *oc, AVStream *st, struct EncoderData * enc_data, AVFrame *frame)
{
    AVCodecContext *c;
    AVPacket pkt = { 0 }; // data and size must be 0;

    int got_packet;

    av_init_packet(&pkt);
    c = st->codec;

    avcodec_encode_audio2(c, &pkt, frame, &got_packet);

    if (!got_packet)
        return;

    pkt.stream_index = st->index;

    /* Write the compressed frame to the media file. */
    if (av_interleaved_write_frame(oc, &pkt) != 0) {
        LOGE(1, "Error while writing audio frame\n");
        exit(1);
    }

}

static void close_audio(AVFormatContext *oc, AVStream *st, struct EncoderData * enc_data)
{
    avcodec_close(st->codec);

    av_free(enc_data->samples);
}

/**************************************************************/
/* video output */


/* Add a video output stream. */
static AVStream *add_video_stream(AVFormatContext *oc, AVCodec **codec,
                                  enum AVCodecID codec_id, struct EncoderData * enc_data)
{
    AVCodecContext *c;
    AVStream *st;

    /* find the video encoder */
    //*codec = avcodec_find_encoder(codec_id);
    *codec = avcodec_find_encoder_by_name("libx264");

    //*codec = avcodec_find_encoder(AV_CODEC_ID_H264);
    if (!(*codec)) {
        LOGE(1, "codec id: %d not found\n", codec_id);
        exit(1);
    }

    st = avformat_new_stream(oc, *codec);
    if (!st) {
        LOGE(1, "Could not alloc stream\n");
        exit(1);
    }

    c = st->codec;

    avcodec_get_context_defaults3(c, *codec);

    c->codec_id = codec_id;


    /* Put sample parameters. */
   // c->bit_rate = enc_data->video_bit_rate;
    /* Resolution must be a multiple of two. */
    c->width    = enc_data->video_width;
    c->height   = enc_data->video_height;
    /* timebase: This is the fundamental unit of time (in seconds) in terms
     * of which frame timestamps are represented. For fixed-fps content,
     * timebase should be 1/framerate and timestamp increments should be
     * identical to 1. */
    c->time_base.den = 30; //enc_data->video_frame_rate;
    c->time_base.num = 1;
    c->gop_size      = 12; /* emit one intra frame every twelve frames at most */
    c->pix_fmt       = STREAM_PIX_FMT;
    c->thread_count = 0;
    c->max_b_frames = 0;
    if (c->codec_id == AV_CODEC_ID_MPEG2VIDEO) {
        /* just for testing, we also add B frames */
        c->max_b_frames = 2;
    }
    if (c->codec_id == AV_CODEC_ID_MPEG1VIDEO) {
        /* Needed to avoid using macroblocks in which some coeffs overflow.
         * This does not happen with normal video, it just happens here as
         * the motion of the chroma plane does not match the luma plane. */
        c->mb_decision = 2;
    }
    /* Some formats want stream headers to be separate. */
    if (oc->oformat->flags & AVFMT_GLOBALHEADER)
        c->flags |= CODEC_FLAG_GLOBAL_HEADER;
    LOGI(1, "cccode: Encoder options: id: %d", c->codec_id);
    LOGI(1, "cccode: Encoder options: bit_rate: %d", c->bit_rate);
    LOGI(1, "cccode: Encoder options: width: %d", c->width);
    LOGI(1, "cccode: Encoder options: height: %d", c->height);
    LOGI(1, "cccode: Encoder options: time_base.den: %d", c->time_base.den);

    c->profile = FF_PROFILE_H264_EXTENDED;
    //c->level = 30;
    c->coder_type = FF_CODER_TYPE_VLC;	// coder = 1
        c->flags |= CODEC_FLAG_LOOP_FILTER; // flags=+loop
        c->me_cmp|= 1;	 // cmp=+chroma, where CHROMA = 1

        c->me_method=ME_HEX;	 // me_method=hex
        c->me_subpel_quality = 7;	 // subq=7
        c->me_range = 16;	 // me_range=16

        c->keyint_min = 12;	 // keyint_min=25
        c->scenechange_threshold = 40;	 // sc_threshold=40
        c->i_quant_factor = 0.71;	 // i_qfactor=0.71
        c->b_frame_strategy = 1;	 // b_strategy=1
        c->qcompress = 0.6;	 // qcomp=0.6
        c->qmin = 30;	 // qmin=10
        c->qmax = 51;	 // qmax=51
        c->max_qdiff = 20;	 // qdiff=4
        c->max_b_frames = 0;	 // bf=3
        c->refs = 3;	 // refs=3
        c->trellis = 1;	 // trellis=1



    return st;
}

static void open_video(AVFormatContext *oc, AVCodec *codec, AVStream *st, struct EncoderData * enc_data)
{
    int ret;
    AVCodecContext *c = st->codec;

    /* open the codec */
    if (avcodec_open2(c, codec, NULL) < 0) {
        LOGE(1, "Could not open codec\n");
        exit(1);
    }

    enc_data->video_outbuf = NULL;
    if (!(oc->oformat->flags & AVFMT_RAWPICTURE)) {
        /* Allocate output buffer. */
        /* XXX: API change will be done. */
        /* Buffers passed into lav* can be allocated any way you prefer,
         * as long as they're aligned enough for the architecture, and
         * they're freed appropriately (such as using av_free for buffers
         * allocated with av_malloc). */
        enc_data->video_outbuf_size = avpicture_get_size(c->pix_fmt, c->width, c->height); //200000;
        enc_data->video_outbuf      = av_malloc(enc_data->video_outbuf_size);
    }

    /* allocate and init a re-usable frame */
    enc_data->frame = avcodec_alloc_frame();
    if (!enc_data->frame) {
        LOGE(1, "Could not allocate video frame\n");
        exit(1);
    }

    /* Allocate the encoded raw picture. */
    ret = avpicture_alloc(&enc_data->dst_picture, c->pix_fmt, c->width, c->height);
    if (ret < 0) {
        LOGE(1, "Could not allocate picture\n");
        exit(1);
    }

    /* If the output format is not YUV420P, then a temporary YUV420P
     * picture is needed too. It is then converted to the required
     * output format. */
    if (c->pix_fmt != PIX_FMT_YUV420P) {
        ret = avpicture_alloc(&enc_data->src_picture, PIX_FMT_YUV420P, c->width, c->height);
        if (ret < 0) {
            LOGE(1, "Could not allocate temporary picture\n");
            exit(1);
        }
    }

    /* copy data and linesize picture pointers to frame */
    *((AVPicture *)enc_data->frame) = enc_data->dst_picture;
    enc_data->frame_count = 0;
}



void write_video_frame2(AVFormatContext *oc, AVStream *st, struct EncoderData * enc_data, AVFrame *frame) {
       int ret;
       AVCodecContext *c = st->codec;
       AVPacket pkt;
       int got_output;


       av_picture_copy((AVPicture*)enc_data->frame, (AVPicture*)frame, c->pix_fmt, c->width, c->height);	// Copy only the frame content without any other disturbing stuff
       enc_data->frame_count++;

       enc_data->frame->pts = (enc_data->frame_count)*40*90;	 // Setting correct pts


       av_init_packet(&pkt);
       pkt.data = NULL;    // packet data will be allocated by the encoder
       pkt.size = 0;

       ret = avcodec_encode_video2(c, &pkt, enc_data->frame, &got_output);
       LOGI(1, "cccode: frame->pts: %" PRId64, enc_data->frame->pts);
        //ret = avcodec_encode_video2(c, &pkt, newFrame, &got_output);
        if (ret < 0) {
            LOGE(1, "Error encoding video frame\n");
            exit(1);
        }
        //LOGI(1, "Video frame encoded");
        /* If size is zero, it means the image was buffered. */
        if (got_output) {
            if (c->coded_frame->pts != AV_NOPTS_VALUE)
                pkt.pts = av_rescale_q(c->coded_frame->pts,
                                       c->time_base, st->time_base);

              //  pkt.dts = enc_data->frame_count * 40 * 90;

              pkt.dts = pkt.pts;
                LOGI(1, "cccode: pts: %"PRId64", dts: %"PRId64, pkt.pts, pkt.dts);
            if (c->coded_frame->key_frame)
                pkt.flags |= AV_PKT_FLAG_KEY;

            pkt.stream_index = st->index;

            /* Write the compressed frame to the media file. */
            ret = av_interleaved_write_frame(oc, &pkt);
            //LOGI(1, "Video frame was saved. Ret: %d", ret);
        } else {
            ret = 0;
            LOGI(1, "Video frame was buffered");
        }

    if (ret != 0) {
        LOGE(1, "Error while writing video frame\n");
        exit(1);
    }

    //av_free(picture_buf);
}




static void close_video(AVFormatContext *oc, AVStream *st, struct EncoderData * enc_data)
{
    avcodec_close(st->codec);
    av_free(enc_data->src_picture.data[0]);
    av_free(enc_data->dst_picture.data[0]);
    av_free(enc_data->frame);
    av_free(enc_data->video_outbuf);
}

/**************************************************************/
int editor_init(struct EncoderData * enc_data, const char *filename) {

    int i;

   /* allocate the output media context */
    avformat_alloc_output_context2(&enc_data->oc, NULL, NULL, filename);
    if (!enc_data->oc) {
        printf("Could not deduce output format from file extension: using MPEG.\n");
        avformat_alloc_output_context2(&enc_data->oc, NULL, "mpeg", filename);
    }
    if (!enc_data->oc) {
        return 1;
    }

    enc_data->fmt = enc_data->oc->oformat;

    /* Add the audio and video streams using the default format codecs
     * and initialize the codecs. */
    enc_data->video_st = NULL;
    enc_data->audio_st = NULL;
    if (enc_data->fmt->video_codec != AV_CODEC_ID_NONE) {
        enc_data->video_st = add_video_stream(enc_data->oc,
                                       &enc_data->video_codec,
                                       enc_data->video_codec_id, enc_data);
    }
    if (enc_data->fmt->audio_codec != AV_CODEC_ID_NONE) {
        enc_data->audio_st = add_audio_stream(enc_data->oc,
                                    &enc_data->audio_codec,
                                    enc_data->audio_codec_id, enc_data);
    }

    /* Now that all the parameters are set, we can open the audio and
     * video codecs and allocate the necessary encode buffers. */
    if (enc_data->video_st)
        open_video(enc_data->oc, enc_data->video_codec, enc_data->video_st, enc_data);
    if (enc_data->audio_st)
        open_audio(enc_data->oc, enc_data->audio_codec, enc_data->audio_st, enc_data);

    av_dump_format(enc_data->oc, 0, filename, 1);

    /* open the output file, if needed */
    if (!(enc_data->fmt->flags & AVFMT_NOFILE)) {
        if (avio_open(&enc_data->oc->pb, filename, AVIO_FLAG_WRITE) < 0) {
            LOGE(1, "Could not open '%s'\n", filename);
            return 1;
        }
    }

    /* Write the stream header, if any. */
    if (avformat_write_header(enc_data->oc, NULL) < 0) {
        LOGE(1, "Error occurred when opening output file\n");
        return 1;
    }

    enc_data->frame->pts = 0;

    return 0;
}


int editor_finish(struct EncoderData * enc_data) {
    int i;

    av_write_trailer(enc_data->oc);

    /* Close each codec. */
    if (enc_data->video_st)
        close_video(enc_data->oc, enc_data->video_st, enc_data);
    if (enc_data->audio_st)
        close_audio(enc_data->oc, enc_data->audio_st, enc_data);

    /* Free the streams. */
    for (i = 0; i < enc_data->oc->nb_streams; i++) {
        av_freep(&enc_data->oc->streams[i]->codec);
        av_freep(&enc_data->oc->streams[i]);
    }

    if (!(enc_data->fmt->flags & AVFMT_NOFILE))
        /* Close the output file. */
        avio_close(enc_data->oc->pb);

    /* free the stream */
    av_free(enc_data->oc);

    return 0;
}
