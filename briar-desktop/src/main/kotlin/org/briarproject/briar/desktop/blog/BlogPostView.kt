/*
 * Briar Desktop
 * Copyright (C) 2023 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.blog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.blog.BlogCommentHeader
import org.briarproject.briar.api.blog.BlogPostHeader
import org.briarproject.briar.api.blog.MessageType
import org.briarproject.briar.api.identity.AuthorInfo
import org.briarproject.briar.api.identity.AuthorInfo.Status.OURSELVES
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.contact.ProfileCircleRss
import org.briarproject.briar.desktop.theme.Blue500
import org.briarproject.briar.desktop.ui.AuthorView
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.Tooltip
import org.briarproject.briar.desktop.ui.TrustIndicatorShort
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp
import org.briarproject.briar.desktop.utils.UiUtils.getContactDisplayName
import org.briarproject.briar.desktop.utils.UiUtils.modifyIf
import org.briarproject.briar.desktop.utils.getRandomAuthor
import org.briarproject.briar.desktop.utils.getRandomId
import org.briarproject.briar.desktop.utils.getRandomString
import kotlin.random.Random

@Suppress("HardCodedStringLiteral")
fun main() = preview {
    Column(verticalArrangement = spacedBy(8.dp)) {
        val post = getRandomBlogPostItem(
            text = "This is a normal blog post.\n\nIt has one author and no comments.",
            time = System.currentTimeMillis() - 999_000
        )
        BlogPostView(post, {}, {})
        val htmlPost = getRandomBlogPostItem(
            text = "<h1>HTML post</h1><p>This is a html blog post.\n\nIt has <a href=\"https://web.archive.org\">one author</a> and no comments.</p>",
            time = System.currentTimeMillis() - 750_000
        )
        BlogPostView(htmlPost, {}, {})
        val commentPost = getRandomBlogCommentItem(
            parent = post,
            comment = "This is a comment on that first blog post.\n\nIt has two lines as well.",
            time = System.currentTimeMillis() - 500_000
        )
        BlogPostView(commentPost, {}, {})
        BlogPostView(
            getRandomBlogCommentItem(
                parent = commentPost,
                comment = "This is a second comment on that first blog post. It has only one line, but a long one.",
                time = System.currentTimeMillis()
            ),
            null,
            null,
        )
        BlogPostView(getRandomBlogPost(getRandomString(1337), 1337), null, null)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun BlogPostView(
    item: BlogPost,
    onItemRepeat: ((BlogPost) -> Unit)?,
    onAuthorClicked: ((GroupId) -> Unit)?,
    modifier: Modifier = Modifier,
) = Card(modifier = modifier) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(modifier = Modifier.weight(1f)) {
            BlogPostViewHeader(item, onItemRepeat, onAuthorClicked, Modifier.padding(8.dp))
            // should be changed back to verticalArrangement = spacedBy(8.dp) on the containing Column
            // when https://github.com/JetBrains/compose-jb/issues/2729 is fixed
            Spacer(Modifier.height(8.dp))
            SelectionContainer {
                HtmlText(
                    modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                    html = item.text ?: "",
                    maxLines = if (onItemRepeat == null) 5 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis,
                ) { link ->
                    // TODO: handle link clicks. Display dialog warning the user about opening an external app
                    //  with the implication that it can be used to identify the user. Also, make this actually
                    //  clickable which it is not in the SelectionContainer at the moment.
                }
            }
            Spacer(Modifier.height(8.dp))
            // if no preview and a comment item, show comments
            if (onItemRepeat != null && item is BlogCommentItem) {
                item.comments.forEach { commentItem ->
                    BlogCommentView(commentItem, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
        if (onItemRepeat != null) Tooltip(
            text = i18n("forum.message.new"),
            modifier = Modifier.width(8.dp),
        ) {
            AnimatedVisibility(visible = !item.isRead) {
                Box(modifier = Modifier.fillMaxSize().background(Blue500))
            }
        }
    }
}

@Composable
private fun BlogPostViewHeader(
    item: BlogPost,
    onItemRepeat: ((BlogPost) -> Unit)?,
    onAuthorClicked: ((GroupId) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = spacedBy(8.dp),
        verticalAlignment = CenterVertically,
    ) {
        Column(
            verticalArrangement = spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            RepeatAuthorView(
                item = item,
                onAuthorClicked = if (onAuthorClicked == null) null else {
                    { onAuthorClicked(item.header.groupId) }
                },
                authorClickTooltip = if (onAuthorClicked == null) null else {
                    val name = getContactDisplayName(item.header.author.name, item.header.authorInfo.alias)
                    i18nF("blog.open.from.author", name)
                },
            )
            if (item is BlogCommentItem) {
                val postHeader = item.postHeader
                // This isn't clickable, because item.type is WRAPPED_POST, so not easy to get the GroupId of the blog
                if (postHeader.isRssFeed) {
                    // todo: currently only re-blogged RSS feeds are supported
                    RssAuthorView(
                        name = postHeader.author.name,
                        timestamp = postHeader.timestamp,
                    )
                } else {
                    AuthorView(
                        author = postHeader.author,
                        authorInfo = postHeader.authorInfo,
                        timestamp = postHeader.timestamp,
                    )
                }
            }
        }
        if (onItemRepeat != null) IconButton(onClick = { onItemRepeat(item) }) {
            Icon(
                imageVector = Icons.Default.Repeat,
                contentDescription = i18n("access.blogs.reblog"),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RepeatAuthorView(
    item: BlogPost,
    modifier: Modifier = Modifier,
    onAuthorClicked: (() -> Unit)?,
    authorClickTooltip: String? = null,
) {
    val author = item.author
    val authorInfo = item.authorInfo
    val timestamp = item.timestamp
    Row(
        horizontalArrangement = spacedBy(8.dp),
        verticalAlignment = CenterVertically,
        modifier = modifier,
    ) {
        Tooltip(
            text = authorClickTooltip,
            modifier = Modifier.weight(1f),
        ) {
            Row(
                modifier = Modifier.modifyIf(onAuthorClicked != null, Modifier.clickable { onAuthorClicked?.invoke() }),
                horizontalArrangement = spacedBy(8.dp),
                verticalAlignment = CenterVertically,
            ) {
                // TODO we may eventually want to move this into its own composable or integrate into ProfileCircle
                Box(
                    contentAlignment = BottomEnd,
                    modifier = Modifier.size(36.dp),
                ) {
                    ProfileCircle(36.dp, author.id, authorInfo)
                    if (item is BlogCommentItem) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            tint = Color.Black,
                            contentDescription = i18n("access.blogs.reblog"),
                            modifier = Modifier.size(16.dp).clip(CircleShape)
                                .border(1.dp, Color.Black, CircleShape).background(Color.White).padding(2.dp)
                        )
                    }
                }
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = getContactDisplayName(author.name, authorInfo.alias),
                    fontWeight = if (authorInfo.status == OURSELVES) FontWeight.Bold else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                TrustIndicatorShort(authorInfo.status)
            }
        }
        Text(
            text = getFormattedTimestamp(timestamp),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
        )
    }
}

@Composable
private fun RssAuthorView(
    name: String,
    timestamp: Long,
) {
    Row(
        horizontalArrangement = spacedBy(8.dp),
        verticalAlignment = CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = spacedBy(8.dp),
            verticalAlignment = CenterVertically,
        ) {
            ProfileCircleRss(27.dp)
            Text(
                modifier = Modifier.weight(1f, fill = false),
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = getFormattedTimestamp(timestamp),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
        )
    }
}

internal fun getRandomBlogPostItem(text: String, time: Long) = BlogPostItem(
    header = BlogPostHeader(
        MessageType.POST,
        GroupId(getRandomId()),
        MessageId(getRandomId()),
        time,
        System.currentTimeMillis(),
        getRandomAuthor(),
        AuthorInfo(AuthorInfo.Status.values().filter { it != AuthorInfo.Status.NONE }.random()),
        Random.nextBoolean() && Random.nextBoolean() && Random.nextBoolean(),
        Random.nextBoolean(),
    ),
    text = text,
)

@Composable
private fun BlogCommentView(header: BlogCommentHeader, modifier: Modifier = Modifier) {
    val comment = header.comment
    if (comment != null) Column(
        verticalArrangement = spacedBy(8.dp),
        modifier = modifier,
    ) {
        HorizontalDivider()
        AuthorView(header.author, header.authorInfo, header.timestamp, Modifier.padding(horizontal = 16.dp))
        SelectionContainer {
            Text(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                text = comment,
            )
        }
    }
}

internal fun getRandomBlogCommentItem(parent: BlogPost, comment: String?, time: Long) = BlogCommentItem(
    header = BlogCommentHeader(
        MessageType.COMMENT,
        GroupId(getRandomId()),
        comment,
        parent.header,
        MessageId(getRandomId()),
        time,
        System.currentTimeMillis(),
        getRandomAuthor(),
        AuthorInfo(AuthorInfo.Status.values().filter { it != AuthorInfo.Status.NONE }.random()),
        Random.nextBoolean(),
    ),
    postHeader = parent.postHeader,
    text = parent.text,
)

internal fun getRandomBlogPost(text: String, time: Long): BlogPost {
    val postItem = getRandomBlogPostItem(text, time - 999_000)
    return if (Random.nextBoolean()) {
        postItem
    } else {
        val comment = if (Random.nextBoolean()) null else text
        getRandomBlogCommentItem(postItem, comment, time)
    }
}
