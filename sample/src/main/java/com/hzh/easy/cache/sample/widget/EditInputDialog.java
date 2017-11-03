package com.hzh.easy.cache.sample.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hzh.easy.cache.sample.R;
import com.hzh.easy.cache.sample.util.SoftKeyBoardUtil;

/**
 * @package com.hzh.easy.cache.sample.widget
 * @fileName EditDialog
 * @date on 2017/11/3  下午3:07
 * @auther 子和
 * @descirbe TODO
 * @email hezihao@linghit.com
 */

public class EditInputDialog extends Dialog implements View.OnClickListener {
    private EditText input;
    private TextView confirm;
    private TextView cancel;
    private View layout;
    private OnClickListener clickListener;
    private OnDisplayChangeListener displayChangeListener;

    public EditInputDialog(@NonNull Context context) {
        super(context);
        init();
    }

    protected EditInputDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        layout = LayoutInflater.from(getContext()).inflate(R.layout.view_edit_input_dialog, null);
        setContentView(layout);
        input = layout.findViewById(R.id.input);
        confirm = layout.findViewById(R.id.confirm);
        cancel = layout.findViewById(R.id.cancle);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    @Override
    public void onClick(View v) {
        if (clickListener == null) {
            return;
        }
        SoftKeyBoardUtil.hideSoftKeyboard(getContext(), input);
        switch (v.getId()) {
            case R.id.confirm:
                clickListener.onClickConfirm();
                break;
            case R.id.cancle:
                clickListener.onClickCancel();
                break;
        }
    }

    public interface OnClickListener {
        void onClickConfirm();

        void onClickCancel();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnDisplayChangeListener {
        void onDisplayChange(boolean isVisible);
    }

    public void setOnDisplayChangeListener(OnDisplayChangeListener listener) {
        this.displayChangeListener = listener;
    }

    public String getInput() {
        if (input == null) {
            input = layout.findViewById(R.id.input);
        }
        return input.getText().toString();
    }

    @Override
    public void show() {
        super.show();
        input.setFocusable(true);
        input.setFocusableInTouchMode(true);
        input.requestFocus();
        if (displayChangeListener != null) {
            displayChangeListener.onDisplayChange(true);
        }
    }

    @Override
    public void dismiss() {
        if (displayChangeListener != null) {
            displayChangeListener.onDisplayChange(false);
        }
        super.dismiss();
    }

    public EditText getInputEditText() {
        if (input == null) {
            input = layout.findViewById(R.id.input);
        }
        return input;
    }
}
