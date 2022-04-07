package com.egamez.org.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.egamez.org.R;

public class CustomTextView extends AppCompatTextView {
    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public CustomTextView(Context context) {
        super(context);
        init(null);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FontStyle);
            int fontStyle = a.getInt(R.styleable.FontStyle_fontTextStyle, 0);
            Typeface myTypeface = null;
            switch (fontStyle) {
                case 1:
                    myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/Poppins-Thin.otf");
                    break;
                case 2:
                    myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/Poppins-Light.otf");
                    break;
                case 3:
                    myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/Poppins-Regular.otf");
                    break;
                case 4:
                    myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/Poppins-Medium.otf");
                    break;
                case 5:
                    myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/Poppins-SemiBold.otf");
                    break;
                case 6:
                    myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/Poppins-Bold.otf");
                    break;
                case 7:
                    myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/Poppins-Black.otf");
                    break;
            }
            setTypeface(myTypeface);
            a.recycle();
        }
    }
}
