package com.example.yungui.music.net.retrofit_interface;

import com.example.yungui.music.model.MusicBean;
import com.example.yungui.music.model.PlayListBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by yungui on 2017/12/12.
 */

public interface MusicApi {
    //    https://api.imjad.cn/cloudmusic/?type=playlist&id=1985119278
    @GET("https://api.imjad.cn/cloudmusic/")
    Observable<PlayListBean> getPlayListBean(@Query("type") String type,
                                             @Query("id") String id);
    @GET("https://api.imjad.cn/cloudmusic/")
    Observable<MusicBean> getMusicBean(@Query("type") String type,
                                       @Query("id") String id);
}
