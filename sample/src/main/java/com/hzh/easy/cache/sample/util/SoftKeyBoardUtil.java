package com.hzh.easy.cache.sample.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Hezihao on 2017/10/9.
 * 软键盘工具类
 */

public class SoftKeyBoardUtil {
    private SoftKeyBoardUtil() {
    }

    /**
     * 显示软键盘
     *
     * @param input 必须是可输入的view 例如EditText
     */
    public static void showSoftKeyboard(Context context, View input) {
        if (context == null || input == null) {
            return;
        }
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(input, 0);
    }

    /**
     * 隐藏软键盘
     *
     * @param view 可任意可见的View
     */
    public static void hideSoftKeyboard(Context context, View view) {
        if (view == null || context == null) {
            return;
        }
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 切换软键盘，显示变隐藏，隐藏变显示
     */
    public static void toggleSoftKeyboard(Context context) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
