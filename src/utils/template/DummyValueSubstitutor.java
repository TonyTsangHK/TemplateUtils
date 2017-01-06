package utils.template;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2017-01-06
 * Time: 16:53
 */

/**
 * Same as EmptyValueSubstitutor, this is to enable any class as a dummy value substitutor
 * Useful for function content substituting only classes
 */
public interface DummyValueSubstitutor extends ValueSubstitutor {
    @Override
    default int getVariableIntegerValue(String[] keys) {
        return 0;
    }

    @Override
    default int getVariableIntegerValue(String[] keys, int defaultValue) {
        return 0;
    }

    @Override
    default int getVariableIntegerValue(String key) {
        return 0;
    }

    @Override
    default int getVariableIntegerValue(String key, int defaultValue) {
        return 0;
    }

    @Override
    default long getVariableLongValue(String[] keys) {
        return 0;
    }

    @Override
    default long getVariableLongValue(String[] keys, long defaultValue) {
        return 0;
    }

    @Override
    default long getVariableLongValue(String key) {
        return 0;
    }
    
    @Override
    default long getVariableLongValue(String key, long defaultValue) {
        return 0;
    }

    @Override 
    default BigDecimal getVariableBigDecimalValue(String[] keys) {
        return BigDecimal.ZERO;
    }

    @Override 
    default BigDecimal getVariableBigDecimalValue(String[] keys, BigDecimal defaultValue) {
        return BigDecimal.ZERO;
    }

    @Override 
    default BigDecimal getVariableBigDecimalValue(String key) {
        return BigDecimal.ZERO;
    }

    @Override 
    default BigDecimal getVariableBigDecimalValue(String key, BigDecimal defaultValue) {
        return BigDecimal.ZERO;
    }

    @Override 
    default <V> V getVariableValue(String[] keys, boolean nullSafe, String ... substitutes) {
        return null;
    }

    @Override
    default <V> V getVariableValue(String[] keys, boolean nullSafe, Map<String, Object> substituteMap) {
        return null;
    }

    @Override 
    default <V> V getVariableValue(String key, boolean nullSafe, String ... substitutes) {
        return null;
    }

    @Override
    default <V> V getVariableValue(String key, boolean nullSafe, Map<String, Object> substituteMap) {
        return null;
    }

    @Override 
    default String getVariableStringValue(String[] keys, boolean nullSafe, String ... substitutes) {
        return null;
    }

    @Override 
    default String getVariableStringValue(String[] keys, boolean nullSafe, Map<String, Object> substitutes) {
        return null;
    }

    @Override
    default String getVariableStringValue(String key, boolean nullSafe, String ... substitutes) {
        return null;
    }

    @Override 
    default String getVariableStringValue(String key, boolean nullSafe, Map<String, Object> substituteMap) {
        return null;
    }

    @Override 
    default <V> V getVariableValue(String[] keys, String ... substitutes) {
        return null;
    }

    @Override 
    default <V> V getVariableValue(String[] keys, Map<String, Object> substituteMap) {
        return null;
    }

    @Override 
    default <V> V getVariableValue(String key, String ... substitutes) {
        return null;
    }

    @Override 
    default <V> V getVariableValue(String key, Map<String, Object> substituteMap) {
        return null;
    }

    @Override 
    default String getVariableStringValue(String[] keys, String ... substitutes) {
        return null;
    }

    @Override 
    default String getVariableStringValue(String[] keys, Map<String, Object> substituteMap) {
        return null;
    }

    @Override 
    default String getVariableStringValue(String key, String ... substitutes) {
        return null;
    }

    @Override 
    default String getVariableStringValue(String key, Map<String, Object> substitutionMap) {
        return null;
    }

    @Override 
    default String variable(String ... keys) {
        return null;
    }
}
