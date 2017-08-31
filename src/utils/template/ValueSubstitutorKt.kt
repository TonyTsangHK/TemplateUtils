package utils.template

import java.math.BigDecimal

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2016-09-07
 * Time: 17:35
 */
// Kotlin implementation of ValueSubstitutor
// Extends this class for kotlin subclasses, otherwise use ValueSubsitutor instead
interface ValueSubstitutorKt: ValueSubstitutor {
    override fun <V> getVariableValue(keys: Array<String>, vararg substitutes: String): V?
    override fun <V> getVariableValue(keys: Array<String>, substituteMap: Map<String, Any>): V?
    override fun <V> getVariableValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): V?
    override fun <V> getVariableValue(keys: Array<String>, nullSafe: Boolean, substituteMap: Map<String, Any>): V?
    override fun <V> getVariableValue(key: String, nullSafe: Boolean, vararg substitutes: String): V?
    override fun <V> getVariableValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): V?
    override fun <V> getVariableValue(key: String, vararg substitutes: String): V?
    override fun <V> getVariableValue(key: String, substituteMap: Map<String, Any>): V?
    override fun getVariableIntegerValue(keys: Array<String>): Int
    override fun getVariableIntegerValue(keys: Array<String>, defaultValue: Int): Int
    override fun getVariableIntegerValue(key: String): Int
    override fun getVariableIntegerValue(key: String, defaultValue: Int): Int
    override fun getVariableLongValue(keys: Array<String>): Long
    override fun getVariableLongValue(keys: Array<String>, defaultValue: Long): Long
    override fun getVariableLongValue(key: String): Long
    override fun getVariableLongValue(key: String, defaultValue: Long): Long
    override fun getVariableBooleanValue(keys: Array<String>): Boolean
    override fun getVariableBooleanValue(keys: Array<String>, defaultValue: Boolean): Boolean
    override fun getVariableBooleanValue(key: String): Boolean
    override fun getVariableBooleanValue(key: String, defaultValue: Boolean): Boolean
    override fun getVariableBigDecimalValue(keys: Array<String>): BigDecimal?
    override fun getVariableBigDecimalValue(keys: Array<String>, defaultValue: BigDecimal?): BigDecimal?
    override fun getVariableBigDecimalValue(key: String): BigDecimal?
    override fun getVariableBigDecimalValue(key: String, defaultValue: BigDecimal?): BigDecimal?
    override fun getVariableStringValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): String?
    override fun getVariableStringValue(keys: Array<String>, nullSafe: Boolean, substitutes: Map<String, Any>): String?
    override fun getVariableStringValue(keys: Array<String>, vararg substitutes: String): String?
    override fun getVariableStringValue(keys: Array<String>, substituteMap: Map<String, Any>): String?
    override fun getVariableStringValue(key: String, nullSafe: Boolean, vararg substitutes: String): String?
    override fun getVariableStringValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): String?
    override fun getVariableStringValue(key: String, vararg substitutes: String): String?
    override fun getVariableStringValue(key: String, substitutionMap: Map<String, Any>): String?
    override fun variable(vararg keys: String): String?
}