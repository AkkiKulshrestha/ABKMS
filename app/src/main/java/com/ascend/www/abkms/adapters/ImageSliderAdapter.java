package com.ascend.www.abkms.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ascend.www.abkms.R;
import com.ascend.www.abkms.model.SliderItem;
import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderAdapter extends SliderViewAdapter<ImageSliderAdapter.SliderAdapterVH> {

    private final Context context;
    private ArrayList<String> SLIDER_IMAGE_URI = new ArrayList<>();
    private View mItemView;
    private List<SliderItem> mSliderItems = new ArrayList<>();

    public ImageSliderAdapter(Context context) {
        this.context = context;
    }

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(SliderItem sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        SliderItem sliderItem = mSliderItems.get(position);

        SLIDER_IMAGE_URI.add(String.valueOf(Uri.parse(sliderItem.getImageUrl())));
        Log.d("getDrawableImg", "" + sliderItem.getImageUrl());
        if (context != null) {
            Glide.with(mItemView)
                    .load(sliderItem.getImageUrl())
                    .fitCenter()
                    .into(viewHolder.imageViewBackground);

        }
      /*  viewHolder.imageViewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> uriString = new ArrayList<>();

                Log.d("SLIDER_IMAGE_URI",""+SLIDER_IMAGE_URI);
                Intent fullImageIntent = new Intent(context, FullScreenImageViewActivity.class);
                // uriString is an ArrayList<String> of URI of all images
                fullImageIntent.putExtra(FullScreenImageViewActivity.URI_LIST_DATA, SLIDER_IMAGE_URI);
                // pos is the position of image will be showned when open
                fullImageIntent.putExtra(FullScreenImageViewActivity.IMAGE_FULL_SCREEN_CURRENT_POS, position);
                fullImageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(fullImageIntent);
            }
        });*/
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {


        ImageView imageViewBackground;
        ImageView imageGifContainer;
        TextView textViewDescription;

        private SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            mItemView = itemView;
        }
    }

}
