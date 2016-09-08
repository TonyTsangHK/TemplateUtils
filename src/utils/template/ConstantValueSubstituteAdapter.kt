package utils.template

import utils.template.constant.ConstantHandler

import java.math.BigDecimal

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-06-04
 * Time: 16:32
 */
abstract class ConstantValueSubstituteAdapter protected constructor(val constantHandler: ConstantHandler) : ValueSubstitutorKt {
    override fun <V> getVariableValue(keys: Array<String>, vararg substitutes: String): V? {
        return constantHandler.getVariableValue<V>(keys, *substitutes)
    }

    override fun <V> getVariableValue(keys: Array<String>, substituteMap: Map<String, Any>): V? {
        return constantHandler.getVariableValue<V>(keys, substituteMap)
    }

    override fun <V> getVariableValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): V? {
        return constantHandler.getVariableValue<V>(keys, nullSafe, *substitutes)
    }

    override fun <V> getVariableValue(keys: Array<String>, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
        return constantHandler.getVariableValue<V>(keys, nullSafe, substituteMap)
    }

    override fun <V> getVariableValue(key: String, nullSafe: Boolean, vararg substitutes: String): V? {
        return constantHandler.getVariableValue<V>(key, nullSafe, *substitutes)
    }

    override fun <V> getVariableValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
        return constantHandler.getVariableValue<V>(key, nullSafe, substituteMap)
    }

    override fun <V> getVariableValue(key: String, vararg substitutes: String): V? {
        return constantHandler.getVariableValue<V>(key, *substitutes)
    }

    override fun <V> getVariableValue(key: String, substituteMap: Map<String, Any>): V? {
        return constantHandler.getVariableValue<V>(key, substituteMap)
    }

    override fun getVariableIntegerValue(keys: Array<String>): Int {
        return constantHandler.getVariableIntegerValue(keys)
    }

    override fun getVariableIntegerValue(keys: Array<String>, defaultValue: Int): Int {
        return constantHandler.getVariableIntegerValue(keys, defaultValue)
    }

    override fun getVariableIntegerValue(key: String): Int {
        return constantHandler.getVariableIntegerValue(key)
    }

    override fun getVariableIntegerValue(key: String, defaultValue: Int): Int {
        return constantHandler.getVariableIntegerValue(key, defaultValue)
    }

    override fun getVariableLongValue(keys: Array<String>): Long {
        return constantHandler.getVariableLongValue(keys)
    }

    override fun getVariableLongValue(keys: Array<String>, defaultValue: Long): Long {
        return constantHandler.getVariableLongValue(keys, defaultValue)
    }

    override fun getVariableLongValue(key: String): Long {
        return constantHandler.getVariableLongValue(key)
    }

    override fun getVariableLongValue(key: String, defaultValue: Long): Long {
        return constantHandler.getVariableLongValue(key, defaultValue)
    }

    override fun getVariableBigDecimalValue(keys: Array<String>): BigDecimal {
        return constantHandler.getVariableBigDecimalValue(keys)
    }

    override fun getVariableBigDecimalValue(keys: Array<String>, defaultValue: BigDecimal): BigDecimal {
        return constantHandler.getVariableBigDecimalValue(keys, defaultValue)
    }

    override fun getVariableBigDecimalValue(key: String): BigDecimal {
        return constantHandler.getVariableBigDecimalValue(key)
    }

    override fun getVariableBigDecimalValue(key: String, defaultValue: BigDecimal): BigDecimal {
        return constantHandler.getVariableBigDecimalValue(key, defaultValue)
    }

    override fun getVariableStringValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): String? {
        return constantHandler.getVariableStringValue(keys, nullSafe, *substitutes)
    }

    override fun getVariableStringValue(keys: Array<String>, nullSafe: Boolean, substitutes: Map<String, Any>): String? {
        return constantHandler.getVariableStringValue(keys, nullSafe, substitutes)
    }

    override fun getVariableStringValue(keys: Array<String>, vararg substitutes: String): String? {
        return constantHandler.getVariableStringValue(keys, *substitutes)
    }

    override fun getVariableStringValue(keys: Array<String>, substituteMap: Map<String, Any>): String? {
        return constantHandler.getVariableStringValue(keys, substituteMap)
    }

    override fun getVariableStringValue(key: String, nullSafe: Boolean, vararg substitutes: String): String? {
        return constantHandler.getVariableStringValue(key, nullSafe, *substitutes)
    }

    override fun getVariableStringValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): String? {
        return constantHandler.getVariableStringValue(key, nullSafe, substituteMap)
    }

    override fun getVariableStringValue(key: String, vararg substitutes: String): String? {
        return constantHandler.getVariableStringValue(key, *substitutes)
    }

    override fun getVariableStringValue(key: String, substitutionMap: Map<String, Any>): String? {
        return constantHandler.getVariableStringValue(key, substitutionMap)
    }

    override fun variable(vararg keys: String): String? {
        return constantHandler.variable(*keys)
    }
}
