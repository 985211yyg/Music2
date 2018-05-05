package com.example.yungui.music.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yungui on 2017/12/11.
 */

public class Music {

    /**
     * id : 1
     * song_id : 26548586
     * song_name : Y.M.C.A.
     * author : The Minions
     */

    private int id;
    private int song_id;
    private String song_name;
    private String author;

    public static Music objectFromData(String str) {

        return new Gson().fromJson(str, Music.class);
    }

    public static Music objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), Music.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Music> arrayMusicFromData(String str) {
        Type listType = new TypeToken<ArrayList<Music>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<Music> arrayMusicFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<Music>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSong_id() {
        return song_id;
    }

    public void setSong_id(int song_id) {
        this.song_id = song_id;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
