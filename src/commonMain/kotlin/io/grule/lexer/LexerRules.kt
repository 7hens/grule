package io.grule.lexer

object LexerRules {
    val x: Lexer = LexerShadow

    val any = x + LexerCharSet.ANY
    val digit = x - ('0'..'9')
    val bit = x - "01"
    val octal = x - ('0'..'7')
    val hex = x - "0123456789ABCDEFabcdef"
    val upper = x - ('A'..'Z')
    val lower = x - ('a'..'z')
    val letter = x + upper or lower
    val word = x + letter or digit or x + '_'
    val space = x - "\t\r\n\u0085\u000B\u000C "
    val wrap = x + "\r\n" or x - "\r\n"
    val eof = Lexer.EOF
}
