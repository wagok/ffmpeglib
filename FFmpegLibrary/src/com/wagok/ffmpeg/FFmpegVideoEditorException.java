package com.wagok.ffmpeg;

public class FFmpegVideoEditorException extends Exception {
    public FFmpegVideoEditorException() {
    }

    public FFmpegVideoEditorException(String detailMessage) {
        super(detailMessage);
    }

    public FFmpegVideoEditorException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public FFmpegVideoEditorException(Throwable throwable) {
        super(throwable);
    }
}
