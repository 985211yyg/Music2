package com.example.yungui.music.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.yungui.music.info.MusicInfo;

import java.util.List;

/**
 * Created by yungui on 2017/12/28.
 */
@Dao
public interface MusicInfoDao {
    // 数据库中放回所有的数据
    @Query("SELECT * FROM musicInfo")
    LiveData<List<MusicInfo>> getAll();

    // 插入多个数据,如果数据重复则替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<MusicInfo> musicInfos);

    // 删除数据
    @Delete
    void delete(MusicInfo musicInfo);

    //根据歌曲id选择对应的歌曲
    @Query("SELECT * FROM musicInfo WHERE  songId= :songId")
    MusicInfo getMusicByID(long songId);

    // 插入多个数据,如果数据重复则替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MusicInfo... musicInfos);

}
