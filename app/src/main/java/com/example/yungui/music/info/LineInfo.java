package com.example.yungui.music.info;

/**
 * Created by yungui on 2017/11/15.
 */
public class LineInfo {
    public String lrc;
    public long startTime;

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "LineInfo{" +
                "lrc='" + lrc + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}
