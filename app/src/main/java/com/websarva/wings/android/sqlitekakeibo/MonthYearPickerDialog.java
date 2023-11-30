package com.websarva.wings.android.sqlitekakeibo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MonthYearPickerDialog extends DialogFragment {


    private GestureDetector gestureDetector_y;
    private GestureDetector gestureDetector_m;
    private ViewFlipper viewFlipper_year;
    private ViewFlipper viewFlipper_month;


    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.month_year_picker_dialog, null);

         viewFlipper_year = view.findViewById(R.id.ViewFlipper_Year);
        //viewFlipper_year.setFlipInterval(200);
        viewFlipper_month = view.findViewById(R.id.ViewFlipper_Month);
        //viewFlipper_year.startFlipping();


        String[] year_month_fromAC = statisticsActivity.monthDisplay.getText().toString().split("/");
        int year_fromAC = Integer.parseInt(year_month_fromAC[0]);
        int yearLimit = year_fromAC + 4;
        int month_fromAC = Integer.parseInt(year_month_fromAC[1]);

        for (int x = 0; 10 > x; x++){
            if (year_fromAC > yearLimit){
                year_fromAC -= 10;
            }
            TextView a = new TextView(getContext());
            String xx = Integer.toString(year_fromAC);
            a.setText(" " + xx +" ");
            a.setTextSize(40);
            viewFlipper_year.addView(a);
            year_fromAC++;
        }

        for (int x = 1; 13 > x; x++){
            if (month_fromAC > 12){
                month_fromAC = 1;
            }
            TextView a = new TextView(getContext());
            String xx = Integer.toString(month_fromAC);
            a.setText(" " + xx +" ");
            a.setTextSize(40);
            viewFlipper_month.addView(a);
            month_fromAC++;
        }

        gestureDetector_y = new GestureDetector(getContext(), new SwipeGestureDetector_Y());
        viewFlipper_year.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gestureDetector_y.onTouchEvent(event);
                return true;
            }
        });

        gestureDetector_m = new GestureDetector(getContext(), new SwipeGestureDetector_M());
        viewFlipper_month.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gestureDetector_m.onTouchEvent(event);
                return true;
            }
        });


        builder.setView(view)
                .setPositiveButton("ＯＫ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView txv_Y = (TextView) viewFlipper_year.getCurrentView();
                        String year = txv_Y.getText().toString().replace(" ","");

                        TextView txv_m = (TextView) viewFlipper_month.getCurrentView();
                        String month = txv_m.getText().toString().replace(" ","");

                        statisticsActivity.monthDisplay.setText(year +"/" + month);
                    }

                })
                .setNegativeButton("キャンセル", null);


        return builder.create();
    }


    class SwipeGestureDetector_Y extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                viewFlipper_year.setInAnimation(getContext(),R.anim.anim_slide_in_right);
                viewFlipper_year.setOutAnimation(getContext(), R.anim.anim_slide_out_left);
                viewFlipper_year.showNext();
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                viewFlipper_year.setInAnimation(getContext(), R.anim.anim_slide_in_left);
                viewFlipper_year.setOutAnimation(getContext(), R.anim.anim_slide_out_right);
                viewFlipper_year.showPrevious();
                return true;
            }
            return false;
        }
    }

    class SwipeGestureDetector_M extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                viewFlipper_month.setInAnimation(getContext(),R.anim.anim_slide_in_right);
                viewFlipper_month.setOutAnimation(getContext(), R.anim.anim_slide_out_left);
                viewFlipper_month.showNext();
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                viewFlipper_month.setInAnimation(getContext(), R.anim.anim_slide_in_left);
                viewFlipper_month.setOutAnimation(getContext(), R.anim.anim_slide_out_right);
                viewFlipper_month.showPrevious();
                return true;
            }
            return false;
        }
    }

}

