package com.remote.control.allsmarttv.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jaku.model.Channel;
import com.remote.control.allsmarttv.R;

import java.util.List;

public class ApplicationsAdapter extends ArrayAdapter<Channel> {

    private final Context mContext;
    private ImageFetch imageFetcherClass;
    private List<Channel> channels;
    private Handler handler;
    private int itemHeight = 0;
    private int numColumns = 0;

    private FrameLayout.LayoutParams mImageViewLayoutParams;

    public ApplicationsAdapter(Context context, ImageFetch imageFetcherClass, List<Channel> channels, Handler handler) {
        super(context, R.layout.empty_list, channels);
        mContext = context;
        this.imageFetcherClass = imageFetcherClass;
        this.channels = channels;
        mImageViewLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.handler = handler;
    }

    @Override
    public int getCount() {
        // If columns have yet to be determined, return no items
        if (getNumColumns() == 0) {
            return 0;
        }

        return channels.size();
    }

    private class ViewHolder {
        ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {

        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.roku_apps_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.apps_image);

            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.imageView.setLayoutParams(mImageViewLayoutParams);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder.imageView.getLayoutParams().height != itemHeight) {
            holder.imageView.setLayoutParams(mImageViewLayoutParams);
        }

        final Channel channel = getItem(position);

        imageFetcherClass.loadImage(RokuCmds.getPic(channel.getId()), holder.imageView);
        return convertView;
    }

    public void setTheItemHeight(int height) {
        if (height == itemHeight) {
            return;
        }
        itemHeight = height;
        mImageViewLayoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
        imageFetcherClass.setImageSize(height);
        notifyDataSetChanged();
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getChannelCount() {
        return channels.size();
    }
}
