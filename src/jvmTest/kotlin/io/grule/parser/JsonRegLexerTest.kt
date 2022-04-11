package io.grule.parser

import io.grule.Grule
import org.junit.Test

class JsonRegLexerTest: Grule() {
    val string by token(L / """("[^"]+")""")
    val float by token(L / "\\d+\\.\\d+")
    val integer by token(L / "\\d+")
    val bool by token(L / "true|false")
    val nil by token(L / "null")

    init {
        token(L - "{}[]:,")
        skip(L + L_space or L_wrap)
    }

    val jObject: Parser by p { jObject or jString or jFloat or jInteger or jBool or jNil or jArray or jDict }
    val jString by P + string
    val jInteger by P + integer
    val jFloat by P + float
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
