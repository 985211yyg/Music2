# Music
#### 应用截图

|    |    |    |
| --- | --- | --- |
|   <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/main.png?raw=true" width="200" align="center">  | <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/detail.png?raw=true" width="200"  align="center">     |   <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/detail_lrc.png?raw=true" width="200"  align="center">   |

|                                                                                                                    |                                                                                                                    |                                                                                                                   |
| ------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/1.gif?raw=true" width="200"  align="center"> | <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/2.gif?raw=true" width="200"  align="center"> | <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/3.gif?raw=true" width="200" align="center"> |
| <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/4.gif?raw=true" width="200" align="center">                                                                                                                    |                                                                                                                     |                                                                                     |

#### 应用简介
Music是一款模仿QQ音乐的音乐播放器，应用在单独的服务进程中控制音乐播放，目前以实现功能如下：
- 通过耳机和通知栏快捷控制音乐播放;

- 滑动底部控制栏播放音乐;

- 底部控制栏播放按钮显示播放进度;

- 动态显示歌词、单行显示歌词、调整歌词字体大小及颜色;

- 扫描本地歌曲以及SD卡歌曲等功能。

部分功能尚未完善，还存在一些已知或未知的 bug，正在逐步修复完善。
**正在尝试使用网易云音乐抓取工具[Spider163](https://github.com/Chengyumeng/spider163)，结合MySQL获取更多的歌曲信息,实现在线播放，抓取歌词，歌单，热门排行等。**

#### 用到的库和开源自定义View
- linelrcview 自定义单行歌词显示view(原计划逐字显示歌曲进度，个人能力还没有实现)

- lrcview 自定义歌词显示空间，支持歌词的动态显示，滑动，滑动快捷播放

- QQPlayButton 自定义仿QQ音乐播放按钮，切换播放、暂停状态，显示播放进度，

- MediaSeekBar 自定义Seekbar 与播放进度同步，支持拖拽快进快退

- CircleImageView 自定义圆形ImageView 用于显示专辑图片，根据播放状态及进度实现唱片效果

- Andandroid应用架构组件Room、LiveData、ViewModle、Lifecycle
- RxJava

- Okhttp和Retrofit 网络请求

- EventBus

- Butterknife

- Gilde 图片加载库

- EasyBlur  图片高斯模糊

- MZBannerView 图片轮播库

- wavesidebar 索引侧边栏库
