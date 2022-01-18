package org.briarproject.briar.desktop.ui

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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.BuildData
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun main() = preview(
    "visible" to true,
) {
    if (getBooleanParameter("visible")) {
        AboutDialog(
            onClose = { setBooleanParameter("visible", false) },
        )
    }
}

@Composable
fun AboutDialog(
    onClose: () -> Unit,
) {
    // sizes of the two columns
    val colSizes = listOf(0.3f, 0.7f)

    // format date
    val buildTime = Instant.ofEpochMilli(BuildData.GIT_TIME).atZone(ZoneId.systemDefault()).toLocalDateTime()

    // rows displayed in table
    val lines = listOf(
        i18n("about.copyright") to "The Briar Project",
        i18n("about.license") to "GNU Affero General Public License v3",
        i18n("about.version") to BuildData.VERSION,
        "Git branch" to BuildData.GIT_BRANCH,
        "Git hash" to BuildData.GIT_HASH,
        "Commit time" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(buildTime),
        i18n("about.website") to "https://briarproject.org",
        i18n("about.contact") to "desktop@briarproject.org",
    )

    BriarDialog(onClose = onClose) {
        val box = this
        Column(
            modifier = Modifier.requiredSize(
                box.maxWidth.times(0.8f), box.maxHeight.times(0.8f)
            ).padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                BriarLogo(modifier = Modifier.height(48.dp))
                Text(
                    i18n("main.title"),
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(start = 16.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
            val scrollState = rememberLazyListState()
            Box {
                LazyColumn(state = scrollState) {
                    item {
                        HorizontalDivider()
                    }
                    items(lines) { (key, value) ->
                        // this is required for Divider between Boxes to have appropriate size
                        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                            Box(modifier = Modifier.weight(colSizes[0]).fillMaxHeight()) {
                                Text(
                                    text = key,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                        .padding(vertical = 8.dp).padding(end = 8.dp)
                                        .align(Alignment.CenterStart)
                                )
                            }
                            VerticalDivider()
                            Box(modifier = Modifier.weight(colSizes[1]).fillMaxHeight()) {
                                SelectionContainer {
                                    Text(
                                        text = value,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                            .padding(start = 8.dp)
                                    )
                                }
                            }
                        }
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
}
