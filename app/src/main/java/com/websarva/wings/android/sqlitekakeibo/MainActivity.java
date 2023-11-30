package com.websarva.wings.android.sqlitekakeibo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

//import android.app.DialogFragment;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements tagAddDialog.onTugAddcomp{

    String[] calendar_sep = new String[3];
    public static int year_bi, month_bi, date_bi;

    private ArrayList<ArrayList<String>> mDataset;
    public static LiveData<List<tagEntity>> liveDataList;
    private Spinner tagSpinner;
    tagDB tagDb;
    static tagSpinnerDao tagDao;

    static ArrayAdapter<String> TagAdapter;//タグ用


    public static final String firstInSpinner = "タグを選択";

    private  TextView dateSelected;

    private String  tag_selected;

    private kakeiboDao dao;

    private EditText sumPrice;
    private EditText apartPrice;

    private ExecutorService executor;

    BottomNavigationView bottomNavigationView;

    





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager rLayoutManager;

        tagSpinner = findViewById(R.id.tagSpinner);


        mRecyclerView = (RecyclerView) findViewById(R.id.tempAdd);
        mRecyclerView.setHasFixedSize(true);
        rLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(rLayoutManager);


        mDataset = new ArrayList<>();
        mAdapter = new MyAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);
        //ここまでリサイクラービュー関連

        Button allCul = findViewById(R.id.allCul);
        Button apartCul = findViewById(R.id.apartCul);
        ButtonOnClick reactButton = new ButtonOnClick();
        allCul.setOnClickListener(reactButton);
        apartCul.setOnClickListener(reactButton);

        Button tagButton = findViewById(R.id.tagAddButton);
        ButtonOnClick_tag buttonOnClick_tag = new ButtonOnClick_tag();
        tagButton.setOnClickListener(buttonOnClick_tag);
        //ここまでボタン3つ分



        //起動時点の日付を取得
        String _localDate = LocalDate.now().toString();
        calendar_sep = _localDate.split("-");
        year_bi = Integer.parseInt(calendar_sep[0]);
        month_bi = Integer.parseInt(calendar_sep[1]);
        date_bi = Integer.parseInt(calendar_sep[2]);
        //日付の表示初期化
        dateSelected = findViewById(R.id.DateSelected);
        dateSelected.setText(year_bi +"-"+month_bi+"-"+date_bi);
        dateSelected.setOnClickListener(new dateSelectedOnclick());


        //room
//        tagDb = tagDB.getDatabase(getApplicationContext());
//        tagDao = tagDb.tagSpinnerDao();
        RoomDatabase.Callback callback = new RoomDatabase.Callback() {
            @Override
            public void onOpen (@NonNull SupportSQLiteDatabase db){
                super.onOpen(db);
                Log.d("Database Callback", "Database has been opened");
            }
        };

        tagDb = Room.databaseBuilder(getApplicationContext(),
                        tagDB.class, "database-name")
                .addCallback(callback)
                .build();
        tagDao = tagDb.tagSpinnerDao();

        TagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        TagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(TagAdapter);

        executor = Executors.newSingleThreadExecutor();

        AdapterView.OnItemSelectedListener spinnerItemSelectedListener = new TagItemClickListener();
        tagSpinner.setOnItemSelectedListener(spinnerItemSelectedListener);

        //database db = database.getDatabase(getApplicationContext());
        database db = Room.databaseBuilder(getApplicationContext(),
                database.class,"database")
                .build();
        dao = db.kakeiboDao();

        Button SenniButton = findViewById(R.id.SenniButton);
        ButtonOnClick_senni senniListener = new ButtonOnClick_senni();
        SenniButton.setOnClickListener(senniListener);

        //テスト用
