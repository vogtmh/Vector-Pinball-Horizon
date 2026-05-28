package com.dozingcatsoftware.bouncy;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

/**
 * A SwitchPreference that tints the toggle thumb and track with a custom color
 * when checked. Each setting gets a distinct color drawn from the pinball table palettes.
 */
public class ColoredSwitchPreference extends SwitchPreference {
    private int switchColor = 0;

    public ColoredSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSwitchColor(int color) {
        switchColor = color;
        notifyChanged();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        if (switchColor != 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Switch sw = findSwitch(view);
            if (sw != null) {
                ColorStateList thumbStates = new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_checked},
                                new int[]{}
                        },
                        new int[]{
                                switchColor,
                                Color.rgb(158, 158, 158)
                        }
                );
                ColorStateList trackStates = new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_checked},
                                new int[]{}
                        },
                        new int[]{
                                Color.argb(128, Color.red(switchColor),
                                        Color.green(switchColor), Color.blue(switchColor)),
                                Color.rgb(80, 80, 80)
                        }
                );
                sw.setThumbTintList(thumbStates);
                sw.setTrackTintList(trackStates);
            }
        }
    }

    private static Switch findSwitch(View view) {
        if (view instanceof Switch) {
            return (Switch) view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                Switch found = findSwitch(group.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }
}
