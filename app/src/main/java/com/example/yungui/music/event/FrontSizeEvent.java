package com.example.yungui.music.event;

/**
 * Created by yungui on 2017/11/28.
 */

public class FrontSizeEvent {
    private int frontSize;

    public FrontSizeEvent() {
    }

    public FrontSizeEvent(int frontSize) {
        this.frontSize = frontSize;
    }

    public int getFrontSize() {
        return frontSize;
    }

    public void setFrontSize(int frontSize) {
        this.frontSize = frontSize;
    }
}
