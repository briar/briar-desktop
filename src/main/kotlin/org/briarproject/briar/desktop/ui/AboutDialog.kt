package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import org.briarproject.briar.desktop.BuildData
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
    val lines = buildList {
        add(Pair("Copyright", "The Briar Project"))
        add(Pair("License", "GNU Affero General Public License v3"))
        add(Pair("Version", BuildData.VERSION))
        add(Pair("Git branch", BuildData.GIT_BRANCH))
        add(Pair("Git hash", BuildData.GIT_HASH))
        add(Pair("Commit time", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(buildTime)))
    }

    BriarDialog(onClose = onClose) {
        val box = this
        Column(
            modifier = Modifier.requiredSize(
                box.maxWidth.times(0.8f), box.maxHeight.times(0.8f)
            ).padding(16.dp)
        ) {
            Row(modifier = Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                val minSize = 20.dp
                BriarLogo(modifier = Modifier.heightIn(minSize, max(minSize, box.maxHeight.times(0.1f))))
                Text("Briar Desktop", style = MaterialTheme.typography.h4, modifier = Modifier.padding(start = 16.dp))
            }
            val scrollState = rememberLazyListState()
            Box {
                LazyColumn(state = scrollState) {
                    item {
                        Divider(modifier = Modifier.fillMaxWidth().height(1.dp))
                    }
                    items(lines) { (key, value) ->
                        // this is required for Divider between Boxes to have appropriate size
                        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                            Box(modifier = Modifier.weight(colSizes[0]).fillMaxHeight()) {
                                Text(
                                    text = key,
                                    modifier = Modifier.padding(horizontal = 8.dp).padding(end = 8.dp)
                                        .align(Alignment.CenterStart)
                                )
                            }
                            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))
                            Box(modifier = Modifier.weight(colSizes[1]).fillMaxHeight()) {
                                TextField(
                                    value = value,
                                    onValueChange = {},
                                    readOnly = true,
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        Divider(modifier = Modifier.fillMaxWidth().height(1.dp))
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
