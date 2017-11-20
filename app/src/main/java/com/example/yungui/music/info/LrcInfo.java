package com.example.yungui.music.info;

import java.util.List;

/**
 * Created by yungui on 2017/11/15.
 */

public class LrcInfo {
    public List<LineInfo> lineInfos;//歌词行
    public String songName;//歌名 ti
    public String singer;//歌手 ar
    public String album;//专辑 al
    public long offset;//时间偏移量

    public List<LineInfo> getLineInfos() {
        return lineInfos;
    }

    public void setLineInfos(List<LineInfo> lineInfos) {
        this.lineInfos = lineInfos;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "LrcInfo{" +
                "lineInfos=" + lineInfos +
                ", songName='" + songName + '\'' +
                ", singer='" + singer + '\'' +
                ", album='" + album + '\'' +
                ", offset=" + offset +
                '}';
    }


}
