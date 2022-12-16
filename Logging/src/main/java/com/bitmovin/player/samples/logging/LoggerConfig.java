package com.bitmovin.player.samples.logging;

import androidx.annotation.NonNull;

import com.bitmovin.player.api.event.Event;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.event.SourceEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Configures an {@link EventLogger} and provides an opinionated suggestion on which events should
 * be logged at which level.
 */
public class LoggerConfig<T extends Event> {
    private final EventLogger.Level logLevel;
    private final List<Class<? extends T>> errorEvents;
    private final List<Class<? extends T>> warningEvents;
    private final List<Class<? extends T>> infoEvents;
    private final List<Class<? extends T>> debugEvents;

    LoggerConfig(EventLogger.Level logLevel,
                 List<Class<? extends T>> errorEvents,
                 List<Class<? extends T>> warningEvents,
                 List<Class<? extends T>> infoEvents,
                 List<Class<? extends T>> debugEvents
    ) {
        this.logLevel = logLevel;
        this.errorEvents = errorEvents;
        this.warningEvents = warningEvents;
        this.infoEvents = infoEvents;
        this.debugEvents = debugEvents;
    }

    public EventLogger.Level getLogLevel() {
        return logLevel;
    }

    public List<Class<? extends T>> getErrorEvents() {
        return errorEvents;
    }

    public List<Class<? extends T>> getWarningEvents() {
        return warningEvents;
    }

    public List<Class<? extends T>> getInfoEvents() {
        return infoEvents;
    }

    public List<Class<? extends T>> getDebugEvents() {
        return debugEvents;
    }

    @NonNull
    public static LoggerConfig<Event> createDefaultPlayerLoggerConfig() {
        return new LoggerConfig<>(
                EventLogger.Level.Info,
                Collections.singletonList(PlayerEvent.Error.class),
                Collections.singletonList(PlayerEvent.Warning.class),
                Arrays.asList(
                        PlayerEvent.Info.class,
                        PlayerEvent.Active.class,
                        PlayerEvent.AdBreakFinished.class,
                        PlayerEvent.AdBreakStarted.class,
                        PlayerEvent.AdClicked.class,
                        PlayerEvent.AdError.class,
                        PlayerEvent.AdFinished.class,
                        PlayerEvent.AdLinearityChanged.class,
                        PlayerEvent.AdManifestLoad.class,
                        PlayerEvent.AdManifestLoaded.class,
                        PlayerEvent.AdQuartile.class,
                        PlayerEvent.AdScheduled.class,
                        PlayerEvent.AdSkipped.class,
                        PlayerEvent.AdStarted.class,
                        PlayerEvent.AudioPlaybackQualityChanged.class,
                        PlayerEvent.CastAvailable.class,
                        PlayerEvent.CastStart.class,
                        PlayerEvent.CastStarted.class,
                        PlayerEvent.CastStopped.class,
                        PlayerEvent.CastWaitingForDevice.class,
                        PlayerEvent.CueEnter.class,
                        PlayerEvent.CueExit.class,
                        PlayerEvent.Destroy.class,
                        PlayerEvent.DroppedVideoFrames.class,
                        PlayerEvent.DvrWindowExceeded.class,
                        PlayerEvent.Inactive.class,
                        PlayerEvent.Metadata.class,
                        PlayerEvent.Muted.class,
                        PlayerEvent.Paused.class,
                        PlayerEvent.Play.class,
                        PlayerEvent.PlaybackFinished.class,
                        PlayerEvent.Playing.class,
                        PlayerEvent.PlaylistTransition.class,
                        PlayerEvent.Ready.class,
                        PlayerEvent.RenderFirstFrame.class,
                        PlayerEvent.Seek.class,
                        PlayerEvent.Seeked.class,
                        PlayerEvent.SourceAdded.class,
                        PlayerEvent.SourceRemoved.class,
                        PlayerEvent.StallEnded.class,
                        PlayerEvent.StallStarted.class,
                        PlayerEvent.TimeShift.class,
                        PlayerEvent.TimeShifted.class,
                        PlayerEvent.Unmuted.class,
                        PlayerEvent.VideoPlaybackQualityChanged.class,
                        PlayerEvent.VideoSizeChanged.class
                ),
                Arrays.asList(
                        PlayerEvent.TimeChanged.class,
                        PlayerEvent.CastTimeUpdated.class
                )
        );
    }

    @NonNull
    public static LoggerConfig<SourceEvent> createDefaultSourceLoggerConfig() {
        return new LoggerConfig<>(
                EventLogger.Level.Info,
                Collections.singletonList(SourceEvent.Error.class),
                Collections.singletonList(SourceEvent.Warning.class),
                Arrays.asList(
                        SourceEvent.AudioDownloadQualityChanged.class,
                        SourceEvent.AudioQualitiesChanged.class,
                        SourceEvent.AudioQualityChanged.class,
                        SourceEvent.AudioTrackChanged.class,
                        SourceEvent.AudioTracksChanged.class,
                        SourceEvent.DrmDataParsed.class,
                        SourceEvent.DurationChanged.class,
                        SourceEvent.Load.class,
                        SourceEvent.Loaded.class,
                        SourceEvent.MetadataParsed.class,
                        SourceEvent.SubtitleTrackChanged.class,
                        SourceEvent.SubtitleTracksChanged.class,
                        SourceEvent.Unloaded.class,
                        SourceEvent.VideoDownloadQualityChanged.class,
                        SourceEvent.VideoQualitiesChanged.class,
                        SourceEvent.VideoQualityChanged.class
                ),
                Collections.singletonList(
                        SourceEvent.DownloadFinished.class
                )
        );
    }

    @NonNull
    public static LoggerConfig<Event> createDefaultViewLoggerConfig() {
        return new LoggerConfig<>(
                EventLogger.Level.Info,
                Collections.singletonList(SourceEvent.Error.class),
                Collections.singletonList(SourceEvent.Warning.class),
                Arrays.asList(
                        PlayerEvent.FullscreenDisabled.class,
                        PlayerEvent.FullscreenEnabled.class,
                        PlayerEvent.FullscreenEnter.class,
                        PlayerEvent.FullscreenExit.class,
                        PlayerEvent.PictureInPictureAvailabilityChanged.class,
                        PlayerEvent.PictureInPictureEnter.class,
                        PlayerEvent.PictureInPictureExit.class,
                        PlayerEvent.ScalingModeChanged.class
                ),
                Collections.emptyList()
        );
    }
}
