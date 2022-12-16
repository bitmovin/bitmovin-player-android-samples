package com.bitmovin.player.samples.logging

import com.bitmovin.player.api.event.Event
import com.bitmovin.player.api.event.OfflineEvent
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import kotlin.reflect.KClass

/**
 * Configures an [EventLogger] and provides an opinionated suggestion on which events should be
 * logged at which level.
 */
sealed interface LoggerConfig<T : Event> {
    val logLevel: EventLogger.Level
    val tag: String
    val errorEvents: List<KClass<out T>>
    val warningEvents: List<KClass<out T>>
    val infoEvents: List<KClass<out T>>
    val debugEvents: List<KClass<out T>>

    data class SourceLoggerConfig(
        override val logLevel: EventLogger.Level = EventLogger.Level.Info,
        override val tag: String = "BitmovinSource",
        override val errorEvents: List<KClass<out SourceEvent>> = listOf(
            SourceEvent.Error::class
        ),
        override val warningEvents: List<KClass<out SourceEvent>> = listOf(
            SourceEvent.Warning::class
        ),
        override val infoEvents: List<KClass<out SourceEvent>> = listOf(
            SourceEvent.AudioDownloadQualityChanged::class,
            SourceEvent.AudioQualitiesChanged::class,
            SourceEvent.AudioQualityChanged::class,
            SourceEvent.AudioTrackChanged::class,
            SourceEvent.AudioTracksChanged::class,
            SourceEvent.DrmDataParsed::class,
            SourceEvent.DurationChanged::class,
            SourceEvent.Load::class,
            SourceEvent.Loaded::class,
            SourceEvent.MetadataParsed::class,
            SourceEvent.SubtitleTrackChanged::class,
            SourceEvent.SubtitleTracksChanged::class,
            SourceEvent.Unloaded::class,
            SourceEvent.VideoDownloadQualityChanged::class,
            SourceEvent.VideoQualitiesChanged::class,
            SourceEvent.VideoQualityChanged::class,
        ),
        override val debugEvents: List<KClass<out SourceEvent>> = listOf(
            SourceEvent.DownloadFinished::class,
        ),
    ) : LoggerConfig<SourceEvent>

    data class PlayerLoggerConfig(
        override val logLevel: EventLogger.Level = EventLogger.Level.Info,
        override val tag: String = "BitmovinPlayer",
        override val errorEvents: List<KClass<out PlayerEvent>> = listOf(
            PlayerEvent.Error::class
        ),
        override val warningEvents: List<KClass<out PlayerEvent>> = listOf(
            PlayerEvent.Warning::class
        ),
        override val infoEvents: List<KClass<out PlayerEvent>> = listOf(
            PlayerEvent.Info::class,
            PlayerEvent.Active::class,
            PlayerEvent.AdBreakFinished::class,
            PlayerEvent.AdBreakStarted::class,
            PlayerEvent.AdClicked::class,
            PlayerEvent.AdError::class,
            PlayerEvent.AdFinished::class,
            PlayerEvent.AdLinearityChanged::class,
            PlayerEvent.AdManifestLoad::class,
            PlayerEvent.AdManifestLoaded::class,
            PlayerEvent.AdQuartile::class,
            PlayerEvent.AdScheduled::class,
            PlayerEvent.AdSkipped::class,
            PlayerEvent.AdStarted::class,
            PlayerEvent.AudioPlaybackQualityChanged::class,
            PlayerEvent.CastAvailable::class,
            PlayerEvent.CastStart::class,
            PlayerEvent.CastStarted::class,
            PlayerEvent.CastStopped::class,
            PlayerEvent.CastWaitingForDevice::class,
            PlayerEvent.CueEnter::class,
            PlayerEvent.CueExit::class,
            PlayerEvent.Destroy::class,
            PlayerEvent.DroppedVideoFrames::class,
            PlayerEvent.DvrWindowExceeded::class,
            PlayerEvent.Inactive::class,
            PlayerEvent.Metadata::class,
            PlayerEvent.Muted::class,
            PlayerEvent.Paused::class,
            PlayerEvent.Play::class,
            PlayerEvent.PlaybackFinished::class,
            PlayerEvent.Playing::class,
            PlayerEvent.PlaylistTransition::class,
            PlayerEvent.Ready::class,
            PlayerEvent.RenderFirstFrame::class,
            PlayerEvent.Seek::class,
            PlayerEvent.Seeked::class,
            PlayerEvent.SourceAdded::class,
            PlayerEvent.SourceRemoved::class,
            PlayerEvent.StallEnded::class,
            PlayerEvent.StallStarted::class,
            PlayerEvent.TimeShift::class,
            PlayerEvent.TimeShifted::class,
            PlayerEvent.Unmuted::class,
            PlayerEvent.VideoPlaybackQualityChanged::class,
            PlayerEvent.VideoSizeChanged::class
        ),
        override val debugEvents: List<KClass<out Event>> = listOf(
            PlayerEvent.TimeChanged::class,
            PlayerEvent.CastTimeUpdated::class
        ),
    ) : LoggerConfig<Event>

    data class ViewLoggerConfig(
        override val logLevel: EventLogger.Level = EventLogger.Level.Info,
        override val tag: String = "BitmovinPlayerView",
        override val errorEvents: List<KClass<out PlayerEvent>> = listOf(
            PlayerEvent.Error::class
        ),
        override val warningEvents: List<KClass<out PlayerEvent>> = listOf(
            PlayerEvent.Warning::class
        ),
        override val infoEvents: List<KClass<out PlayerEvent>> = listOf(
            PlayerEvent.FullscreenDisabled::class,
            PlayerEvent.FullscreenEnabled::class,
            PlayerEvent.FullscreenEnter::class,
            PlayerEvent.FullscreenExit::class,
            PlayerEvent.PictureInPictureAvailabilityChanged::class,
            PlayerEvent.PictureInPictureEnter::class,
            PlayerEvent.PictureInPictureExit::class,
            PlayerEvent.ScalingModeChanged::class,
        ),
        override val debugEvents: List<KClass<out PlayerEvent>> = emptyList(),
    ) : LoggerConfig<Event>

    data class OfflineLoggerConfig(
        override val logLevel: EventLogger.Level = EventLogger.Level.Info,
        override val tag: String = "BitmovinOffline",
        override val errorEvents: List<KClass<out OfflineEvent>> = listOf(
            OfflineEvent.Error::class
        ),
        override val warningEvents: List<KClass<out OfflineEvent>> = listOf(
            OfflineEvent.Warning::class
        ),
        override val infoEvents: List<KClass<out OfflineEvent>> = listOf(
            OfflineEvent.Info::class
        ),
        override val debugEvents: List<KClass<out OfflineEvent>> = emptyList(),
    ) : LoggerConfig<OfflineEvent>
}
