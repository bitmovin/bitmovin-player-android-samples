package com.bitmovin.player.samples.logging

import android.util.Log
import com.bitmovin.player.api.event.Event
import com.bitmovin.player.api.event.EventEmitter
import java.lang.ref.WeakReference
import kotlin.reflect.KClass


/**
 * A generic logger that attaches to all specified [Event]s of an [EventEmitter] and  calls the
 * according log function depending on the configured [LoggerConfig.logLevel].
 *
 * [EventLogger.Level]s are inclusive, meaning that all higher log levels will be logged in addition
 * to the configured log level.
 * E.g. if [EventLogger.Level.Warning] is configured, events in [EventLogger.Level.Warning] will
 * call the [logWarning] function and events in [LoggerConfig.errorEvents] will call the [logError]
 * function.
 *
 * If not configured otherwise, logs will be written to the [android.util.Log] logger with the
 * respective log level.
 *
 * The attached [EventEmitter] is held in a [WeakReference] in order to avoid potential memory
 * leaks even if not properly detached.
 */
class EventLogger<T : Event>(
    private val config: LoggerConfig<T>,
    private val logError: (Event) -> Unit = { Log.e(config.tag, it.toString()) },
    private val logWarning: (Event) -> Unit = { Log.w(config.tag, it.toString()) },
    private val logInfo: (Event) -> Unit = { Log.i(config.tag, it.toString()) },
    private val logDebug: (Event) -> Unit = { Log.d(config.tag, it.toString()) },
) {
    enum class Level { Debug, Info, Warning, Error }

    private var eventEmitterReference: WeakReference<EventEmitter<T>>? = null

    fun attach(eventEmitter: EventEmitter<T>) {
        detach()
        this.eventEmitterReference = WeakReference(eventEmitter)
        when (config.logLevel) {
            Level.Debug -> {
                eventEmitter.onAll(config.errorEvents, logError)
                eventEmitter.onAll(config.warningEvents, logWarning)
                eventEmitter.onAll(config.infoEvents, logInfo)
                eventEmitter.onAll(config.debugEvents, logDebug)
            }
            Level.Info -> {
                eventEmitter.onAll(config.errorEvents, logError)
                eventEmitter.onAll(config.warningEvents, logWarning)
                eventEmitter.onAll(config.infoEvents, logInfo)

            }
            Level.Warning -> {
                eventEmitter.onAll(config.errorEvents, logError)
                eventEmitter.onAll(config.warningEvents, logWarning)

            }
            Level.Error -> {
                eventEmitter.onAll(config.errorEvents, logError)
            }
        }
    }

    fun detach() {
        val eventEmitter = eventEmitterReference?.get() ?: return
        with(eventEmitter) {
            off(logError)
            off(logWarning)
            off(logInfo)
            off(logDebug)
        }
    }

    private fun EventEmitter<T>.onAll(
        events: List<KClass<out Event>>, block: (Event) -> Unit
    ) = events.filterIsInstance<KClass<T>>().forEach { on(it, block) }
}

