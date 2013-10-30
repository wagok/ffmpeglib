package com.wagok.ffmpeg;

public enum RotationHint {
    DEFAULT(0),                     //90CounterCLockwise and Vertical Flip (default)
    CLOCKWISE_90(1),                //90Clockwise
    COUNTER_CLOCKWISE_90(2),        //90CounterClockwise
    CLOCKWISE_AND_VERTICAL_FLIP(3); //90Clockwise and Vertical Flip

    private int param;

    private RotationHint(int param) {
        this.param = param;
    }

    public int getParam() {
        return param;
    }
}
