package com.ascend.www.abkms.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ascend.www.abkms.R;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;
import com.bumptech.glide.Glide;
import com.github.tntkhang.fullscreenimageview.library.FullScreenImageViewActivity;
import com.smarteist.autoimageslider.SliderViewAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    private Context context;
    JSONArray result_array;
    int slider_image_no=0;
    Dialog DialogPreview;
    ArrayList<String>  SLIDER_IMAGE_URI = new ArrayList<>();
    public SliderAdapterExample(Context context) {
        this.context = context;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        //viewHolder.textViewDescription.setText("This is slider item " + position);
        String slider_ary = UtilitySharedPreferences.getPrefs(context,"SliderImagesArray");

        try{
            JSONObject jsonObject = new JSONObject(slider_ary);

            boolean status = jsonObject.getBoolean("status");
            if(status) {
                result_array = jsonObject.getJSONArray("result");
                for (slider_image_no = 0; slider_image_no< result_array.length(); slider_image_no++ ){

                    JSONObject jsonObject1 = result_array.getJSONObject(slider_image_no);

                    String ImageName = jsonObject1.getString("image_name");
                    String image_url = jsonObject1.getString("image_url");
                    SLIDER_IMAGE_URI.add(String.valueOf(Uri.parse( image_url )));
                    switch (position) {
                        case 0:
                            if(slider_image_no==0) {
                                Glide.with(viewHolder.itemView)
                                        .load(image_url)
                                        .into(viewHolder.imageViewBackground);

                            }
                            break;

                        case 1:
                            if(slider_image_no==1) {
                                Glide.with(viewHolder.itemView)
                                        .load(image_url)
                                        .fitCenter()
                                        .into(viewHolder.imageViewBackground);

                            }
                            break;
                        case 2:
                            if(slider_image_no==2) {
                                Glide.with(viewHolder.itemView)
                                        .load(image_url)
                                        .fitCenter()
                                        .into(viewHolder.imageViewBackground);

                            }
                            break;
                        case 3:
                            if(slider_image_no==3) {
                                Glide.with(viewHolder.itemView)
                                        .load(image_url)
                                        .fitCenter()
                                        .into(viewHolder.imageViewBackground);

                            }
                            break;
                        case 4:
                            if(slider_image_no==4) {
                                Glide.with(viewHolder.itemView)
                                        .load(image_url)
                                        .fitCenter()
                                        .into(viewHolder.imageViewBackground);

                            }

                            break;


                        default:
                            Glide.with(viewHolder.itemView)
                                    .load(R.drawable.slider_image1)
                                    .fitCenter()
                                    .into(viewHolder.imageViewBackground);

                            break;

                    }




                }


                viewHolder.imageViewBackground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ShowPreviewPopup(1);
                        Intent fullImageIntent = new Intent(context, FullScreenImageViewActivity.class);
                        // uriString is an ArrayList<String> of URI of all images
                        fullImageIntent.putExtra(FullScreenImageViewActivity.URI_LIST_DATA, SLIDER_IMAGE_URI);
                        // pos is the position of image will be showned when open
                        fullImageIntent.putExtra(FullScreenImageViewActivity.IMAGE_FULL_SCREEN_CURRENT_POS, slider_image_no);
                        context.startActivity(fullImageIntent);

                    }
                });



            }

        }catch (Exception e){
            e.printStackTrace();
        }







    }





    @Override
    public int getCount() {
        //slider view count could be dynamic size
        int count = 5;

        if(result_array!=null && result_array.length()>0) {
            count = result_array.length();
            return count;
        }else {
            return count;
        }
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;


        }
    }
}