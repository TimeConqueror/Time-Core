package ru.timeconqueror.timecore.api.common.advancement.criteria;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public abstract class TimeSimpleTrigger<I, T extends TimeSimpleTrigger.TimeCriterionInstance<I>> implements ICriterionTrigger<T> {
    private final HashMap<PlayerAdvancements, PerPlayerListenerSet<I, T>> listenerSets = new HashMap<>();

    public void trigger(ServerPlayerEntity player, I info) {
        PerPlayerListenerSet<I, T> listenerSet = this.listenerSets.get(player.getAdvancements());

        if (listenerSet != null) {
            listenerSet.trigger(player, info);
        }
    }

    @Override
    public void addPlayerListener(PlayerAdvancements advancements, Listener<T> listener) {
        PerPlayerListenerSet<I, T> listeners = this.listenerSets.get(advancements);

        if (listeners == null) {
            listeners = createListenerSet(advancements);
            this.listenerSets.put(advancements, listeners);
        }

        listeners.add(listener);
    }

    @Override
    public void removePlayerListener(PlayerAdvancements advancements, Listener<T> listener) {
        PerPlayerListenerSet<I, T> listenerSet = listenerSets.get(advancements);
        if (listenerSet != null) {
            listenerSet.remove(listener);
            if (listenerSet.isEmpty()) {
                listenerSets.remove(advancements);
            }
        }
    }

    @Override
    public void removePlayerListeners(PlayerAdvancements advancements) {
        listenerSets.remove(advancements);
    }

    @Override
    public abstract T createInstance(JsonObject json, ConditionArrayParser conditions);

    public abstract PerPlayerListenerSet<I, T> createListenerSet(PlayerAdvancements advancements);

    /**
     * Note: when you extend TimeCriterionInstance,
     * you should specify full path to TimeSimpleTrigger.TimeCriterionInstance to prevent renderToBuffer errors, like here:
     * <blockquote><code>
     * public static class Instance extends <b>TimeSimpleTrigger.</b>TimeCriterionInstance<BlockActivatedInfo>
     * </></>
     *
     * @param <I>
     */
    public abstract static class TimeCriterionInstance<I> extends CriterionInstance {
        public TimeCriterionInstance(ResourceLocation criterionIn, EntityPredicate.AndPredicate playerCondition) {
            super(criterionIn, playerCondition);
        }

        public abstract boolean test(ServerPlayerEntity player, I info);
    }

    protected static class PerPlayerListenerSet<I, T extends TimeSimpleTrigger.TimeCriterionInstance<I>> {
        protected final PlayerAdvancements playerAdvancements;
        protected final Set<Listener<T>> listeners = new HashSet<>();

        public PerPlayerListenerSet(PlayerAdvancements playerAdvancements) {
            this.playerAdvancements = playerAdvancements;
        }

        public void add(ICriterionTrigger.Listener<T> listener) {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<T> listener) {
            this.listeners.remove(listener);
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        void trigger(ServerPlayerEntity player, I info) {
            List<Listener<T>> list = null;

            for (Listener<T> listener : this.listeners) {
                if (listener.getTriggerInstance().test(player, info)) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (ICriterionTrigger.Listener<T> listener1 : list) {
                    listener1.run(this.playerAdvancements);
                }
            }
        }
    }
}
