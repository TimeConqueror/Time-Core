package ru.timeconqueror.timecore.api.auxiliary.debug;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Profiler {
    private Map<String, List<Long>> profilingMap;

    public Profiler() {
        profilingMap = new HashMap<>();
    }

    public String[] getIdentifiers() {
        String[] tValues = new String[profilingMap.size()];
        profilingMap.keySet().toArray(tValues);
        return tValues;
    }

    /**
     * Add a new time to the list of identifiers.
     * Will be ignored if {@code totalTime} == 0
     */
    public void addTimeToList(String identifier, long totalTime) {
        try {
            if (totalTime == 0) {
                return;
            }

            if (!profilingMap.containsKey(identifier)) {
                profilingMap.put(identifier, new LinkedList<>());
            }

            LinkedList<Long> list = (LinkedList<Long>) profilingMap.get(identifier);

            list.addLast(totalTime);

            while (list.size() > 50) {
                list.removeFirst();
            }
        } catch (Exception e) {
            // Just do nothing. Profiling is for debug purposes only anyways...
        }
    }

    /**
     * Return the average time by identifier.
     */
    public long getAverageTime(String identifier) {
        try {
            if (!profilingMap.containsKey(identifier)) {
                return -1;
            }

            int sum = 0;
            List<Long> list = profilingMap.get(identifier);
            for (long time : list) {
                sum += time;
            }

            return sum / Math.max(1, list.size());
        } catch (Exception e) {
            return -1;
        }
    }
}
