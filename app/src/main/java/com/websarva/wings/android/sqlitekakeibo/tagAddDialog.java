package com.websarva.wings.android.sqlitekakeibo;

import android.app.AlertDialog;
import android.app.Dialog;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.room.Room;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class tagAddDialog extends DialogFragment {

    public onTugAddcomp onTugAddcomp;

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.tag_dialog, null);

        //tagDB db = tagDB.getDatabase(getContext());
        tagDB db = Room.databaseBuilder(getContext(),
                        tagDB.class, "database-name")
                .build();
        tagSpinnerDao tagDao = db.tagSpinnerDao();

        builder.setView(view)
                .setPositiveButton("ＯＫ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //タグのListに入力を受け取ったものを登録
                        EditText inputTagName = view.findViewById(R.id.tagNameToAdd);
                        String preTag = inputTagName.getText().toString();
                        preTag = preTag.replaceAll("　", "　");
                        String newTag = preTag.trim();
                        //ArrayList<String> tagList  = MainActivity.tagList;

                            //!tagList.contains(newTag) &&
                        if (                               !newTag.equals(MainActivity.firstInSpinner)){
                            //tagList.add(newTag);
                            tagEntity entity = new tagEntity(newTag);
                            tagDao.insert(entity).subscribeOn(Schedulers.io()) // バックグラウンドスレッドで実行
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
                                            Log.d("tagAdd","complete");
                                            onTugAddcomp.sendResult();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            // エラーが発生した場合の処理
                                            // ここでエラーをハンドリングするか、エラーメッセージを表示できます
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("キャンセル", null);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onTugAddcomp = (onTugAddcomp) getActivity();//呼び出し元を特定
    }

    public interface onTugAddcomp{
        void sendResult();
    }
}
