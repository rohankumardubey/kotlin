package test.text

import kotlin.test.*
import org.junit.Test as test

// could not be local inside isEmptyAndBlank, because non-toplevel declarations is not yet supported for JS
class IsEmptyCase(val value: String?, val isNull: Boolean = false, val isEmpty: Boolean = false, val isBlank: Boolean = false)

fun createString(content: String): CharSequence = content
fun createStringBuilder(content: String): CharSequence = StringBuilder((content as Any).toString()) // required for Rhino JS


val charSequenceBuilders = listOf(::createString, ::createStringBuilder)

fun withOneCharSequenceArg(f: ((String) -> CharSequence) -> Unit) {
    for (arg1Builder in charSequenceBuilders) f(arg1Builder)
}

fun withOneCharSequenceArg(arg1: String, f: (CharSequence) -> Unit)
        = withOneCharSequenceArg { arg1Builder -> f(arg1Builder(arg1)) }

fun withTwoCharSequenceArgs(f: ((String) -> CharSequence, (String) -> CharSequence) -> Unit) {
    for (arg1Builder in charSequenceBuilders)
        for (arg2Builder in charSequenceBuilders)
            f(arg1Builder, arg2Builder)
}

fun assertContentEquals(expected: String, actual: CharSequence, message: String? = null) {
    assertEquals(expected, actual.toString(), message)
}

// helper predicates available on both platforms
fun Char.isAsciiDigit() = this in '0'..'9'
fun Char.isAsciiLetter() = this in 'A'..'Z' || this in 'a'..'z'
fun Char.isAsciiUpperCase() = this in 'A'..'Z'

class StringTest {

    @test fun isEmptyAndBlank() = withOneCharSequenceArg { arg1 ->

        val cases = listOf(
            IsEmptyCase(null,              isNull = true),
            IsEmptyCase("",                isEmpty = true, isBlank = true),
            IsEmptyCase("  \r\n\t\u00A0",  isBlank = true),
            IsEmptyCase(" Some ")
        )

        for (case in cases) {
            val value = case.value?.let { arg1(it) }
            assertEquals(case.isNull || case.isEmpty, value.isNullOrEmpty(), "failed for case '$value'")
            assertEquals(case.isNull || case.isBlank, value.isNullOrBlank(), "failed for case '$value'")
            if (value != null)
            {
                assertEquals(case.isEmpty, value.isEmpty(), "failed for case '$value'")
                assertEquals(case.isBlank, value.isBlank(), "failed for case '$value'")
            }
        }
    }

    @test fun orEmpty() {
        val s: String? = "hey"
        val ns: String? = null

        assertEquals("hey", s.orEmpty())
        assertEquals("", ns.orEmpty())
    }

    @test fun startsWithString() {
        assertTrue("abcd".startsWith("ab"))
        assertTrue("abcd".startsWith("abcd"))
        assertTrue("abcd".startsWith("a"))
        assertFalse("abcd".startsWith("abcde"))
        assertFalse("abcd".startsWith("b"))
        assertFalse("".startsWith("a"))
        assertTrue("some".startsWith(""))
        assertTrue("".startsWith(""))

        assertFalse("abcd".startsWith("aB", ignoreCase = false))
        assertTrue("abcd".startsWith("aB", ignoreCase = true))
    }

    @test fun startsWithStringForCharSequence() = withTwoCharSequenceArgs { arg1, arg2 ->
        fun String.startsWithCs(prefix: String, ignoreCase: Boolean = false): Boolean =
            arg1(this).startsWith(arg2(prefix), ignoreCase)

        assertTrue("abcd".startsWithCs("ab"))
        assertTrue("abcd".startsWithCs("abcd"))
        assertTrue("abcd".startsWithCs("a"))
        assertFalse("abcd".startsWithCs("abcde"))
        assertFalse("abcd".startsWithCs("b"))
        assertFalse("".startsWithCs("a"))
        assertTrue("some".startsWithCs(""))
        assertTrue("".startsWithCs(""))

        assertFalse("abcd".startsWithCs("aB", ignoreCase = false))
        assertTrue("abcd".startsWithCs("aB", ignoreCase = true))
    }

    @test fun endsWithString() {
        assertTrue("abcd".endsWith("d"))
        assertTrue("abcd".endsWith("abcd"))
        assertFalse("abcd".endsWith("b"))
        assertFalse("str??".endsWith("R??", ignoreCase = false))
        assertTrue("str??".endsWith("R??", ignoreCase = true))
        assertFalse("".endsWith("a"))
        assertTrue("some".endsWith(""))
        assertTrue("".endsWith(""))
    }

    @test fun endsWithStringForCharSequence() = withTwoCharSequenceArgs { arg1, arg2 ->
        fun String.endsWithCs(suffix: String, ignoreCase: Boolean = false): Boolean =
            arg1(this).endsWith(arg2(suffix), ignoreCase)
        
        assertTrue("abcd".endsWithCs("d"))
        assertTrue("abcd".endsWithCs("abcd"))
        assertFalse("abcd".endsWithCs("b"))
        assertFalse("str??".endsWithCs("R??", ignoreCase = false))
        assertTrue("str??".endsWithCs("R??", ignoreCase = true))
        assertFalse("".endsWithCs("a"))
        assertTrue("some".endsWithCs(""))
        assertTrue("".endsWithCs(""))
    }

    @test fun startsWithChar() = withOneCharSequenceArg { arg1 ->
        fun String.startsWith(char: Char, ignoreCase: Boolean = false): Boolean =
            arg1(this).startsWith(char, ignoreCase)

        assertTrue("abcd".startsWith('a'))
        assertFalse("abcd".startsWith('b'))
        assertFalse("abcd".startsWith('A', ignoreCase = false))
        assertTrue("abcd".startsWith('A', ignoreCase = true))
        assertFalse("".startsWith('a'))
    }

    @test fun endsWithChar() = withOneCharSequenceArg { arg1 ->
        fun String.endsWith(char: Char, ignoreCase: Boolean = false): Boolean =
            arg1(this).endsWith(char, ignoreCase)

        assertTrue("abcd".endsWith('d'))
        assertFalse("abcd".endsWith('b'))
        assertFalse("str??".endsWith('??', ignoreCase = false))
        assertTrue("str??".endsWith('??', ignoreCase = true))
        assertFalse("".endsWith('a'))
    }

