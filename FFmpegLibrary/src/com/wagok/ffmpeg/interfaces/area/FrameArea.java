package com.wagok.ffmpeg.interfaces.area;

public class FrameArea implements IPlayArea {

    private int startFrameMark;
    private int endFrameMark;

    public FrameArea(int startFrameMark, int endFrameMark) {
        this.startFrameMark = startFrameMark;
        this.endFrameMark = endFrameMark;
    }

    public int getStartFrameMark() {
        return startFrameMark;
    }

    public int getEndFrameMark() {
        return endFrameMark;
    }

    @Override
    public AreaType getType() {
        return AreaType.FRAME_AREA;
    }
}
