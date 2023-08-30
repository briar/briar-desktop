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

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.intellij.lang.annotations.Language
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeVisitor

fun main() = preview {
    @Language("HTML")
    val testHtml = """
<h1>Headline</h1>
<p>some text</p>
<h2>second headline</h2>
<p>
    Hello World
    <b>bold</b>, <i>italic</i>, <u>underline</u>, <b><i><u>all three</u></i></b>
</p>
<p>
    This paragraph<br/>
    contains a <a href="https://google.com">link to Google</a>
</p>
<p>
<ol>
  <li>foo</li>
  <li>bar</li>
</ol>
</p>""".trimIndent()

    HtmlText(testHtml) {
        println(it)
    }
}

// This file is adapted from https://github.com/jeremyrempel/yahnapp (HtmlText.kt)

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    handleLink: (String) -> Unit,
) {

    // Elements we support:
    //    "h1", "h2", "h3", "h4", "h5", "h6",
    //    "a", "b", "i", "u", "br", "p"

    // Elements we still need to add support for:
    //    "blockquote", "cite", "code", "dd", "dl", "dt", "em",
    //    "li", "ol", "pre", "q", "small", "span", "strike", "strong", "sub",
    //    "sup", "ul"

    val h1 = MaterialTheme.typography.h1.toSpanStyle()
    val h2 = MaterialTheme.typography.h2.toSpanStyle()
    val h3 = MaterialTheme.typography.h3.toSpanStyle()
    val h4 = MaterialTheme.typography.h4.toSpanStyle()
    val h5 = MaterialTheme.typography.h5.toSpanStyle()
    val h6 = MaterialTheme.typography.h6.toSpanStyle()
    val bold = SpanStyle(fontWeight = FontWeight.Bold)
    val italic = SpanStyle(fontStyle = FontStyle.Italic)
    val underline = SpanStyle(textDecoration = TextDecoration.Underline)
    val link = SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colors.primaryVariant)

    val paragraph = ParagraphStyle()

    val formattedString = remember(html) {
        buildAnnotatedString {
            var cursorPosition = 0
            val appendAndUpdateCursor: (String) -> Unit = {
                append(it)
                cursorPosition += it.length
            }

            val addParagraph: () -> Unit = {
                pushStyle(paragraph)
                if (cursorPosition > 0) {
                    appendAndUpdateCursor("\n")
                }
            }

            val addLink: (node: Element) -> Unit = { node ->
                val start = cursorPosition
                val end = start + node.text().length
                val href = node.attr("href")

                addStringAnnotation(
                    tag = "link",
                    start = start,
                    end = end,
                    annotation = href
                )
                pushStyle(link)
            }

            val doc = Jsoup.parse(html)

            doc.traverse(object : NodeVisitor {
                override fun head(node: Node, depth: Int) {
                    when (node) {
                        is TextNode -> {
                            if (node.text().isNotBlank()) {
                                appendAndUpdateCursor(node.text())
                            }
                        }

                        is Element -> {
                            when (node.tagName()) {
                                "h1" -> pushStyle(h1)
                                "h2" -> pushStyle(h2)
                                "h3" -> pushStyle(h3)
                                "h4" -> pushStyle(h4)
                                "h5" -> pushStyle(h5)
                                "h6" -> pushStyle(h6)
                                "b" -> pushStyle(bold)
                                "i" -> pushStyle(italic)
                                "u" -> pushStyle(underline)
                                "br" -> appendAndUpdateCursor("\n")
                                "p" -> addParagraph()
                                "a" -> addLink(node)
                            }
                        }

                        else -> {
                            throw Exception("Unknown node type")
                        }
                    }
                }

                override fun tail(node: Node, depth: Int) {
                    if (node is Element) {
                        when (node.tagName()) {
                            "h1", "h2", "h3", "h4", "h5", "h6", "b", "i", "u", "a", "p" -> pop()
                        }
                    }
                }
            })
        }
    }

    ClickableText(
        text = formattedString,
        modifier = modifier,
        overflow = overflow,
        maxLines = maxLines,
        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
        onClick = { offset ->
            formattedString
                .getStringAnnotations(start = offset, end = offset)
                .firstOrNull { it.tag == "link" }
                ?.let { annotation ->
                    handleLink(annotation.item)
                }
        }
    )
}
