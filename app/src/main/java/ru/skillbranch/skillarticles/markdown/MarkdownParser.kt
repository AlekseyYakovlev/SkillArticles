package ru.skillbranch.skillarticles.markdown

import java.util.regex.Pattern

object MarkdownParser {

    private const val LINE_SEPARATOR = "\n"

    //group regex
    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+$)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+$)"
    private const val ITALIC_GROUP = "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))"
    private const val BOLD_GROUP =
        "((?<!\\*)\\*{2}[^*].*?[^*]?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?[^_]?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!~)~{2}[^~].*?[^~]?~{2}(?!~))"
    private const val RULE_GROUP = "(^[-_*]{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?[^`\\s]?`(?!`))"
    private const val LINK_GROUP = "(\\[[^\\[\\]]*?]\\(.+?\\)|^\\[*?]\\(.*?\\))"
    private const val BLOCK_CODE_GROUP = "(^`{3}[^`][\\s\\S]*?[^`]`{3}$)"
    private const val ORDER_LIST_GROUP = "(^[\\d]+[.] .+$)"

    //result regex
    private const val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP" +
            "|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP|$INLINE_GROUP|$LINK_GROUP" +
            "|$BLOCK_CODE_GROUP|$ORDER_LIST_GROUP"

    private val elementsPattern by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }

    private val hashRegex = "^#{1,6}".toRegex()
    private val linkRegex = "\\[(.*)]\\((.*)\\)".toRegex()
    private val orderedListRegex = "^[\\d]+[.]".toRegex()

    /**
     * parse markdown text to elements
     */
    fun parse(string: String): MarkdownText {
        val elements = mutableListOf<Element>()
        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }

    /**
     * clear markdown text to string from markdown symbols
     */
    fun clear(string: String?): String? {
        string ?: return null

        val elements = findElements(string)

        return if (elements.size == 1 && elements[0] is Element.Text) {
            elements[0].text.toString()
        } else {
            val resString = elements.fold("") { result, el -> result.plus(el.text) }
            clear(resString)
        }
    }


    /**
     * find markdown elements in markdown text
     */
    private fun findElements(string: CharSequence): List<Element> {
        val parents = mutableListOf<Element>()
        val matcher = elementsPattern.matcher(string)
        var lastStartIndex = 0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            //if something is found then everything before it is TEXT
            if (lastStartIndex < startIndex) {
                parents.add(Element.Text(string.subSequence(lastStartIndex, startIndex)))
            }

            //found text
            var text: CharSequence

            //groups range for iterate by groups (1..9) or (1..11) optionally
            val groups = 1..11
            var group = -1
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }

            when (group) {
                //NOT FOUND -> BREAK
                -1 -> break@loop

                //UNORDERED LIST
                1 -> {
                    //text without "*. "
                    text = string.subSequence(startIndex.plus(2), endIndex)

                    //find inner elements
                    val subs = findElements(text)
                    val element = Element.UnorderedListItem(text, subs)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //HEADER
                2 -> {
                    val hashSymbols = hashRegex.find(string.subSequence(startIndex, endIndex))
                    val headerLevel = hashSymbols!!.value.length

                    //text without "{#} "
                    text = string.subSequence(startIndex.plus(headerLevel.inc()), endIndex)

                    val element = Element.Header(headerLevel, text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //QUOTE
                3 -> {
                    //text without "> "
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subElements = findElements(text)
                    val element = Element.Quote(text, subElements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //ITALIC
                4 -> {
                    //text without "*{}*"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subElements = findElements(text)
                    val element = Element.Italic(text, subElements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //BOLD
                5 -> {
                    //text without "**{}**"
                    text = string.subSequence(startIndex.plus(2), endIndex.minus(2))
                    val subElements = findElements(text)
                    val element = Element.Bold(text, subElements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //STRIKE
                6 -> {
                    //text without "~~{}~~"
                    text = string.subSequence(startIndex.plus(2), endIndex.minus(2))
                    val subElements = findElements(text)
                    val element = Element.Strike(text, subElements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //RULE
                7 -> {
                    //text without "***" insert empty character
                    val element = Element.Rule()
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //INLINE CODE
                8 -> {
                    //text without "`{}`"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val element = Element.InlineCode(text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //LINK
                9 -> {
                    //full text for regex
                    text = string.subSequence(startIndex, endIndex)
                    val (title: String, link: String) = linkRegex.find(text)!!.destructured
                    val element = Element.Link(link, title)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //10 -> BLOCK CODE - optionally
                10 -> {
                    //text without "```{}```"
                    text = string.subSequence(startIndex.plus(3), endIndex.minus(3))
                    val lines = text.split(LINE_SEPARATOR)
                    val size = lines.size

                    lines.forEachIndexed { index, line ->
                        val elementType = when {
                            size == 1 -> {
                                text = line
                                Element.BlockCode.Type.SINGLE
                            }
                            index == 0 -> {
                                text = "$line$LINE_SEPARATOR"
                                Element.BlockCode.Type.START
                            }
                            index == size.dec() -> {
                                text = line
                                Element.BlockCode.Type.END
                            }
                            index in 1..size.minus(2) -> {
                                text = "$line$LINE_SEPARATOR"
                                Element.BlockCode.Type.MIDDLE
                            }
                            else -> error("Out of index exception")
                        }
                        val subElements = findElements(line)
                        val element = Element.BlockCode(elementType, text, subElements)
                        parents.add(element)
                    }
                    lastStartIndex = endIndex
                }

                //11 -> NUMERIC LIST
                11 -> {
                    val textWithOrder = string.subSequence(startIndex, endIndex)
                    val order = orderedListRegex.find(textWithOrder)!!.value
                    text = textWithOrder.removeRange(0..order.length)
                    val subs = findElements(text)
                    val element = Element.OrderedListItem(order, text, subs)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
            }

        }

        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(Element.Text(text))
        }

        return parents
    }
}

data class MarkdownText(val elements: List<Element>)

sealed class Element() {
    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Header(
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Rule(
        override val text: CharSequence = " ", //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class InlineCode(
        override val text: CharSequence, //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Link(
        val link: String,
        override val text: CharSequence, //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class OrderedListItem(
        val order: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class BlockCode(
        val type: Type = Type.MIDDLE,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element() {
        enum class Type { START, END, MIDDLE, SINGLE }
    }
}