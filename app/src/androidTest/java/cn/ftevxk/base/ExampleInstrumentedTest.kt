package cn.ftevxk.base

import android.util.Log
import androidx.test.rule.ActivityTestRule
import cn.ftevxk.base.extension.getSpValue
import cn.ftevxk.base.extension.putSpValue
import cn.ftevxk.base.extension.removeSpKey
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleInstrumentedTest {

    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    private val activity by lazy { activityTestRule.activity }

    /**
     * SharedPreferences数据测试
     */
    @Test
    fun testSpValue() {
        testRemoveSpValue()
        testPutSpValue()
        testGetSpValue()
    }

    private fun testPutSpValue() {
        activity.putSpValue("111", mutableListOf(true, 0.11f, 0.22, "666", 111, 222L))
        activity.putSpValue("222", mutableMapOf(true to 0.11f, "666" to 111))
        Log.d("class=>ExampleInstrumentedTest", "testPutSpValue(ExampleInstrumentedTest.kt:" +
                "${Throwable().stackTrace[0].lineNumber})\n" +
                "putSpValue Success"
        )
    }

    private fun testGetSpValue() {
        val lists = activity.getSpValue("111", mutableListOf<Any>("empty"))
        var listStr = "getSpValue(111) Success =>\n"
        lists.forEach { listStr += "type = ${it.javaClass.simpleName}, value = $it\n" }
        Log.d("class=>ExampleInstrumentedTest", "testGetSpValue(ExampleInstrumentedTest.kt:" +
                "${Throwable().stackTrace[0].lineNumber})\n" + listStr)


        val maps = activity.getSpValue("222", mutableMapOf<Any, Any>("" to "empty"))
        var mapStr = "getSpValue(222) Success => $maps\n"
        maps.forEach {
            mapStr += "keyType = ${it.key.javaClass.simpleName}, keyValue = ${it.key}; " +
                    "valueType = ${it.value.javaClass.simpleName}, valueValue = ${it.value}\n"
        }
        Log.d("class=>ExampleInstrumentedTest", "testGetSpValue(ExampleInstrumentedTest.kt:" +
                "${Throwable().stackTrace[0].lineNumber})\n$mapStr")
    }

    private fun testRemoveSpValue() {
        val key1 = activity.removeSpKey("111")
        val key2 = activity.removeSpKey("222")
        Log.d("class=>ExampleInstrumentedTest", "testPutSpValue(ExampleInstrumentedTest.kt:" +
                "${Throwable().stackTrace[0].lineNumber})\n" +
                "removeSpKey key(111) = $key1, key(222) = $key2"
        )
    }
}