    @test fun commonPrefix() = withTwoCharSequenceArgs { arg1, arg2 ->
        fun String.commonPrefixWith(other: String, ignoreCase: Boolean = false): String =
            arg1(this).commonPrefixWith(arg2(other), ignoreCase)

        assertEquals("", "".commonPrefixWith(""))
        assertEquals("", "any".commonPrefixWith(""))
        assertEquals("", "".commonPrefixWith("any"))
        assertEquals("", "some".commonPrefixWith("any"))

        assertEquals("an", "annual".commonPrefixWith("any"))
        assertEquals("an", "annual".commonPrefixWith("Any", ignoreCase = true))
        assertEquals("", "annual".commonPrefixWith("Any", ignoreCase = false))
        // surrogate pairs
        val dth54 = "\uD83C\uDC58" // domino tile horizontal 5-4
        val dth55 = "\uD83C\uDC59" // domino tile horizontal 5-5
        assertEquals("", dth54.commonPrefixWith(dth55))
        assertEquals(dth54, "$dth54$dth54".commonPrefixWith("$dth54$dth55"))
    }

    @test fun commonSuffix() = withTwoCharSequenceArgs { arg1, arg2 ->
        fun String.commonSuffixWith(other: String, ignoreCase: Boolean = false): String =
            arg1(this).commonSuffixWith(arg2(other), ignoreCase)

        assertEquals("", "".commonSuffixWith(""))
        assertEquals("", "any".commonSuffixWith(""))
        assertEquals("", "".commonSuffixWith("any"))
        assertEquals("", "some".commonSuffixWith("any"))

        assertEquals("ly", "yearly".commonSuffixWith("monthly"))
        assertEquals("str??", "str??".commonSuffixWith("BISTR??", ignoreCase = true))
        assertEquals("", "yearly".commonSuffixWith("HARDLY", ignoreCase = false))
        // surrogate pairs
        val dth54  = "\uD83C\uDC58" // domino tile horizontal 5-4
        val kimono = "\uD83D\uDC58" // kimono
        assertEquals("", dth54.commonSuffixWith(kimono))
        assertEquals("$dth54", "d$dth54".commonSuffixWith("s$dth54"))
    }

    @test fun capitalize() {
        assertEquals("A", "A".capitalize())
        assertEquals("A", "a".capitalize())
        assertEquals("Abcd", "abcd".capitalize())
        assertEquals("Abcd", "Abcd".capitalize())
    }

    @test fun decapitalize() {
        assertEquals("a", "A".decapitalize())
        assertEquals("a", "a".decapitalize())
        assertEquals("abcd", "abcd".decapitalize())
        assertEquals("abcd", "Abcd".decapitalize())
        assertEquals("uRL", "URL".decapitalize())
    }

    @test fun slice() {
        val iter = listOf(4, 3, 0, 1)
        // abcde
        // 01234
        assertEquals("bcd", "abcde".substring(1..3))
        assertEquals("dcb", "abcde".slice(3 downTo 1))
        assertEquals("edab", "abcde".slice(iter))
    }

    @test fun sliceCharSequence() = withOneCharSequenceArg { arg1 ->
        val iter = listOf(4, 3, 0, 1)

        val data = arg1("ABCDabcd")
        // ABCDabcd
        // 01234567
        assertEquals("BCDabc", data.slice(1..6).toString())
        assertEquals("baD", data.slice(5 downTo 3).toString())
        assertEquals("aDAB", data.slice(iter).toString())
    }

    @test fun reverse() {
        assertEquals("dcba", "abcd".reversed())
        assertEquals("4321", "1234".reversed())
        assertEquals("", "".reversed())
    }

    @test fun reverseCharSequence() = withOneCharSequenceArg { arg1 ->
        fun String.reversedCs(): CharSequence = arg1(this).reversed()

        assertContentEquals("dcba", "abcd".reversedCs())
        assertContentEquals("4321", "1234".reversedCs())
        assertContentEquals("", "".reversedCs())
    }

    @test fun indices() = withOneCharSequenceArg { arg1 ->
        fun String.indices(): IntRange = arg1(this).indices

        assertEquals(0..4, "abcde".indices())
        assertEquals(0..0, "a".indices())
        assertTrue("".indices().isEmpty())
    }

    @test fun replaceRange() = withTwoCharSequenceArgs { arg1, arg2 ->
        val s = arg1("sample text")
        val replacement = arg2("??")

        assertContentEquals("sa??e text", s.replaceRange(2, 5, replacement))
        assertContentEquals("sa?? text", s.replaceRange(2..5, replacement))
        assertFails {
            s.replaceRange(5..2, replacement)
        }
        assertFails {
            s.replaceRange(5, 2, replacement)
        }

        // symmetry with indices
        assertContentEquals(replacement.toString(), s.replaceRange(s.indices, replacement))
    }

    @test fun removeRange() = withOneCharSequenceArg("sample text") { s ->
        assertContentEquals("sae text", s.removeRange(2, 5))
        assertContentEquals("sa text", s.removeRange(2..5))

        assertContentEquals(s.toString(), s.removeRange(2,2))

        // symmetry with indices
        assertContentEquals("", s.removeRange(s.indices))

        // symmetry with replaceRange
        assertContentEquals(s.toString().replaceRange(2, 5, ""), s.removeRange(2, 5))
        assertContentEquals(s.toString().replaceRange(2..5, ""), s.removeRange(2..5))
    }

    @test fun substringDelimited() {
        val s = "-1,22,3+"
        // chars
        assertEquals("22,3+", s.substringAfter(','))
        assertEquals("3+", s.substringAfterLast(','))
        assertEquals("-1", s.substringBefore(','))
        assertEquals("-1,22", s.substringBeforeLast(','))

        // strings
        assertEquals("22,3+", s.substringAfter(","))
        assertEquals("3+", s.substringAfterLast(","))
        assertEquals("-1", s.substringBefore(","))
        assertEquals("-1,22", s.substringBeforeLast(","))

        // non-existing delimiter
        assertEquals("", s.substringAfter("+"))
        assertEquals("", s.substringBefore("-"))
        assertEquals(s, s.substringBefore("="))
        assertEquals(s, s.substringAfter("="))
        assertEquals("xxx", s.substringBefore("=", "xxx"))
        assertEquals("xxx", s.substringAfter("=", "xxx"))

    }

