## 使用方法

Gradle:
```
implementation 'com.ftevxk:utils:1.0.0'
```

### 1.LogExtension用法:
1)任意地方调用: Any.dLog("logcat")<br>
2)打印输出(点击可跳转log位置): 方法名(文件名:行数): 打印内容
### 2.PreferencesExtension用法([示例文件](https://github.com/ftevxk/utils/blob/master/app/src/androidTest/java/com/ftevxk/example/ExampleInstrumentedTest.kt)):
1)SP存储，可同时多个不同类型的Pair: Context.putSpValue("key" to "value", "key1" to 0)<br>
2)SP读取，根据第二个参数默认值的类型返回: Context.getSpValue("key1", 0): Int
### 3.UnitExtension用法:
1)快速转换dp、sp等值,根据参数类型返回对应类型的值: Any.getDpUnit(10): Int、Any.getSpUnit(10f): Float
### 4.ImageExtension用法(加载URL图片并设置为圆形):
```xml
<ImageView
    android:layout_width="64dp"
    android:layout_height="64dp"
    android:layout_margin="10dp"
    bind:circle="@{true}"
    bind:res="@{`https://kotlinlang.org/assets/images/favicon.ico`}" />
```
### 5.ShapeExtension用法(设置为30dp的圆角矩形蓝色背景):
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    bind:shape_radius="@{30}"
    bind:shape_solid_color="@{Color.BLUE}"
    bind:shape_type="@{`rect`}"
    bind:unit="@{`dp`}"/>
```
### 6.DataBindAdapter用法:
[RecyclerViewExtension](https://github.com/ftevxk/utils/blob/master/library/src/main/java/com/ftevxk/base/extension/RecyclerViewExtension.kt):<br>
1)扩展RecyclerView的Item点击及长按事件<br>(与DataBindAdapter无关，官方RecyclerView可用，可区分点击的为item中具体的某个控件)<br>
2)扩展DataBindAdapter数据操作方法、构建简单的列表类型RecyclerView方法<br>
[Activity文件](https://github.com/ftevxk/utils/blob/master/app/src/main/java/com/ftevxk/example/MainActivity.kt)、
[Activity布局](https://github.com/ftevxk/utils/blob/master/app/src/main/res/layout/activity_main.xml)<br>
[ItemModel文件](https://github.com/ftevxk/utils/blob/master/app/src/main/java/com/ftevxk/example/MainItemModel.kt)、
[Item布局](https://github.com/ftevxk/utils/blob/master/app/src/main/res/layout/item_main.xml)
### 7.InterceptEditText用法:
1)带拦截的EditText，可拦截字符输入、字符删除、按键输入、光标选择
