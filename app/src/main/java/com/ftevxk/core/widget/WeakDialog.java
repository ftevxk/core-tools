package com.ftevxk.core.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * @author Roshine
 * @date 2020/6/22 14:43
 * @desc 避免内存泄漏的Dialog, 在DialogFragment中重写onCreateDialog中返回该dialog
 */
public class WeakDialog extends Dialog {
    protected WeakDialog(@NonNull Context context) {
        super(context);
    }

    public WeakDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected WeakDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void setOnShowListener(@Nullable OnShowListener listener) {
        super.setOnShowListener(WeakDialogProxy.proxy(listener));
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(WeakDialogProxy.proxy(listener));
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        super.setOnCancelListener(WeakDialogProxy.proxy(listener));
    }

    public static class WeakDialogProxy {

        public static WeakOnCancelListener proxy(OnCancelListener real) {
            return new WeakOnCancelListener(real);
        }

        public static WeakOnDismissListener proxy(OnDismissListener real) {
            return new WeakOnDismissListener(real);
        }

        public static WeakOnShowListener proxy(OnShowListener real) {
            return new WeakOnShowListener(real);
        }
    }

    public static class WeakOnCancelListener implements OnCancelListener {
        private WeakReference<OnCancelListener> mRef;

        public WeakOnCancelListener(OnCancelListener real) {
            this.mRef = new WeakReference<>(real);
        }


        @Override
        public void onCancel(DialogInterface dialog) {
            OnCancelListener real = mRef.get();
            if (real != null) {
                real.onCancel(dialog);
            }
        }
    }

    public static class WeakOnDismissListener implements OnDismissListener {
        private WeakReference<OnDismissListener> mRef;

        public WeakOnDismissListener(OnDismissListener real) {
            this.mRef = new WeakReference<>(real);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            OnDismissListener real = mRef.get();
            if (real != null) {
                real.onDismiss(dialog);
            }
        }
    }

    public static class WeakOnShowListener implements OnShowListener {
        private WeakReference<OnShowListener> mRef;

        public WeakOnShowListener(OnShowListener real) {
            this.mRef = new WeakReference<>(real);
        }

        @Override
        public void onShow(DialogInterface dialog) {
            OnShowListener real = mRef.get();
            if (real != null) {
                real.onShow(dialog);
            }
        }
    }
}