    @test fun replaceDelimited() {
        val s = "/user/folder/file.extension"
        // chars
        assertEquals("/user/folder/file.doc", s.replaceAfter('.', "doc"))
        assertEquals("/user/folder/another.doc", s.replaceAfterLast('/', "another.doc"))
        assertEquals("new name.extension", s.replaceBefore('.', "new name"))
        assertEquals("/new/path/file.extension", s.replaceBeforeLast('/', "/new/path"))

        // strings
        assertEquals("/user/folder/file.doc", s.replaceAfter(".", "doc"))
        assertEquals("/user/folder/another.doc", s.replaceAfterLast("/", "another.doc"))
        assertEquals("new name.extension", s.replaceBefore(".", "new name"))
        assertEquals("/new/path/file.extension", s.replaceBeforeLast("/", "/new/path"))

        // non-existing delimiter
        assertEquals("/user/folder/file.extension", s.replaceAfter("=", "doc"))
        assertEquals("/user/folder/file.extension", s.replaceAfterLast("=", "another.doc"))
        assertEquals("/user/folder/file.extension", s.replaceBefore("=", "new name"))
        assertEquals("/user/folder/file.extension", s.replaceBeforeLast("=", "/new/path"))
        assertEquals("xxx", s.replaceBefore("=", "new name", "xxx"))
        assertEquals("xxx", s.replaceBeforeLast("=", "/new/path", "xxx"))
    }

    @test fun stringIterator() = withOneCharSequenceArg("239") { data ->
        var sum = 0
        for(c in data)
            sum += (c - '0')
        assertTrue(sum == 14)
    }

    @test fun trimStart() = withOneCharSequenceArg { arg1 ->
        fun String.trimStartCS(): CharSequence = arg1(this).trimStart()
        assertContentEquals("", "".trimStartCS())
        assertContentEquals("a", "a".trimStartCS())
        assertContentEquals("a", " a".trimStartCS())
        assertContentEquals("a", "  a".trimStartCS())
        assertContentEquals("a  ", "  a  ".trimStartCS())
        assertContentEquals("a b", "  a b".trimStartCS())
        assertContentEquals("a b ", "  a b ".trimStartCS())
        assertContentEquals("a", " \u00A0 a".trimStartCS())

        assertContentEquals("a", "\ta".trimStartCS())
        assertContentEquals("a", "\t\ta".trimStartCS())
        assertContentEquals("a", "\ra".trimStartCS())
        assertContentEquals("a", "\na".trimStartCS())

        assertContentEquals("a=", arg1("-=-=a=").trimStart('-','='))
        assertContentEquals("123a", arg1("ab123a").trimStart {  !it.isAsciiDigit() })
    }

    @test fun trimEnd() = withOneCharSequenceArg { arg1 ->
        fun String.trimEndCS(): CharSequence = arg1(this).trimEnd()
        assertContentEquals("", "".trimEndCS())
        assertContentEquals("a", "a".trimEndCS())
        assertContentEquals("a", "a ".trimEndCS())
        assertContentEquals("a", "a  ".trimEndCS())
        assertContentEquals("  a", "  a  ".trimEndCS())
        assertContentEquals("a b", "a b  ".trimEndCS())
        assertContentEquals(" a b", " a b  ".trimEndCS())
        assertContentEquals("a", "a \u00A0 ".trimEndCS())

        assertContentEquals("a", "a\t".trimEndCS())
        assertContentEquals("a", "a\t\t".trimEndCS())
        assertContentEquals("a", "a\r".trimEndCS())
        assertContentEquals("a", "a\n".trimEndCS())

        assertContentEquals("=a", arg1("=a=-=-").trimEnd('-','='))
        assertContentEquals("ab123", arg1("ab123a").trimEnd { !it.isAsciiDigit() })
    }

    @test fun trimStartAndEnd() = withOneCharSequenceArg { arg1 ->
        val examples = arrayOf("a",
                " a ",
                "  a  ",
                "  a b  ",
                "\ta\tb\t",
                "\t\ta\t\t",
                "\ra\r",
                "\na\n",
                " \u00A0 a \u00A0 "
        )

        for ((source, example) in examples.map { it to arg1(it) }) {
            assertContentEquals(source.trimEnd().trimStart(), example.trim())
            assertContentEquals(source.trimStart().trimEnd(), example.trim())
        }

        val examplesForPredicate = arrayOf("123",
                "-=123=-"
        )

        val trimChars = charArrayOf('-', '=')
        val trimPredicate = { it: Char -> !it.isAsciiDigit() }
        for ((source, example) in examplesForPredicate.map { it to arg1(it) }) {
            assertContentEquals(source.trimStart(*trimChars).trimEnd(*trimChars), example.trim(*trimChars))
            assertContentEquals(source.trimStart(trimPredicate).trimEnd(trimPredicate), example.trim(trimPredicate))
        }
    }

    @test fun padStart() = withOneCharSequenceArg { arg1 ->
        val s = arg1("s")
        assertContentEquals("s", s.padStart(0))
        assertContentEquals("s", s.padStart(1))
        assertContentEquals("--s", s.padStart(3, '-'))
        assertContentEquals("  ", arg1("").padStart(2))
        assertFails {
            s.padStart(-1)
        }
    }

    @test fun padEnd() = withOneCharSequenceArg { arg1 ->
        val s = arg1("s")
        assertContentEquals("s", s.padEnd(0))
        assertContentEquals("s", s.padEnd(1))
        assertContentEquals("s--", s.padEnd(3, '-'))
        assertContentEquals("  ", arg1("").padEnd(2))
        assertFails {
            s.padEnd(-1)
        }
    }

    @test fun removePrefix() = withOneCharSequenceArg("pre") { prefix ->
        assertEquals("fix", "prefix".removePrefix(prefix), "Removes prefix")
        assertEquals("prefix", "preprefix".removePrefix(prefix), "Removes prefix once")
        assertEquals("sample", "sample".removePrefix(prefix))
        assertEquals("sample", "sample".removePrefix(""))
    }

    @test fun removeSuffix() = withOneCharSequenceArg("fix") { suffix ->
        assertEquals("suf", "suffix".removeSuffix(suffix), "Removes suffix")
        assertEquals("suffix", "suffixfix".removeSuffix(suffix), "Removes suffix once")
        assertEquals("sample", "sample".removeSuffix(suffix))
        assertEquals("sample", "sample".removeSuffix(""))
    }

