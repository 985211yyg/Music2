package com.example.yungui.music.model;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * Created by yungui on 2017/12/4.
 */

public class SectionMusicBean extends SectionEntity<MusicBean> {

    public SectionMusicBean(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public SectionMusicBean(MusicBean musicBean) {
        super(musicBean);
    }
}
