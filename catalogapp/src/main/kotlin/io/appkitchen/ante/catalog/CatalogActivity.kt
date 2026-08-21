package io.appkitchen.ante.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.appkitchen.ante.core.designsystem.icons.AnteIcons
import io.appkitchen.ante.core.designsystem.samples.AnteSamples
import io.appkitchen.ante.core.designsystem.samples.ComponentSample
import io.appkitchen.ante.core.designsystem.samples.Render
import io.appkitchen.ante.core.designsystem.samples.TokenSample
import io.appkitchen.ante.core.designsystem.theme.AnteTheme

/**
 * The design system catalog.
 *
 * Renders the samples from `:core:designsystem` and nothing else. There are deliberately no
 * interactive knobs beyond the light/dark toggle: a knob lets the catalog show a state that no
 * screenshot covers, and once catalog output and golden output can diverge, neither one is evidence
 * about the other. Every component page is the component's screenshot frames, in order, rendered by
 * the same function the screenshot test captures.
 *
 * Navigation is a single saveable string rather than a navigation library: two levels, one back
 * edge, and the catalog's only project dependency stays the design system.
 */
class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CatalogApp() }
    }
}

/** The token page; the one non-component page. Not a component id, so it cannot collide. */
private const val TOKENS_PAGE = "tokens"

@Composable
private fun CatalogApp() {
    // null means follow the system; a non-null value pins the theme for this session.
    var pinnedDark by remember { mutableStateOf<Boolean?>(null) }
    val systemDark = isSystemInDarkTheme()
    val darkTheme = pinnedDark ?: systemDark

    // null is the list; otherwise TOKENS_PAGE or a ComponentSample id.
    var page by rememberSaveable { mutableStateOf<String?>(null) }
    BackHandler(enabled = page != null) { page = null }

    AnteTheme(darkTheme = darkTheme) {
        val current = page
        val component = AnteSamples.components.firstOrNull { it.id == current }
        when {
            current == null ->
                ListPage(
                    darkTheme = darkTheme,
                    onToggleTheme = { pinnedDark = !darkTheme },
                    onOpen = { page = it },
                )
            current == TOKENS_PAGE ->
                DetailPage(
                    title = stringResource(R.string.page_tokens),
                    darkTheme = darkTheme,
                    onToggleTheme = { pinnedDark = !darkTheme },
                    onBack = { page = null },
                ) {
                    TokenSample(modifier = Modifier.verticalScroll(rememberScrollState()))
                }
            component != null ->
                DetailPage(
                    title = component.title,
                    darkTheme = darkTheme,
                    onToggleTheme = { pinnedDark = !darkTheme },
                    onBack = { page = null },
                ) {
                    ComponentPage(component)
                }
            // A stale id restored from saved state that no longer names a component: show the
            // list rather than a blank page. Back clears the stale id.
            else ->
                ListPage(
                    darkTheme = darkTheme,
                    onToggleTheme = { pinnedDark = !darkTheme },
                    onOpen = { page = it },
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListPage(darkTheme: Boolean, onToggleTheme: () -> Unit, onOpen: (String) -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = { ThemeToggle(darkTheme, onToggleTheme) },
            )
        },
    ) { contentPadding ->
        LazyColumn(contentPadding = contentPadding) {
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.page_tokens)) },
                    trailingContent = { Icon(AnteIcons.ChevronRight, contentDescription = null) },
                    modifier = Modifier.clickable { onOpen(TOKENS_PAGE) },
                )
                HorizontalDivider()
            }
            items(AnteSamples.components, key = { it.id }) { component ->
                ListItem(
                    headlineContent = { Text(component.title) },
                    supportingContent = {
                        Text(
                            stringResource(
                                R.string.frame_count,
                                component.frames.size,
                                component.captureCount,
                            )
                        )
                    },
                    trailingContent = { Icon(AnteIcons.ChevronRight, contentDescription = null) },
                    modifier = Modifier.clickable { onOpen(component.id) },
                )
                HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailPage(
    title: String,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            AnteIcons.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                actions = { ThemeToggle(darkTheme, onToggleTheme) },
            )
        },
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) { content() }
    }
}

/**
 * A component's frames, top to bottom in spec order. Each frame is labelled with its golden name so
 * a catalog screen and a failing golden can be matched by eye.
 */
@Composable
private fun ComponentPage(component: ComponentSample) {
    LazyColumn {
        items(component.frames, key = { it.name }) { frame ->
            Text(
                text = frame.name,
                style = AnteTheme.typography.labelMedium,
                color = AnteTheme.colorScheme.onSurfaceVariant,
                modifier =
                    Modifier.padding(
                        start = AnteTheme.spacing.screenHorizontal,
                        end = AnteTheme.spacing.screenHorizontal,
                        top = AnteTheme.spacing.lg,
                    ),
            )
            frame.Render()
            HorizontalDivider()
        }
    }
}

@Composable
private fun ThemeToggle(darkTheme: Boolean, onToggle: () -> Unit) {
    TextButton(onClick = onToggle) {
        val label = if (darkTheme) R.string.theme_dark else R.string.theme_light
        Text(stringResource(label))
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