    @test fun removeSurrounding() = withOneCharSequenceArg { arg1 ->
        val pre = arg1("<")
        val post = arg1(">")
        assertEquals("value", "<value>".removeSurrounding(pre, post))
        assertEquals("<value>", "<<value>>".removeSurrounding(pre, post), "Removes surrounding once")
        assertEquals("<value", "<value".removeSurrounding(pre, post), "Only removes surrounding when both prefix and suffix present")
        assertEquals("value>", "value>".removeSurrounding(pre, post), "Only removes surrounding when both prefix and suffix present")
        assertEquals("value", "value".removeSurrounding(pre, post))

        assertEquals("<->", "<->".removeSurrounding(arg1("<-"), arg1("->")), "Does not remove overlapping prefix and suffix")
    }

    @test fun removePrefixCharSequence() = withTwoCharSequenceArgs { arg1, arg2 ->
        fun String.removePrefix(prefix: String) = arg1(this).removePrefix(arg2(prefix))
        val prefix = "pre"

        assertContentEquals("fix", "prefix".removePrefix(prefix), "Removes prefix")
        assertContentEquals("prefix", "preprefix".removePrefix(prefix), "Removes prefix once")
        assertContentEquals("sample", "sample".removePrefix(prefix))
        assertContentEquals("sample", "sample".removePrefix(""))
    }

    @test fun removeSuffixCharSequence() = withTwoCharSequenceArgs { arg1, arg2 ->
        fun String.removeSuffix(suffix: String) = arg1(this).removeSuffix(arg2(suffix))
        val suffix = "fix"

        assertContentEquals("suf", "suffix".removeSuffix(suffix), "Removes suffix")
        assertContentEquals("suffix", "suffixfix".removeSuffix(suffix), "Removes suffix once")
        assertContentEquals("sample", "sample".removeSuffix(suffix))
        assertContentEquals("sample", "sample".removeSuffix(""))
    }

    @test fun removeSurroundingCharSequence() = withTwoCharSequenceArgs { arg1, arg2 ->
        fun String.removeSurrounding(prefix: String, postfix: String) = arg1(this).removeSurrounding(arg2(prefix), arg2(postfix))

        assertContentEquals("value", "<value>".removeSurrounding("<", ">"))
        assertContentEquals("<value>", "<<value>>".removeSurrounding("<", ">"), "Removes surrounding once")
        assertContentEquals("<value", "<value".removeSurrounding("<", ">"), "Only removes surrounding when both prefix and suffix present")
        assertContentEquals("value>", "value>".removeSurrounding("<", ">"), "Only removes surrounding when both prefix and suffix present")
        assertContentEquals("value", "value".removeSurrounding("<", ">"))

        assertContentEquals("<->", "<->".removeSurrounding("<-", "->"), "Does not remove overlapping prefix and suffix")
    }

    /*
    // unit test commented out until rangesDelimitiedBy would become public

    test fun rangesDelimitedBy() {
        assertEquals(listOf(0..2, 4..3, 5..7), "abc--def".rangesDelimitedBy('-').toList())
        assertEquals(listOf(0..2, 5..7, 9..10), "abc--def-xy".rangesDelimitedBy("--", "-").toList())
        assertEquals(listOf(0..2, 7..9, 14..16), "123<br>456<BR>789".rangesDelimitedBy("<br>", ignoreCase = true).toList())
        assertEquals(listOf(2..2, 4..6), "a=b=c=d".rangesDelimitedBy("=", startIndex = 2, limit = 2).toList())

        val s = "sample"
        assertEquals(listOf(s.indices), s.rangesDelimitedBy("-").toList())
        assertEquals(listOf(s.indices), s.rangesDelimitedBy("-", startIndex = -1).toList())
        assertTrue(s.rangesDelimitedBy("-", startIndex = s.length).single().isEmpty())
    }
    */


    @test fun split() = withOneCharSequenceArg { arg1 ->
        operator fun String.unaryPlus(): CharSequence = arg1(this)

        assertEquals(listOf(""), (+"").split(";"))
        assertEquals(listOf("test"), (+"test").split(*charArrayOf()), "empty list of delimiters, none matched -> entire string returned")
        assertEquals(listOf("test"), (+"test").split(*arrayOf<String>()), "empty list of delimiters, none matched -> entire string returned")

        assertEquals(listOf("abc", "def", "123;456"), (+"abc;def,123;456").split(';', ',', limit = 3))
        assertEquals(listOf("abc", "def", "123", "456"), (+"abc<BR>def<br>123<bR>456").split("<BR>", ignoreCase = true))

        assertEquals(listOf("abc", "def", "123", "456"), (+"abc=-def==123=456").split("==", "=-", "="))

        assertEquals(listOf("", "a", "b", "c", ""), (+"abc").split(""))
        assertEquals(listOf("", "a", "b", "b", "a", ""), (+"abba").split("", "a"))
        assertEquals(listOf("", "", "b", "b", "", ""), (+"abba").split("a", ""))
    }

    @test fun splitToLines() = withOneCharSequenceArg { arg1 ->
        val string = arg1("first line\rsecond line\nthird line\r\nlast line")
        assertEquals(listOf("first line", "second line", "third line", "last line"), string.lines())


        val singleLine = arg1("single line")
        assertEquals(listOf(singleLine.toString()), singleLine.lines())
    }


    @test fun indexOfAnyChar() = withOneCharSequenceArg("abracadabra") { string ->
        val chars = charArrayOf('d', 'b')
        assertEquals(1, string.indexOfAny(chars))
        assertEquals(6, string.indexOfAny(chars, startIndex = 2))
        assertEquals(-1, string.indexOfAny(chars, startIndex = 9))

        assertEquals(8, string.lastIndexOfAny(chars))
        assertEquals(6, string.lastIndexOfAny(chars, startIndex = 7))
        assertEquals(-1, string.lastIndexOfAny(chars, startIndex = 0))

        assertEquals(-1, string.indexOfAny(charArrayOf()))
    }

    @test fun indexOfAnyCharIgnoreCase() = withOneCharSequenceArg("abraCadabra") { string ->
        val chars = charArrayOf('B', 'c')
        assertEquals(1, string.indexOfAny(chars, ignoreCase = true))
        assertEquals(4, string.indexOfAny(chars, startIndex = 2, ignoreCase = true))
        assertEquals(-1, string.indexOfAny(chars, startIndex = 9, ignoreCase = true))

        assertEquals(8, string.lastIndexOfAny(chars, ignoreCase = true))
        assertEquals(4, string.lastIndexOfAny(chars, startIndex = 7, ignoreCase = true))
        assertEquals(-1, string.lastIndexOfAny(chars, startIndex = 0, ignoreCase = true))
    }

