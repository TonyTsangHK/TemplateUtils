package utils.template

import utils.template.constant.ConstantFactory
import utils.template.constant.ConstantHandler

import java.math.BigDecimal

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-01-09
 * Time: 15:16
 */
class SubstituteInstance : ValueSubstitutorKt {
    fun testingMethod(parameterMap: Map<String, Any>): Any {
        return parameterMap.size
    }

    fun testingMethod(v1: Int?, v2: Int?, v3: Double?): Any {
        return 3
    }

    fun fakeHTML(): String {
        return "<div>Fake html inside div!</div>"
    }

    override fun <V> getVariableValue(keys: Array<String>, vararg substitutes: String): V? {
        return constantHandler!!.getVariableValue<V>(keys, *substitutes)
    }

    override fun <V> getVariableValue(keys: Array<String>, substituteMap: Map<String, Any>): V? {
        return constantHandler!!.getVariableValue<V>(keys, substituteMap)
    }

    override fun <V> getVariableValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): V? {
        return constantHandler!!.getVariableValue<V>(keys, nullSafe, *substitutes)
    }

    override fun <V> getVariableValue(keys: Array<String>, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
        return constantHandler!!.getVariableValue<V>(keys, nullSafe, substituteMap)
    }

    override fun <V> getVariableValue(key: String, nullSafe: Boolean, vararg substitutes: String): V? {
        return constantHandler!!.getVariableValue<V>(key, nullSafe, *substitutes)
    }

    override fun <V> getVariableValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
        return constantHandler!!.getVariableValue<V>(key, nullSafe, substituteMap)
    }

    override fun <V> getVariableValue(key: String, vararg substitutes: String): V? {
        return constantHandler!!.getVariableValue<V>(key, *substitutes)
    }

    override fun <V> getVariableValue(key: String, substituteMap: Map<String, Any>): V? {
        return constantHandler!!.getVariableValue<V>(key, substituteMap)
    }

    override fun getVariableIntegerValue(keys: Array<String>): Int {
        return constantHandler!!.getVariableIntegerValue(keys)
    }

    override fun getVariableLongValue(keys: Array<String>): Long {
        return constantHandler!!.getVariableLongValue(keys)
    }

    override fun getVariableBigDecimalValue(keys: Array<String>): BigDecimal {
        return constantHandler!!.getVariableBigDecimalValue(keys)
    }

    override fun getVariableIntegerValue(keys: Array<String>, defaultValue: Int): Int {
        return constantHandler!!.getVariableIntegerValue(keys, defaultValue)
    }

    override fun getVariableLongValue(keys: Array<String>, defaultValue: Long): Long {
        return constantHandler!!.getVariableLongValue(keys, defaultValue)
    }

    override fun getVariableBigDecimalValue(keys: Array<String>, defaultValue: BigDecimal?): BigDecimal? {
        return constantHandler!!.getVariableBigDecimalValue(keys, defaultValue)
    }

    override fun getVariableIntegerValue(key: String): Int {
        return constantHandler!!.getVariableIntegerValue(key)
    }

    override fun getVariableLongValue(key: String): Long {
        return constantHandler!!.getVariableLongValue(key)
    }

    override fun getVariableBigDecimalValue(key: String): BigDecimal {
        return constantHandler!!.getVariableBigDecimalValue(key)
    }

    override fun getVariableIntegerValue(key: String, defaultValue: Int): Int {
        return constantHandler!!.getVariableIntegerValue(key, defaultValue)
    }

    override fun getVariableLongValue(key: String, defaultValue: Long): Long {
        return constantHandler!!.getVariableLongValue(key, defaultValue)
    }

    override fun getVariableBooleanValue(keys: Array<String>): Boolean {
        return constantHandler!!.getVariableBooleanValue(keys)
    }

    override fun getVariableBooleanValue(keys: Array<String>, defaultValue: Boolean): Boolean {
        return constantHandler!!.getVariableBooleanValue(keys, defaultValue)
    }

    override fun getVariableBooleanValue(key: String): Boolean {
        return constantHandler!!.getVariableBooleanValue(key)
    }

    override fun getVariableBooleanValue(key: String, defaultValue: Boolean): Boolean {
        return constantHandler!!.getVariableBooleanValue(key, defaultValue)
    }

    override fun getVariableBigDecimalValue(key: String, defaultValue: BigDecimal?): BigDecimal? {
        return constantHandler!!.getVariableBigDecimalValue(key, defaultValue)
    }

    override fun getVariableStringValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): String? {
        return constantHandler!!.getVariableStringValue(keys, nullSafe, *substitutes)
    }

    override fun getVariableStringValue(keys: Array<String>, nullSafe: Boolean, substitutes: Map<String, Any>): String? {
        return constantHandler!!.getVariableStringValue(keys, nullSafe, substitutes)
    }

    override fun getVariableStringValue(keys: Array<String>, vararg substitutes: String): String? {
        return constantHandler!!.getVariableStringValue(keys, *substitutes)
    }

    override fun getVariableStringValue(keys: Array<String>, substituteMap: Map<String, Any>): String? {
        return constantHandler!!.getVariableStringValue(keys, substituteMap)
    }

    override fun getVariableStringValue(key: String, nullSafe: Boolean, vararg substitutes: String): String? {
        return constantHandler!!.getVariableStringValue(key, nullSafe, *substitutes)
    }

    override fun getVariableStringValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): String? {
        return constantHandler!!.getVariableStringValue(key, nullSafe, substituteMap)
    }

    override fun getVariableStringValue(key: String, vararg substitutes: String): String? {
        return constantHandler!!.getVariableStringValue(key, *substitutes)
    }

    override fun getVariableStringValue(key: String, substituteMap: Map<String, Any>): String? {
        return constantHandler!!.getVariableStringValue(key, substituteMap)
    }

    override fun variable(vararg keys: String): String? {
        return constantHandler!!.variable(*keys)
    }

    fun getConstantHandler(): ConstantHandler? {
        return constantHandler
    }

    companion object {
        private val masterConstantHandler = ConstantFactory.getInstance().getConstantHandler(
                SubstituteInstance::class.java, "master.json")
        private val constantHandler = ConstantFactory.getInstance().getConstantHandler(
                masterConstantHandler!!, SubstituteInstance::class.java, "def.json")

        fun <V> getConstantValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): V? {
            return constantHandler!!.getConstantValue<V>(keys, nullSafe, *substitutes)
        }

        fun <V> getConstantValue(keys: Array<String>, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
            return constantHandler!!.getConstantValue<V>(keys, nullSafe, substituteMap)
        }

        fun <V> getConstantValue(key: String, nullSafe: Boolean, vararg substitutes: String): V? {
            return constantHandler!!.getConstantValue<V>(key, nullSafe, *substitutes)
        }

        fun <V> getConstantValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
            return constantHandler!!.getConstantValue<V>(key, nullSafe, substituteMap)
        }

        fun getConstantStringValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): String? {
            return constantHandler!!.getConstantStringValue(keys, nullSafe, *substitutes)
        }

        fun getConstantStringValue(keys: Array<String>, nullSafe: Boolean, substituteMap: Map<String, Any>): String? {
            return constantHandler!!.getConstantStringValue(keys, nullSafe, substituteMap)
        }

        fun getConstantStringValue(keys: Array<String>): String? {
            return constantHandler!!.getConstantStringValue(keys)
        }

        fun getConstantStringValue(key: String, nullSafe: Boolean, vararg substitutes: String): String? {
            return constantHandler!!.getConstantStringValue(key, nullSafe, *substitutes)
        }

        fun getConstantStringValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): String? {
            return constantHandler!!.getConstantStringValue(key, nullSafe, substituteMap)
        }

        fun getConstantStringValue(key: String): String {
            return constantHandler!!.getConstantStringValue(key)
        }

        fun keys(): List<String> {
            return constantHandler!!.keys()
        }
    }
}
