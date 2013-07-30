package com.wagok.ffmpeg.interfaces.area;

public interface IPlayArea {
    enum AreaType{
        TIME_AREA, FRAME_AREA
    }

    AreaType getType();
}
