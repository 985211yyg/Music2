package com.example.yungui.music.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.bumptech.glide.Glide;
import com.example.yungui.music.R;
import com.example.yungui.music.model.PlayListBean;
import com.example.yungui.music.utils.Constants;
import com.example.yungui.music.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yungui on 2017/12/4.
 */

public class MyDelegateAdapter extends DelegateAdapter.Adapter<MyDelegateAdapter.ViewHolder> {
    private LayoutHelper helper;
    private int count;
    private int viewType;
    private Context context;
    private List<PlayListBean> playListBeans = new ArrayList<>();

    private OnItemClickListener itemClickListener;
    private OnItemChildClickListener itemChildClickListener;


    public MyDelegateAdapter(Context context, LayoutHelper helper, int count, List<? extends Object> data, int viewType) {
        this.helper = helper;
        this.count = count;

        this.viewType = viewType;
        this.context = context;
        switch (viewType) {
            case Constants.SONG_LIST:
                this.playListBeans = (List<PlayListBean>) data;
                break;
            case Constants.DAILY_SUGGEST:

                break;

            case Constants.NEW_CD:
                break;

            case Constants.SPECIAL_RADIO:
                break;

            case Constants.RANKING:
                break;
            case Constants.MUSICIAN:
                break;
        }
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return helper;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Constants.SONG_LIST_HEADER:
                return new ViewHolder(LinearLayout.inflate(context, R.layout.fragment_music_music_hall_item_header, null));
            case Constants.SONG_LIST:
                return new ViewHolder(LinearLayout.inflate(context, R.layout.fragment_music_music_hall_item_song_list, null));
            case Constants.DAILY_SUGGEST_HEADER:
                return new ViewHolder(LinearLayout.inflate(context, R.layout.fragment_music_music_hall_item_header, null));
            case Constants.DAILY_SUGGEST:
                return new ViewHolder(LinearLayout.inflate(context, R.layout.fragment_music_music_hall_item_daily_suggest, null));
            case Constants.NEW_CD_HEADER:
                return new ViewHolder(LinearLayout.inflate(context, R.layout.fragment_music_music_hall_item_header, null));
            case Constants.NEW_CD:
                return new ViewHolder(LinearLayout.inflate(context, R.layout.fragment_music_music_hall_item_new_cd, null));
            case Constants.SPECIAL_RADIO_HEADER:
                return new ViewHolder(LinearLayout.inflate(context, R.layout.fragment_music_music_hall_item_header, null));
            case Constants.SPECIAL_RADIO:
                return new ViewHolder(LinearLayout.inflate(context, R.layout.fragment_music_music_hall_item_special_radio, null));
            case Constants.RANKING_HEADER:
                return new ViewHolder(LinearLayout.inflate(context, R.layout.fragment_music_music_hall_item_header, null));
            case Constants.RANKING:
                return new ViewHolder(LinearLayout.inflate(context, R.layout.fragment_music_music_hall_item_ranking, null));
            case Constants.MUSICIAN_HEADER:
                return new ViewHolder(LinearLayout.inflate(context, R.layout.fragment_music_music_hall_item_header, null));
            case Constants.MUSICIAN:
                return new ViewHolder(LinearLayout.inflate(context, R.layout.fragment_music_music_hall_item_musician, null));
            default:
                return null;
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (viewType) {
            case Constants.SONG_LIST_HEADER:
                holder.header.setText("歌单推荐");
                break;

            case Constants.DAILY_SUGGEST_HEADER:
                holder.header.setText("每日为你推荐·30首");
                break;

            case Constants.NEW_CD_HEADER:
                holder.header.setText("新歌速递");
                break;

            case Constants.SPECIAL_RADIO_HEADER:
                holder.header.setText("精选电台");
                break;

            case Constants.RANKING_HEADER:
                holder.header.setText("排行版");
                break;

            case Constants.MUSICIAN_HEADER:
                holder.header.setText("音乐人");
                break;

            case Constants.SONG_LIST:

                break;

            case Constants.DAILY_SUGGEST:
                break;

            case Constants.NEW_CD:
                break;

            case Constants.SPECIAL_RADIO:
                break;

            case Constants.RANKING:
                break;
            case Constants.MUSICIAN:
                break;

        }

    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, name, desc, header, sort;
        public ImageView icon, iv_playBtn;
        public CircleImageView circleImageView;
        public ImageButton headerButton, moreButton, ib_playBtn;

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.itemClick(v, getAdapterPosition());
                    }
                }
            });
            switch (viewType) {
                case Constants.SONG_LIST_HEADER://11
                case Constants.DAILY_SUGGEST_HEADER://22
                case Constants.NEW_CD_HEADER://33
                case Constants.SPECIAL_RADIO_HEADER://44
                case Constants.RANKING_HEADER://55
                case Constants.MUSICIAN_HEADER://66
                    header = itemView.findViewById(R.id.header);
                    headerButton = itemView.findViewById(R.id.header_btn);
                    headerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (itemClickListener != null) {
                                itemClickListener.itemClick(v, getAdapterPosition());
                            }
                        }
                    });
                    break;

                case Constants.SONG_LIST://1
                    icon = itemView.findViewById(R.id.song_list_icon);
                    title = itemView.findViewById(R.id.song_list_title);
                    iv_playBtn = itemView.findViewById(R.id.song_list_play_btn);
                    iv_playBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (itemClickListener != null) {
                                itemClickListener.itemClick(v, getAdapterPosition());
                            }
                        }
                    });

                    break;
                case Constants.DAILY_SUGGEST://2
                    icon = itemView.findViewById(R.id.daily_suggest_icon);
                    name = itemView.findViewById(R.id.daily_suggest_song_name);
                    desc = itemView.findViewById(R.id.daily_suggest_desc);
                    ib_playBtn = itemView.findViewById(R.id.daily_suggest_more);
                    ib_playBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (itemClickListener != null) {
                                itemClickListener.itemClick(v, getAdapterPosition());
                            }
                        }
                    });
                    break;
                case Constants.NEW_CD://3
                    icon = itemView.findViewById(R.id.new_cd_icon);
                    sort = itemView.findViewById(R.id.new_cd_sort);
                    desc = itemView.findViewById(R.id.new_cd_desc);
                    break;
                case Constants.SPECIAL_RADIO://5
                    circleImageView = itemView.findViewById(R.id.special_radio_circleImageView);
                    sort = itemView.findViewById(R.id.new_cd_sort);
                    iv_playBtn = itemView.findViewById(R.id.special_radio_play);
                    iv_playBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (itemClickListener != null) {
                                itemClickListener.itemClick(v, getAdapterPosition());
                            }
                        }
                    });
                    break;
                case Constants.RANKING://4
                    icon = itemView.findViewById(R.id.ranking_icon);
                    title = itemView.findViewById(R.id.ranking_title);
                    desc = itemView.findViewById(R.id.ranking_des);
                    break;
                case Constants.MUSICIAN://6
                    icon = itemView.findViewById(R.id.musician_icon);
                    title = itemView.findViewById(R.id.musician_title);
                    desc = itemView.findViewById(R.id.musician_desc);
                    moreButton = itemView.findViewById(R.id.musician_more);
                    iv_playBtn = itemView.findViewById(R.id.musician_play);
                    moreButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (itemChildClickListener != null) {
                                itemChildClickListener.itemChildClick(v, getAdapterPosition());
                            }
                        }
                    });
                    iv_playBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (itemChildClickListener != null) {
                                itemChildClickListener.itemChildClick(v, getAdapterPosition());
                            }
                        }
                    });
                    break;
            }


        }

    }

    public void addData(List<? extends Object> data, int dataType) {
        switch (dataType) {
            case Constants.SONG_LIST:
                playListBeans = (List<PlayListBean>) data;
                break;

            case Constants.DAILY_SUGGEST:
                break;

            case Constants.NEW_CD:
                break;

            case Constants.SPECIAL_RADIO:

                break;

            case Constants.RANKING:

                break;
            case Constants.MUSICIAN:
                break;
        }
        notifyDataSetChanged();

    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;

    }

    public void setOnItemChildClickListener(OnItemChildClickListener itemChildClickListener) {
        this.itemChildClickListener = itemChildClickListener;

    }

    public interface OnItemClickListener {
        void itemClick(View view, int position);
    }

    public interface OnItemChildClickListener {
        void itemChildClick(View childView, int position);
    }

}
