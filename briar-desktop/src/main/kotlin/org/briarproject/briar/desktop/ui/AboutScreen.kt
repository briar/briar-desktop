package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.BuildData
import org.briarproject.briar.desktop.Strings
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Suppress("HardCodedStringLiteral")
fun main() = preview(
    "visible" to true,
) {
    if (getBooleanParameter("visible")) {
        AboutScreen(
            onBackButton = { setBooleanParameter("visible", false) },
        )
    }
}

@Composable
fun AboutScreen(
    onBackButton: () -> Unit,
) = Box {
    AboutScreen()

    IconButton(
        icon = Icons.Filled.ArrowBack,
        contentDescription = i18n("access.return_to_previous_screen"),
        onClick = onBackButton,
        modifier = Modifier.align(Alignment.TopStart)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AboutScreen(modifier: Modifier = Modifier.padding(16.dp)) {
    // format date
    val buildTime = Instant.ofEpochMilli(BuildData.GIT_TIME).atZone(ZoneId.systemDefault()).toLocalDateTime()

    // rows displayed in table
    val lines = buildList {
        add(Entry(i18n("about.copyright"), Strings.APP_AUTHORS))
        add(Entry(i18n("about.license"), Strings.LICENSE))
        add(Entry(i18n("about.version"), BuildData.VERSION))
        add(Entry(i18n("about.version.core"), BuildData.CORE_VERSION))
        if (BuildData.GIT_BRANCH != null) add(Entry("Git branch", BuildData.GIT_BRANCH)) // NON-NLS
        if (BuildData.GIT_TAG != null) add(Entry("Git tag", BuildData.GIT_TAG)) // NON-NLS
        if (BuildData.GIT_BRANCH == null && BuildData.GIT_TAG == null)
            add(Entry("Git branch/tag", "None detected")) // NON-NLS
        add(Entry("Git hash", BuildData.GIT_HASH)) // NON-NLS
        add(Entry("Commit time", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(buildTime))) // NON-NLS
        add(Entry(i18n("about.website"), Strings.WEBSITE, true))
        add(Entry(i18n("about.contact"), Strings.EMAIL, true))
    }

    Column(modifier) {
        Row(
            modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            BriarLogo(modifier = Modifier.height(48.dp))
            Text(
                Strings.APP_NAME,
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(start = 16.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
        val scrollState = rememberLazyListState()
        Box {
            LazyColumn(
                modifier = Modifier.semantics {
                    contentDescription = i18n("access.about.list")
                },
                state = scrollState
            ) {
                item {
                    HorizontalDivider()
                }
                items(lines) {
                    AboutEntry(it)
                    HorizontalDivider()
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
            )
        }
    }
}

private data class Entry(
    val label: String,
    val value: String,
    val showCopy: Boolean = false
)

// sizes of the two columns
private val colSizes = listOf(0.3f, 0.7f)

@Composable
private fun AboutEntry(entry: Entry) =
    Row(
        Modifier
            .fillMaxWidth()
            // this is required for Divider between Boxes to have appropriate size
            .height(IntrinsicSize.Min)
            .semantics(mergeDescendants = true) {
                // manual text setting can be removed if Compose issue resolved
                // https://github.com/JetBrains/compose-jb/issues/2111
                text = buildAnnotatedString { append("${entry.label}: ${entry.value}") }
            }
    ) {
        Box(modifier = Modifier.weight(colSizes[0]).fillMaxHeight()) {
            Text(
                text = entry.label,
                modifier = Modifier.padding(8.dp).align(Alignment.CenterStart)
            )
        }
        VerticalDivider()
        Box(modifier = Modifier.weight(colSizes[1]).fillMaxHeight()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SelectionContainer {
                    Text(
                        text = entry.value,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                if (entry.showCopy) {
                    val clipboardManager = LocalClipboardManager.current
                    IconButton(
                        icon = Icons.Filled.ContentCopy,
                        contentDescription = i18n("copy"),
                        onClick = {
                            clipboardManager.setText(AnnotatedString(entry.value))
                        }
                    )
                }
            }
        }
    }
