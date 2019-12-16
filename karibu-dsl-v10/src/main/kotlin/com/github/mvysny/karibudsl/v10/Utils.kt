package com.github.mvysny.karibudsl.v10

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.dom.ClassList
import com.vaadin.flow.dom.DomEventListener
import com.vaadin.flow.dom.DomListenerRegistration
import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.Method

fun String.containsWhitespace(): Boolean = any { it.isWhitespace() }

private val messages = mapOf("cantConvertToInteger" to "Can't convert to integer",
        "cantConvertToDecimal" to "Can't convert to decimal number",
        "from" to "From:",
        "to" to "To:",
        "set" to "Set",
        "clear" to "Clear",
        "all" to "All"
)
/**
 * Change this function to provide a proper i18n for your apps. For a list of all keys used by Karibu-DSL see [messages].
 */
var karibuDslI18n: (key: String) -> String = { key -> messages[key] ?: key }

/**
 * Returns the getter method for given property name; fails if there is no such getter.
 */
fun Class<*>.getGetter(propertyName: String): Method {
    val descriptors: Array<out PropertyDescriptor> = Introspector.getBeanInfo(this).propertyDescriptors
    val descriptor: PropertyDescriptor? = descriptors.firstOrNull { it.name == propertyName }
    requireNotNull(descriptor) { "No such field '$propertyName' in $this; available properties: ${descriptors.joinToString { it.name }}" }
    val getter: Method = requireNotNull(descriptor.readMethod) { "The $this.$propertyName property does not have a getter: $descriptor" }
    return getter
}
