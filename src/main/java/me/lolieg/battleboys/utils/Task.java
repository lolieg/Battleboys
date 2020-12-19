package me.lolieg.battleboys.utils;

import java.util.Random;

public enum Task {
    IDLE,
    GATHER,
    QUIZ,
    FIGHT;

    public Task getRandom(){
        Random random = new Random();
        return Task.values()[random.nextInt(Task.values().length)];
    }
}
