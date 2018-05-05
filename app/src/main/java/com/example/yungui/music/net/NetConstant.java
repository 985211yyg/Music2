package com.example.yungui.music.net;

/**
 * Created by yungui on 2017/12/4.
 */

public class NetConstant {

    //轮播页面
    public static final String BANNER_IMG_1 = "http://p1.music.126.net/3F_SflZq_8XU6VZZjpBelg==/18616930883632237.jpg";
    public static final String BANNER_IMG_1_href = "http://music.163.com/#/mv?id=5756080";
    public static final String BANNER_IMG_2 = "http://p1.music.126.net/RfBUxTdn-Q4XxW3iF83BKg==/18686200116173903.jpg";
    public static final String BANNER_IMG_2_href = "http://music.163.com/#//song?id=521417778";
    public static final String BANNER_IMG_3 = "http://p1.music.126.net/xHHu__C2HOyoLJhOijl7xw==/19051237974950053.jpg";
    public static final String BANNER_IMG_3_href = "http://music.163.com/#//mv?id=5754044";
    public static final String BANNER_IMG_4 = "http://p1.music.126.net/gwtSwpNn0ucjIzIWwSEV7w==/18566353348743298.jpg";
    public static final String BANNER_IMG_4_href = "http://music.163.com/#//song?id=521416693";
    public static final String BANNER_IMG_5 = "http://p1.music.126.net/cuzCYU_nUczR4qqiw4ABwQ==/18829136627784790.jpg";
    public static final String BANNER_IMG_5_href = "http://music.163.com/#/djradio?id=526246601";
    public static final String[] BANNER_ITEM_HREF = new String[]{BANNER_IMG_1_href, BANNER_IMG_2_href, BANNER_IMG_3_href, BANNER_IMG_4_href, BANNER_IMG_5_href};


    public static final String MP3_URL = "http://music.163.com/song/media/outer/url?id=";
    /**
     * 搜索
     * s=龙卷风 搜索关键字
     * type=1 类型 1：单曲
     * 100：歌手
     * 10：专辑
     * 1004：mv
     * 1006:歌词
     * 1000：歌单
     */
    public static final String BASE_SEARCH_URL = "http://music.163.com/#/search/m/?s={}&type={}";
    /**
     * 歌手
     */
    public static final String SINGER = "http://music.163.com/#/discover/artist";
    /**
     * 排行
     */
    public static final String TOP = "http://music.163.com/#/discover/toplist";

    /**
     * 电台
     */
    public static final String RADIO = "http://music.163.com/#/discover/djradio";

    /**
     * 热门推荐
     */
    public static final String SUGGEST = "http://music.163.com/#/discover";

    /**
     * 新碟上架
     */
    public static final String NEWCD = "http://music.163.com/#/discover/album";

    /**
     * 歌单首页  http://music.163.com/#/discover/playlist/?order=hot&cat={category}&limit=35&offset={offset}
     */
    public static final String PLAYLIST = "http://music.163.com/#/discover/playlist";

    /**
     * ===============================直接返回Gson数据的连接 ，但是前提都是要知道歌曲或者歌单的id=======================================================
     */
    /**
     * 基础歌曲url >>https://api.imjad.cn/cloudmusic/?type=detail&id=28012031
     *    +     type：  type=song 单曲下载地址不过没用
     *                  type=lyric 歌词
     *                  type=comments  评论
     *                  type=detail   基本的信息，歌曲名歌曲id，歌手名歌手id，专辑封面图之类的
     *                  type=playlist  歌单的相关信息，例如创建者的信息啊，封面图啊，歌单被播放的次数啊....
     *                                 当然还有最重要的歌单内所有歌曲的简略信息（包含歌曲id等等）
     *
     *
     */
    public static final String BASE_URL = "https://api.imjad.cn/cloudmusic/?";


    /**
     *搜索url >>>https://api.imjad.cn/cloudmusic/?type=search&search_type=1&s=cocoon
     *        search_type： 1 单曲
     *                      1000 歌单
     *                      1002 用户
     *                      1004 mv
     *                      1006 歌词
     *                      1009 主播电台
     *
     *
     */
    public static final String Search_Url="https://api.imjad.cn/cloudmusic/?type=search&";



}

