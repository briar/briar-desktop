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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    <b>bold</b>, <i>italic</i>, <u>underline</u>, <strike>strikethrough</strike>, <b><i><u>all three <strike>or four</strike></u></i></b>
</p>
<p>
    This paragraph<br/>
    contains a <a href="https://google.com">link to Google</a>
</p>
<blockquote>This is a block quote<br/>with multiple lines.</blockquote>
<ul>
  <li>foo</li>
  <li>bar</li>
</ul>
<p>
<ol>
  <li>foo</li>
  <li>bar</li>
</ol>
</p>
    """.trimIndent()

    HtmlText(testHtml) {
        println(it)
    }
}

// This file is adapted from https://github.com/jeremyrempel/yahnapp (HtmlText.kt)

@OptIn(ExperimentalTextApi::class)
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
    //    "a", "b"/"strong", "i"/"em"/"cite", "u", "strike", "sub", "sup", "q",
    //    "br", "p", "blockquote",

    // Elements that are currently only partially supported:
    //    "ul" with "li": no nested lists, no "ul" within another paragraph "p"

    // Elements we still need to add support for:
    //    "code", "dd", "dl", "dt",
    //    "ol", "pre", "small", "span",

    val h1 = MaterialTheme.typography.h1.toSpanStyle()
    val h2 = MaterialTheme.typography.h2.toSpanStyle()
    val h3 = MaterialTheme.typography.h3.toSpanStyle()
    val h4 = MaterialTheme.typography.h4.toSpanStyle()
    val h5 = MaterialTheme.typography.h5.toSpanStyle()
    val h6 = MaterialTheme.typography.h6.toSpanStyle()
    val bold = SpanStyle(fontWeight = FontWeight.Bold)
    val italic = SpanStyle(fontStyle = FontStyle.Italic)
    val underline = SpanStyle(textDecoration = TextDecoration.Underline)
    val strikethrough = SpanStyle(textDecoration = TextDecoration.LineThrough)
    // todo: combination underline + strikethrough not working
    val subscript = SpanStyle(baselineShift = BaselineShift.Subscript)
    val superscript = SpanStyle(baselineShift = BaselineShift.Superscript)
    val link = SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colors.primaryVariant)

    // todo: trim newlines / whitespaces?!
    // todo: nested paragraphs not possible, but in HTML it is?
    val blockquote = ParagraphStyle(textIndent = TextIndent(20.sp, 20.sp))
    val paragraph = ParagraphStyle()

    val formattedString = remember(html) {
        buildAnnotatedString {
            var cursorPosition = 0 // todo: couldn't this be replaced with this.length?
            fun appendAndUpdateCursor(str: String) {
                append(str)
                cursorPosition += str.length
            }

            fun addParagraph() {
                pushStyle(paragraph)
                if (cursorPosition > 0) {
                    appendAndUpdateCursor("\n")
                }
            }

            fun addLink(node: Element) {
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

            fun startBlockQuote() {
                pushStyle(blockquote)
                pushStringAnnotation("quote", "")
            }

            fun endBlockQuote() {
                pop()
                pop()
            }

            // todo: this should be properly localized
            fun startInlineQuote() {
                appendAndUpdateCursor("\"")
            }

            fun endInlineQuote() {
                appendAndUpdateCursor("\"")
            }

            fun startUnorderedList() {
                pushStyle(blockquote)
            }

            fun endUnorderedList() {
                pop()
            }

            fun startBullet() {
                pushStringAnnotation("bullet", "")
            }

            fun endBullet() {
                pop()
                appendAndUpdateCursor("\n")
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
                                // headers
                                "h1" -> pushStyle(h1)
                                "h2" -> pushStyle(h2)
                                "h3" -> pushStyle(h3)
                                "h4" -> pushStyle(h4)
                                "h5" -> pushStyle(h5)
                                "h6" -> pushStyle(h6)

                                // inline formatting
                                "b", "strong" -> pushStyle(bold)
                                "i", "em", "cite" -> pushStyle(italic)
                                "u" -> pushStyle(underline)
                                "strike" -> pushStyle(strikethrough)
                                "sub" -> pushStyle(subscript)
                                "sup" -> pushStyle(superscript)
                                "q" -> startInlineQuote()
                                "a" -> addLink(node)

                                // lists
                                "ul" -> startUnorderedList()
                                "li" -> startBullet()

                                // misc
                                "br" -> appendAndUpdateCursor("\n")
                                "blockquote" -> startBlockQuote()
                                "p" -> addParagraph()
                                // else -> throw Exception("Unsupported tag '${node.tagName()}'")
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
                            "h1", "h2", "h3", "h4", "h5", "h6",
                            "b", "strong", "i", "em", "cite", "u", "strike", "sub", "sup",
                            "a", "p",
                            -> pop()

                            "q" -> endInlineQuote()

                            "ul" -> endUnorderedList()
                            "li" -> endBullet()

                            "blockquote" -> endBlockQuote()
                        }
                    }
                }
            })
        }
    }

    val textMeasurer = rememberTextMeasurer()
    // todo: doesn't respect actual text size, but also cannot be changed currently
    val textLayoutResult = remember { textMeasurer.measure("\u2022") }
    val color = MaterialTheme.colors.onSurface

    var onDraw: DrawScope.() -> Unit by remember { mutableStateOf({}) }

    ClickableText(
        text = formattedString,
        modifier = modifier.drawBehind { onDraw() },
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
        },
        onTextLayout = { layoutResult ->
            val quotes = formattedString.getStringAnnotations("quote").map {
                val firstLine = layoutResult.getLineForOffset(it.start)
                val lastLine = layoutResult.getLineForOffset(it.end - 1)
                Rect(
                    top = layoutResult.getLineTop(firstLine),
                    bottom = layoutResult.getLineBottom(lastLine),
                    left = 0f,
                    right = 0f
                )
            }
            val bullets = formattedString.getStringAnnotations("bullet").map {
                val line = layoutResult.getLineForOffset(it.start)
                Rect(
                    top = layoutResult.getLineTop(line),
                    bottom = layoutResult.getLineBottom(line),
                    left = 0f,
                    right = layoutResult.getLineLeft(line)
                )
            }
            onDraw = {
                quotes.forEach {
                    val line = it.copy(left = it.left + 10.dp.toPx(), right = it.right + 11.dp.toPx())
                    drawRect(
                        color = Color.Green,
                        topLeft = line.topLeft,
                        size = line.size,
                    )
                }
                bullets.forEach {
                    drawText(
                        textLayoutResult = textLayoutResult,
                        color = color,
                        topLeft = Offset(
                            it.center.x - textLayoutResult.size.width / 2,
                            it.center.y - textLayoutResult.size.height / 2,
                        ),
                    )
                }
            }
        }
    )
}

private fun AnnotatedString.getStringAnnotations(tag: String) =
    getStringAnnotations(tag, 0, length)
