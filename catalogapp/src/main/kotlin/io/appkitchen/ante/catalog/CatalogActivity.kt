package io.appkitchen.ante.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.appkitchen.ante.core.designsystem.samples.TokenSample
import io.appkitchen.ante.core.designsystem.theme.AnteTheme

/**
 * The design system catalog.
 *
 * Renders the samples from `:core:designsystem` and nothing else. There are deliberately no
 * interactive knobs beyond the light/dark toggle: a knob lets the catalog show a state that no
 * screenshot covers, and once catalog output and golden output can diverge, neither one is evidence
 * about the other.
 */
class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CatalogApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogApp() {
    // null means follow the system; a non-null value pins the theme for this session.
    var pinnedDark by remember { mutableStateOf<Boolean?>(null) }
    val systemDark = isSystemInDarkTheme()
    val darkTheme = pinnedDark ?: systemDark

    AnteTheme(darkTheme = darkTheme) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        TextButton(onClick = { pinnedDark = !darkTheme }) {
                            val label = if (darkTheme) R.string.theme_dark else R.string.theme_light
                            Text(stringResource(label))
                        }
                    },
                )
            },
        ) { contentPadding ->
            TokenSample(
                modifier = Modifier.padding(contentPadding).verticalScroll(rememberScrollState())
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TokenSamplePreview() {
    AnteTheme { TokenSample() }
}

@Preview(showBackground = true)
@Composable
private fun TokenSampleDarkPreview() {
    AnteTheme(darkTheme = true) { TokenSample() }
}
