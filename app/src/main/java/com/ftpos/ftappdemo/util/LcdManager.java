package com.ftpos.ftappdemo.util;


import static com.ftsafe.library.lcdservice.Lcd.DISPLAY_STYLE_CENTER;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ftsafe.library.lcdservice.Lcd;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LcdManager {
    private static final LcdManager INSTANCE = new LcdManager();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Lcd lcd;
    private static final int SCREEN_CENTER_X = 0;
    private static final int TITLE_POS_Y = 5;
    private static final int CONTENT_START_Y = 100;
    private static final int LINE_SPACING = 40;
    private static final int FONT_SIZE_TITLE = 24;
    private static final int FONT_SIZE_CONTENT = 32;

    private LcdManager() {
    }

    public static LcdManager getInstance() {
        return INSTANCE;
    }


    public void init(@NonNull Lcd lcdInstance) {
        this.lcd = lcdInstance;
        this.lcd.setScreenMode(0);
    }


    public void clearScreen() {
        executorService.execute(() -> {
            try {
                lcd.clearFullScreen();
            } catch (Exception e) {
                Log.e("LcdManager", "Error in clearScreen", e);
            }
        });
    }


    public void showInitScreen(@NonNull String msg) {
        executorService.execute(() -> {
            try {
                lcd.clearFullScreen();
                lcd.textLinePosEx(SCREEN_CENTER_X, TITLE_POS_Y, msg, 12);
            } catch (Exception e) {
                Log.e("LcdManager", "Error in showInitScreen", e);
            }
        });
    }


    public void showInitPinScreen(@NonNull String tips) {
        executorService.execute(() -> {
            try {
                lcd.clearFullScreen();
                lcd.textLine(0, tips, DISPLAY_STYLE_CENTER);
                lcd.textLine(1, "", DISPLAY_STYLE_CENTER);
            } catch (Exception e) {
                Log.e("LcdManager", "Error in showInitPinScreen", e);
            }
        });
    }

    public void updateInputPinScreen(@NonNull String pin) {
        executorService.execute(() -> {
            try {
                lcd.clearLine(1);
                lcd.textLine(1, pin, DISPLAY_STYLE_CENTER);
            } catch (Exception e) {
                Log.e("LcdManager", "Error in showPinScreen", e);
            }
        });
    }
}