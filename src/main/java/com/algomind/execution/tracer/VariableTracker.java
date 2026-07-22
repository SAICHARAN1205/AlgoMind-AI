package com.algomind.execution.tracer;

import java.util.HashMap;
import java.util.Map;

public class VariableTracker {
    private final Map<String, Integer> variables = new HashMap<>();
    private int[] array;

    public VariableTracker(int[] initialArray) {
        if (initialArray != null) {
            this.array = initialArray.clone();
        }
    }

    public void setVariable(String name, int value) {
        variables.put(name, value);
    }

    public int getVariable(String name) {
        return variables.getOrDefault(name, 0); // Default to 0 if not found, though ideally should throw error
    }

    public void setArrayElement(int index, int value) {
        if (array != null && index >= 0 && index < array.length) {
            array[index] = value;
        } else {
            throw new ArrayIndexOutOfBoundsException("You tried accessing an index outside the array size: " + index);
        }
    }

    public int getArrayElement(int index) {
        if (array != null && index >= 0 && index < array.length) {
            return array[index];
        } else {
            throw new ArrayIndexOutOfBoundsException("You tried accessing an index outside the array size: " + index);
        }
    }

    public int getArrayLength() {
        return array == null ? 0 : array.length;
    }

    public Map<String, Integer> getVariablesSnapshot() {
        return new HashMap<>(variables);
    }

    public int[] getArraySnapshot() {
        return array == null ? null : array.clone();
    }
}
