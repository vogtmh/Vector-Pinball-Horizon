package com.dozingcatsoftware.bouncy;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowInsets;

public class BouncyPreferences extends PreferenceActivity {

    private static boolean supportsMultitouch() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    private static boolean supportsHapticFeedback(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator == null || !vibrator.hasVibrator()) {
                return false;
            }
        }
        return true;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setNavigationBarColor(Color.BLACK);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            this.getListView().setOnApplyWindowInsetsListener(this::applyWindowInsets);
        }

        // If multitouch or haptic feedback APIs aren't available, disable the preference items.
        if (!supportsMultitouch()) {
            ColoredSwitchPreference mtPref = (ColoredSwitchPreference) findPreference("independentFlippers");
            mtPref.setChecked(false);
            mtPref.setEnabled(false);
        }
        if (!supportsHapticFeedback(this)) {
            ColoredSwitchPreference hapticPref = (ColoredSwitchPreference) findPreference("haptic");
            hapticPref.setChecked(false);
            hapticPref.setEnabled(false);
        }

        // Assign each toggle a color from the pinball table palettes.
        assignSwitchColor("sound",               Color.rgb(227, 154,  59)); // Gold (T7/T8)
        assignSwitchColor("music",               Color.rgb(  0, 170, 102)); // Teal (T6/T7)
        assignSwitchColor("haptic",              Color.rgb(255, 153,   0)); // Orange (T6)
        assignSwitchColor("independentFlippers", Color.rgb( 57, 147, 221)); // Blue (T8)
        assignSwitchColor("zoom",                Color.rgb(170,  34, 204)); // Purple (T6)
        assignSwitchColor("showBallTrails",      Color.rgb(  0, 224, 224)); // Cyan (T1-T3)
        assignSwitchColor("showScoreAnimations", Color.rgb(204,   0,   0)); // Red (T5)
        assignSwitchColor("useOpenGL",           Color.rgb(  0, 204,   0)); // Green (T5)
        assignSwitchColor("showFPS",             Color.rgb(192,  50,  33)); // Crimson (T8)

        // For the speed preference we want to show the user-visible value as the summary.
        // Setting the summary to "%s" is supposed to do that, but it doesn't work on older
        // Android versions (at least not 2.3), so we set it manually.
        ListPreference speedPref = (ListPreference) findPreference("gameSpeed");
        speedPref.setSummary(entryForListValue(speedPref, speedPref.getValue()));
        speedPref.setOnPreferenceChangeListener((pref, newValue) -> {
            speedPref.setSummary(entryForListValue(speedPref, String.valueOf(newValue)));
            return true;
        });
    }

    private void assignSwitchColor(String key, int color) {
        ColoredSwitchPreference pref = (ColoredSwitchPreference) findPreference(key);
        if (pref != null) pref.setSwitchColor(color);
    }

    private static CharSequence entryForListValue(ListPreference pref, String value) {
        CharSequence[] entryValues = pref.getEntryValues();
        for (int i = 0; i < entryValues.length; i++) {
            if (value.contentEquals(entryValues[i])) {
                return pref.getEntries()[i];
            }
        }
        return "";
    }

    @TargetApi(Build.VERSION_CODES.R)
    WindowInsets applyWindowInsets(View v, WindowInsets windowInsets) {
        Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
        // android.util.Log.i("Preferences", "insets: " + insets);
        v.setPadding(insets.left, insets.top, insets.right, insets.bottom);

        return WindowInsets.CONSUMED;
    }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Return to main activity when "P" is pressed, to avoid accidentally quitting by hitting
        // "back" multiple times. See https://github.com/dozingcat/Vector-Pinball/issues/103.
        if (keyCode == KeyEvent.KEYCODE_P) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
