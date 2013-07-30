#ifndef EDITOR_H_INCLUDED
#define EDITOR_H_INCLUDED

struct EncoderData {

// Audio
   float t, tincr, tincr2;
   int16_t *samples;
   int audio_input_frame_size;
// Video
   AVFrame *frame;
   AVPicture src_picture, dst_picture;
   uint8_t *video_outbuf;
   int frame_count, video_outbuf_size;
// Main
   AVOutputFormat *fmt;
   AVFormatContext *oc;
   AVStream *audio_st, *video_st;
   AVCodec *audio_codec, *video_codec;
   double audio_pts, video_pts;

   int video_frame_rate;
   int video_bit_rate;
   int video_width;
   int video_height;
   enum AVCodecID video_codec_id;
   int audio_sample_rate;
   int audio_bit_rate;
   enum AVCodecID audio_codec_id;
   int audio_channels;
   long duration;
};

static AVStream *add_audio_stream(AVFormatContext *oc, AVCodec **codec,
                                  enum AVCodecID codec_id, struct EncoderData * enc_data);


static void open_audio(AVFormatContext *oc, AVCodec *codec, AVStream *st, struct EncoderData * enc_data);

static void get_audio_frame(int16_t *samples, int frame_size, int nb_channels, struct EncoderData * enc_data);

static void write_audio_frame(AVFormatContext *oc, AVStream *st, struct EncoderData * enc_data);

static void close_audio(AVFormatContext *oc, AVStream *st, struct EncoderData * enc_data);

static AVStream *add_video_stream(AVFormatContext *oc, AVCodec **codec,
                                  enum AVCodecID codec_id, struct EncoderData * enc_data);

static void open_video(AVFormatContext *oc, AVCodec *codec, AVStream *st, struct EncoderData * enc_data);


static void fill_yuv_image(AVPicture *pict, int frame_index,
                           int width, int height);

void write_video_frame(AVFormatContext *oc, AVStream *st, struct EncoderData * enc_data);

static void close_video(AVFormatContext *oc, AVStream *st, struct EncoderData * enc_data);

int editor_init(struct EncoderData * enc_data, const char *filename);

int editor_finish(struct EncoderData * enc_data);

int editor_main(const char *filename, struct EncoderData * enc_data);

void write_video_frame2(AVFormatContext *oc, AVStream *st, struct EncoderData * enc_data, AVFrame *frame);

void write_audio_frame2(AVFormatContext *oc, AVStream *st, struct EncoderData * enc_data, AVFrame *frame);

#endif // EDITOR_H_INCLUDED