    @test fun indexOfAnyString() = withOneCharSequenceArg("abracadabra") { string ->
        val substrings = listOf("rac", "ra")
        assertEquals(2, string.indexOfAny(substrings))
        assertEquals(9, string.indexOfAny(substrings, startIndex = 3))
        assertEquals(2, string.indexOfAny(substrings.reversed()))
        assertEquals(-1, string.indexOfAny(substrings, 10))

        assertEquals(9, string.lastIndexOfAny(substrings))
        assertEquals(2, string.lastIndexOfAny(substrings, startIndex = 8))
        assertEquals(2, string.lastIndexOfAny(substrings.reversed(), startIndex = 8))
        assertEquals(-1, string.lastIndexOfAny(substrings, 1))

        assertEquals(0, string.indexOfAny(listOf("dab", "")), "empty strings are not ignored")
        assertEquals(-1, string.indexOfAny(listOf()))
    }

    @test fun indexOfAnyStringIgnoreCase() = withOneCharSequenceArg("aBraCadaBrA") { string ->
        val substrings = listOf("rAc", "Ra")

        assertEquals(2, string.indexOfAny(substrings, ignoreCase = true))
        assertEquals(9, string.indexOfAny(substrings, startIndex = 3, ignoreCase = true))
        assertEquals(-1, string.indexOfAny(substrings, startIndex = 10, ignoreCase = true))

        assertEquals(9, string.lastIndexOfAny(substrings, ignoreCase = true))
        assertEquals(2, string.lastIndexOfAny(substrings, startIndex = 8, ignoreCase = true))
        assertEquals(-1, string.lastIndexOfAny(substrings, startIndex = 1, ignoreCase = true))
    }

    @test fun findAnyOfStrings() = withOneCharSequenceArg("abracadabra") { string ->
        val substrings = listOf("rac", "ra")
        assertEquals(2 to "rac", string.findAnyOf(substrings))
        assertEquals(9 to "ra", string.findAnyOf(substrings, startIndex = 3))
        assertEquals(2 to "ra", string.findAnyOf(substrings.reversed()))
        assertEquals(null, string.findAnyOf(substrings, 10))

        assertEquals(9 to "ra", string.findLastAnyOf(substrings))
        assertEquals(2 to "rac", string.findLastAnyOf(substrings, startIndex = 8))
        assertEquals(2 to "ra", string.findLastAnyOf(substrings.reversed(), startIndex = 8))
        assertEquals(null, string.findLastAnyOf(substrings, 1))

        assertEquals(0 to "", string.findAnyOf(listOf("dab", "")), "empty strings are not ignored")
        assertEquals(null, string.findAnyOf(listOf()))
    }

    @test fun findAnyOfStringsIgnoreCase() = withOneCharSequenceArg("aBraCadaBrA") { string ->
        val substrings = listOf("rAc", "Ra")

        assertEquals(2 to substrings[0], string.findAnyOf(substrings, ignoreCase = true))
        assertEquals(9 to substrings[1], string.findAnyOf(substrings, startIndex = 3, ignoreCase = true))
        assertEquals(null, string.findAnyOf(substrings, startIndex = 10, ignoreCase = true))

        assertEquals(9 to substrings[1], string.findLastAnyOf(substrings, ignoreCase = true))
        assertEquals(2 to substrings[0], string.findLastAnyOf(substrings, startIndex = 8, ignoreCase = true))
        assertEquals(null, string.findLastAnyOf(substrings, startIndex = 1, ignoreCase = true))
    }

    @test fun indexOfChar() = withOneCharSequenceArg("bcedef") { string ->
        assertEquals(-1, string.indexOf('a'))
        assertEquals(2, string.indexOf('e'))
        assertEquals(2, string.indexOf('e', 2))
        assertEquals(4, string.indexOf('e', 3))
        assertEquals(4, string.lastIndexOf('e'))
        assertEquals(2, string.lastIndexOf('e', 3))

        for (startIndex in -1..string.length+1) {
            assertEquals(string.indexOfAny(charArrayOf('e'), startIndex), string.indexOf('e', startIndex))
            assertEquals(string.lastIndexOfAny(charArrayOf('e'), startIndex), string.lastIndexOf('e', startIndex))
        }

    }

    @test fun indexOfCharIgnoreCase() = withOneCharSequenceArg("bCEdef") { string ->
        assertEquals(-1, string.indexOf('a', ignoreCase = true))
        assertEquals(2, string.indexOf('E', ignoreCase = true))
        assertEquals(2, string.indexOf('e', 2, ignoreCase = true))
        assertEquals(4, string.indexOf('E', 3, ignoreCase = true))
        assertEquals(4, string.lastIndexOf('E', ignoreCase = true))
        assertEquals(2, string.lastIndexOf('e', 3, ignoreCase = true))


        for (startIndex in -1..string.length+1){
            assertEquals(string.indexOfAny(charArrayOf('e'), startIndex, ignoreCase = true), string.indexOf('E', startIndex, ignoreCase = true))
            assertEquals(string.lastIndexOfAny(charArrayOf('E'), startIndex, ignoreCase = true), string.lastIndexOf('e', startIndex, ignoreCase = true))
        }
    }

    @test fun indexOfString() = withOneCharSequenceArg("bceded") { string ->
        for (index in string.indices)
            assertEquals(index, string.indexOf("", index))
        assertEquals(1, string.indexOf("ced"))
        assertEquals(4, string.indexOf("ed", 3))
        assertEquals(-1, string.indexOf("abcdefgh"))
    }

    @test fun indexOfStringIgnoreCase() = withOneCharSequenceArg("bceded") { string ->
        for (index in string.indices)
            assertEquals(index, string.indexOf("", index, ignoreCase = true))
        assertEquals(1, string.indexOf("cEd", ignoreCase = true))
        assertEquals(4, string.indexOf("Ed", 3, ignoreCase = true))
        assertEquals(-1, string.indexOf("abcdefgh", ignoreCase = true))
    }


