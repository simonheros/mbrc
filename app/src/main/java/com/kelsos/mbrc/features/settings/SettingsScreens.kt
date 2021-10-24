package com.kelsos.mbrc.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.theme.Accent
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.getViewModel

@Composable
private fun Header(modifier: Modifier = Modifier, text: String) {
  Row(modifier = modifier.fillMaxWidth()) {
    Text(text = text, color = Accent, style = MaterialTheme.typography.subtitle2)
  }
}

@Composable
private fun Category(text: String, content: @Composable ColumnScope.() -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 18.dp, horizontal = 16.dp)
  ) {
    Header(modifier = Modifier.padding(vertical = 8.dp), text = text)
    content()
  }
}

@Composable
private fun SettingButton(
  onClick: () -> Unit,
  end: @Composable (ColumnScope.() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  TextButton(
    onClick = onClick, modifier = Modifier
      .padding(vertical = 8.dp)
      .defaultMinSize(minHeight = 48.dp)
      .fillMaxWidth()
  ) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
      content()
    }
    if (end != null) {
      Column(horizontalAlignment = Alignment.End) {
        end()
      }
    }
  }
}

@Composable
private fun Setting(text: String, onClick: () -> Unit) {
  SettingButton(onClick) {
    Text(
      text = text,
      color = MaterialTheme.colors.onSurface
    )
  }
}

@Composable
private fun SettingWithSummary(
  text: String,
  summary: String,
  onClick: () -> Unit,
  end: @Composable() (ColumnScope.() -> Unit)? = null
) {
  SettingButton(onClick, end = end) {
    Text(
      text = text,
      color = MaterialTheme.colors.onSurface
    )
    Text(
      text = summary,
      style = MaterialTheme.typography.caption,
      color = MaterialTheme.colors.onSurface
    )
  }
}

@Composable
fun SettingsScreen(vm: SettingsViewModel = getViewModel()) {
  val uiState by vm.state.collectAsState(initial = SettingsState.default())
  SettingsScreen(uiState)
}

@Composable
private fun SettingsScreen(state: SettingsState) = Surface {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
  ) {
    RemoteTopAppBar(openDrawer = { /*TODO*/ })
    Category(text = stringResource(id = R.string.settings_connection)) {
      ManageConnections()
    }
    Divider()
    Category(text = stringResource(id = R.string.settings_miscellaneous)) {
      CallActionSetting()
      PluginCheckSetting()
      DebugLoggingSetting()
      LibraryActionSetting()
    }
    Category(text = stringResource(id = R.string.settings_about)) {
      OpenSourceLicenses()
      License()
      Version(state.version)
      BuildTime(state.buildTime)
      Revision(state.revision)
    }
  }

}

@Composable
private fun Revision(revision: String) {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_about_revision),
    summary = revision
  ) {

  }
}

@Composable
private fun BuildTime(buildTime: String) {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_about_build_time),
    summary = buildTime
  ) {

  }
}

@Composable
private fun Version(version: String) {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_about_version),
    summary = version
  ) {

  }
}

@Composable
private fun License() {
  Setting(text = stringResource(id = R.string.settings_about_license)) {

  }
}

@Composable
private fun OpenSourceLicenses() {
  Setting(text = stringResource(id = R.string.settings_about_oss_license)) {

  }
}

@Composable
private fun LibraryActionSetting() {
  SettingWithSummary(text = stringResource(id = R.string.settings_misc_library_default_title),
    summary = stringResource(
      id = R.string.settings_misc_library_default_description
    ),
    onClick = { /*TODO*/ })
}

@Composable
fun DebugLoggingSetting() {
  SettingWithSummary(text = stringResource(id = R.string.settings_misc_debug_title),
    summary = stringResource(
      id = R.string.settings_misc_debug_description
    ),
    onClick = { /*TODO*/ }) {
    Checkbox(checked = false, onCheckedChange = {})
  }
}

@Composable
private fun ManageConnections() {
  Setting(text = stringResource(id = R.string.settings_manage_connections)) {

  }
}

@Composable
private fun CallActionSetting() {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_misc_call_action_title),
    summary = stringResource(id = R.string.settings_misc_call_action_description),
    onClick = {}
  )
}

@Composable
private fun PluginCheckSetting() {
  SettingWithSummary(
    text = stringResource(id = R.string.settings_misc_plugin_updates_title),
    summary = stringResource(id = R.string.settings_misc_plugin_updates_description),
    onClick = { /*TODO*/ }) {
    Checkbox(checked = false, onCheckedChange = {})
  }
}

@Preview
@Composable
private fun HeaderPreview() {
  RemoteTheme {
    Header(Modifier, "Header")
  }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
  RemoteTheme {
    SettingsScreen()
  }
}