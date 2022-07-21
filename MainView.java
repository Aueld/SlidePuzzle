package com.customslidepuzzle;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

public class MainView extends LinearLayout{
    private final TextView textView;
    //private com.google.android.gms.common.SignInButton button;

    public MainView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.BLACK);

        textView = new TextView(getContext());
        //button = new com.google.android.gms.common.SignInButton(getContext());

        addView(textView);

        //addView(button);
    }

    public TextView getTextView() {
        return textView;
    }
    //public com.google.android.gms.common.SignInButton getButton () { return button; }
}
