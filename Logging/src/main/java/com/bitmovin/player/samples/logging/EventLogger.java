package com.bitmovin.player.samples.logging;

import com.bitmovin.player.api.event.Event;
import com.bitmovin.player.api.event.EventEmitter;
import com.bitmovin.player.api.event.EventListener;

import java.lang.ref.WeakReference;

/**
 * A generic logger that attaches to all specified {@link Event}s of an {@link EventEmitter} and
 * calls the according log listener depending on the configured {@link LoggerConfig#getLogLevel()}.
 * <p>
 * {@link Level}s are inclusive, meaning that all higher log levels will be logged in addition
 * to the configured log level.
 * E.g. if Warning is configured, events in {@link LoggerConfig#getWarningEvents()} will
 * call the {@link EventLogger#warningListener} listener and events in
 * {@link LoggerConfig#getErrorEvents()} will call the {@link EventLogger#errorListener} function.
 * <p>
 * The attached {@link EventEmitter} is held in a {@link WeakReference} in order to avoid
 * potential memory leaks even if not properly detached.
 */
public class EventLogger<T extends Event> {
    private final LoggerConfig<T> loggerConfig;
    private final EventListener<T> errorListener;
    private final EventListener<T> warningListener;
    private final EventListener<T> infoListener;
    private final EventListener<T> debugListener;
    private WeakReference<EventEmitter<T>> eventEmitterReference;

    enum Level {
        Error, Warning, Info, Debug
    }

    EventLogger(LoggerConfig<T> loggerConfig,
                EventListener<T> errorListener,
                EventListener<T> warningListener,
                EventListener<T> infoListener,
                EventListener<T> debugListener
    ) {
        this.loggerConfig = loggerConfig;
        this.errorListener = errorListener;
        this.warningListener = warningListener;
        this.infoListener = infoListener;
        this.debugListener = debugListener;
    }

    public void attach(EventEmitter<T> eventEmitter) {
        this.eventEmitterReference = new WeakReference<>(eventEmitter);

        for (Class<? extends T> event : loggerConfig.getErrorEvents()) {
            emitEvent(eventEmitter, event, errorListener);
        }
        if (loggerConfig.getLogLevel() == Level.Error) return;

        for (Class<? extends T> event : loggerConfig.getWarningEvents()) {
            emitEvent(eventEmitter, event, warningListener);
        }
        if (loggerConfig.getLogLevel() == Level.Warning) return;

        for (Class<? extends T> event : loggerConfig.getInfoEvents()) {
            emitEvent(eventEmitter, event, infoListener);
        }
        if (loggerConfig.getLogLevel() == Level.Info) return;

        for (Class<? extends T> event : loggerConfig.getDebugEvents()) {
            emitEvent(eventEmitter, event, debugListener);
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends T> void emitEvent(
            EventEmitter<T> eventEmitter,
            Class<E> event,
            EventListener<T> listener) {
        // EventListener is a functional interface where T is an input parameter.
        // As such it should be covariant but is not currently declared as such.
        eventEmitter.on(event, (EventListener<E>) listener);
    }

    public void detach() {
        if (eventEmitterReference == null || eventEmitterReference.get() == null) return;

        EventEmitter<T> eventEmitter = this.eventEmitterReference.get();
        eventEmitter.off(errorListener);
        eventEmitter.off(warningListener);
        eventEmitter.off(infoListener);
        eventEmitter.off(debugListener);
    }
}


