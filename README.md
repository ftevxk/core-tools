## 示例

<p>
<img src="https://github.com/ftevxk/core-tools/blob/master/image/1.gif" height="420" alt="效果图"/>
</p>

<p>
<img src="https://github.com/ftevxk/core-tools/blob/master/image/2.png" width="48%" height="280" alt="关键代码1"/>
<img src="https://github.com/ftevxk/core-tools/blob/master/image/3.png" width="48%" height="280" alt="关键代码2"/>
</p>

* [MainActivity.kt](https://github.com/ftevxk/utils/blob/master/app/src/main/java/com/ftevxk/example/MainActivity.kt)
* [activity_main.xml](https://github.com/ftevxk/utils/blob/master/app/src/main/res/layout/activity_main.xml)
* [MainItemModel.kt](https://github.com/ftevxk/utils/blob/master/app/src/main/java/com/ftevxk/example/MainItemModel.kt)
* [item_main.xml](https://github.com/ftevxk/utils/blob/master/app/src/main/res/layout/item_main.xml)

## 使用方法

```
implementation 'com.ftevxk:core-tools:1.0.3'
```

### Kotlin、Databinding入门学习文章：

#### 1.Kotlin
* [Kotlin 简介与入门指南](https://blog.csdn.net/z4909801/article/details/72636852)
* [Kotlin 进阶](https://blog.csdn.net/ccw0054/article/details/79045504)
* [Kotlin 的一些实用小技巧](https://www.jianshu.com/p/b8220a278fb0)

#### 2.DataBinding
* [Android DataBinding 实战全解](https://www.jianshu.com/p/0fe0b6b7dae1)
* [Android Databinding 使用全面总结](https://www.jianshu.com/p/572822d9eff9)
* [Android DataBinding 从入门到进阶](https://www.jianshu.com/p/bd9016418af2)

## 主要扩展工具

### 1.LogExtension用法:
* 任意地方调用: Any.dLog("logcat")
* 打印输出(点击可跳转log位置): 方法名(文件名:行数): 打印内容

<img src="https://github.com/ftevxk/core-tools/blob/master/image/4.png"/>
<img src="https://github.com/ftevxk/core-tools/blob/master/image/5.png"/>

### 2.PreferencesExtension用法([ExampleInstrumentedTest.kt](https://github.com/ftevxk/utils/blob/master/app/src/androidTest/java/com/ftevxk/example/ExampleInstrumentedTest.kt)):
* SP存储，可同时多个不同类型的Pair: Context.putSpValue("key" to "value", "key1" to 0)
* SP读取，根据第二个参数默认值的类型返回: Context.getSpValue("key1", 0): Int

<img src="https://github.com/ftevxk/core-tools/blob/master/image/6.png"/>

### 3.UnitExtension、InterceptEditText用法:
* [UnitExtension.kt](https://github.com/ftevxk/core-tools/blob/master/library/src/main/java/com/ftevxk/core/extension/UnitExtension.kt)：
快速转换dp、sp等值,根据参数类型返回对应类型的值: Any.getDpUnit(10): Int、Any.getSpUnit(10f): Float

* [InterceptEditText.kt](https://github.com/ftevxk/utils/blob/master/library/src/main/java/com/ftevxk/core/widget/InterceptEditText.kt)：
通过setInterceptListener设置监听，可拦截字符输入、字符删除、按键输入、光标选择

```kotlin
val editText = InterceptEditText(this)
editText.setMargins(horizontal = getDpUnit(20))
editText.setInterceptListener(object : InterceptEditText.InterceptListener {
    override fun commitText(target: InputConnection,
                            text: CharSequence,
                            newCursorPosition: Int): Boolean? {
        dLog("拦截所有输入")
        return false
     }
})
```

### 4.ImageExtension用法(加载URL图片并设置为圆形):

```xml
<ImageView
    android:layout_width="64dp"
    android:layout_height="64dp"
    android:layout_margin="10dp"
    tools:circle="@{true}"
    tools:res="@{`https://kotlinlang.org/assets/images/favicon.ico`}" />
```

### 5.ShapeExtension用法(设置为30dp的圆角矩形蓝色背景):

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    tools:shape_radius="@{30}"
    tools:shape_solid_color="@{Color.BLUE}"
    tools:shape_type="@{`rect`}"
    tools:unit="@{`dp`}"/>
```

### 6.DataBindAdapter用法:

* [RecyclerViewExtension.kt](https://github.com/ftevxk/utils/blob/master/library/src/main/java/com/ftevxk/core/extension/RecyclerViewExtension.kt)
-RecyclerView点击长按事件及一些DataBindAdapter操作扩展
* 扩展RecyclerView的Item点击及长按事件<br>(与DataBindAdapter无关，官方RecyclerView可用，可区分点击的为item中具体的某个控件)
* 扩展DataBindAdapter数据操作方法、构建简单的列表类型RecyclerView方法<br>
