package ltd.boku.bakingapp.fragment;

import android.annotation.SuppressLint;
import android.app.Application;
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

import com.google.android.exoplayer2.BuildConfig;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.FilteringManifestParser;
import com.google.android.exoplayer2.offline.ProgressiveDownloadAction;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.chunk.ChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.RepresentationKey;
import com.google.android.exoplayer2.source.dash.offline.DashDownloadAction;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.hls.offline.HlsDownloadAction;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import com.google.android.exoplayer2.source.hls.playlist.RenditionKey;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.StreamKey;
import com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloadAction;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.MainActivity;
import ltd.boku.bakingapp.R;
import ltd.boku.bakingapp.databinding.FragmentStepLayoutBinding;
import ltd.boku.bakingapp.model.Step;
import ltd.boku.bakingapp.utils.AppUtility;
import ltd.boku.bakingapp.utils.DownloadTracker;
import ltd.boku.bakingapp.utils.MediaPlayerUtils;
import ltd.boku.bakingapp.viewmodels.ParticularStepViewModel;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static ltd.boku.bakingapp.fragment.RecipeStepFragment.STEPS_EXTRA;
import static ltd.boku.bakingapp.fragment.RecipeStepFragment.STEP_POSITION;

public class ParticularStepFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ParticularStepFragment";

    FragmentStepLayoutBinding fragmentStepLayoutBinding;
    private SimpleExoPlayer player;
    PlayerView playerView;
    public static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    public static final String PLAYBACK_POSITION = "playback-position";
    public static final String CURRENT_WINDOW = "current-window";
    public static final String PLAY_WHEN_READY = "playwhen-ready";
    private DataSource.Factory mediaDataSourceFactory;
    MediaSource mediaSource;
    private DefaultTrackSelector.Parameters trackSelectorParameters;


    public List<Step> steps = new ArrayList<>();
    public Step step = new Step();
    int position = -1;


    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = false;
    MainActivity mContext;
    ParticularStepViewModel particularStepViewModel;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: entering: position= " + position);
        // this.setRetainInstance(true);
        mContext = (MainActivity) context;
        steps = (List<Step>) getArguments().getSerializable(STEPS_EXTRA);
        if (position < 0) {
            position = (int) getArguments().getSerializable(STEP_POSITION);
        }
        step = steps.get(position);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentStepLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_layout, container, false);
        mediaDataSourceFactory = buildDataSourceFactory(true);
        Log.d(TAG, "onCreateView: entering");
        particularStepViewModel = ViewModelProviders.of(this).get(ParticularStepViewModel.class);
        fragmentStepLayoutBinding.imageButtonNext.setOnClickListener(this);
        fragmentStepLayoutBinding.imageButtonPrev.setOnClickListener(this);
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(STEP_POSITION, 0);
            currentWindow = savedInstanceState.getInt(CURRENT_WINDOW, 0);
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION, 0);
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY, false);
            if (player != null) {
                player.seekTo(currentWindow, playbackPosition);
            }
            step = steps.get(position);
            ParticularStepViewModel.stepMutableLiveData.postValue(step);
        }
        ParticularStepViewModel.setStepMutableLiveData(step);
        particularStepViewModel.getStepMutableLiveData().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                Log.d(TAG, "onChanged: entering");
                ParticularStepFragment.this.step = step;
                if (step != null) {
                    fragmentStepLayoutBinding.stepInstructionText.setText(step.getDescription());
                }
                if (step.getVideoURL().isEmpty()) {
                    releasePlayer();
                    fragmentStepLayoutBinding.playerView.setVisibility(View.GONE);
                    fragmentStepLayoutBinding.noVideo.setVisibility(View.VISIBLE);
                } else {
                    fragmentStepLayoutBinding.playerView.setVisibility(View.VISIBLE);
                    fragmentStepLayoutBinding.noVideo.setVisibility(View.GONE);
                    setupVideoPlayer();
                }
            }
        });
        return fragmentStepLayoutBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: entering");
    }


    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return ((MediaPlayerUtils) mContext.getApplication()).buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return buildMediaSource(uri, null);
    }

    @SuppressWarnings("unchecked")
    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .setManifestParser(
                                new FilteringManifestParser<>(
                                        new DashManifestParser(), (List<RepresentationKey>) getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .setManifestParser(
                                new FilteringManifestParser<>(
                                        new SsManifestParser(), (List<StreamKey>) getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory)
                        .setPlaylistParser(
                                new FilteringManifestParser<>(
                                        new HlsPlaylistParser(), (List<RenditionKey>) getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private List<?> getOfflineStreamKeys(Uri uri) {
        return (((MediaPlayerUtils) mContext.getApplication()).getDownloadTracker().getOfflineStreamKeys(uri));
    }

    private void setupVideoPlayer() {
        if (player == null) {
            playerView = fragmentStepLayoutBinding.playerView;
            TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
            trackSelector.setParameters(trackSelectorParameters);

            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                    ((MediaPlayerUtils) mContext.getApplication()).useExtensionRenderers()
                            ? (DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;

            DefaultRenderersFactory rendererFactory =
                    new DefaultRenderersFactory(getContext(), extensionRendererMode);

            player = ExoPlayerFactory.newSimpleInstance(rendererFactory,
                    trackSelector,
                    new DefaultLoadControl());

            mediaSource = buildMediaSource(Uri.parse(step.getVideoURL()));
        }
        playerView.setPlayer(player);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource,false,false);
        boolean isLandscape = (getContext().getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE);
        if (isLandscape && getContext().getResources().getConfiguration().smallestScreenWidthDp < 600) {
            int displayHeight = getContext().getResources().getDisplayMetrics().heightPixels;
            Log.d(TAG, "setupVideoPlayer: display height: " + displayHeight);
            ViewGroup.LayoutParams layoutParams = fragmentStepLayoutBinding.playerView.getLayoutParams();
            layoutParams.height = displayHeight;
            fragmentStepLayoutBinding.playerView.setLayoutParams(layoutParams);
            mContext.getSupportActionBar().hide();
        }

    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.imageButton_next:
                if (position + 1 < steps.size()) {
                    position += 1;
                } else {
                    position = 0;
                }
                RecipeStepFragment.prevPosition = position;
                step = steps.get(position);
                ParticularStepViewModel.stepMutableLiveData.postValue(step);
                mContext.getSupportActionBar().show();
                break;
            case R.id.imageButton_prev:
                if (position - 1 > -1) {
                    position -= 1;
                } else {
                    position = steps.size() - 1;
                }
                RecipeStepFragment.prevPosition = position;
                step = steps.get(position);
                ParticularStepViewModel.stepMutableLiveData.postValue(step);
                mContext.getSupportActionBar().show();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: entering");
        if (player != null) {
            currentWindow = player.getCurrentWindowIndex();
            playbackPosition = Math.max(0,player.getContentPosition());
            playWhenReady = player.getPlayWhenReady();
            outState.putBoolean(PLAY_WHEN_READY, playWhenReady);
            outState.putInt(CURRENT_WINDOW, currentWindow);
            outState.putLong(PLAYBACK_POSITION, playbackPosition);
        }
        outState.putInt(STEP_POSITION, position);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored: entering");
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: emteromg");
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
            mediaSource = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        boolean saved=isStateSaved();
        Log.d(TAG, "onDestroy: "+ saved);
        if (getResources().getBoolean(R.bool.isTablet) && !saved) {
           getFragmentManager().popBackStack();
        }
    }
}

