@file:Suppress("ValidFragment", "unused")

package com.ftevxk.core.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment

@PublishedApi
internal class ResultFragment @SuppressLint("ValidFragment") constructor(
        private val intent: Intent, private val result: (ResultFragment, Intent?) -> Unit) : Fragment() {

    private val mRequestCode = View.generateViewId()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        startActivityForResult(intent, mRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mRequestCode) {
            result.invoke(this, data)
        }
    }
}