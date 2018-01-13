package com.example.yungui.music.utils;

import com.example.yungui.music.MainApplication;
import com.example.yungui.music.model.Music;
import com.example.yungui.music.model.PlayList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by yungui on 2017/12/11.
 */

public class JsonUtils {
    public static List<Music> paresMusicFromAssetsSource(String sourceName) {
        return Music.arrayMusicFromData(pares(sourceName));
    }

    public static List<PlayList> paresPlayListFromAssetsSource(String sourceName) {
        return PlayList.arrayPlayListFromData(pares(sourceName));
    }

    private static String pares(String sourceName) {
        StringBuffer buffer = new StringBuffer();
        try {
            InputStream inputStream = MainApplication.context.getResources().getAssets().open(sourceName);
            InputStreamReader reader = new InputStreamReader(inputStream);
            char[] cBuffer = new char[1024];
            int hasRead = 0;
            while ((hasRead = reader.read(cBuffer)) > 0) {
                buffer.append(new String(cBuffer, 0, hasRead));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static Observable<List<Music>> getMusicList(final String sourceName) {
        return Observable.create(new ObservableOnSubscribe<List<Music>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Music>> e) throws Exception {
                List<Music> musics = new ArrayList<>();
                musics = paresMusicFromAssetsSource(sourceName);
                if (musics != null && musics.size() > 0) {
                    e.onNext(musics);
                }
            }
        });
    }

    public static Observable<List<PlayList>> getPlayList(final String sourceName) {
        return Observable.create(new ObservableOnSubscribe<List<PlayList>>() {
            @Override
            public void subscribe(ObservableEmitter<List<PlayList>> e) throws Exception {
                List<PlayList> playLists = new ArrayList<>();
                playLists = paresPlayListFromAssetsSource(sourceName);
                if (playLists != null && playLists.size() > 0) {
                    e.onNext(playLists);
                }
            }
        });
    }

}
