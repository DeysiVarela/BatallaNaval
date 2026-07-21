package com.deysi.battleship.service;

import com.deysi.battleship.model.GameState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Background autosave timer.
 */
public class AutoSaveService {

    private final PersistenceService persistenceService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Creates autosave service bound to a persistence implementation.
     *
     * @param persistenceService persistence backend
     */
    public AutoSaveService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * Starts periodic autosave every 20 seconds.
     *
     * @param stateSupplier game state provider
     */
    public void start(Supplier<GameState> stateSupplier) {
        scheduler.scheduleAtFixedRate(() -> {
            GameState state = stateSupplier.get();
            if (state != null) {
                persistenceService.save(state);
            }
        }, 20, 20, TimeUnit.SECONDS);
    }
}
