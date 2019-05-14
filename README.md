# Kotlin + DataBinding搭建的基础框架

## 主要扩展方法
1、DataBindAdapter：DataBinding通用RecyclerView.Adapter封装，需要配合DataBindItemModel作为数据源使用；
2、ImageExtension：使用Glide扩展ImageView基本加载功能；
3、ShapeExtension：简单常用自定义Drawable功能，Kotlin代码可以获得Drawable对象，XML布局可以直接设置继承自TextView控件的背景；
4、PreferencesExtension：SharedPreferences数据存取，额外支持JSONObject、JSONArray、MutableList、MutableMap（MutableList和MutableMap暂不支持嵌套）；
5、UnitExtension：数值转换单位值扩展；
