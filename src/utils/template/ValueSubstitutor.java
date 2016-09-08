package utils.template;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2012-12-08
 * Time: 11:15
 */
// No plan to convert to kotlin
// Array<String> can't be represented in java
// Converting this interface would require all the subclass to be converted
public interface ValueSubstitutor {
    <V> V getVariableValue(String[] keys, String... substitutes);
    <V> V getVariableValue(String[] keys, Map<String, Object> substituteMap);
    <V> V getVariableValue(String[] keys, boolean nullSafe, String... substitutes);
    <V> V getVariableValue(String[] keys, boolean nullSafe, Map<String, Object> substituteMap);
    <V> V getVariableValue(String key, boolean nullSafe, String... substitutes);
    <V> V getVariableValue(String key, boolean nullSafe, Map<String, Object> substituteMap);
    <V> V getVariableValue(String key, String... substitutes);
    <V> V getVariableValue(String key, Map<String, Object> substituteMap);
    int getVariableIntegerValue(String[] keys);
    int getVariableIntegerValue(String[] keys, int defaultValue);
    int getVariableIntegerValue(String key);
    int getVariableIntegerValue(String key, int defaultValue);
    long getVariableLongValue(String[] keys);
    long getVariableLongValue(String[] keys, long defaultValue);
    long getVariableLongValue(String key);
    long getVariableLongValue(String key, long defaultValue);
    BigDecimal getVariableBigDecimalValue(String[] keys);
    BigDecimal getVariableBigDecimalValue(String[] keys, BigDecimal defaultValue);
    BigDecimal getVariableBigDecimalValue(String key);
    BigDecimal getVariableBigDecimalValue(String key, BigDecimal defaultValue);
    String getVariableStringValue(String[] keys, boolean nullSafe, String... substitutes);
    String getVariableStringValue(String[] keys, boolean nullSafe, Map<String, Object> substitutes);
    String getVariableStringValue(String[] keys, String... substitutes);
    String getVariableStringValue(String[] keys, Map<String, Object> substituteMap);
    String getVariableStringValue(String key, boolean nullSafe, String... substitutes);
    String getVariableStringValue(String key, boolean nullSafe, Map<String, Object> substituteMap);
    String getVariableStringValue(String key, String... substitutes);
    String getVariableStringValue(String key, Map<String, Object> substitutionMap);
    String variable(String... keys);
}