    @test fun contains() = withTwoCharSequenceArgs { arg1, arg2 ->
        operator fun String.contains(other: String): Boolean = arg1(this).contains(arg2(other))
        operator fun String.contains(other: Char): Boolean = arg1(this).contains(other)

        assertTrue("pl" in "sample")
        assertFalse("PL" in "sample")
        assertTrue(arg1("s??mple").contains(arg2("??"), ignoreCase = true))

        assertTrue("" in "sample")
        assertTrue("" in "")

        assertTrue('??' in "s??mple")
        assertFalse('??' in "s??mple")
        assertTrue(arg1("s??mple").contains('??', ignoreCase = true))
    }

    @test fun equalsIgnoreCase() {
        assertFalse("sample".equals("Sample", ignoreCase = false))
        assertTrue("sample".equals("Sample", ignoreCase = true))
        assertFalse("sample".equals(null, ignoreCase = false))
        assertFalse("sample".equals(null, ignoreCase = true))
        assertTrue(null.equals(null, ignoreCase = true))
        assertTrue(null.equals(null, ignoreCase = false))
    }


    @test fun replace() {
        val input = "abbAb"
        assertEquals("abb${'$'}b", input.replace('A', '$'))
        assertEquals("/bb/b", input.replace('A', '/', ignoreCase = true))

        assertEquals("${'$'}bAb", input.replace("ab", "$"))
        assertEquals("/b/", input.replace("ab", "/", ignoreCase = true))

        assertEquals("-a-b-b-A-b-", input.replace("", "-"))
    }

    @test fun replaceFirst() {
        val input = "AbbabA"
        assertEquals("Abb${'$'}bA", input.replaceFirst('a','$'))
        assertEquals("${'$'}bbabA", input.replaceFirst('a','$', ignoreCase = true))
        // doesn't pass in Rhino JS
        // assertEquals("schrodinger", "schr??dinger".replaceFirst('??', 'o', ignoreCase = true))

        assertEquals("Abba${'$'}", input.replaceFirst("bA", "$"))
        assertEquals("Ab${'$'}bA", input.replaceFirst("bA", "$", ignoreCase = true))

        assertEquals("-test", "test".replaceFirst("", "-"))
    }

    @test fun count() = withOneCharSequenceArg("hello there\tfoo\nbar") { text ->
        val whitespaceCount = text.count { it.isWhitespace() }
        assertEquals(3, whitespaceCount)
    }

    @test fun testSplitByChar() = withOneCharSequenceArg("ab\n[|^$&\\]^cd") { s ->
        s.split('b').let { list ->
            assertEquals(2, list.size)
            assertEquals("a", list[0])
            assertEquals("\n[|^$&\\]^cd", list[1])
        }
        s.split('^').let { list ->
            assertEquals(3, list.size)
            assertEquals("cd", list[2])
        }
        s.split('.').let { list ->
            assertEquals(1, list.size)
            assertEquals(s.toString(), list[0])
        }
    }

    @test fun forEach() = withOneCharSequenceArg("abcd1234") { data ->
        var count = 0
        val sb = StringBuilder()
        data.forEach {
            count++
            sb.append(it)
        }
        assertEquals(data.length, count)
        assertEquals(data.toString(), sb.toString())
    }


    @test fun filter() {
        assertEquals("acdca", ("abcdcba").filter { !it.equals('b') })
        assertEquals("1234", ("a1b2c3d4").filter { it.isAsciiDigit() })
    }

    @test fun filterCharSequence() = withOneCharSequenceArg { arg1 ->
        assertContentEquals("acdca", arg1("abcdcba").filter { !it.equals('b') })
        assertContentEquals("1234", arg1("a1b2c3d4").filter { it.isAsciiDigit() })
    }

    @test fun filterNot() {
        assertEquals("acdca", ("abcdcba").filterNot { it.equals('b') })
        assertEquals("abcd", ("a1b2c3d4").filterNot { it.isAsciiDigit() })
    }

    @test fun filterNotCharSequence() = withOneCharSequenceArg { arg1 ->
        assertContentEquals("acdca", arg1("abcdcba").filterNot { it.equals('b') })
        assertContentEquals("abcd", arg1("a1b2c3d4").filterNot { it.isAsciiDigit() })
    }

    @test fun filterIndexed() {
        val data = "abedcf"
        assertEquals("abdf", data.filterIndexed { index, c -> c == 'a' + index })
    }

    @test fun filterIndexedCharSequence() = withOneCharSequenceArg("abedcf") { data ->
        assertContentEquals("abdf", data.filterIndexed { index, c -> c == 'a' + index })
    }

    @test fun all() = withOneCharSequenceArg("AbCd") { data ->
        assertTrue {
            data.all { it.isAsciiLetter() }
        }
        assertFalse {
            data.all { it.isAsciiUpperCase() }
        }
    }

    @test fun any() = withOneCharSequenceArg("a1bc") { data ->
        assertTrue {
            data.any() { it.isAsciiDigit() }
        }
        assertFalse {
            data.any() { it.isAsciiUpperCase() }
        }
    }

    @test fun find() = withOneCharSequenceArg("a1b2c3") { data ->
        assertEquals('1', data.first { it.isAsciiDigit() })
        assertNull(data.firstOrNull { it.isAsciiUpperCase() })
    }

    @test fun findNot() = withOneCharSequenceArg("1a2b3c") { data ->
        assertEquals('a', data.filterNot { it.isAsciiDigit() }.firstOrNull())
        assertNull(data.filterNot { it.isAsciiLetter() || it.isAsciiDigit() }.firstOrNull())
    }

    @test fun partition() {
        val data = "a1b2c3"
        val pair = data.partition { it.isAsciiDigit() }
        assertEquals("123", pair.first, "pair.first")
        assertEquals("abc", pair.second, "pair.second")
    }

    @test fun partitionCharSequence() = withOneCharSequenceArg("a1b2c3") { data ->
        val pair = data.partition { it.isAsciiDigit() }
        assertContentEquals("123", pair.first, "pair.first")
        assertContentEquals("abc", pair.second, "pair.second")
    }

    @test fun map() = withOneCharSequenceArg { arg1 ->
        assertEquals(listOf('a', 'b', 'c'), arg1("abc").map { it })

        assertEquals(listOf(true, false, true), arg1("AbC").map { it.isAsciiUpperCase() })

        assertEquals(listOf<Boolean>(), arg1("").map { it.isAsciiUpperCase() })

        assertEquals(listOf(97, 98, 99), arg1("abc").map { it.toInt() })
    }

