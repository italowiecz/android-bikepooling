package com.italo.bikepooling.adapter;

/**
 * Created by italo on 28/11/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.italo.bikepooling.FeedImageView;
import com.italo.bikepooling.R;
import com.italo.bikepooling.app.AppController;
import com.italo.bikepooling.data.FeedItem;

import java.util.List;

public class FeedListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView etName = convertView.findViewById(R.id.name);
        TextView etTimestamp = convertView.findViewById(R.id.timestamp);
        EditText etData = convertView.findViewById(R.id.data);
        EditText etHora = convertView.findViewById(R.id.hora);
        EditText etDistance = convertView.findViewById(R.id.distance);
        EditText etExpectedTime = convertView.findViewById(R.id.expectedTime);
        TextView etDescriptionMsg = convertView.findViewById(R.id.descriptionMsg);
        NetworkImageView profilePic = convertView.findViewById(R.id.profilePic);
        FeedImageView routeImage = convertView.findViewById(R.id.routeImage);

        FeedItem item = feedItems.get(position);

        etName.setText(item.getNome());

        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS);
        etTimestamp.setText(timeAgo);

        if (!TextUtils.isEmpty(item.getDescricao())) {
            etDescriptionMsg.setText(item.getDescricao());
            etDescriptionMsg.setVisibility(View.VISIBLE);
        } else {
            etDescriptionMsg.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(item.getData())) {
            etData.setText(item.getData());
            etData.setVisibility(View.VISIBLE);
        } else {
            etData.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(item.getHora())) {
            etHora.setText(item.getHora());
            etHora.setVisibility(View.VISIBLE);
        } else {
            etHora.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(item.getDistancia())) {
            etDistance.setText(item.getDistancia());
            etDistance.setVisibility(View.VISIBLE);
        } else {
            etDistance.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(item.getTempoEstimado())) {
            etExpectedTime.setText(item.getTempoEstimado());
            etExpectedTime.setVisibility(View.VISIBLE);
        } else {
            etExpectedTime.setVisibility(View.GONE);
        }

        // user profile pic
        profilePic.setImageUrl(item.getImagemProfile(), imageLoader);

        // Feed image
        if (item.getImagemRota() != null) {
            routeImage.setImageUrl(item.getImagemRota(), imageLoader);
            routeImage.setVisibility(View.VISIBLE);
            routeImage
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            routeImage.setVisibility(View.GONE);
        }

        return convertView;
    }

}