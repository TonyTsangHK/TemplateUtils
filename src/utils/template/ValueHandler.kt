package utils.template

import utils.data.DataManipulator
import utils.math.MathUtil

import java.math.BigDecimal

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2014-01-08
 * Time: 17:34
 */
class ValueHandler @JvmOverloads constructor(private val substitutor: ValueSubstitutor, private val overrideMap: Map<String, Any>? = null): ValueSubstitutor {
    override fun <V> getVariableValue(keys: Array<String>, vararg substitutes: String): V? {
        return getVariableValue<V>(keys, false, *substitutes)
    }

    override fun <V> getVariableValue(keys: Array<String>, substituteMap: Map<String, Any>): V? {
        return getVariableValue(keys, false, substituteMap)
    }

    private fun getOverrideValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): Any? {
        if (overrideMap == null) {
            return null
        }

        var cmap: Map<String, Any>? = overrideMap

        var k: String

        var v: Any? = null

        for (i in keys.indices) {
            k = keys[i].trim { it <= ' ' }
            v = cmap?.get(k)
            if (i == keys.size - 1) {
                break
            } else if (v is Map<*, *>) {
                cmap = v as Map<String, Any>?
            } else {
                return null
            }
        }

        if (v != null) {
            if (v is String && substitutes != null && substitutes.size > 0) {

                for (i in substitutes.indices) {
                    println("$i: null? " + (substitutes[i] == null))
                    v = (v as String).replace("\\{$i\\}".toRegex(), substitutes[i])
                }

                return v
            } else {
                return v
            }
        } else {
            return if (nullSafe) "" else null
        }
    }

    private fun getOverrideValue(keys: Array<String>, nullSafe: Boolean, substituteMap: Map<String, Any>?): Any? {
        if (overrideMap == null) {
            return null
        }

        var cmap: Map<String, Any>? = overrideMap

        var k: String

        var v: Any? = null

        for (i in keys.indices) {
            k = keys[i].trim { it <= ' ' }
            v = cmap?.get(k)
            if (i == keys.size - 1) {
                break
            } else if (v is Map<*, *>) {
                cmap = v as Map<String, Any>?
            } else {
                return null
            }
        }

        if (v != null) {
            if (v is String && substituteMap != null && substituteMap.size > 0) {
                for (key in substituteMap.keys) {
                    v = (v as String).replace("\\{$key\\}".toRegex(), substituteMap[key].toString())
                }

                return v
            } else {
                return v
            }
        } else {
            return if (nullSafe) "" else null
        }
    }

    override fun <V> getVariableValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): V? {
        val v = getOverrideValue(keys, nullSafe, *substitutes)

        if (v != null) {
            return v as V?
        }

        return substitutor.getVariableValue<V>(keys, nullSafe, *substitutes)
    }

    override fun <V> getVariableValue(keys: Array<String>, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
        val v = getOverrideValue(keys, nullSafe, substituteMap)

        if (v != null) {
            return v as V?
        }

        return substitutor.getVariableValue<V>(keys, nullSafe, substituteMap)
    }

    override fun <V> getVariableValue(key: String, nullSafe: Boolean, vararg substitutes: String): V? {
        return getVariableValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), nullSafe, *substitutes)
    }

    override fun <V> getVariableValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
        return getVariableValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), nullSafe, substituteMap)
    }

    override fun <V> getVariableValue(key: String, vararg substitutes: String): V? {
        return getVariableValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), *substitutes)
    }

    override fun <V> getVariableValue(key: String, substituteMap: Map<String, Any>): V? {
        return getVariableValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), substituteMap)
    }

    override fun getVariableIntegerValue(keys: Array<String>): Int {
        return getVariableIntegerValue(keys, -1)
    }

    override fun getVariableIntegerValue(keys: Array<String>, defaultValue: Int): Int {
        val v = getVariableValue<Any>(keys)

        if (v == null) {
            return defaultValue
        } else if (v is Int) {
            return v.toInt()
        } else {
            return MathUtil.parseInt(v.toString(), 10, defaultValue)
        }
    }

    override fun getVariableIntegerValue(key: String): Int {
        return getVariableIntegerValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray())
    }

    override fun getVariableIntegerValue(key: String, defaultValue: Int): Int {
        return getVariableIntegerValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), defaultValue)
    }

    override fun getVariableLongValue(keys: Array<String>): Long {
        return getVariableLongValue(keys, -1)
    }

    override fun getVariableLongValue(keys: Array<String>, defaultValue: Long): Long {
        val v = getVariableValue<Any>(keys)

        if (v == null) {
            return defaultValue
        } else if (v is Long) {
            return v.toLong()
        } else {
            return MathUtil.parseLong(v.toString(), 10, defaultValue)
        }
    }

    override fun getVariableLongValue(key: String): Long {
        return getVariableLongValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray())
    }

    override fun getVariableLongValue(key: String, defaultValue: Long): Long {
        return getVariableLongValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), defaultValue)
    }

    override fun getVariableBooleanValue(keys: Array<String>): Boolean {
        return getVariableBooleanValue(keys, false);
    }

    override fun getVariableBooleanValue(keys: Array<String>, defaultValue: Boolean): Boolean {
        val v = getVariableValue<Any>(keys, false)
        
        return DataManipulator.extractBoolean(v, defaultValue)
    }

    override fun getVariableBooleanValue(key: String): Boolean {
        return getVariableBooleanValue(key, false)
    }

    override fun getVariableBooleanValue(key: String, defaultValue: Boolean): Boolean {
        return getVariableBooleanValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), defaultValue)
    }

    override fun getVariableBigDecimalValue(keys: Array<String>): BigDecimal {
        return getVariableBigDecimalValue(keys, BigDecimal.ZERO)!!
    }

    override fun getVariableBigDecimalValue(keys: Array<String>, defaultValue: BigDecimal?): BigDecimal? {
        val v = getVariableValue<Any>(keys)

        if (v == null) {
            return defaultValue
        } else if (v is BigDecimal) {
            return v
        } else {
            return MathUtil.parseBigDecimal(v.toString(), defaultValue)
        }
    }

    override fun getVariableBigDecimalValue(key: String): BigDecimal {
        return getVariableBigDecimalValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray())
    }

    override fun getVariableBigDecimalValue(key: String, defaultValue: BigDecimal?): BigDecimal? {
        return getVariableBigDecimalValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), defaultValue)
    }

    override fun getVariableStringValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): String? {
        val v = getVariableValue<Any>(keys, nullSafe, *substitutes)

        if (v == null) {
            return if (nullSafe) "" else null
        } else {
            return v.toString()
        }
    }

    override fun getVariableStringValue(
            keys: Array<String>, nullSafe: Boolean, substituteMap: Map<String, Any>
    ): String? {
        val v = getVariableValue<Any>(keys, nullSafe, substituteMap)

        if (v == null) {
            return if (nullSafe) "" else null
        } else {
            return v.toString()
        }
    }

    override fun getVariableStringValue(keys: Array<String>, vararg substitutes: String): String? {
        return getVariableStringValue(keys, false, *substitutes)
    }

    override fun getVariableStringValue(keys: Array<String>, substituteMap: Map<String, Any>): String? {
        return getVariableStringValue(keys, false, substituteMap)
    }

    override fun getVariableStringValue(key: String, nullSafe: Boolean, vararg substitutes: String): String? {
        return getVariableStringValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), nullSafe, *substitutes)
    }

    override fun getVariableStringValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): String? {
        return getVariableStringValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), nullSafe, substituteMap)
    }

    override fun getVariableStringValue(key: String, vararg substitutes: String): String? {
        return getVariableStringValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), *substitutes)
    }

    override fun getVariableStringValue(key: String, substituteMap: Map<String, Any>): String? {
        return getVariableStringValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), substituteMap)
    }

    override fun variable(vararg keys: String): String? {
        if (keys.size == 0) {
            return ""
        } else if (keys.size == 1) {
            return getVariableStringValue(keys[0])
        } else {
            val substitutes = Array<String>(keys.size-1, { keys[it+1] })

            return getVariableStringValue(keys[0], *substitutes)
        }
    }
}
