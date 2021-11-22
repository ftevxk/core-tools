package com.ftevxk.example

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.ftevxk.core.extension.*
import com.ftevxk.core.widget.InterceptEditText
import com.ftevxk.example.base.BaseBindActivity
import com.ftevxk.example.databinding.ActivityMainBinding
import com.ftevxk.example.databinding.ItemMainBinding
import com.ftevxk.example.viewmodel.MainItemModel
import kotlin.random.Random

class MainActivity : BaseBindActivity<ActivityMainBinding>() {

    private val menuId = View.generateViewId()

    private val models by lazy {
        mutableListOf<MainItemModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //SharedPreferences数据测试在androidTest下的ExampleInstrumentedTest中
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        initData()
        initListener()
    }

    private fun initView() {
        //构建简单的列表类型RecyclerView
        binding.recycler.buildSimpleBindAdapter()
    }

    private fun initData() {
        //模拟10条数据
        (0 until 10).forEach {
            models.add(MainItemModel(title = (it + 1).toString()))
        }
        //RecyclerView设置数据
        binding.recycler.setItemModels(models)
    }

    private fun initListener() {
        //设置Item点击事件
        binding.recycler.setOnItemClickListener { holder, position ->
            //获取item中间文本控件
            val button = holder.getItemBinding<ItemMainBinding>()?.button
            //点击控件toast，其余地方替换一个随机数字
            if (holder.isClickControlView(button)) {
                toastShort(button?.text ?: (position + 1).toString())
            } else {
                //拷贝一个仅替换title
                val model = models[position]
                        .copy(title = Random.nextInt(100).toString())
                //RecyclerView设置单条数据
                binding.recycler.setItemModel(model, position)
            }
        }
        //设置Item长按事件
        binding.recycler.setOnItemLongClickListener { holder, position ->
            val button = holder.getItemBinding<ItemMainBinding>()?.button
            //其他范围才弹框询问是否删除
            if (!holder.isClickControlView(button)) {
                AlertDialog.Builder(this)
                        .setTitle("是否删除该条?")
                        .setPositiveButton("确定") { dialog, _ ->
                            binding.recycler.removeItemModel(position)
                            dialog.dismiss()
                        }
                        .setNegativeButton("取消") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        //右上角增加添加按钮
        menu?.add(0, menuId, 0, "添加")
                ?.setIcon(android.R.drawable.ic_menu_add)
                ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            menuId -> {
                val editText = getAddEditText()
                //右上角添加按钮弹框询问
                AlertDialog.Builder(this)
                        .setTitle("是否添加新数据?")
                        .setView(editText)
                        .setPositiveButton("确定") { dialog, _ ->
                            if (editText.text.isEmpty()) {
                                //如果不输入位置则追加在末尾
                                binding.recycler.addItemModel(MainItemModel(title = "new-${Random.nextInt(100)}"))
                            } else {
                                //将数据插入指定位置
                                val index = editText.text.toString().toInt()
                                if (index > models.size) {
                                    toastShort("ItemModel插入的位置不能大于${models.size}")
                                } else {
                                    binding.recycler.addItemModel(MainItemModel(title = "new-${Random.nextInt(100)}"), index)
                                }
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton("取消") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getAddEditText(): EditText {
        //创建一个带输入拦截的EditText
        val editText = InterceptEditText(this)
        editText.hint = "输入需要插入的位置，不输入则插入末尾"
        editText.inputType = EditorInfo.TYPE_CLASS_NUMBER
        editText.textSize = 14f
        editText.setMargins(horizontal = getDpUnit(20))
        editText.setInterceptListener(object : InterceptEditText.InterceptListener {
            //设置输入拦截，判断输入的位置如果大于models则不让输入
            override fun commitText(target: InputConnection, text: CharSequence, newCursorPosition: Int): Boolean? {
                if ((editText.text.toString() + text).toInt() > models.size) {
                    toastShort("ItemModel插入的位置不能大于${models.size}")
                    return false
                }
                return super.commitText(target, text, newCursorPosition)
            }
        })
        return editText
    }
}