# Music
**截图**
<img src="https://github.com/985211yygg/Music2/blob/master/screenshots/main.png?raw=true" width="40%" height="50%" align="left">

<img src="https://github.com/985211yygg/Music2/blob/master/screenshots/detail.png?raw=true" width="40%" height="50%" align="right">

<img src="https://github.com/985211yygg/Music2/blob/master/screenshots/detail_lrc.png?raw=true" width="40%" height="50%" align="center">


**录屏**

<img src="https://github.com/985211yygg/Music2/blob/master/screenshots/1.gif?raw=true" width="40%" height="50%" align="left">

<img src="https://github.com/985211yygg/Music2/blob/master/screenshots/2.gif?raw=true" width="40%" height="50%" align="right">

<img src="https://github.com/985211yygg/Music2/blob/master/screenshots/3.gif?raw=true" width="40%" height="50%" align="left">

<img src="https://github.com/985211yygg/Music2/blob/master/screenshots/4.gif?raw=true" width="40%" height="50%" align="right">
https://github.com/985211yygg/Music2/blob/master/screenshots/4.gif?raw=true

Music是一款模仿QQ音乐的音乐播放器，应用在单独的服务进程中控制音乐播放，目前以实现功能如下：
- 通过耳机和通知栏快捷控制音乐播放;
- 滑动底部控制栏播放音乐;
- 底部控制栏播放按钮显示播放进度;
- 动态显示歌词、单行显示歌词、调整歌词字体大小及颜色;
- 扫描本地歌曲以及SD卡歌曲等功能。

部分功能尚未完善，还存在一些已知或未知的 bug，正在逐步修复完善。

**用到的库和开源自定义View**
- linelrcview 自定义单行歌词显示view(原计划逐字显示歌曲进度，个人能力还没有实现)
- lrcview 自定义歌词显示空间，支持歌词的动态显示，滑动，滑动快捷播放
- QQPlayButton 自定义仿QQ音乐播放按钮，切换播放、暂停状态，显示播放进度，
- MediaSeekBar 自定义Seekbar 与播放进度同步，支持拖拽快进快退
- CircleImageView 自定义圆形ImageView 用于显示专辑图片，根据播放状态及进度实现唱片效果
-
- Andandroid应用架构组件Room、LiveData、ViewModle、Lifecycle
- RxJava
- Retrofit
- Okhttp
- EventBus
- Butterknife
- Gilde
- EasyBlur  图片高斯模糊
- MZBannerView 图片轮播库
- wavesidebar 索引侧边栏库

**License**

>Copyright 2017 DuanJiaNing
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
