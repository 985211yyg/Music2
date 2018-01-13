package com.example.yungui.music.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.yungui.music.R;
import com.example.yungui.music.activity.PopupItemActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yungui on 2017/12/27.
 */

public class PopupDialogFragment extends DialogFragment {

    public static PopupDialogFragment newInstance() {
        Bundle args = new Bundle();
        PopupDialogFragment fragment = new PopupDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(), R.style.CustomPopupDialog);
        dialog.setContentView(R.layout.fragment_local_pup_more);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.TOP | Gravity.RIGHT);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.y = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, getResources().getDisplayMetrics());
        window.setAttributes(layoutParams);
        ButterKnife.bind(this, dialog);
        return dialog;
    }

    @OnClick({R.id.pop_local_sort,
            R.id.pop_local_scanning,
            R.id.pop_local_download_lrc,
            R.id.pop_local_recover,
            R.id.pop_local_update_ql})
    public void popupWindowItemClick(View view) {
        switch (view.getId()) {
            case R.id.pop_local_sort:
                Jump2Activity(PopupItemActivity.TAG_SCANNING, PopupItemActivity.SCANNING);
                break;
            case R.id.pop_local_update_ql:
                Jump2Activity(PopupItemActivity.TAG_SCANNING, PopupItemActivity.SCANNING);
                break;
            case R.id.pop_local_scanning:
                Jump2Activity(PopupItemActivity.TAG_SCANNING, PopupItemActivity.SCANNING);
                break;
            case R.id.pop_local_download_lrc:
                Jump2Activity(PopupItemActivity.TAG_SCANNING, PopupItemActivity.SCANNING);
                break;
            case R.id.pop_local_recover:
                Jump2Activity(PopupItemActivity.TAG_SCANNING, PopupItemActivity.SCANNING);
                break;
        }
    }

    private void Jump2Activity(int tag, String title) {
        Intent intent = new Intent(getContext(), PopupItemActivity.class);
        intent.putExtra(PopupItemFragment.TAG, tag);
        intent.putExtra(PopupItemFragment.TITLE, title);
        startActivity(intent);
        dismiss();
    }

}
