package com.kelsos.mbrc.features.settings

import androidx.lifecycle.ViewModel

class SettingsViewModel(private val settings: SettingsManager) : ViewModel() {
  val state = settings.state
}