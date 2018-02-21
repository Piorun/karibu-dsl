package com.github.vok.karibudsl.flow

import com.vaadin.flow.component.*

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@DslMarker
annotation class VaadinDsl

private val fireEventMethod = Component::class.java.getDeclaredMethod("fireEvent", ComponentEvent::class.java)
fun Component.fireEvent(event: ComponentEvent<*>) {
    fireEventMethod.isAccessible = true
    fireEventMethod.invoke(this, event)
}
fun <R> R.serverClick() where R : Component, R : HasClickListeners<R> {
    fireEvent(HasClickListeners.ClickEvent<R>(this, true))
}

fun (@VaadinDsl HasComponents).text(text: String, block: (@VaadinDsl Text).() -> Unit = {}) = init(Text(text), block)
