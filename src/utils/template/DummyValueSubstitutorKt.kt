package utils.template

import java.math.BigDecimal

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2017-01-06
 * Time: 16:45
 */

/**
 * Same as EmptyValueSubstitutor, this is to enable any class as a dummy value substitutor
 * Useful for function content substituting only classes
 */
interface DummyValueSubstitutorKt : ValueSubstitutorKt {
    override fun getVariableIntegerValue(keys: Array<String>): Int {
        return 0
    }

    override fun getVariableIntegerValue(keys: Array<String>, defaultValue: Int): Int {
        return 0
    }

    override fun getVariableIntegerValue(key: String): Int {
        return 0
    }

    override fun getVariableIntegerValue(key: String, defaultValue: Int): Int {
        return 0
    }

    override fun getVariableLongValue(keys: Array<String>): Long {
        return 0
    }

    override fun getVariableLongValue(keys: Array<String>, defaultValue: Long): Long {
        return 0
    }

    override fun getVariableLongValue(key: String): Long {
        return 0
    }

    override fun getVariableLongValue(key: String, defaultValue: Long): Long {
        return 0
    }

    override fun getVariableBooleanValue(keys: Array<String>): Boolean {
        return false
    }

    override fun getVariableBooleanValue(keys: Array<String>, defaultValue: Boolean): Boolean {
        return false
    }

    override fun getVariableBooleanValue(key: String): Boolean {
        return false
    }

    override fun getVariableBooleanValue(key: String, defaultValue: Boolean): Boolean {
        return false
    }

    override fun getVariableBigDecimalValue(keys: Array<String>): BigDecimal {
        return BigDecimal.ZERO
    }

    override fun getVariableBigDecimalValue(keys: Array<String>, defaultValue: BigDecimal?): BigDecimal {
        return BigDecimal.ZERO
    }

    override fun getVariableBigDecimalValue(key: String): BigDecimal {
        return BigDecimal.ZERO
    }

    override fun getVariableBigDecimalValue(key: String, defaultValue: BigDecimal?): BigDecimal {
        return BigDecimal.ZERO
    }

    override fun <V> getVariableValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): V? {
        return null
    }

    override fun <V> getVariableValue(keys: Array<String>, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
        return null
    }

    override fun <V> getVariableValue(key: String, nullSafe: Boolean, vararg substitutes: String): V? {
        return null
    }

    override fun <V> getVariableValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
        return null
    }

    override fun getVariableStringValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): String? {
        return null
    }

    override fun getVariableStringValue(keys: Array<String>, nullSafe: Boolean, substitutes: Map<String, Any>): String? {
        return null
    }

    override fun getVariableStringValue(key: String, nullSafe: Boolean, vararg substitutes: String): String? {
        return null
    }

    override fun getVariableStringValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): String? {
        return null
    }

    override fun <V> getVariableValue(keys: Array<String>, vararg substitutes: String): V? {
        return null
    }

    override fun <V> getVariableValue(keys: Array<String>, substituteMap: Map<String, Any>): V? {
        return null
    }

    override fun <V> getVariableValue(key: String, vararg substitutes: String): V? {
        return null
    }

    override fun <V> getVariableValue(key: String, substituteMap: Map<String, Any>): V? {
        return null
    }

    override fun getVariableStringValue(keys: Array<String>, vararg substitutes: String): String? {
        return null
    }

    override fun getVariableStringValue(keys: Array<String>, substituteMap: Map<String, Any>): String? {
        return null
    }

    override fun getVariableStringValue(key: String, vararg substitutes: String): String? {
        return null
    }

    override fun getVariableStringValue(key: String, substitutionMap: Map<String, Any>): String? {
        return null
    }

    override fun variable(vararg keys: String): String? {
        return null
    }
}