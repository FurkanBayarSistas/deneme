package com.audiocodes.mv.webrtcclient.Structure;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.audiocodes.mv.webrtcclient.R;

public class ImageViewWithText  extends RelativeLayout {
    private static final String TAG = "ImageViewWithText";

    View rootView;
    TextView valueTextView;
    ImageView valueImageView;

    public ImageViewWithText(Context context) {
        super(context);
        init(context, null);
    }

    public ImageViewWithText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rootView = inflate(context, R.layout.image_view_with_text, this);
        valueTextView = (TextView) rootView.findViewById(R.id.imageviewwithtext_text);
        valueImageView = (ImageView) rootView.findViewById(R.id.imageviewwithtext_imageview);
        if(attrs!=null)
        {
//            int[] set = {
//                    android.R.attr.background, // idx 0
//                    android.R.attr.src, // idx 1
//                    android.R.attr.text,        // idx 2
//                    //android.R.attr.textColor,       // idx 3
//                    //android.R.attr.text,
//            };
//            TypedArray typedArray = context.obtainStyledAttributes(attrs, set);

            TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ImageViewWithTextStyle,
                    0, 0
            );

//            mShowText = a.getBoolean(R.styleable.PieChart_showText, false);
//            mTextY = a.getDimension(R.styleable.PieChart_labelY, 0.0f);
//            mTextWidth = a.getDimension(R.styleable.PieChart_labelWidth, 0.0f);
//            mTextHeight = a.getDimension(R.styleable.PieChart_labelHeight, 0.0f);
//            mTextPos = a.getInteger(R.styleable.PieChart_labelPosition, 0);
//            mTextColor = a.getColor(R.styleable.PieChart_labelColor, 0xff000000);
//            mHighlightStrength = a.getFloat(R.styleable.PieChart_highlightStrength, 1.0f);
//            mPieRotation = a.getInt(R.styleable.PieChart_pieRotation, 0);
//            mPointerRadius = a.getDimension(R.styleable.PieChart_pointerRadius, 2.0f);
//            mAutoCenterInSlice = a.getBoolean(R.styleable.PieChart_autoCenterPointerInSlice, false);

            Drawable attBackDrawable = typedArray.getDrawable(R.styleable.ImageViewWithTextStyle_BackgroundImage);
            Drawable attSrcDrawable = typedArray.getDrawable(R.styleable.ImageViewWithTextStyle_srcImage);
            CharSequence attText = typedArray.getText(R.styleable.ImageViewWithTextStyle_Text);
            int textColor = typedArray.getColor(R.styleable.ImageViewWithTextStyle_TextColor, -2);
            float attTextSize = typedArray.getDimensionPixelSize(R.styleable.ImageViewWithTextStyle_TextSize,-1);
            //int textColor = typedArray.getColor(3, Color.CYAN);
            //float attTextSize = typedArray.getFloat(3,-1);
           // int attTextSize = typedArray.getDimensionPixelSize(3,-1);

            //Log.d(TAG, "attrs " + drawable + " " + t);


            if(attBackDrawable!=null) {
                valueImageView.setBackground(attBackDrawable);
            }
            if(attSrcDrawable!=null) {
                valueImageView.setImageDrawable(attSrcDrawable);
            }
            if(attText!=null) {
                valueTextView.setText(attText);
            }
            if(textColor!=-2) {
                valueTextView.setTextColor(textColor);
            }
            if(attTextSize>0) {
                valueTextView.setTextSize(attTextSize);
            }
            typedArray.recycle();

            //valueTextView.setText(attrs.getAttributeValue(android.R.attr.text));
        }
    }

    public TextView getValueTextView() {
        return valueTextView;
    }

    public ImageView getValueImageView() {
        return valueImageView;
    }
//        minusButton = rootView.findViewById(R.id.minusButton);
//        plusButton = rootView.findViewById(R.id.plusButton);
//
//        minusButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                decrementValue(); //we'll define this method later
//            }
//        });
//
//        plusButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                incrementValue(); //we'll define this method later        }
//            });
//        }

}