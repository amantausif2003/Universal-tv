package com.remote.control.allsmarttv.activitiesUi;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jaku.core.JakuRequest;
import com.jaku.model.Channel;
import com.jaku.request.LaunchAppRequest;
import com.remote.control.allsmarttv.R;
import com.remote.control.allsmarttv.utils.ApplicationsAdapter;
import com.remote.control.allsmarttv.utils.AppsUtil;
import com.remote.control.allsmarttv.utils.ImageClass;
import com.remote.control.allsmarttv.utils.ImageFetch;
import com.remote.control.allsmarttv.utils.RequestTask;
import com.remote.control.allsmarttv.utils.RokuCmds;
import com.remote.control.allsmarttv.utils.RokuRequest;
import com.remote.control.allsmarttv.utils.RokureqType;
import com.remote.control.allsmarttv.utils.VersionUtil;
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class Roku_Apps extends AppCompatActivity{

    private static final String IMAGE_CACHE = "thumbs";
    private int imageThumbSize;
    private int imageThumbSpacing;
    private ApplicationsAdapter applicationsAdapter;
    private ImageFetch imageFetch;
    Handler handler;
    ImageView back;
    private SwipeRefreshLayout swipeRefreshLayout;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportedClass.loadLangLocale(getBaseContext());
        setContentView(R.layout.activity_apps);

        handler = new Handler();

        imageThumbSize = getResources().getDimensionPixelSize(R.dimen.thumb_size);
        imageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.thumb_spacing);

        ImageClass.ImageCacheParams cacheParams =
                new ImageClass.ImageCacheParams(Roku_Apps.this, IMAGE_CACHE);

        cacheParams.setMemCacheSizePercent(0.25f);

        imageFetch = new ImageFetch(Roku_Apps.this, imageThumbSize);
        imageFetch.setLoadingImage(R.drawable.ic_empty);
        imageFetch.addImageCache(Roku_Apps.this.getSupportFragmentManager(), cacheParams);

        applicationsAdapter = new ApplicationsAdapter(Roku_Apps.this, imageFetch, new ArrayList<Channel>(), handler);

        IntentFilter intentFilter = new IntentFilter();
        Roku_Apps.this.registerReceiver(broadcastReceiver, intentFilter);

        final GridView mGridView = findViewById(R.id.apps_list);
        back = findViewById(R.id.back_apps_roku);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        Intent intent = new Intent(Roku_Apps.this, Roku_Remote.class);
                        startActivity(intent);
                        finish();
            }
        });

        swipeRefreshLayout = findViewById(R.id.refresh_swipe_roku);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        loadChannels();
                    }
                }
        );


        mGridView.setAdapter(applicationsAdapter);

        loadChannels();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Channel channel = (Channel) parent.getItemAtPosition(position);

                performLaunch(channel.getId());
            }
        });
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!VersionUtil.hasHoneycomb()) {
                        imageFetch.setPauseWork(true);
                    }
                } else {
                    imageFetch.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (applicationsAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (imageThumbSize + imageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - imageThumbSpacing;
                                applicationsAdapter.setNumColumns(numColumns);
                                applicationsAdapter.setTheItemHeight(columnWidth);

                                if (VersionUtil.hasJellyBean()) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        imageFetch.setExitTasksEarly(false);
        applicationsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        imageFetch.setPauseWork(false);
        imageFetch.setExitTasksEarly(true);
        imageFetch.flushCache();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        compositeDisposable.dispose();

        imageFetch.closeCache();

        Roku_Apps.this.unregisterReceiver(broadcastReceiver);
    }


    private void loadChannels() {
        compositeDisposable.add(Observable.fromCallable(new AppsUtil(Roku_Apps.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channels -> onLoadFinished((List<Channel>) channels)));
    }

    private void onLoadFinished(List<Channel> channels) {
        swipeRefreshLayout.setRefreshing(false);

        if (channels.size() == 0) {
            //setListShown(true);
            return;
        }

        applicationsAdapter.clear();
        applicationsAdapter.notifyDataSetChanged();

        for (int i = 0; i < channels.size(); i++) {
            applicationsAdapter.add(channels.get(i));
        }

        applicationsAdapter.notifyDataSetChanged();

    }

    public void onLoaderReset(Loader<List<Channel>> channels) {

        applicationsAdapter.clear();
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadChannels();
        }
    };

    private void performLaunch(String appId) {
        String url = RokuCmds.getTv();

        LaunchAppRequest launchAppIdRequest = new LaunchAppRequest(url, appId);
        JakuRequest request = new JakuRequest(launchAppIdRequest, null);

        new RequestTask(request, new RokuRequest() {
            @Override
            public void requestResult(RokureqType rokuRequestType, RequestTask.Result result) {

            }

            @Override
            public void onErrorResponse(RequestTask.Result result) {

            }
        }).execute(RokureqType.launch);
    }

    public void refresh() {
        if (applicationsAdapter.getChannelCount() == 0) {
            loadChannels();
        }
    }

    @Override
    public void onBackPressed() {

                Intent intent = new Intent(Roku_Apps.this, Roku_Remote.class);
                startActivity(intent);
                finish();
    }
}
