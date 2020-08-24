package com.example.demoplayer.HorizontalRecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.demoplayer.HomeHorizontal;
import com.example.demoplayer.Models.MediaObject;
import com.example.demoplayer.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

public class HorizontalVideoPlayerViewHolder extends RecyclerView.ViewHolder {


    FrameLayout media_container;
    TextView title,name;
    ImageView thumbnail, volumeControl;
    ProgressBar progressBar;
    View parent;
    RequestManager requestManager;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
   ImageView shareBtn,likeBtn,commentBtn,likeAnimate,download;
   // Button followBtn;
   // CircleImageView profile_img;

    public HorizontalVideoPlayerViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        media_container = itemView.findViewById(R.id.media_container);
        thumbnail = itemView.findViewById(R.id.thumbnail);
       // title = itemView.findViewById(R.id.textTitle);
        //name = itemView.findViewById(R.id.textView8);
       // progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl = itemView.findViewById(R.id.volume_control);
        shareBtn = itemView.findViewById(R.id.btnShare);
      //  likeBtn = itemView.findViewById(R.id.imageView6);
      //  commentBtn = itemView.findViewById(R.id.imageView7);
      //  followBtn = itemView.findViewById(R.id.btnEdit);
      //  profile_img = itemView.findViewById(R.id.circleImageView);
      //  no_likes = itemView.findViewById(R.id.textView9);
      //  no_comments = itemView.findViewById(R.id.textView10);
      //  likeAnimate = itemView.findViewById(R.id.imageView5);
      //  download = itemView.findViewById(R.id.imageView23);



    }


    public void onBind(MediaObject mediaObject, RequestManager requestManager) {
        this.requestManager = requestManager;
        parent.setTag(this);
      //  title.setText(mediaObject.getDescription()+",\n"+mediaObject.getDate());
        makeThumbnail(mediaObject.getThumbnail());

    }

    private void makeThumbnail(String thumbnailUrl){
        storage.getReference().child("images/" + thumbnailUrl)
                .getDownloadUrl().
                addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("TAG", "image uri = " + uri.toString());
                        requestManager.load(uri.toString()).into(thumbnail);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("TAG", "image uri = Failed" );
            }
        });
    }


}