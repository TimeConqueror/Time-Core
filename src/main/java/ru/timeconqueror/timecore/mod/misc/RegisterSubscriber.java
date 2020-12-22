package ru.timeconqueror.timecore.mod.misc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraftforge.eventbus.api.IEventBus;
import ru.timeconqueror.timecore.registry.IOrderedRegister;
import ru.timeconqueror.timecore.registry.newreg.TimeRegister;

import java.util.*;

public class RegisterSubscriber {
    public static void regToBus(List<TimeRegister> registers, IEventBus modEventBus) {
        Multimap<TimeRegister, TimeRegister> children = ArrayListMultimap.create();
        Map<TimeRegister, Integer> remainingDeps = new HashMap<>();

        Queue<TimeRegister> independents = new ArrayDeque<>();
        for (TimeRegister register : registers) {
            int depCount = 0;

            if (register instanceof IOrderedRegister) {
                for (Class<?> dependency : ((IOrderedRegister) register).getDependencies()) {
                    for (TimeRegister possibleParent : registers) {
                        if (dependency.isAssignableFrom(possibleParent.getClass())) {
                            children.put(possibleParent, register);
                            depCount++;
                        }
                    }
                }
            }

            remainingDeps.put(register, depCount);

            if (depCount == 0) {
                independents.add(register);
            }
        }

        while (!independents.isEmpty()) {
            TimeRegister register = independents.poll();
            register.regToBus(modEventBus);

            remainingDeps.remove(register);

            for (TimeRegister child : children.get(register)) {
                int depCount = remainingDeps.get(child) - 1;
                remainingDeps.put(child, depCount);

                if (depCount == 0) {
                    independents.add(child);
                }
            }
        }

        if (!remainingDeps.isEmpty()) {

            for (TimeRegister parent : remainingDeps.keySet()) {
                Deque<TimeRegister> path = new ArrayDeque<>();
                findCycles(parent, children, path);
            }
        }

    }

    private static void findCycles(TimeRegister parent, Multimap<TimeRegister, TimeRegister> children, Deque<TimeRegister> path) {
        if (path.contains(parent)) {
            StringBuilder builder = new StringBuilder();
            builder.append(parent.getClass().getName());
            while (!path.isEmpty()) {
                TimeRegister last = path.removeLast();
                builder.append(" -> ").append(last.getClass().getName());

                if (last == parent) break;
            }

            throw new IllegalStateException("Found cycle in register dependencies:\n" + builder);
        }

        path.addLast(parent);

        for (TimeRegister child : children.get(parent)) {
            findCycles(child, children, path);
        }

        path.removeLast();
    }
}