//        ArrayList<String> tempList = new  ArrayList<>(Arrays.asList("2023/11/18", Integer.toString(date_bi),"8979"));
//        mDataset.add(tempList);
//
//        DBEntity entity = new DBEntity("2023/11/18",Integer.toString(date_bi) ,"8979");
//        dao.insert(entity)
//                .subscribeOn(Schedulers.io()) // バックグラウンドスレッドで実行
//                .observeOn(AndroidSchedulers.mainThread()) // メインスレッドで結果を受け取る
//                .subscribe(new CompletableObserver() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        // サブスクリプションが開始された際の処理
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        // 処理が正常に完了した場合の処理
//                        // ここで成功を通知するか、他のアクションを実行できます
//                        //Context context = getApplicationContext();
//                        //Toast.makeText(context,tempDate+ tag_selected+inputPrice_st,Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        // エラーが発生した場合の処理
//                    }
//                });
//
//
//        tagEntity tagtestentity = new tagEntity(Integer.toString(date_bi));
//        tagDao.insert(tagtestentity).subscribeOn(Schedulers.io()) // バックグラウンドスレッドで実行
//                .observeOn(AndroidSchedulers.mainThread()) // メインスレッドで結果を受け取る
//                .subscribe(new CompletableObserver() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        // サブスクリプションが開始された際の処理
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        // 処理が正常に完了した場合の処理
//                        // ここで成功を通知するか、他のアクションを実行できます
//                        Log.d("tagAdd","complete");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        // エラーが発生した場合の処理
//                        // ここでエラーをハンドリングするか、エラーメッセージを表示できます
//                    }
//                });



        bottomNavigationView = findViewById(R.id.bottom_navigation_toM);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.bottombar_main:
                    //何もしない
                    return true;
                case R.id.bottombar_st:
                     intent = new Intent(MainActivity.this, statisticsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.bottombar_tag:
                     intent = new Intent(MainActivity.this, tagEditActivity.class);
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
        });

    }



    @Override
    public void onResume() {
        super.onResume();

        tagSpinnerRenew();

        bottomNavigationView.setSelectedItemId(R.id.bottombar_main);

    }

    private class ButtonOnClick_tag implements View.OnClickListener{
        @Override
        public void onClick(View view){
            DialogFragment dialog = new tagAddDialog();
            dialog.show(getSupportFragmentManager(),"tagDialog");

        }
    }

    private class ButtonOnClick_senni implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent(MainActivity.this, statisticsActivity.class);
            startActivity(intent);

        }
    }

    private class ButtonOnClick implements View.OnClickListener{
        @Override
        public void onClick(View view){
             apartPrice = findViewById(R.id.apartPrice);
             sumPrice = findViewById(R.id.sumPrice);

            int id = view.getId();

            if (tag_selected != null){
                switch (id){
                    case R.id.allCul:
                        if (!sumPrice.getText().toString().equals("") && !sumPrice.getText().toString().equals("0")//toString()をつけないと空欄ですり抜ける
                                && sumPrice.getText() != null){

                            String inputPrice_st = sumPrice.getText().toString();
                            String tempDate = year_bi + "/" + month_bi + "/" + date_bi;
                            ArrayList<String> tempList = new  ArrayList<>(Arrays.asList(tempDate, tag_selected,inputPrice_st));
                            mDataset.add(tempList);

                            DBEntity entity = new DBEntity(tempDate, tag_selected,inputPrice_st);

                            sumPrice.setText("");
                            dao.insert(entity)
                                    .subscribeOn(Schedulers.io()) // バックグラウンドスレッドで実行
                                    .observeOn(AndroidSchedulers.mainThread()) // メインスレッドで結果を受け取る
                                    .subscribe(new CompletableObserver() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            // サブスクリプションが開始された際の処理
                                        }

                                        @Override
                                        public void onComplete() {
                                            // 処理が正常に完了した場合の処理
                                            // ここで成功を通知するか、他のアクションを実行できます
                                            //Context context = getApplicationContext();
                                            //Toast.makeText(context,tempDate+ tag_selected+inputPrice_st,Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            // エラーが発生した場合の処理
                                        }
                                    });

                        }
                        break;


                    case R.id.apartCul:


                        if (!sumPrice.getText().toString().equals("") && !apartPrice.getText().toString().equals("")//toString()をつけないと空欄ですり抜ける
                                && sumPrice.getText() != null && apartPrice.getText() != null){
                            int sum = Integer.parseInt(sumPrice.getText().toString());
                            int apart = Integer.parseInt(apartPrice.getText().toString());
                            if (sum > apart){
                                Integer p = (sum-apart);
                                String nextSum = p.toString();
                                sumPrice.setText(nextSum);
                                apartPrice.setText("");

                                String tempDate = year_bi + "/" + month_bi + "/" + date_bi;
                                String inputPrice_st = Integer.toString(apart);
                                ArrayList<String> tempList = new ArrayList<>(Arrays.asList(tempDate, tag_selected,inputPrice_st));
                                mDataset.add(tempList);

                                DBEntity entity = new DBEntity(tempDate, tag_selected,inputPrice_st);
                                dao.insert(entity)
                                        .subscribeOn(Schedulers.io()) // バックグラウンドスレッドで実行
                                        .observeOn(AndroidSchedulers.mainThread()) // メインスレッドで結果を受け取る
                                        .subscribe(new CompletableObserver() {
                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                // サブスクリプションが開始された際の処理
                                            }

                                            @Override
                                            public void onComplete() {
                                                // 処理が正常に完了した場合の処理
                                                // ここで成功を通知するか、他のアクションを実行できます
                                                Context context = getApplicationContext();
                                                //Toast.makeText(context,tempDate+ tag_selected+inputPrice_st,Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                // エラーが発生した場合の処理
                                                // ここでエラーをハンドリングするか、エラーメッセージを表示できます
                                            }
                                        });
                            }
                        }

                        break;
                }
            }

        }
    }


    private class TagItemClickListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent,View view, int position, long id) {
            tag_selected = tagSpinner.getSelectedItem().toString();

        }

        public void onNothingSelected(AdapterView<?> parent) {
            if (tagSpinner.getSelectedItem() != null){
                tag_selected = tagSpinner.getSelectedItem().toString();
            }
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 選択された日付を処理する
                        year_bi = year;
                        month_bi = month +1;
                        date_bi = dayOfMonth;
                        //日付の表示変更
                        dateSelected.setText(year_bi +"-"+month_bi+"-"+date_bi);
                    }
                },

                year_bi, month_bi -1, date_bi
        );
        datePickerDialog.show();
    }

    class dateSelectedOnclick implements View.OnClickListener{
        @Override
        public void onClick(View view){
            showDatePickerDialog();
        }
    }


    //タグ更新メソッド
    public void tagSpinnerRenew(){

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // バックグラウンドで実行する処理
                Log.d("test","tagRenew");
                List<tagEntity> tagList = tagDao.setListToTest();
                Log.d("tagCount", Integer.toString(tagList.size()));
                List<String> tempTagList = new ArrayList<>();

                for (tagEntity entity : tagList){
                    tempTagList.add(entity.getTag());
                    Log.d("test","tag");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // ここにUIを更新するコードを書く
                        TagAdapter.clear();
                        TagAdapter.addAll(tempTagList);
                        TagAdapter.notifyDataSetChanged();
                        Log.d("test","tagRenewComplete");
                    }
                });


            }

        });
    }

    @Override
    public void sendResult(){
        tagSpinnerRenew();
    }

}