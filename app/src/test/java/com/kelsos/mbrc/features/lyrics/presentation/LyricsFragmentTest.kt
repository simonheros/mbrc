package com.kelsos.mbrc.features.lyrics.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.lyrics.LyricsState
import com.kelsos.mbrc.features.lyrics.LyricsStateImpl
import com.kelsos.mbrc.features.minicontrol.MiniControlViewModel
import com.kelsos.mbrc.utils.MainThreadExecutor
import com.kelsos.mbrc.utils.doesNotExist
import com.kelsos.mbrc.utils.isGone
import com.kelsos.mbrc.utils.isVisible
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class LyricsFragmentTest : KoinTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var viewModel: LyricsViewModel
  private lateinit var lyricsState: LyricsState
  private lateinit var adapter: LyricsAdapter

  @Before
  fun setUp() {
    viewModel = mockk()
    lyricsState = LyricsStateImpl()
    adapter = LyricsAdapter(MainThreadExecutor())

    every { viewModel.lyrics } answers { lyricsState }

    val testModule = module {
      single { viewModel }
      single { adapter }
      single<MiniControlViewModel> { mockk(relaxed = true) }
    }
    startKoin {
      modules(
        listOf(
          testModule
        )
      )
    }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun `should display empty view when no lyrics available`() {
    lyricsState.set(emptyList())

    launchFragmentInContainer<LyricsFragment>()

    onView(withId(R.id.lyrics__empty_text)).isVisible()
  }

  @Test
  fun `should display the lyrics based on the lyrics state`() {
    lyricsState.set(listOf("line one", "line two"))

    launchFragmentInContainer<LyricsFragment>()

    onView(withId(R.id.lyrics__empty_text)).isGone()
    onView(withText("line one")).isVisible()
    onView(withText("line two")).isVisible()
  }

  @Test
  fun `should update the displayed lyrics when the list gets updated`() {

    lyricsState.set(listOf("line one"))

    launchFragmentInContainer<LyricsFragment>()

    onView(withId(R.id.lyrics__empty_text)).isGone()
    onView(withText("line one")).isVisible()
    onView(withText("line two")).doesNotExist()

    lyricsState.set(listOf("line two"))

    onView(withText("line one")).doesNotExist()
    onView(withText("line two")).isVisible()
  }
}
