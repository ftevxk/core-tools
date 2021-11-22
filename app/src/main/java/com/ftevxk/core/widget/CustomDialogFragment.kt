@file:Suppress("MemberVisibilityCanBePrivate")

package com.ftevxk.core.widget

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ftevxk.example.R


/**
 * 自定义布局的DialogFragment，默认带居中显示和底部显示方法
 */
class CustomDialogFragment : AppCompatDialogFragment() {
    private var layoutRes = 0
    private var showListener: OnShowListener? = null
    private var listener: OnDialogFragmentCreateListener? = null
    private var cancelListener: DialogInterface.OnCancelListener? = null
    private var dismissListener: DialogInterface.OnDismissListener? = null

    companion object {
        fun newInstance(@LayoutRes layoutRes: Int): CustomDialogFragment {
            val args = Bundle()
            args.putInt("layoutRes", layoutRes)
            val fragment = CustomDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * 显示默认居中DialogFragment
     */
    fun showDialogFragment(fragmentManager: FragmentManager, cancelable: Boolean,
                           listener: OnDialogFragmentCreateListener?): CustomDialogFragment {
        this.listener = listener
        isCancelable = cancelable
        show(fragmentManager, layoutRes.toString())
        return this
    }

    fun showBottomDialogFragment(fragmentManager: FragmentManager, cancelable: Boolean,
                                 listener: (dialogFragment: CustomDialogFragment, rootView: View) -> Unit): CustomDialogFragment {
        return showBottomDialogFragment(fragmentManager, cancelable, object : OnDialogFragmentCreateListener {
            override fun onViewCreate(dialogFragment: CustomDialogFragment, rootView: View) {
                listener.invoke(this@CustomDialogFragment, rootView)
            }
        })
    }

    /**
     * 显示底部DialogFragment，覆盖listener带底部滑入滑出动画
     * 如果需要其他动画使用showDialogFragment
     */
    fun showBottomDialogFragment(fragmentManager: FragmentManager, cancelable: Boolean,
                                 listener: OnDialogFragmentCreateListener): CustomDialogFragment {
        this.listener = object : OnDialogFragmentCreateListener {
            override fun onWindowAnimations(): Int {
                return R.style.Animation_Design_BottomSheetDialog
            }

            override fun onLayoutParams(dialogFragment: DialogFragment, layoutParams: WindowManager.LayoutParams) {
                layoutParams.gravity = Gravity.BOTTOM
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                listener.onLayoutParams(dialogFragment, layoutParams)
            }

            override fun onViewCreate(dialogFragment: CustomDialogFragment, rootView: View) {
                listener.onViewCreate(dialogFragment, rootView)
            }
        }
        isCancelable = cancelable
        show(fragmentManager, layoutRes.toString())
        return this
    }

    fun showDialogFragment(fragmentManager: FragmentManager, listener: OnDialogFragmentCreateListener?): CustomDialogFragment {
        return showDialogFragment(fragmentManager, true, listener)
    }

    fun showBottomDialogFragment(fragmentManager: FragmentManager, listener: OnDialogFragmentCreateListener): CustomDialogFragment {
        return showBottomDialogFragment(fragmentManager, true, listener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth)
        } else {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutRes = arguments?.getInt("layoutRes") ?: 0
        return inflater.inflate(layoutRes, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (listener != null) {
            listener!!.onViewCreate(this, view)
        }
    }

    override fun onStart() {
        super.onStart()
        if (listener != null && dialog != null) {
            val window = dialog!!.window
            if (window != null) {
                // 设置Dialog动画
                val windowAnimations = listener!!.onWindowAnimations()
                if (windowAnimations > 0) {
                    window.setWindowAnimations(windowAnimations)
                }

                // 一定要设置Background，如果不设置，window属性设置无效
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val params = window.attributes
                listener!!.onLayoutParams(this, params)
                window.attributes = params
            }
        }
    }

    fun setOnShowListener(showListener: OnShowListener?): CustomDialogFragment {
        this.showListener = showListener
        return this
    }

    fun setOnCancelListener(cancelListener: DialogInterface.OnCancelListener?): CustomDialogFragment {
        this.cancelListener = cancelListener
        return this
    }

    fun setOnDismissListener(dismissListener: DialogInterface.OnDismissListener?): CustomDialogFragment {
        this.dismissListener = dismissListener
        return this
    }

    override fun show(manager: FragmentManager, tag: String?) {
        //https://blog.csdn.net/maxwell0401/article/details/84024365
//        super.show(manager, tag);
        try {
            val c = Class.forName("androidx.fragment.app.DialogFragment")
            val con = c.getConstructor()
            val obj = con.newInstance()
            val dismissed = c.getDeclaredField("mDismissed")
            dismissed.isAccessible = true
            dismissed[obj] = false
            val shownByMe = c.getDeclaredField("mShownByMe")
            shownByMe.isAccessible = true
            shownByMe[obj] = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
        if (showListener != null) showListener!!.onShow(dialog)
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        //https://blog.csdn.net/maxwell0401/article/details/84024365
//        int show = super.show(transaction, tag);
        try {
            val c = Class.forName("androidx.fragment.app.DialogFragment")
            val con = c.getConstructor()
            val obj = con.newInstance()
            val dismissed = c.getDeclaredField("mDismissed")
            dismissed.isAccessible = true
            dismissed[obj] = false
            val shownByMe = c.getDeclaredField("mShownByMe")
            shownByMe.isAccessible = true
            shownByMe[obj] = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        transaction.add(this, tag)
        val show = transaction.commitAllowingStateLoss()
        if (showListener != null) showListener!!.onShow(dialog)
        return show
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        super.showNow(manager, tag)
        if (showListener != null) showListener!!.onShow(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if (cancelListener != null) cancelListener!!.onCancel(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (dismissListener != null) dismissListener!!.onDismiss(dialog)
    }

    override fun dismiss() {
        //https://blog.csdn.net/maxwell0401/article/details/84024365
//        super.dismiss();
        dismissAllowingStateLoss()
    }

    interface OnDialogFragmentCreateListener {
        /**
         * Dialog动画，style需要如下
         *
         * <style name="FragmentDialogAnimation">
        <item name="android:windowEnterAnimation"></item>
        <item name="android:windowExitAnimation"></item>
        </style>
         */
        fun onWindowAnimations(): Int {
            return 0
        }

        /**
         * 设置LayoutParams，主要设置width、height、gravity
         */
        fun onLayoutParams(dialogFragment: DialogFragment, layoutParams: WindowManager.LayoutParams) {}

        /**
         * 布局创建初始化，可以通过rootView找控件设置属性
         */
        fun onViewCreate(dialogFragment: CustomDialogFragment, rootView: View)
    }

    /**
     * 处理DialogFragment内存泄露问题
     * 造成原因 => https://blog.csdn.net/u012165769/article/details/106843679/
     */
    @Suppress("UNREACHABLE_CODE")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        super.onCreateDialog(savedInstanceState)
        return WeakDialog(requireContext(), theme)
    }
}