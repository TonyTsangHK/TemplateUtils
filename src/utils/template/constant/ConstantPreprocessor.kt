package utils.template.constant

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2018-11-19
 * Time: 10:23
 */
interface ConstantPreprocessor {
    fun doPreprocess(constantMap: MutableMap<String, Any?>)
    fun addConstantChangeListener(constantChangeListener: ConstantChangeListener)
}