package com.example.yungui.music.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yungui on 2017/12/12.
 */

public class PlayList {

    /**
     * id : 1
     * title : 入耳即醉 百听不厌
     * link : 1985119278
     * cnt : 43792
     * dsc : 曲风：全部
     */

    private int id;
    private String title;
    private String link;
    private int cnt;
    private String dsc;

    public static PlayList objectFromData(String str) {

        return new Gson().fromJson(str, PlayList.class);
    }

    public static PlayList objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), PlayList.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<PlayList> arrayPlayListFromData(String str) {

        Type listType = new TypeToken<ArrayList<PlayList>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<PlayList> arrayPlayListFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<PlayList>>() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getDsc() {
        return dsc;
    }

    public void setDsc(String dsc) {
        this.dsc = dsc;
    }
}
