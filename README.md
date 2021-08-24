# Grule

[![](https://jitpack.io/v/7hens/grule.svg)](https://jitpack.io/#7hens/grule)
![GitHub](https://img.shields.io/github/license/7hens/grule)

Grule is Kotlin multiplatform library as a general-purpose parser.

NOTE: grule is experimental yet.

## Sample - Json parser

```kotlin
val source = """{ "a": [1, 2.34], "b": "hello" }"""
println(source)
println("-----------------")

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

    val charStream = CharReader.fromString(source).toStream()
    val astNode = jObject.parse(charStream)
    println(astNode.toStringTree())
}
```

Output

```plain
{ "a": [1, 2.34], "b": "hello" }
-----------------
jObject
└── jDict
    ├── {
    ├── jPair
    │   ├── jString
    │   │   └── string ("a")
    │   ├── :
    │   └── jObject
    │       └── jArray
    │           ├── [
    │           ├── jObject
    │           │   └── jInteger
    │           │       └── integer (1)
    │           ├── ,
    │           ├── jObject
    │           │   └── jFloat
    │           │       └── float (2.34)
    │           └── ]
    ├── ,
    ├── jPair
    │   ├── jString
    │   │   └── string ("b")
    │   ├── :
    │   └── jObject
    │       └── jString
    │           └── string ("hello")
    └── }
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
    implementation("com.github.7hens.grule:grule:0.1-SNAPSHOT")
}
```
