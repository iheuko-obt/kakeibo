package com.websarva.wings.android.sqlitekakeibo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tagEditActivity extends AppCompatActivity implements tagEdit_dialog.onTagEdit {
    RecyclerView TrecyclerView;

    private ExecutorService executor;
    private tagDB tagDB;
    private tagSpinnerDao tagDao;

    private ArrayList<ArrayList<String>> tag_id;
    ArrayList<String> tempTagList;
    private RecyclerView.Adapter<tagEdit_adapter.ItemViewHolder> adapter;

    BottomNavigationView bottomNavigationView;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_edit_activity);

        tagDB = Room.databaseBuilder(getApplicationContext(),
                        tagDB.class, "database-name")
                .build();
        tagDao = tagDB.tagSpinnerDao();

        TrecyclerView = findViewById(R.id.TrecyclerView);

        executor = Executors.newSingleThreadExecutor();

        TrecyclerView.setHasFixedSize(true);
        //LinearLayoutManager LayoutManager = new LinearLayoutManager(this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        TrecyclerView.setLayoutManager(staggeredGridLayoutManager);
        tag_id = new ArrayList<>();
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new tagEdit_adapter(tag_id,fragmentManager);
        TrecyclerView.setAdapter(adapter);


        bottomNavigationView = findViewById(R.id.bottom_navigation_toE);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.bottombar_main:
                    intent = new Intent(tagEditActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.bottombar_st:
                    intent = new Intent(tagEditActivity.this, statisticsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.bottombar_tag:
                    return true;
                default:
                    return false;
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        tag_id.clear();
        bottomNavigationView.setSelectedItemId(R.id.bottombar_tag);

        executor.execute(new Runnable() {//更新処理　クラス化できる
            @Override
            public void run() {
                List<tagEntity> tagList = tagDao.setListToTest();
                for (tagEntity entity : tagList){
                    //Log.d("b",entity.getTag());
                    tempTagList = new ArrayList<>(Arrays.asList(Integer.toString(entity.getId()), entity.getTag()));
                    tag_id.add(tempTagList);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        //Log.d("a",Integer.toString(tag_id.size()));
                    }
                });
            }
        });

    }

    @Override
    public void onTagEditCall() {
        //recyclerの更新処理
        tag_id.clear();

        executor.execute(new Runnable() {//更新処理　クラス化できる
            @Override
            public void run() {
                List<tagEntity> tagList = tagDao.setListToTest();
                for (tagEntity entity : tagList){
                    tempTagList = new ArrayList<>(Arrays.asList(Integer.toString(entity.getId()), entity.getTag()));
                    tag_id.add(tempTagList);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
