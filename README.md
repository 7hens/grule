# Grule

[![](https://jitpack.io/v/7hens/grule.svg)](https://jitpack.io/#7hens/grule)

Grule is Kotlin multiplatform library as a general-purpose parser.

NOTE: grule is experimental yet. Any api is a subject to change.

## Sample - Json parser

```kotlin
val input = """{ "a": [1, 2.34], "b": "hello" }"""
val charStream = CharStream(CharReader.fromString(input))

Grule {
    val string by TokenL + '"' + ANY.until(L + '"')
    val float by TokenL + DIGIT.repeat(1) + "." + DIGIT.repeat(1)
    val integer by TokenL + DIGIT.repeat(1)
    val bool by TokenL + "true" or L + "false"
    val nil by TokenL + "null"

    TokenL + -"{}[]:,"
    SkipL + SPACE or LINE

    val jObject by P
    val jString by P + string
    val jInteger by P + integer
    val jFloat by P + float
    val jBool by P + bool
    val jNil by P + nil
    val jArray by P + "[" + jObject.repeatWith(P + ",").optional() + "]"
    val jPair by P + jString + ":" + jObject
    val jDict by P + "{" + jPair.repeatWith(P + ",").optional() + "}"
    jObject or jString or jFloat or jInteger or jBool or jNil or jArray or jDict

    val astNode = jObject.parse(charStream)
    println(astNode)
}
```

<details>
<summary>Outputs</summary>

```plain
[
  jDict = [
    [
      { = [
        '{' [1:1] $LexerBuilder
      ]
      jPair = [
        [
          jString = [
            [
              string = [
                '"a"' [1:3] string
              ]
            ]
          ]
          : = [
            ':' [1:6] $LexerBuilder
          ]
          jObject = [
            [
              jArray = [
                [
                  [ = [
                    '[' [1:8] $LexerBuilder
                  ]
                  jObject = [
                    [
                      jInteger = [
                        [
                          integer = [
                            '1' [1:9] integer
                          ]
                        ]
                      ]
                    ]
                    [
                      jFloat = [
                        [
                          float = [
                            '2.34' [1:12] float
                          ]
                        ]
                      ]
                    ]
                  ]
                  , = [
                    ',' [1:10] $LexerBuilder
                  ]
                  ] = [
                    ']' [1:16] $LexerBuilder
                  ]
                ]
              ]
            ]
          ]
        ]
        [
          jString = [
            [
              string = [
                '"b"' [1:19] string
              ]
            ]
          ]
          : = [
            ':' [1:22] $LexerBuilder
          ]
          jObject = [
            [
              jString = [
                [
                  string = [
                    '"hello"' [1:24] string
                  ]
                ]
              ]
            ]
          ]
        ]
      ]
      , = [
        ',' [1:17] $LexerBuilder
      ]
      } = [
        '}' [1:32] $LexerBuilder
      ]
    ]
  ]
]
```

</details>
