@file:Suppress("UNCHECKED_CAST")

package com.ftevxk.example.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseBindActivity<Binding : ViewBinding> : AppCompatActivity(){

    open val binding by lazy {
        //内部反射泛型完成初始化
        val bindingClass = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>
        val method= bindingClass.getMethod("inflate", LayoutInflater::class.java)
        return@lazy method.invoke(bindingClass, layoutInflater) as Binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}