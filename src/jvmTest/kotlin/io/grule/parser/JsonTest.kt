package io.grule.parser

import io.grule.Grammar
import org.junit.Test

class JsonTest : Grammar() {
    val string by lexer { X + '"' + ANY.untilNonGreedy(X + '"') }
    val number by lexer { X + DIGIT.repeat(1) + (X + "." + DIGIT.repeat(1)).optional() }
    val bool by lexer { X + "true" or X + "false" }
    val nil by lexer { X + "null" }

    init {
        lexer { X - "{}[]:," }
        lexer { SPACE }
    }

    val jObject: Parser by p { jString or jNumber or jBool or jNil or jArray or jDict }
    val jString by P + string
    val jNumber by P + number
    val jBool by P + bool
    val jNil by P + nil
    val jArray by P + "[" + jObject.join(P + ",") + "]"
    val jPair by P + jString + ":" + jObject
    val jDict by P + "{" + jPair.join(P + ",") + "}"

    @Test
    fun json() {
        val source = """{ "a": [1, 2.34], "b": "hello" }"""
        println(source)
        println("-----------------")

        val astNode = parse(jObject, source)
        println(astNode.toStringTree())
    }
}
