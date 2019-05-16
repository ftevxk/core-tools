package cn.ftevxk.base

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.ftevxk.base.databinding.ActivityMainBinding
import cn.ftevxk.base.databinding.ItemMainBinding
import cn.ftevxk.base.extension.*
import com.pawegio.kandroid.alert
import com.pawegio.kandroid.toast
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val menuId = View.generateViewId()

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

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
        binding.recycler.buildSimpleBindAdapter()
    }

    private fun initData() {
        //模拟20条数据
        (0 until 10).forEach {
            models.add(MainItemModel(title = (it + 1).toString()))
        }
        binding.recycler.setItemModels(models)
    }

    private fun initListener() {
        //设置点击、长按事件
        binding.recycler.setOnItemClickListener(onItemClickListener = { holder, position, location ->
            //获取item中间文本控件
            val textView = holder.getItemBinding<ItemMainBinding>()?.textView
            //点击控件toast，其余地方替换一个随机数字
            if (binding.recycler.isClickControlView(textView, location)) {
                toast(textView?.text ?: (position + 1).toString())
            } else {
                //拷贝一个仅替换title
                val model = models[position]
                    .copy(title = Random.nextInt(100).toString())
                binding.recycler.setItemModel(model, position)
            }
        }, onItemLongClickListener = { _, position, _ ->
            //长按弹框询问是否删除
            alert("是否删除该条?") {
                negativeButton("取消") {}
                positiveButton("确定") {
                    binding.recycler.removeItemModel<MainItemModel>(position)
                }
            }.show()
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menu?.add(0, menuId, 0, "添加")
            ?.setIcon(android.R.drawable.ic_menu_add)
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            menuId -> {
                alert("是否添加新数据?") {
                    val editText = getAddEditText()
                    customView(editText)
                    negativeButton("取消") {}
                    positiveButton("确定") {
                        if (editText.text.isEmpty()) {
                            binding.recycler.setItemModel(
                                MainItemModel(title = "new-${Random.nextInt(100)}"),
                                additional = true
                            )
                        } else {
                            val index = editText.text.toString().toInt()
                            if (index > models.size) {
                                toast("输入的位置不能大于${models.size}")
                            } else {
                                binding.recycler.setItemModel(
                                    MainItemModel(title = "new-${Random.nextInt(100)}"),
                                    index, true
                                )
                            }
                        }
                    }
                }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getAddEditText(): EditText {
        val editText = EditText(this)
        editText.hint = "输入需要插入的位置，不输入则插入末尾"
        editText.inputType = EditorInfo.TYPE_CLASS_NUMBER
        editText.textSize = 14f
        editText.setMargins(left = 20.toDpUnit().toInt(), right = 20.toDpUnit().toInt())
        return editText
    }
}