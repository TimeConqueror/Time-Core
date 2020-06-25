package ru.timeconqueror.timecore.animation.entityai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.profiler.IProfiler;
import ru.timeconqueror.timecore.api.animation.StateMachine;

public class TimeGoalSelector extends GoalSelector {
    private final StateMachine<?> stateMachine;

    public TimeGoalSelector(IProfiler profiler, StateMachine<?> stateMachine) {
        super(profiler);
        this.stateMachine = stateMachine;
    }

    //some copypaste from common GoalSelector with some changes
    @Override
    public void tick() {
        this.profiler.startSection("goalCleanup");
        this.getRunningGoals().filter((runningGoal) -> !runningGoal.isRunning()
                || runningGoal.getMutexFlags().stream().anyMatch(this.disabledFlags::contains)
                || !runningGoal.shouldContinueExecuting())
                .forEach(Goal::resetTask);
        this.flagGoals.forEach((flag, goal) -> {
            if (!goal.isRunning()) {
                this.flagGoals.remove(flag);
            }

        });
        this.profiler.endSection();
        this.profiler.startSection("goalUpdate");
        this.goals.stream()
                .filter((goal) -> !goal.isRunning())
                .filter((goal) -> goal.getMutexFlags().stream()
                        .noneMatch(this.disabledFlags::contains))
                .filter((goal) -> goal.getMutexFlags().stream()
                        .allMatch((flag) -> this.flagGoals.getOrDefault(flag, DUMMY).isPreemptedBy(goal)))
                .filter(PrioritizedGoal::shouldExecute)
                .forEach((goal) -> {
                    goal.getMutexFlags().forEach((flag) -> {
                        PrioritizedGoal prioritizedgoal = this.flagGoals.getOrDefault(flag, DUMMY);
                        prioritizedgoal.resetTask();
                        this.flagGoals.put(flag, goal);
                    });
                    goal.startExecuting();
                });
        this.profiler.endSection();
        this.profiler.startSection("goalTick");
        this.getRunningGoals().forEach(PrioritizedGoal::tick);
        this.profiler.endSection();
    }
}
