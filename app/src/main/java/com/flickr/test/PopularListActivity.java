package com.flickr.test;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flickr.test.models.Item;
import com.flickr.test.models.PublicData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PopularListActivity extends AppCompatActivity {

    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.empty)
    TextView mEmptyView;

    AppCompatActivity mAppContext = this;
    private boolean isPullRefresh = false;
    private List<Item> publicDataList = new ArrayList<>();
    private PopularListAdapter popularListAdapter;
    Dialog image_dialog;
    ImageView imageView_cancel_prev;
    TouchImageView mImage_view;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(mAppContext, 2);
        mRecyclerView.setLayoutManager(layoutManager);

        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.swipe_color_1),
                getResources().getColor(R.color.swipe_color_2),
                getResources().getColor(R.color.swipe_color_3),
                getResources().getColor(R.color.swipe_color_4)
        );

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isPullRefresh = true;
                updateList(0, 20, true);
            }
        });
        initList();
    }


    /**
     * Initialize UI component and listener.
     * And starts download task of list item.
     */
    private void initList() {

        if (popularListAdapter != null)/*if not null than prev data show*/ {
            mRecyclerView.setAdapter(popularListAdapter);
            popularListAdapter.notifyDataSetChanged();
            if (publicDataList.size() == 0) {
                //page = 1;
                mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
//                mEmptyView.setText(message);
            }
        } else {
            popularListAdapter = new PopularListAdapter(mAppContext, publicDataList);
            mRecyclerView.setAdapter(popularListAdapter);
            updateList(0, 20, true);
        }


        image_dialog = new Dialog(mAppContext, R.style.ThemeDialogImageSlider);
        image_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        image_dialog.setContentView(R.layout.dialog_imageview);

        image_dialog.addContentView(new View(mAppContext), (new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT)));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(image_dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        image_dialog.getWindow().setAttributes(lp);
        image_dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        imageView_cancel_prev = (ImageView) image_dialog.findViewById(R.id.imageView_cancel_prev);
        mImage_view = (TouchImageView) image_dialog.findViewById(R.id.mImage_view);
        progress_bar = (ProgressBar) image_dialog.findViewById(R.id.progress_bar);
        imageView_cancel_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_bar.setVisibility(View.VISIBLE);
                mImage_view.setVisibility(View.GONE);
                image_dialog.dismiss();
            }
        });
    }


    void onClick(String img_url) {
        image_dialog.show();
        Glide.with(mAppContext).load(img_url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                progress_bar.setVisibility(View.INVISIBLE);
                mImage_view.setVisibility(View.VISIBLE);
                mImage_view.setImageBitmap(resource);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (image_dialog.isShowing()) {
            progress_bar.setVisibility(View.VISIBLE);
            mImage_view.setVisibility(View.GONE);
            image_dialog.dismiss();
        }
        super.onBackPressed();
    }

    void updateList(final int page, final int item_per_page, final boolean refresh_list) {
        if (isNetworkAvailable(PopularListActivity.this)) {
            if (page == 0 && !isPullRefresh) {
                mProgress.setVisibility(View.VISIBLE);
            }

            FlickrApplication.getRestClient().getRestService().getPublicData("json", 1, page).enqueue(new Callback<PublicData>() {
                @Override
                public void onResponse(Call<PublicData> call, Response<PublicData> response) {
                    try {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (isPullRefresh) {
                            isPullRefresh = false;

                            publicDataList.clear();
                            if (popularListAdapter != null) {
                                popularListAdapter.notifyDataSetChanged();
                            }
                        }
                        if (response != null && response.body() != null) {
                            if (response.body().getItems().size() > 0) {
                                publicDataList.addAll(response.body().getItems());
                                popularListAdapter.notifyDataSetChanged();
                            }

                            if (publicDataList.size() == 0)
                                mEmptyView.setVisibility(View.VISIBLE);

                        }
                    } catch (Exception ex) {
                        String a = ex.getMessage();
                    }
                    mProgress.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<PublicData> call, Throwable t) {
                    if (page == 0 && publicDataList.size() == 0)
                        mEmptyView.setVisibility(View.VISIBLE);
                    String message = getResources().getString(R.string.network_problem_message);
                    Toast.makeText(mAppContext, message, Toast.LENGTH_SHORT).show();
                    mProgress.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    public boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else return activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            } else {
                return false;
            }
        }
        return false;
    }

}
