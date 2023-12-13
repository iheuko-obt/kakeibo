package com.websarva.wings.android.sqlitekakeibo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DataEditDialog extends DialogFragment {

    private EditText year;
    private EditText month;
    private EditText day;
    private Spinner Tagspinner;
    private EditText Price;
    private Button button_a;
    private Button button_b;
    private Button button_c;
    private DBEntity entity;

    private kakeiboDao dao;
    private database db;


    ArrayList<statisticsActivity.DataPack> mDataset_E;
    statisticsActivity.DataPack DataPack;

    public onDataEdit onDataEdit;
    private static ExecutorService executor;
    ArrayAdapter<String> adapter;



    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dataedit_dialog, null);

        builder.setView(view);

        Context context = getContext();

        //db = database.getDatabase(getContext());
        db = Room.databaseBuilder(getContext(),
                        database.class,"database")
                .build();
        dao = db.kakeiboDao();

        year = view.findViewById(R.id.year);
        month = view.findViewById(R.id.month);
        day = view.findViewById(R.id.day);
        Tagspinner = view.findViewById(R.id.Tag);
        Price = view.findViewById(R.id.Price);
        button_a = view.findViewById(R.id.button_a);
        button_b = view.findViewById(R.id.button_b);
        button_c = view.findViewById(R.id.button_c);

        int ID = getArguments().getInt("key");
        this.mDataset_E = statisticsActivity.mDataset_E;
        DataPack = mDataset_E.get(ID);
        entity = DataPack.getRoomEntity();
        String[] a = DataPack.getDateToDisplay().split("/");

        year.setText(a[0]);
        month.setText(a[1]);
        day.setText(a[2]);
        Price.setText(DataPack.getEachPrice());

        EditCompleteOnCrick editCompleteOnCrick = new EditCompleteOnCrick();
        button_a.setOnClickListener(editCompleteOnCrick);

        DeleteOnCrick deleteOnCrick = new DeleteOnCrick();
        button_b.setOnClickListener(deleteOnCrick);

        OnCrickCloseDialog onCrickCloseDialog = new OnCrickCloseDialog();
        button_c.setOnClickListener(onCrickCloseDialog);

        tagDB tagDb = Room.databaseBuilder(getContext(),
                        tagDB.class, "database-name")
                .build();
        tagSpinnerDao tagDao = tagDb.tagSpinnerDao();

        adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Tagspinner.setAdapter(adapter);
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // バックグラウンドで実行する処理
                List<tagEntity> tagList = tagDao.setListToTest();
                List<String> tempTagList = new ArrayList<>();

                for (tagEntity entity : tagList){
                    tempTagList.add(entity.getTag());
                }

                adapter.addAll(tempTagList);
                setSelection(Tagspinner, DataPack.getTag());
            }

        });//tagはOnCreate内で、DataPackが保持している物が選択された状態にしたい




        return builder.create();
    }


    public  void setSelection(Spinner spinner, String item) {
        int index = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(item)) {
                index = i; break;
            }
        }
        spinner.setSelection(index);
    }

    public  class EditCompleteOnCrick implements View.OnClickListener{
        //変更確定とダイアログを閉じる
        @Override
        public void onClick(View view){
            boolean key = true;
            try {
                Double.parseDouble(year.getText().toString());
                Double.parseDouble(month.getText().toString());
                Double.parseDouble(day.getText().toString());
                Double.parseDouble(Price.getText().toString());
            }
            catch (NumberFormatException e){
                closeDialog();
                key = false;
            }
            if (year.getText().toString().length() == 4 && key ){
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        String date = year.getText() + "/" + month.getText() + "/" + day.getText();
                        DBEntity newEntity = new DBEntity(date,Tagspinner.getSelectedItem().toString(),Price.getText().toString());
                        newEntity.setId(entity.getId());
                        dao.updateAData(newEntity);

                    }


                });
            }

            closeDialog();
            onDataEdit.sendRequest();

        }
    }


    public  class DeleteOnCrick implements View.OnClickListener{
        //entity削除とダイアログを閉じる
        @Override
        public void onClick(View view){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteAData(entity);


            }


        });

        closeDialog();
        onDataEdit.sendRequest();

    }
    }

    public  class OnCrickCloseDialog implements View.OnClickListener{
        //entity削除とダイアログを閉じる
        @Override
        public void onClick(View view){
            closeDialog();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataEdit = (onDataEdit) getActivity();//呼び出し元を特定
    }


    public interface onDataEdit{
        void sendRequest();//statisticsactivity側でrecyclerの表示を更新
    }

    public void closeDialog(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        });
    }


}
