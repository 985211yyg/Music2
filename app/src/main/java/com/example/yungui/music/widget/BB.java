package com.example.yungui.music.widget;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.cache.CacheInterceptor;
import okhttp3.internal.http.RetryAndFollowUpInterceptor;

public class BB {
    OkHttpClient mOkHttpClient = new OkHttpClient.Builder().build();
    Request mRequest = new Request.Builder().url("www.baidu.com").build();

    public static void main(String[] args) {
        BB bb = new BB();
        bb.mOkHttpClient.newCall(bb.mRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
        try {
            bb.mOkHttpClient.newCall(bb.mRequest).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
