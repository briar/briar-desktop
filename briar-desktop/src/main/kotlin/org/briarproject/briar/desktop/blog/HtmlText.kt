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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.blog.ListType.ORDERED
import org.briarproject.briar.desktop.blog.ListType.UNORDERED
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.intellij.lang.annotations.Language
import org.jetbrains.annotations.NonNls
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeVisitor

fun main() = preview {
    @NonNls
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
  <li>direct children<ul><li>child1</li><li>child2</li></ul></li>
  <ul>
    <li>bar1</li>
    <li>bar2</li>
  </ul>
</ul>
<ul>
  <li>foo</li>
  <li>bar</li>
</ul>
    """.trimIndent()

    SelectionContainer {
        HtmlText(testHtml) {
            println(it)
        }
    }
}

// This file is adapted from https://github.com/jeremyrempel/yahnapp (HtmlText.kt)

enum class ListType {
    ORDERED,
    UNORDERED
}

val listBullets = listOf(
    "\u2022",
    "\u25e6",
    "\u25aa",
)

@OptIn(ExperimentalTextApi::class)
@Composable
@Suppress("HardCodedStringLiteral")
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    handleLink: (String) -> Unit,
) {

    data class IndentInfo(val indent: TextUnit, val start: Int)

    val indentStack = ArrayDeque<IndentInfo>()
    val listNesting = ArrayDeque<ListType>()

    var lastCharWasNewline = true

    // Elements we support:
    //    "h1", "h2", "h3", "h4", "h5", "h6",
    //    "a", "b"/"strong", "i"/"em"/"cite", "u", "strike", "sub", "sup", "q",
    //    "br", "p", "blockquote",

    // Elements that are currently only partially supported:
    //    "ul"/"ol" with "li": no nested lists, no "ul" within another paragraph "p"

    // Elements we still need to add support for:
    //    "code", "dd", "dl", "dt", "pre", "small", "span"

    val h1 = MaterialTheme.typography.h1.toParagraphStyle()
    val h2 = MaterialTheme.typography.h2.toParagraphStyle()
    val h3 = MaterialTheme.typography.h3.toParagraphStyle()
    val h4 = MaterialTheme.typography.h4.toParagraphStyle()
    val h5 = MaterialTheme.typography.h5.toParagraphStyle()
    val h6 = MaterialTheme.typography.h6.toParagraphStyle()
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

    val formattedString = remember(html) {
        buildAnnotatedString {
            var cursorPosition = 0 // todo: couldn't this be replaced with this.length?
            fun appendAndUpdateCursor(str: String) {
                append(str)
                cursorPosition += str.length
                lastCharWasNewline = str.last() == '\n'
            }

            fun ensureNewline() {
                if (!lastCharWasNewline) appendAndUpdateCursor("\n")
            }

            fun pushIndent(indent: TextUnit) {
                check(indent.isSp) { "only TextUnit.sp allowed" }

                var combinedIndent = indent
                if (indentStack.isNotEmpty()) {
                    val prev = indentStack.top()
                    if (prev.start < cursorPosition) {
                        println("pushIndent: ${prev.start}-$cursorPosition")
                        addStyle(
                            style = ParagraphStyle(textIndent = TextIndent(prev.indent, prev.indent)),
                            start = prev.start,
                            end = cursorPosition
                        )
                        // ensureNewline()
                    }
                    combinedIndent = (prev.indent.value + indent.value).sp
                }

                indentStack.push(IndentInfo(combinedIndent, cursorPosition))
            }

            fun popIndent() {
                check(indentStack.isNotEmpty()) { "nothing to pop from" }
                val prev = indentStack.pop()
                if (prev.start < cursorPosition) {
                    println("popIndent: ${prev.start}-$cursorPosition")
                    addStyle(
                        style = ParagraphStyle(textIndent = TextIndent(prev.indent, prev.indent)),
                        start = prev.start,
                        end = cursorPosition
                    )
                    ensureNewline()
                }

                if (indentStack.isNotEmpty()) {
                    val next = indentStack.pop()
                    indentStack.push(next.copy(start = cursorPosition))
                }
            }

            fun startParagraph() {
                pushIndent(0.sp)
            }

            fun endParagraph() {
                popIndent()
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
                pushIndent(20.sp)
                pushStringAnnotation("quote", "")
            }

            fun endBlockQuote() {
                pop()
                popIndent()
            }

            // todo: quotation marks should be properly localized
            fun startInlineQuote() {
                appendAndUpdateCursor("\"")
            }

            fun endInlineQuote() {
                appendAndUpdateCursor("\"")
            }

            fun startUnorderedList() {
                listNesting.push(UNORDERED)
                pushIndent(20.sp)
            }

            fun endUnorderedList() {
                popIndent()
                listNesting.pop()
            }

            fun startOrderedList() {
                listNesting.push(ORDERED)
                pushIndent(20.sp)
            }

            fun endOrderedList() {
                popIndent()
                listNesting.pop()
            }

            fun startBullet() {
                check(listNesting.isNotEmpty()) { "<li> outside of list" }
                appendAndUpdateCursor(listBullets[listNesting.size] + " ")
                pushStringAnnotation("bullet", listNesting.size.toString())
            }

            fun endBullet() {
                pop()
                ensureNewline()
            }

            // replace multiple newlines/whitespaces to single whitespace
            // todo: also trim text (at least) inside <p> tags
            val cleanHtml = html.replace("\\s+".toRegex(), " ")
            val doc = Jsoup.parse(cleanHtml)

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
                                "ol" -> startOrderedList()
                                "li" -> startBullet()

                                // misc
                                "br" -> appendAndUpdateCursor("\n")
                                "blockquote" -> startBlockQuote()
                                "p" -> startParagraph()
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
                            "a",
                            -> pop()

                            "q" -> endInlineQuote()

                            "ul" -> endUnorderedList()
                            "ol" -> endOrderedList()
                            "li" -> endBullet()

                            "p" -> endParagraph()
                            "blockquote" -> endBlockQuote()
                        }
                    }
                }
            })
        }
    }

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

            onDraw = {
                quotes.forEach {
                    val line = it.copy(left = it.left + 10.dp.toPx(), right = it.right + 11.dp.toPx())
                    drawRect(
                        color = Color.Green,
                        topLeft = line.topLeft,
                        size = line.size,
                    )
                }
            }
        }
    )
}

private fun AnnotatedString.getStringAnnotations(tag: String) =
    getStringAnnotations(tag, 0, length)


fun <E> ArrayDeque<E>.push(e: E) {
    addLast(e)
}

fun <E> ArrayDeque<E>.pop(): E {
    return removeLast()
}

fun <E> ArrayDeque<E>.top(): E {
    return last()
}
