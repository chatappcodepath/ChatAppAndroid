package com.google.firebase.codelab.friendlychat.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.firebase.codelab.friendlychat.R;

/**
 * Created by Disha on 10/20/2016.
 */
public class TrailerGridAdapter extends BaseAdapter {
    private Context context;
    private Integer[] imageIds = {
            R.mipmap.ic_launcher,R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,  R.mipmap.ic_launcher
    };

    public TrailerGridAdapter(Context c) {
        context = c;
    }

    public int getCount() {
        return imageIds.length;
    }

    public Object getItem(int position) {
        return imageIds[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        ImageView iview;
        if (view == null) {
            iview = new ImageView(context);
            iview.setLayoutParams(new GridView.LayoutParams(150,200));
            iview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iview.setPadding(5, 5, 5, 5);
        } else {
            iview = (ImageView) view;
        }
        iview.setImageResource(imageIds[position]);
        return iview;
    }
}

