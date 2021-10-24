package com.kelsos.mbrc.features.settings

import android.app.Application
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.common.utilities.RemoteUtils.getVersionCode
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.logging.FileLoggingTree
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.time.Instant

class SettingsManagerImpl(
  private val context: Application
) : SettingsManager {

  private val dataStore = context.dataStore

  override val state: Flow<SettingsState>
    get() = dataStore.data.map {
      SettingsState(
        version = BuildConfig.VERSION_NAME,
        revision = BuildConfig.GIT_SHA,
        buildTime = BuildConfig.BUILD_TIME,
        callAction = CallAction.from(it.user.callAction),
        libraryAction = Queue.from(it.user.libraryAction),
        onlyAlbumArtists = it.user.displayAlbumArtist,
        checkPluginUpdate = it.user.updateCheck,
        debugLog = it.user.enableLog
      )
    }

  init {
    runBlocking {
      setupManager()
    }
  }

  private suspend fun setupManager() {
    val settings = dataStore.data.first()
    val loggingEnabled = settings.user.enableLog
    if (loggingEnabled) {
      Timber.plant(FileLoggingTree(this.context.applicationContext))
    } else {
      val fileLoggingTree = Timber.forest().find { it is FileLoggingTree }
      fileLoggingTree?.let { Timber.uproot(it) }
    }
  }

  override suspend fun getCallAction(): CallAction {
    val settings = dataStore.data.first()
    return CallAction.from(settings.user.callAction)
  }

  override suspend fun isPluginUpdateCheckEnabled(): Boolean {
    val settings = dataStore.data.first()
    return settings.user.updateCheck
  }

  override suspend fun getLastUpdated(required: Boolean): Instant {
    val settings = dataStore.data.first()
    val epoch = if (required) {
      settings.app.lastRequiredUpdateCheck
    } else {
      settings.app.lastUpdateCheck
    }
    return Instant.ofEpochMilli(epoch)
  }

  override suspend fun setLastUpdated(lastChecked: Instant, required: Boolean) {
    val epoch = lastChecked.toEpochMilli()
    dataStore.updateData {
      val settings = dataStore.data.first()
      val appBuilder = settings.app.toBuilder()
      val appSettings = if (required) {
        appBuilder.setLastRequiredUpdateCheck(epoch)
      } else {
        appBuilder.setLastUpdateCheck(epoch)
      }
      settings.toBuilder().setApp(appSettings.build()).build()
    }
  }

  override fun onlyAlbumArtists(): Flow<Boolean> {
    return dataStore.data.map { it.user.displayAlbumArtist }
  }

  override suspend fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean) {
    dataStore.updateData {
      val settings = dataStore.data.first()
      val userSettings = settings.user.toBuilder()
        .setDisplayAlbumArtist(onlyAlbumArtist)
        .build()
      settings.toBuilder()
        .setUser(userSettings)
        .build()
    }
  }

  override suspend fun shouldShowChangeLog(): Boolean {
    val settings = dataStore.data.first()
    val appSettings = settings.app
    val lastVersionCode = appSettings.lastRunVersion
    val currentVersion = getVersionCode().toLong()

    if (lastVersionCode < currentVersion) {
      dataStore.updateData {
        val app = appSettings.toBuilder()
          .setLastRunVersion(currentVersion)
          .build()
        settings.toBuilder()
          .setApp(app)
          .build()
      }
      Timber.d("Update or fresh install")
      return true
    }
    return false
  }
}

private fun CallAction.Companion.from(callAction: User.CallAction?): CallAction =
  when (callAction) {
    User.CallAction.PAUSE -> CallAction.Pause
    User.CallAction.STOP -> CallAction.Stop
    User.CallAction.REDUCE -> CallAction.Reduce
    else -> CallAction.None
  }
