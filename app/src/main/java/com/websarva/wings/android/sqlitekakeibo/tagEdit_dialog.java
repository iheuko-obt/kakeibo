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

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class tagEdit_dialog extends DialogFragment {
       //キャンセルはdismiss 確定ボタンはまずtagDBの該当タグをupDate()　databaseの該当タグのついたデータを新しいタグに
       //削除ボタンはtagDBの該当タグをdelete()　databaseの該当タグのついたデータを削除

       public onTagEdit onTagEdit;

       tagDB tagDB;
       tagSpinnerDao tagDao;

       database db;
       kakeiboDao dao;

       int id;//受け取ったID
       EditText editText;
       private static ExecutorService executor;
       String heldTag;


       @NonNull
       @Override
       public Dialog onCreateDialog(@NonNull Bundle savedInstanceState){
              AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
              LayoutInflater inflater = requireActivity().getLayoutInflater();
              View view = inflater.inflate(R.layout.tag_edit_dialog, null);

              builder.setView(view);

              editText = view.findViewById(R.id.editTextText);
              Button buttonA = view.findViewById(R.id.kakutei);
              Button buttonB = view.findViewById(R.id.sakujyo);
              Button buttonC = view.findViewById(R.id.cansel);

              buttonA.setOnClickListener(new EditCompleteOnCrick());
              buttonB.setOnClickListener(new deleteButtonOnCrick());
              buttonC.setOnClickListener(new dismissButtonOnCrick());


              executor = Executors.newSingleThreadExecutor();
              //db = database.getDatabase(getContext());
              db = Room.databaseBuilder(getContext(),
                              database.class,"database")
                      .build();
              dao = db.kakeiboDao();
              tagDB = Room.databaseBuilder(getContext(),
                              tagDB.class, "database-name")
                      .build();
              tagDao = tagDB.tagSpinnerDao();

              String[] a = getArguments().getStringArray("key");
              id = Integer.parseInt(a[0]);
              heldTag = a[1];
              editText.setText(heldTag);



              return builder.create();
       }

       public class EditCompleteOnCrick implements View.OnClickListener{
              @Override
              public void onClick(View v) {
                     String preTag = editText.getText().toString();
                     preTag = preTag.replaceAll("　"," ");
                     String newTag = preTag.trim();
                     executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                   // バックグラウンドで実行する処理
                                   tagEntity entity = tagDao.getTagEntity(id);
                                   entity.setTag(newTag);
                                   tagDao.update(entity)
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
                                                         onTagEdit.onTagEditCall();
                                                         closeDialog();
                                                  }

                                                  @Override
                                                  public void onError(Throwable e) {
                                                         // エラーが発生した場合の処理
                                                         // ここでエラーをハンドリングするか、エラーメッセージを表示できます
                                                  }
                                           });

                                   List<DBEntity> entityList = dao.getApartList(heldTag);
                                   Log.d("why",Integer.toString(entityList.size()));
                                   for (DBEntity AnEntity : entityList){
                                          AnEntity.setTag(newTag);
                                          dao.updateAData(AnEntity);
                                   }



                            }


                     });

              }
       }

       public class deleteButtonOnCrick implements View.OnClickListener{
              @Override
              public void onClick(View v) {
                     executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                   // バックグラウンドで実行する処理
                                   tagEntity entity = tagDao.getTagEntity(id);
                                   tagDao.delete(entity)
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
                                                         onTagEdit.onTagEditCall();

                                                  }

                                                  @Override
                                                  public void onError(Throwable e) {
                                                         // エラーが発生した場合の処理
                                                         // ここでエラーをハンドリングするか、エラーメッセージを表示できます
                                                  }
                                           });

                                   List<DBEntity> entityList = dao.getApartList(heldTag);
                                   Log.d("why",Integer.toString(entityList.size()));
                                   for (DBEntity AnEntity : entityList){
                                          dao.deleteAData(AnEntity);
                                   }
                                   closeDialog();



                            }

                     });

              }
       }

       public class dismissButtonOnCrick implements View.OnClickListener{
              @Override
              public void onClick(View v) {
                     closeDialog();
              }
       }


       @Override
       public void onAttach(Context context) {
              super.onAttach(context);
              onTagEdit = (onTagEdit) getActivity();//呼び出し元を特定
       }



       public interface onTagEdit{
              void onTagEditCall();
       }

       private void closeDialog(){
              getActivity().runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                            dismiss();
                     }
              });
       }

}