    @test fun mapTo() = withOneCharSequenceArg { arg1 ->
        val result1 = arrayListOf<Char>()
        val return1 = arg1("abc").mapTo(result1, { it })
        assertEquals(result1, return1)
        assertEquals(arrayListOf('a', 'b', 'c'), result1)

        val result2 = arrayListOf<Boolean>()
        val return2 = arg1("AbC").mapTo(result2, { it.isAsciiUpperCase() })
        assertEquals(result2, return2)
        assertEquals(arrayListOf(true, false, true), result2)

        val result3 = arrayListOf<Boolean>()
        val return3 = arg1("").mapTo(result3, { it.isAsciiUpperCase() })
        assertEquals(result3, return3)
        assertEquals(arrayListOf<Boolean>(), result3)

        val result4 = arrayListOf<Int>()
        val return4 = arg1("abc").mapTo(result4, { it.toInt() })
        assertEquals(result4, return4)
        assertEquals(arrayListOf(97, 98, 99), result4)
    }

    @test fun flatMap() = withOneCharSequenceArg("abcd") { data ->
        val result = data.flatMap { ('a'..it) + ' ' }
        assertEquals("a ab abc abcd ".toList(), result)
    }

    @test fun fold() = withOneCharSequenceArg { arg1 ->
        // calculate number of digits in the string
        val data = arg1("a1b2c3def")
        val result = data.fold(0, { digits, c -> if(c.isAsciiDigit()) digits + 1 else digits } )
        assertEquals(3, result)

        //simulate all method
        assertEquals(true, arg1("ABCD").fold(true, { r, c -> r && c.isAsciiUpperCase() }))

        //get string back
        assertEquals(data.toString(), data.fold("", { s, c -> s + c }))
    }

    @test fun foldRight() = withOneCharSequenceArg { arg1 ->
        // calculate number of digits in the string
        val data = arg1("a1b2c3def")
        val result = data.foldRight(0, { c, digits -> if(c.isAsciiDigit()) digits + 1 else digits })
        assertEquals(3, result)

        //simulate all method
        assertEquals(true, arg1("ABCD").foldRight(true, { c, r -> r && c.isAsciiUpperCase() }))

        //get string back
        assertEquals(data.toString(), data.foldRight("", { s, c -> "" + s + c }))
    }

    @test fun reduceIndexed() = withOneCharSequenceArg { arg1 ->
        // get the 3rd character
        assertEquals('c', arg1("bacfd").reduceIndexed { index, v, c -> if (index == 2) c else v })

        expect('c') {
            "ab".reduceIndexed { index, acc, e ->
                assertEquals(1, index)
                assertEquals('a', acc)
                assertEquals('b', e)
                e + (e - acc)
            }
        }

        assertTrue(assertFails {
            arg1("").reduceIndexed { index, a, b -> '\n' }
        } is UnsupportedOperationException)
    }

    @test fun reduceRightIndexed() = withOneCharSequenceArg { arg1 ->
        // get the 3rd character
        assertEquals('c', arg1("bacfd").reduceRightIndexed { index, c, v -> if (index == 2) c else v })

        expect('c') {
            "ab".reduceRightIndexed { index, e, acc ->
                assertEquals(0, index)
                assertEquals('b', acc)
                assertEquals('a', e)
                acc + (acc - e)
            }
        }

        assertTrue(assertFails {
            arg1("").reduceRightIndexed { index, a, b -> '\n' }
        } is UnsupportedOperationException)
    }

    @test fun reduce() = withOneCharSequenceArg { arg1 ->
        // get the smallest character(by char value)
        assertEquals('a', arg1("bacfd").reduce { v, c -> if (v > c) c else v })

        assertTrue(assertFails {
            arg1("").reduce { a, b -> '\n' }
        } is UnsupportedOperationException)
    }

    @test fun reduceRight() = withOneCharSequenceArg { arg1 ->
        // get the smallest character(by char value)
        assertEquals('a', arg1("bacfd").reduceRight { c, v -> if (v > c) c else v })

        assertTrue(assertFails {
            arg1("").reduceRight { a, b -> '\n' }
        } is UnsupportedOperationException)
    }

    @test fun groupBy() = withOneCharSequenceArg("abAbaABcD") { data ->
        // group characters by their case
        val result = data.groupBy { it.isAsciiUpperCase() }
        assertEquals(2, result.size)
        assertEquals(listOf('a','b','b','a','c'), result[false])
        assertEquals(listOf('A','A','B','D'), result[true])
    }

    @test fun joinToString() {
        val data = "abcd".toList()
        val result = data.joinToString("_", "(", ")")
        assertEquals("(a_b_c_d)", result)

        val data2 = "verylongstring".toList()
        val result2 = data2.joinToString("-", "[", "]", 11, "oops")
        assertEquals("[v-e-r-y-l-o-n-g-s-t-r-oops]", result2)

        val data3 = "a1/b".toList()
        val result3 = data3.joinToString() { it.toUpperCase().toString() }
        assertEquals("A, 1, /, B", result3)
    }

    @test fun joinTo() {
        val data = "kotlin".toList()
        val sb = StringBuilder()
        data.joinTo(sb, "^", "<", ">")
        assertEquals("<k^o^t^l^i^n>", sb.toString())
    }


    @test fun dropWhile() {
        val data = "ab1cd2"
        assertEquals("1cd2", data.dropWhile { it.isAsciiLetter() })
        assertEquals("", data.dropWhile { true })
        assertEquals("ab1cd2", data.dropWhile { false })
    }

    @test fun dropWhileCharSequence() = withOneCharSequenceArg("ab1cd2") { data ->
        assertContentEquals("1cd2", data.dropWhile { it.isAsciiLetter() })
        assertContentEquals("", data.dropWhile { true })
        assertContentEquals("ab1cd2", data.dropWhile { false })
    }


    @test fun drop() {
        val data = "abcd1234"
        assertEquals("d1234", data.drop(3))
        assertFails {
            data.drop(-2)
        }
        assertEquals("", data.drop(data.length + 5))
    }

    @test fun dropCharSequence() = withOneCharSequenceArg("abcd1234") { data ->
        assertContentEquals("d1234", data.drop(3))
        assertFails {
            data.drop(-2)
        }
        assertContentEquals("", data.drop(data.length + 5))
    }

