package com.bsreeinf.jobapp.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.ScrollView;

public class AnimatedScrollView extends ScrollView {
    private Animation inAnimation;
    private Animation outAnimation;

    public AnimatedScrollView(Context context) {
        super(context);
    }

    public AnimatedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setInAnimation(Animation inAnimation) {
        this.inAnimation = inAnimation;
    }

    public void setOutAnimation(Animation outAnimation) {
        this.outAnimation = outAnimation;
    }

    @Override
    public void setVisibility(int visibility) {
        if (getVisibility() != visibility) {
            if (visibility == VISIBLE) {
                if (inAnimation != null)
                    startAnimation(inAnimation);
            } else if ((visibility == INVISIBLE) || (visibility == GONE)) {
                if (outAnimation != null)
                    startAnimation(outAnimation);
            }
        }

        super.setVisibility(visibility);
    }
}
