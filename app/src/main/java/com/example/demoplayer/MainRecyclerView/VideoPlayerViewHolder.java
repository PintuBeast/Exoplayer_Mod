package com.example.demoplayer.MainRecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.demoplayer.Home;
import com.example.demoplayer.HomeHorizontal;
import com.example.demoplayer.Models.MediaObject;
import com.example.demoplayer.R;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class VideoPlayerViewHolder extends RecyclerView.ViewHolder {

private String OriginalVideoUri;

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

    public VideoPlayerViewHolder(@NonNull View itemView) {
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

   shareBtn.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {


           Intent i=new Intent(itemView.getContext(), HomeHorizontal.class);
           i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           i.putExtra("OriginalUri",OriginalVideoUri);
           itemView.getContext().startActivity(i);
           ((Activity)itemView.getContext()).finish();
       }
   });

    }


    public void onBind(MediaObject mediaObject, RequestManager requestManager) {
        this.requestManager = requestManager;
        parent.setTag(this);
      //  title.setText(mediaObject.getDescription()+",\n"+mediaObject.getDate());
        makeThumbnail(mediaObject.getThumbnail());

        String mediaUrld = mediaObject.getMedia_url();
setVideoUri(mediaUrld);
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


    private void setVideoUri(String videoUrl) {

        storage.getReference().child("videos/" + videoUrl)
                .getDownloadUrl().
                addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "video uri = " + uri.toString());
                       OriginalVideoUri=uri.toString();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "video uri = playVideoFromUrl failed" );
            }
        });
    }



}