    @test fun takeWhile() {
        val data = "ab1cd2"
        assertEquals("ab", data.takeWhile { it.isAsciiLetter() })
        assertEquals("", data.takeWhile { false })
        assertEquals("ab1cd2", data.takeWhile { true })
    }

    @test fun takeWhileCharSequence() = withOneCharSequenceArg("ab1cd2") { data ->
        assertContentEquals("ab", data.takeWhile { it.isAsciiLetter() })
        assertContentEquals("", data.takeWhile { false })
        assertContentEquals("ab1cd2", data.takeWhile { true })
    }

    @test fun take() {
        val data = "abcd1234"
        assertEquals("abc", data.take(3))
        assertFails {
            data.take(-7)
        }
        assertEquals(data, data.take(data.length + 42))
    }

    @test fun takeCharSequence() = withOneCharSequenceArg("abcd1234") { data ->
        assertEquals("abc", data.take(3))
        assertFails {
            data.take(-7)
        }
        assertContentEquals(data.toString(), data.take(data.length + 42))
    }


    @test fun testReplaceAllClosure() = withOneCharSequenceArg("test123zzz") { s ->
        val result = s.replace("\\d+".toRegex()) { mr ->
            "[" + mr.value + "]"
        }
        assertEquals("test[123]zzz", result)
    }

    @test fun testReplaceAllClosureAtStart() = withOneCharSequenceArg("123zzz") { s ->
        val result = s.replace("\\d+".toRegex()) { mr ->
            "[" + mr.value + "]"
        }
        assertEquals("[123]zzz", result)
    }

    @test fun testReplaceAllClosureAtEnd() = withOneCharSequenceArg("test123") { s ->
        val result = s.replace("\\d+".toRegex()) { mr ->
            "[" + mr.value + "]"
        }
        assertEquals("test[123]", result)
    }

    @test fun testReplaceAllClosureEmpty() = withOneCharSequenceArg("") { s ->
        val result = s.replace("\\d+".toRegex()) { mr ->
            "x"
        }
        assertEquals("", result)

    }

    @test fun trimMargin() {
        // WARNING
        // DO NOT REFORMAT AS TESTS MAY FAIL DUE TO INDENTATION CHANGE

        assertEquals("ABC\n123\n456", """ABC
                                      |123
                                      |456""".trimMargin())

        assertEquals("ABC\n  123\n  456", """ABC
                                      |123
                                      |456""".replaceIndentByMargin(newIndent = "  "))

        assertEquals("ABC \n123\n456", """ABC${" "}
                                      |123
                                      |456""".trimMargin())

        assertEquals(" ABC\n123\n456", """ ABC
                                        >>123
                                        ${"\t"}>>456""".trimMargin(">>"))

        assertEquals("", "".trimMargin())

        assertEquals("", """
                            """.trimMargin())

        assertEquals("", """
                            |""".trimMargin())

        assertEquals("", """
                            |
                            """.trimMargin())

        assertEquals("    a", """
            |    a
        """.trimMargin())

        assertEquals("    a", """
            |    a""".trimMargin())

        assertEquals("    a", """ |    a
        """.trimMargin())

        assertEquals("    a", """ |    a""".trimMargin())

        assertEquals("\u0000|ABC", "${"\u0000"}|ABC".trimMargin())
    }

    @test fun trimIndent() {
        // WARNING
        // DO NOT REFORMAT AS TESTS MAY FAIL DUE TO INDENTATION CHANGE

        assertEquals("123", """
        123
        """.trimIndent())

        assertEquals("123\n   456", """
        123
           456
           """.trimIndent())

        assertEquals("   123\n456", """
           123
        456
        """.trimIndent())

        assertEquals("     123\n  456", """
           123
        456
        """.replaceIndent(newIndent = "  "))

        assertEquals("   123\n456", """
           123
        456""".trimIndent())

        assertEquals("    ", """
${"    "}
        """.trimIndent())

        val deindented = """
                                                            ,.
                      ,.                     _       oo.   `88P
                     ]88b              ,o.  d88.    ]88b     '
                      888   _          Y888o888     d88P     _     _
                      888 ,888          `Y88888o_  ,888    d88b   d88._____
                      888,888P ,oooooo.   ;888888b.]88P    888'   d888888888p
                      888888P d88888888.  J88b'YPP ]88b   ,888    d888P'''888.
                      8888P' ]88P   `888  d88[     d88P   ]88b    888'    Y88b
                      8888p  ]88b    888  888      d88[    888    888.    `888
                     ,88888b  888[   888  888.     d88[    888.   Y88b     Y88[
                     d88PY88b `888L,d88P  Y88b     Y88b    ]88b   `888     888'
                     888  Y88b  Y88888P    888.     888.    888.   Y88b   `88P
                    d88P   888   `'P'      Y888.    `888.   `88P   `Y8P     '
                    Y8P'    '               `YP      Y8P'     '

                    ____       dXp   _    _        _________
                  ddXXXXXp     XXP  ,XX  dXb      Yo.XXXXXX      ,oooooo.
                  X'L_oXXP     XX'   XX[ dXb      dXb            YPPPPXXX'
                  XYXXXXX     ]XX    dXb dXb      dX8Xooooo         dXXP
                  XXb`YYXXo.   YXXo_ dXP dXP      YXb''''''       ,XXP'
                  `XX   `YYXb   `YXXXXP  XX[      ]XX            ,XX'
                   YXb     YXb     `''   XXXXooL  `XX._____      `XXXXXXXXooooo.
                   `XP      '             ''''''   YPXXXXXX'       ''''''`''YPPP
        """.trimIndent()

        assertEquals(23, deindented.lines().size)
        val indents = deindented.lines().map { "^\\s*".toRegex().find(it)!!.value.length }
        assertEquals(0, indents.min())
        assertEquals(42, indents.max())
        assertEquals(1, deindented.lines().count { it.isEmpty() })
    }

    @test fun testIndent() {
        assertEquals("  ABC\n  123", "ABC\n123".prependIndent("  "))
        assertEquals("  ABC\n  \n  123", "ABC\n\n123".prependIndent("  "))
        assertEquals("  ABC\n  \n  123", "ABC\n \n123".prependIndent("  "))
        assertEquals("  ABC\n   \n  123", "ABC\n   \n123".prependIndent("  "))
        assertEquals("  ", "".prependIndent("  "))
    }
}
