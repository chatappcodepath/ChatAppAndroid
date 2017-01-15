package com.lzchat.LZChat.chatAddons.movie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzchat.LZChat.chatAddons.movie.models.Movie;

import java.util.ArrayList;

/**
 * Created by Disha on 10/20/2016.
 */
public class TrailerGridAdapter extends ArrayAdapter<Movie> {


    private Context mContext;
    private int layoutResourceId;

    public ArrayList<Movie> getmGridData() {
        return mGridData;
    }

    public void setmGridData(ArrayList<Movie> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    private ArrayList<Movie> mGridData = new ArrayList<Movie>();

    public TrailerGridAdapter(Context mContext, int layoutResourceId, ArrayList<Movie> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(com.lzchat.firebase.codelab.friendlychat.R.id.tvTrailerTitle);
            holder.imageView = (ImageView) row.findViewById(com.lzchat.firebase.codelab.friendlychat.R.id.ivTrailerImage);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Movie item = mGridData.get(position);
        holder.titleTextView.setText(item.getTitle());

        Glide.with(this.getContext()).load(item.getPoster_path()).into(holder.imageView);
        return row;
    }

    @Override
    public int getCount() {
        int count = mGridData.size();
        return count;
    }

    @Override
    public Movie getItem(int position) {
        return mGridData.get(position);
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }


}
