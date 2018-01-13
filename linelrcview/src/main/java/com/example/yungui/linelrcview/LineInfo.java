package com.example.yungui.linelrcview;

/**
 *
 * @author yungui
 * @date 2017/11/15
 */
public class LineInfo {
    public long duration;//每行歌词的周期
    public boolean isNullLrc;
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

    public boolean isNullLrc() {
        return isNullLrc;
    }

    public void setNullLrc(boolean nullLrc) {
        isNullLrc = nullLrc;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "LineInfo{" +
                "duration=" + duration +
                ", isNullLrc=" + isNullLrc +
                ", lrc='" + lrc + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}
