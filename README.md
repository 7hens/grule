# Grule

[![](https://jitpack.io/v/7hens/grule.svg)](https://jitpack.io/#7hens/grule)
![GitHub](https://img.shields.io/github/license/7hens/grule)

Grule is Kotlin multiplatform library as a general-purpose parser.

NOTE: grule is experimental yet.

## Sample - Json parser

```kotlin
class JsonRegMatcherTest : Grammar() {
    val string by lexer { X / """(".*?")""" }
    val number by lexer { X / "\\d+(\\.\\d+)?" }
    val bool by lexer { X / "true|false" }
    val nil by lexer { X / "null" }

    init {
        lexer.token { X - "{}[]:," }
        lexer.skip { SPACE or WRAP }
    }

    val jObject: Parser by parser { jString or jNumber or jBool or jNil or jArray or jDict }
    val jString by parser { X + string }
    val jNumber by parser { X + number }
    val jBool by parser { X + bool }
    val jNil by parser { X + nil }
    val jArray by parser { X + "[" + jObject.join(X + ",") + "]" }
    val jPair by parser { X + jString + ":" + jObject }
    val jDict by parser { X + "{" + jPair.join(X + ",") + "}" }

    @Test
    fun json() {
        val source = """{ "a": [1, 2.34], "b": "hello" }"""
        println(source)
        println("-----------------")

        val astNode = jObject.parse(this, source)
        println(astNode.toStringTree())
    }
}
```

Output

```plain
{ "a": [1, 2.34], "b": "hello" }
-----------------
jObject/jDict
 ├─ ({)
 ├─ jPair
 │   ├─ jString/string ("a")
 │   ├─ (:)
 │   └─ jObject/jArray
 │       ├─ ([)
 │       ├─ jObject/jNumber/number (1)
 │       ├─ (,)
 │       ├─ jObject/jNumber/number (2.34)
 │       └─ (])
 ├─ (,)
 ├─ jPair
 │   ├─ jString/string ("b")
 │   ├─ (:)
 │   └─ jObject/jString/string ("hello")
 └─ (})
```

## Setting up the dependency

Add it in your root build.gradle at the end of repositories:

```kotlin
allprojects {
    repositories {
        maven("https://jitpack.io")
    }
}
```

```kotlin
dependencies {
    implementation("com.github.7hens.grule:grule:0.1")
}
```
