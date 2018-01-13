package com.example.yungui.lrcview.utils;

import com.example.yungui.lrcview.bean.LineInfo;
import com.example.yungui.lrcview.bean.LrcInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yungui on 2017/11/15.
 */

public class LrcUtils {
    private static LrcInfo lrcInfo;
    private static List<LineInfo> lineInfos;

    public static LrcUtils newInstance() {
        lrcInfo = new LrcInfo();
        lineInfos = new ArrayList<>();

        return new LrcUtils();
    }

    public LrcUtils setupLrcResource(InputStream inputStream, String charsetName) {
        if (inputStream != null) {

            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charsetName);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    analysis(lrcInfo, line);
                }
                lrcInfo.setLineInfos(lineInfos);
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public LrcInfo getLrcInfo() {
        return lrcInfo;
    }

    private void analysis(LrcInfo lrcInfoList, String line) {
        int index = line.lastIndexOf("]");
        if (line != null) {
            if (line.startsWith("[ti:")) {
                //歌名
                lrcInfo.setSongName(line.substring(4, index).trim());
            } else if (line.startsWith("[ar:")) {
                //歌手
                lrcInfo.setSinger(line.substring(4, index).trim());
            } else if (line.startsWith("[al:")) {
                lrcInfo.setAlbum(line.substring(4, index).trim());
            } else if (line.startsWith("[offset:")) {
                lrcInfo.setOffset(Long.parseLong(line.substring(8, index).trim()));
            } else if (line.startsWith("[dy:")) {
                return;
            }
            if (index == 9 && line.trim().length() > 10) {
                //时间
                LineInfo lineInfo = new LineInfo();
                lineInfo.lrc = line.substring(10, line.length());
                lineInfo.startTime = covertTime(line.substring(0, 10));
                lineInfos.add(lineInfo);
            }
        }
    }

    private long covertTime(String time) {
        long minute = Long.parseLong(time.substring(1, 3));
        long second = Long.parseLong(time.substring(4, 6));
        long millisecond = Long.parseLong(time.substring(7, 9));
        return millisecond + second * 1000 + minute * 60 * 1000;
    }
}
