# Grule

[![](https://jitpack.io/v/7hens/grule.svg)](https://jitpack.io/#7hens/grule)
![GitHub](https://img.shields.io/github/license/7hens/grule)

Grule is Kotlin multiplatform library as a general-purpose parser.

NOTE: grule is experimental yet.

## Sample - Json parser

```kotlin
class JsonRegLexerTest: Grule() {
    val string by token(L / """(".*?")""")
    val number by token(L / "\\d+(\\.\\d+)?")
    val bool by token(L / "true|false")
    val nil by token(L / "null")

    init {
        token(L - "{}[]:,")
        skip(L + L_space or L_wrap)
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

```

Output

```plain
{ "a": [1, 2.34], "b": "hello" }
-----------------
jObject
 └─ jDict
     ├─ ({)
     ├─ jPair
     │   ├─ jString
     │   │   └─ string ("a")
     │   ├─ (:)
     │   └─ jObject
     │       └─ jArray
     │           ├─ ([)
     │           ├─ jObject
     │           │   └─ jInteger
     │           │       └─ integer (1)
     │           ├─ (,)
     │           ├─ jObject
     │           │   └─ jFloat
     │           │       └─ float (2.34)
     │           └─ (])
     ├─ (,)
     ├─ jPair
     │   ├─ jString
     │   │   └─ string ("b")
     │   ├─ (:)
     │   └─ jObject
     │       └─ jString
     │           └─ string ("hello")
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
