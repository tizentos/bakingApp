package ltd.boku.bakingapp.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.MainActivity;
import ltd.boku.bakingapp.R;
import ltd.boku.bakingapp.databinding.FragmentStepLayoutBinding;
import ltd.boku.bakingapp.model.Step;
import ltd.boku.bakingapp.utils.AppUtility;
import ltd.boku.bakingapp.viewmodels.ParticularStepViewModel;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static ltd.boku.bakingapp.fragment.RecipeStepFragment.STEPS_EXTRA;
import static ltd.boku.bakingapp.fragment.RecipeStepFragment.STEP_POSITION;

public class ParticularStepFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "ParticularStepFragment";

    FragmentStepLayoutBinding fragmentStepLayoutBinding;
    private SimpleExoPlayer player;
    PlayerView playerView;
    public List<Step> steps= new ArrayList<>();
    public Step step=new Step();
    int position=-1;


    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = false;
    MainActivity mContext;
    ParticularStepViewModel particularStepViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: entering: position= "+ position);
       // this.setRetainInstance(true);
        mContext=(MainActivity)context;
        steps=(List<Step>) getArguments().getSerializable(STEPS_EXTRA);
        if(position < 0){
            position=(int)getArguments().getSerializable(STEP_POSITION);
        }
        step=steps.get(position);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentStepLayoutBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_step_layout,container,false);
        Log.d(TAG, "onCreateView: entering");
        return fragmentStepLayoutBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: entering");

        particularStepViewModel= ViewModelProviders.of(this).get(ParticularStepViewModel.class);
        ParticularStepViewModel.setStepMutableLiveData(step);
        particularStepViewModel.getStepMutableLiveData().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                Log.d(TAG, "onChanged: entering");
                ParticularStepFragment.this.step=step;
                if (step != null) {
                    fragmentStepLayoutBinding.stepInstructionText.setText(step.getDescription());
                }
                if (step.getVideoURL().isEmpty()){
                    releasePlayer();
                    fragmentStepLayoutBinding.playerView.setVisibility(View.GONE);
                    fragmentStepLayoutBinding.noVideo.setVisibility(View.VISIBLE);
                }else{
//                    releasePlayer();
                    fragmentStepLayoutBinding.playerView.setVisibility(View.VISIBLE);
                    fragmentStepLayoutBinding.noVideo.setVisibility(View.GONE);
                    setupVideoPlayer();
                }
            }
        });
        fragmentStepLayoutBinding.imageButtonNext.setOnClickListener(this);
        fragmentStepLayoutBinding.imageButtonPrev.setOnClickListener(this);
    }

    private void setupVideoPlayer(){
        if (player==null) {
            playerView = fragmentStepLayoutBinding.playerView;

            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());

            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow,playbackPosition);
        }
        player.seekTo(0);
        DefaultHttpDataSourceFactory dataSourceFactory =
                new DefaultHttpDataSourceFactory(
                        Util.getUserAgent(
                                getContext(), getString(R.string.app_name)));
        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(step.getVideoURL()));
        player.prepare(mediaSource);

        boolean isLandscape= (getContext().getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE);
        if(isLandscape && getContext().getResources().getConfiguration().smallestScreenWidthDp < 600) {
            int displayHeight = getContext().getResources().getDisplayMetrics().heightPixels;
            Log.d(TAG, "setupVideoPlayer: display height: " + displayHeight);
            ViewGroup.LayoutParams layoutParams = fragmentStepLayoutBinding.frameVideo.getLayoutParams();
            layoutParams.height = displayHeight;
            fragmentStepLayoutBinding.frameVideo.setLayoutParams(layoutParams);
            mContext.getSupportActionBar().hide();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: emteromg");
        releasePlayer();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
////        hideSystemUi();
//        if ((Util.SDK_INT <= 23 || player == null)) {
//            setupVideoPlayer();
//        }
//    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        switch(viewId){
            case R.id.imageButton_next:
                if (position+1 < steps.size()){
                    position+=1;
                }else{
                    position=0;
                }
                step=steps.get(position);
                ParticularStepViewModel.stepMutableLiveData.postValue(step);
                mContext.getSupportActionBar().show();
                break;
            case R.id.imageButton_prev:
                if (position-1 > -1 ){
                    position-=1;
                }else{
                    position=steps.size()-1;
                }
                step=steps.get(position);
                ParticularStepViewModel.stepMutableLiveData.postValue(step);
                mContext.getSupportActionBar().show();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: entering");
        outState.putInt(STEP_POSITION,position);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored: entering");
        if (savedInstanceState != null){
            position=savedInstanceState.getInt(STEP_POSITION,0);
            step=steps.get(position);
            ParticularStepViewModel.stepMutableLiveData.postValue(step);
        }
    }
}
