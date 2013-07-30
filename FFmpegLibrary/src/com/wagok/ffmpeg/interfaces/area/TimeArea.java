package com.wagok.ffmpeg.interfaces.area;

public class TimeArea implements IPlayArea {

    private long startTimeMark;
    private long endTimeMark;

    public TimeArea(long startTimeMark, long endTimeMark) {
        this.startTimeMark = startTimeMark;
        this.endTimeMark = endTimeMark;
    }

    public long getStartTimeMark() {
        return startTimeMark;
    }

    public long getEndTimeMark() {
        return endTimeMark;
    }

    @Override
    public AreaType getType() {
        return AreaType.TIME_AREA;
    }
}
