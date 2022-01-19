package com.example.slidepuzzle;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainView extends LinearLayout{
    private final TextView textView;

    public MainView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.BLACK);

        textView = new TextView(getContext());

        addView(textView);
    }

    public TextView getTextView() {
        return textView;
    }
}
