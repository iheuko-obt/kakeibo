package com.websarva.wings.android.sqlitekakeibo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Single;

public class statisticsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener , DataEditDialog.onDataEdit{

    private kakeiboDao dao;
    private database db;

    private  RecyclerView recyclerList;

    public static ArrayList<DataPack> mDataset_E;
    private List<DBEntity> entities;
    ArrayList<String> tempListTOAdd;

    public static TextView monthDisplay;
    private TextView sumPrice;
    private int sumP;
    private String sumPS;

    private Spinner tagSpinner;
    ArrayAdapter<String> adapter;
    private String tag_selected;

    private tagDB tagDB;
    private tagSpinnerDao tagDao;

    public static int year;
    public static int month;

    private Button button_R;
    private Button button_L;

    Context context;

    private ExecutorService executor;
    private RecyclerView.Adapter<MyAdapter_E.ItemViewHolder> Radapter;

    BottomNavigationView bottomNavigationView;




    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_st);
        context = getApplicationContext();


        String _localDate = LocalDate.now().toString();
        String[] calendar_sep = _localDate.split("-");
        year = Integer.parseInt(calendar_sep[0]);
        month = Integer.parseInt(calendar_sep[1]);

        //db = database.getDatabase(getApplicationContext());
        db = Room.databaseBuilder(getApplicationContext(),
                        database.class,"database")
                .build();
        dao = db.kakeiboDao();
        sumP = 0;


        RecyclerView.LayoutManager RLayoutManager;

        recyclerList = findViewById(R.id.recyclerList);
        recyclerList.setHasFixedSize(true);
        RLayoutManager = new LinearLayoutManager(this);
        recyclerList.setLayoutManager(RLayoutManager);
        mDataset_E = new ArrayList<>();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Radapter = new MyAdapter_E(mDataset_E,fragmentManager);
        recyclerList.setAdapter(Radapter);
        //ここまでリサイクラービュー関連

        monthDisplay = findViewById(R.id.monthDisplay);
        monthDisplay.setText(year +"/" + month);
        MonthDisplayOnClick monthDisplayOnClick = new MonthDisplayOnClick();
        monthDisplay.setOnClickListener(monthDisplayOnClick);



        sumPrice = findViewById(R.id.sum_price);
        executor =Executors.newSingleThreadExecutor();

        monthDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String[] year_month = monthDisplay.getText().toString().split("/");
                int year = Integer.parseInt(year_month[0]);
                int month = Integer.parseInt(year_month[1]);
                sumP = 0;


                mDataset_E.clear();

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (DBEntity entity : entities) {
                            if (year == entity.getYearToSort() && month == entity.getMonthToSort()){
                                mDataset_E.add(new DataPack(entity.getDateToDisplay(), entity.getTag(), entity.getEachPrice(),entity));
                                sumP += entity.getPriceToCul();
                                //Log.d("testSum",Integer.toString(sumP));
                            }
                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // ここにUIを更新するコードを書く
                                Radapter.notifyDataSetChanged();
                                //Log.d("testSumEnd",Integer.toString(sumP));
                                sumPS = Integer.toString(sumP);
                                sumPrice.setText(sumPS);

                                //Toast.makeText(context, "11111111", Toast.LENGTH_SHORT).show();

                            }
                        });


                    }




                });



            }
        });


        //tag関係準備
        tagSpinner = findViewById(R.id.TagSpinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(adapter);
        AdapterView.OnItemSelectedListener spinnerItemSelectedListener = new TagItemClickListener();
        tagSpinner.setOnItemSelectedListener(spinnerItemSelectedListener);
        //tagDB = tagDB.getDatabase(getApplicationContext());
        tagDB = Room.databaseBuilder(getApplicationContext(),
                        tagDB.class, "database-name")
                .build();
        tagDao = tagDB.tagSpinnerDao();


        instantMonthButtonListener instantMonthButtonListener = new instantMonthButtonListener();
         button_R = findViewById(R.id.button_R);
         button_L = findViewById(R.id.button_L);
         button_R.setOnClickListener(instantMonthButtonListener);
         button_L.setOnClickListener(instantMonthButtonListener);

         Button senniButton = findViewById(R.id.senniButton);
         senniButton.setOnClickListener(new ButtonOnClick_senni());

         bottomNavigationView = findViewById(R.id.bottom_navigation_toS);
         bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.bottombar_main:
                    intent = new Intent(statisticsActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.bottombar_st:
                    //何もしない
                    return true;
                case R.id.bottombar_tag:
                    intent = new Intent(statisticsActivity.this, tagEditActivity.class);
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
         });




    }

    @Override
    public void onResume(){
        super.onResume();

        bottomNavigationView.setSelectedItemId(R.id.bottombar_st);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // バックグラウンドで実行する処理
                entities = dao.getList();
                mDataset_E.clear();
                String[] year_month = monthDisplay.getText().toString().split("/");
                int year = Integer.parseInt(year_month[0]);
                int month = Integer.parseInt(year_month[1]);
                sumP = 0;

                for (DBEntity entity : entities) {
                    if (year == entity.getYearToSort() && month == entity.getMonthToSort()){
                        mDataset_E.add(new DataPack(entity.getDateToDisplay(), entity.getTag(), entity.getEachPrice(),entity));
                        sumP += entity.getPriceToCul();
                    }
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // ここにUIを更新するコードを書く
                        Radapter.notifyDataSetChanged();
                        sumPrice.setText(Integer.toString(sumP));
                    }
                });

            }

        });


        executor.execute(new Runnable() {
            @Override
            public void run() {
                // バックグラウンドで実行する処理
                //Log.d("test","tagRenew");
                List<tagEntity> tagList = tagDao.setListToTest();
                //Log.d("tagCount", Integer.toString(tagList.size()));
                List<String> tempTagList = new ArrayList<>();
                tempTagList.add("全てのタグ");

                for (tagEntity entity : tagList){
                    tempTagList.add(entity.getTag());
                    //Log.d("test","tag");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // ここにUIを更新するコードを書く
                        adapter.clear();
                        adapter.addAll(tempTagList);
                        adapter.notifyDataSetChanged();
                        //Log.d("test","tagrenewComplete");
                    }
                });


            }

        });


    }




    private class MonthDisplayOnClick implements View.OnClickListener{
        @Override
        public void onClick(View view){
            DialogFragment dialog = new MonthYearPickerDialog();
            dialog.show(getSupportFragmentManager(),"Dialog");
        }


    }

    private static class instantMonthButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            String[] recentBind = monthDisplay.getText().toString().split("/");
            int year = Integer.parseInt(recentBind[0]);
            int month = Integer.parseInt(recentBind[1]);
            int id = view.getId();
            switch (id){
                case R.id.button_R   -> {
                    if (month == 12){
                        year = year +1;
                        monthDisplay.setText(year + "/" + 1);
                    }
                    else {
                        month = month+1;
                        monthDisplay.setText(year +"/"+ month);
                    }
                }

                case R.id.button_L -> {
                    if (month == 1){
                        year = year -1;
                        monthDisplay.setText(year +"/"+ 12);
                    }
                    else {
                        month = month -1;
                        monthDisplay.setText(year+"/"+ month);
                    }
                }
            }
        }
    }

    private class TagItemClickListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent,View view, int position, long id) {
            tag_selected = tagSpinner.getSelectedItem().toString();

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (!tag_selected.equals("全てのタグ")){
                        mDataset_E.clear();
                        entities = dao.getApartList(tag_selected);
                        String[] year_month = monthDisplay.getText().toString().split("/");
                        int year = Integer.parseInt(year_month[0]);
                        int month = Integer.parseInt(year_month[1]);
                        sumP = 0;

                        for (DBEntity entity : entities) {
                            if (year == entity.getYearToSort() && month == entity.getMonthToSort()){
                                mDataset_E.add(new DataPack(entity.getDateToDisplay(), entity.getTag(), entity.getEachPrice(),entity));
                                sumP += entity.getPriceToCul();
                            }
                        }
                    }
                    else {
                        mDataset_E.clear();
                        entities = dao.getList();
                        String[] year_month = monthDisplay.getText().toString().split("/");
                        int year = Integer.parseInt(year_month[0]);
                        int month = Integer.parseInt(year_month[1]);
                        sumP = 0;

                        for (DBEntity entity : entities) {
                            if (year == entity.getYearToSort() && month == entity.getMonthToSort()){
                                mDataset_E.add(new DataPack(entity.getDateToDisplay(), entity.getTag(), entity.getEachPrice(),entity));
                                sumP += entity.getPriceToCul();
                            }
                        }

                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // ここにUIを更新するコードを書く
                            Radapter.notifyDataSetChanged();
                            sumPS = Integer.toString(sumP);
                            sumPrice.setText(sumPS);
                        }
                    });
                }


            });
        }

        public void onNothingSelected(AdapterView<?> parent) {
            tag_selected = tagSpinner.getSelectedItem().toString();
        }

    }

    public static class DataPack{
        private String DateToDisplay;
        private String Tag;
        private String EachPrice;
        private DBEntity roomEntity;

        public DataPack(String DateToDisplay,String Tag,String EachPrice,DBEntity roomEntity){
            this.DateToDisplay = DateToDisplay;
            this.Tag = Tag;
            this.EachPrice = EachPrice;
            this.roomEntity = roomEntity;
        }

        public String getDateToDisplay() {
            return DateToDisplay;
        }

        public String getTag() {
            return Tag;
        }

        public String getEachPrice() {
            return EachPrice;
        }

        public DBEntity getRoomEntity() {
            return roomEntity;
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

    }

    @Override
    public void sendRequest(){
        //recyclerの表示更新(entity取得)
        tag_selected = tagSpinner.getSelectedItem().toString();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (!tag_selected.equals("全てのタグ")){
                    mDataset_E.clear();
                    entities = dao.getApartList(tag_selected);
                    String[] year_month = monthDisplay.getText().toString().split("/");
                    int year = Integer.parseInt(year_month[0]);
                    int month = Integer.parseInt(year_month[1]);
                    sumP = 0;

                    for (DBEntity entity : entities) {
                        if (year == entity.getYearToSort() && month == entity.getMonthToSort()){
                            mDataset_E.add(new DataPack(entity.getDateToDisplay(), entity.getTag(), entity.getEachPrice(),entity));
                            sumP += entity.getPriceToCul();
                        }
                    }


                }
                else {
                    mDataset_E.clear();
                    entities = dao.getList();
                    String[] year_month = monthDisplay.getText().toString().split("/");
                    int year = Integer.parseInt(year_month[0]);
                    int month = Integer.parseInt(year_month[1]);
                    sumP = 0;

                    for (DBEntity entity : entities) {
                        if (year == entity.getYearToSort() && month == entity.getMonthToSort()){
//                                tempListTOAdd = new ArrayList<>(Arrays.asList(entity.getDateToDisplay(), entity.getTag(), entity.getEachPrice()));
//                                mDataset.add(tempListTOAdd);
                            mDataset_E.add(new DataPack(entity.getDateToDisplay(), entity.getTag(), entity.getEachPrice(),entity));
                            sumP += entity.getPriceToCul();
                        }
                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // ここにUIを更新するコードを書く
                        Radapter.notifyDataSetChanged();
                        sumPS = Integer.toString(sumP);
                        sumPrice.setText(sumPS);
                    }
                });
            }


        });

    }

    //仮設
    private class ButtonOnClick_senni implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent(statisticsActivity.this, tagEditActivity.class);
            startActivity(intent);

        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        executor.shutdown();
    }

}
