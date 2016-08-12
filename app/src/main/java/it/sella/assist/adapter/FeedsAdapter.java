package it.sella.assist.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.sella.assist.R;
import it.sella.assist.data.SellaAssistContract;
import it.sella.assist.ui.FullScreenImageActivity;
import it.sella.assist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 22-Jul-16.
 */
public class FeedsAdapter extends RecyclerViewCursorAdapter<FeedsAdapter.FeedViewHolder> implements View.OnClickListener {
    private static final String TAG = FeedsAdapter.class.getSimpleName();
    private Context context;
    private OnItemClickListener onItemClickListener;

    public FeedsAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, Cursor cursor) {
        holder.bindData(context, cursor);
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_feed, parent, false);
        return new FeedViewHolder(v);
    }

    @Override
    public void onClick(final View view) {
        if (this.onItemClickListener != null) {
            final RecyclerView recyclerView = (RecyclerView) view.getParent();
            final int position = recyclerView.getChildLayoutPosition(view);
            if (position != RecyclerView.NO_POSITION) {
                final Cursor cursor = this.getItem(position);
                this.onItemClickListener.onItemClicked(cursor);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(Cursor cursor);
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        public final ImageView profileImage;
        public final TextView createByName;
        public final TextView startTimestamp;
        public final TextView itArea;
        public final TextView important;
        public final TextView message;
        public final TextView url;
        public final ImageView image;

        public FeedViewHolder(View view) {
            super(view);
            profileImage = (ImageView) view.findViewById(R.id.feed_profileImage);
            createByName = (TextView) view.findViewById(R.id.feed_createdByName_textview);
            startTimestamp = (TextView) view.findViewById(R.id.feed_startTimestamp_textview);
            itArea = (TextView) view.findViewById(R.id.feed_area_textview);
            important = (TextView) view.findViewById(R.id.feed_important_textview);
            message = (TextView) view.findViewById(R.id.feed_message_textview);
            url = (TextView) view.findViewById(R.id.feed_url_textview);
            image = (ImageView) view.findViewById(R.id.feed_image);
        }

        public void bindData(final Context context, final Cursor cursor) {
            String createdByName = cursor.getString(SellaAssistContract.FeedEntry.FEED_CREATED_BY_NAME);
            this.createByName.setText(createdByName);

            long startTimestamp = cursor.getLong(SellaAssistContract.FeedEntry.FEED_START_TIMESTAMP);
            this.startTimestamp.setText(Utility.getFeedTimeStamp(startTimestamp));

            String itArea = cursor.getString(SellaAssistContract.FeedEntry.FEED_IT_AREA);

            if (itArea != null) {
                this.itArea.setText(itArea);
                this.itArea.setVisibility(View.VISIBLE);
            } else {
                this.itArea.setText("IT Racolta");
            }

            if (Boolean.parseBoolean(cursor.getString(SellaAssistContract.FeedEntry.FEED_IS_IMPORTANT))) {
                this.important.setVisibility(View.VISIBLE);
            } else {
                this.important.setVisibility(View.GONE);
            }

            this.message.setText(cursor.getString(SellaAssistContract.FeedEntry.FEED_MESSAGE));

            String url = cursor.getString(SellaAssistContract.FeedEntry.FEED_URL);

            if (url != null) {
                this.url.setText(Html.fromHtml("<a href=\"" + url + "\">" + url + "</a> "));
                this.url.setMovementMethod(LinkMovementMethod.getInstance());
                this.url.setVisibility(View.VISIBLE);
            } else {
                this.url.setVisibility(View.GONE);
            }

            String profileImageStr = cursor.getString(SellaAssistContract.FeedEntry.FEED_PROFILE_PIC);
            Picasso.with(context)
                    .load(Uri.parse(profileImageStr))
                    .placeholder(R.drawable.profile_placeholder)
                    .resize(256, 256)
                    .error(R.drawable.profile_placeholder)
                    .into(profileImage);

            final String imageStr = cursor.getString(SellaAssistContract.FeedEntry.FEED_IMAGE);

            if (imageStr != null) {

                Picasso.with(context)
                        .load(Uri.parse(imageStr))
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .into(image);

                this.image.setVisibility(View.VISIBLE);
                this.image.setAdjustViewBounds(true);
                this.image.setScaleType(ImageView.ScaleType.FIT_XY);
                this.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent fullScreenIntent = new Intent(v.getContext(), FullScreenImageActivity.class);
                        fullScreenIntent.putExtra("image_url", imageStr);
                        context.startActivity(fullScreenIntent);
                    }
                });
            } else {
                this.image.setVisibility(View.GONE);
            }
        }
    }
}

