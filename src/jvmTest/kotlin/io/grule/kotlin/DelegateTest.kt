package io.grule.kotlin

import org.junit.Test
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class DelegateTest {
    @Test
    fun test() {
        val value by CustomProperty()
        println("value: $value")
        println("value: $value")
    }
    
    private class CustomProperty: ReadOnlyProperty<Any?, String> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): String {
            println("thisRef: $thisRef")
            println("property: ${property.name}")
            return "hello"
        }
    }
}