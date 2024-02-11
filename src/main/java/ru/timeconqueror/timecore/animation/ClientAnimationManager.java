package ru.timeconqueror.timecore.animation;

import ru.timeconqueror.timecore.api.animation.Clock;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

public class ClientAnimationManager extends BaseAnimationManager {

    public ClientAnimationManager(Clock clock, SharedMolangObject sharedMolangObject) {
        super(clock, sharedMolangObject);
    }
}
