package moe.seikimo.magixbot.utils;

import moe.seikimo.magixbot.MagixBot;

public interface ThreadUtils {
    /**
     * Sleeps the current thread for the specified amount of time.
     *
     * @param millis The amount of time to sleep in milliseconds.
     */
    static void sleep(double millis) {
        try {
            Thread.sleep(Math.round(millis));
        } catch (InterruptedException ex) {
            MagixBot.getLogger().warn("Thread interrupted!", ex);
        }
    }
}
