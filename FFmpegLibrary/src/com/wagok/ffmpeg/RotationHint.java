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

    public static RotationHint getRotationHintFromDegree(int degree) {
        RotationHint hint;
        switch (degree) {
            case 90:
                hint = RotationHint.CLOCKWISE_90;
                break;
            case 270:
                hint = RotationHint.COUNTER_CLOCKWISE_90;
                break;
            case 360:
                hint = RotationHint.CLOCKWISE_AND_VERTICAL_FLIP;
                break;
            default:
                hint = RotationHint.DEFAULT;
        }
        return hint;
    }
}
