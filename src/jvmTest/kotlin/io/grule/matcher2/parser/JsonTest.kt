package io.grule.matcher2.parser

import io.grule.lexer.LexerFactory2
import org.junit.Test

class JsonTest {
    val source = """{ "a": [1, 2.34], "b": "hello" }"""
    val lexer = LexerFactory2()
    val string by lexer { X - '"' + any.untilNonGreedy(X - '"') }
    val number by lexer { X + digit.repeat(1) + (X + "." + digit.repeat(1)).optional() }
    val bool by lexer { X + "true" or X + "false" }
    val nil by lexer { X + "null" }

    init {
        lexer.token { X - "{}[]:," }
        lexer.skip { space }
    }

    val parser = ParserFactory2()
    val jObject: Parser by parser { X + jString or jNumber or jBool or jNil or jArray or jDict }
    val jString by parser { X + string }
    val jNumber by parser { X + number }
    val jBool by parser { X + bool }
    val jNil by parser { X + nil }
    val jArray by parser { X + "[" + jObject.join(X + ",") + "]" }
    val jPair by parser { X + jString + ":" + jObject }
    val jDict by parser { X + "{" + jPair.join(X + ",") + "}" }

    @Test
    fun test() {
        println(source)
        println("-----------------")

        val astNode = jObject.parse(lexer.tokenStream(source))
        println(astNode.toStringTree())
    }
}