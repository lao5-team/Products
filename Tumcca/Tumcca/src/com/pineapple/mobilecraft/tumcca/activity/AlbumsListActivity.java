package com.pineapple.mobilecraft.tumcca.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.fragment.AlbumListFragment;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 8/23/15.
 *
 * 用于See All功能
 */
public class AlbumsListActivity extends FragmentActivity {

    public static final int MODE_LIKE = 0;
    public static final int MODE_COLLECT = 1;

    private int mDataMode = MODE_LIKE;
    private AlbumListFragment mAlbumsFragment;
    private int mAuthorId = 0;
    public static void startActivity(Activity activity, int mode, int authorId){
        Intent intent = new Intent(activity, AlbumsListActivity.class);
        intent.putExtra("data_mode", mode);
        intent.putExtra("authorId", authorId);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_list);

        mDataMode = getIntent().getIntExtra("data_mode", MODE_COLLECT);
        mAuthorId = getIntent().getIntExtra("authorId", 0);

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                mAlbumsFragment = new AlbumListFragment();

                mAlbumsFragment.setAlbumLoader(new AlbumListFragment.AlbumListLoader() {
                    @Override
                    public List<Album> getInitialAlbums() {
                        if(mDataMode == MODE_LIKE){
                            return WorksServer.getLikeAlbums(mAuthorId, 1, 4);

                        }
                        else if(mDataMode == MODE_COLLECT){
                            return WorksServer.getCollectAlbums(mAuthorId, 1, 4);
                        }
                        return null;
                    }

                    @Override
                    public void loadHeadAlbums() {

                    }

                    @Override
                    public void loadTailAlbums(int page) {
                        if(mDataMode == MODE_LIKE){
                            mAlbumsFragment.addAlbumsTail(WorksServer.getLikeAlbums(mAuthorId, page, 4));

                        }
                        else if(mDataMode == MODE_COLLECT){
                            mAlbumsFragment.addAlbumsTail(WorksServer.getCollectAlbums(mAuthorId, page, 4));
                        }
                    }
                });
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mAlbumsFragment).commit();

            }
        });

    }
}