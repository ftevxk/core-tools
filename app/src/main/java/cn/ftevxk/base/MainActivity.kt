package cn.ftevxk.base

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
        (0 until 20).forEach {
            models.add(MainItemModel(title = (it + 1).toString()))
        }
        binding.recycler.setItemModels(models)
    }

    private fun initListener() {
        //设置点击、长按事件
        binding.recycler.setOnItemClickListener(onItemClickListener = { holder, position, location ->
            //获取item中间文本控件
            val textView = holder.getItemBinding<ItemMainBinding>()?.textView
            //点击控件替换一个随机数字，其余地方toast
            if (binding.recycler.isClickControlView(textView, location)) {
                //拷贝一个仅替换title
                val model = models[position]
                        .copy(title = Random.nextInt(100).toString())
                binding.recycler.setItemModel(model, position)
            } else {
                toast(position.toString())
            }
        }, onItemLongClickListener = { _, position, _ ->
            //长按弹框询问是否删除
            alert("是否删除该条?") {
                negativeButton("取消") {}
                positiveButton("确定") {
                    models.removeAt(position)
                    binding.recycler.setItemModels(models)
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
                    negativeButton("取消") {}
                    positiveButton("确定") {
                        models.add(MainItemModel(title = "new-${Random.nextInt(100)}"))
                        binding.recycler.setItemModels(models)
                    }
                }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}