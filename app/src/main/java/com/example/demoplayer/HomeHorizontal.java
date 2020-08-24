package com.example.demoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.demoplayer.HorizontalRecyclerView.HorizontalVideoPlayerRecyclerAdapter;
import com.example.demoplayer.HorizontalRecyclerView.HorizontalVideoPlayerRecyclerView;
import com.example.demoplayer.MainRecyclerView.VerticalSpacingItemDecorator;
import com.example.demoplayer.MainRecyclerView.VideoPlayerRecyclerAdapter;
import com.example.demoplayer.MainRecyclerView.VideoPlayerRecyclerView;
import com.example.demoplayer.Models.MediaObject;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeHorizontal extends AppCompatActivity {

    private ArrayList<MediaObject> mediaObjectList = new ArrayList<>();

    private HorizontalVideoPlayerRecyclerView recyclerView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String TAG = "Home";
    private MediaObject mediaObject;

    PlayerView playerViewOriginal;
    SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_horizontal);

        Intent i=getIntent();
        String sUri=i.getStringExtra("OriginalUri").toString();

        playerViewOriginal=findViewById(R.id.exoplayerOriginal);
        initializePlayer(sUri);
       init();

    }



    private void initializePlayer(String sUri) {



        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the player
         player = ExoPlayerFactory.newSimpleInstance(HomeHorizontal.this, trackSelector);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                HomeHorizontal.this, Util.getUserAgent(HomeHorizontal.this, "RecyclerView VideoPlayer"));

        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(sUri));

        playerViewOriginal.setPlayer(player);
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
    }




    private void init() {


        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        ///////////////reyclerView////////////////////////

        recyclerView = (HorizontalVideoPlayerRecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        // layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(0);
        recyclerView.addItemDecoration(itemDecorator);

//////////////////////////////////////////////////////////////////
        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
/////////////////////////////////////////////////////////////////

        LoadAllPosts();

    }


    private void LoadAllPosts() {
        mediaObjectList.clear();
        CollectionReference ref = db.collection("media_objects");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        mediaObject = document.toObject(MediaObject.class);
                        Log.d(TAG, " => " + mediaObject.getMedia_url());
                        mediaObjectList.add(mediaObject);
                    }

                    recyclerView.setMediaObjects(mediaObjectList);
                    HorizontalVideoPlayerRecyclerAdapter adapter = new HorizontalVideoPlayerRecyclerAdapter(mediaObjectList, initGlide());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.setKeepScreenOn(true);
                    recyclerView.smoothScrollToPosition(mediaObjectList.size() + 1);

                    Log.d(TAG, "mediaObjectList size " + mediaObjectList.size());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeHorizontal.this, "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }




    @Override
    protected void onDestroy() {
        if (recyclerView != null)
            recyclerView.releasePlayer();
        player.stop();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (recyclerView != null)
            recyclerView.releasePlayer();
        player.stop();
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (recyclerView != null)
        {    recyclerView.releasePlayer(); }

        player.stop();
        Intent i=new Intent(this, Home.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private RequestManager initGlide() {

        RequestOptions options = new RequestOptions()
                .placeholder(R.color.colorPrimaryDark)
                .error(R.color.colorPrimaryDark);
        return Glide.with(this)
                .setDefaultRequestOptions(options);

    }

}