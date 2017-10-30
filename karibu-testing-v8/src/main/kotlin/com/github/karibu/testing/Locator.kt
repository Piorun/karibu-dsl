package com.github.karibu.testing

import com.github.vok.karibudsl.hasStyleName
import com.github.vok.karibudsl.walk
import com.vaadin.data.HasValue
import com.vaadin.ui.Button
import com.vaadin.ui.Component
import com.vaadin.ui.HasComponents
import com.vaadin.ui.UI

/**
 * Finds a VISIBLE component which matches all of given predicates. This component and all of its descendants are searched.
 * @param id the required [Component.getId]; if null, no particular id is matched.
 * @param caption the required [Component.getCaption]; if null, no particular caption is matched.
 * @param predicates the predicates the component needs to match, not null. May be empty - in such case all components will match.
 * @param styles if not null, the component must match all of these styles. Space-separated.
 * @return the only matching component, never null.
 * @throws IllegalArgumentException if no component matched, or if more than one component matches.
 */
fun <T: Component> Component._get(clazz: Class<T>, id: String? = null, caption: String? = null, styles: String? = null, vararg predicates: (Component)->Boolean = arrayOf()): T {

    fun getCriteria(): String {
        val list = mutableListOf<String>(if (clazz.simpleName.isBlank()) clazz.name else clazz.simpleName)
        if (id != null) list.add("id='$id'")
        if (caption != null) list.add("caption='$caption'")
        if (!styles.isNullOrBlank()) list.add("styles='$styles'")
        if (!predicates.isEmpty()) list.addAll(predicates.map { it.toString() })
        return list.joinToString(" and ")
    }

    val result = _find(clazz, id, caption, styles, *predicates)
    if (result.isEmpty()) {
        throw IllegalArgumentException("No visible ${clazz.simpleName} in ${toPrettyString()} matching ${getCriteria()}. Component tree:\n${toPrettyTree()}")
    }
    if (result.size > 1) {
        throw IllegalArgumentException("Too many components in ${toPrettyString()} matching ${getCriteria()}: ${result.joinToString { it.toPrettyString() }}. Component tree:\n${toPrettyTree()}")
    }
    return clazz.cast(result.get(0))
}

/**
 * Finds a VISIBLE component in the current UI which matches all of given predicates. This component and all of its descendants are searched.
 * @param id the required [Component.getId]; if null, no particular id is matched.
 * @param caption the required [Component.getCaption]; if null, no particular caption is matched.
 * @param predicates the predicates the component needs to match, not null. May be empty - in such case all components will match.
 * @param styles if not null, the component must match all of these styles. Space-separated.
 * @return the only matching component, never null.
 * @throws IllegalArgumentException if no component matched, or if more than one component matches.
 */
inline fun <reified T: Component> _get(id: String? = null, caption: String? = null, styles: String? = null, vararg predicates: (Component)->Boolean = arrayOf()): T =
        UI.getCurrent()._get(T::class.java, id, caption, styles, *predicates)

/**
 * Finds a list of VISIBLE components which matches all of given predicates. This component and all of its descendants are searched.
 * @param id the required [Component.getId]; if null, no particular id is matched.
 * @param caption the required [Component.getCaption]; if null, no particular caption is matched.
 * @param predicates the predicates the component needs to match, not null. May be empty - in such case all components will match.
 * @return the list of matching components, may be empty.
 */
fun <T: Component> Component._find(clazz: Class<T>, id: String? = null, caption: String? = null, styles: String? = null, vararg predicates: (Component)->Boolean = arrayOf()): List<T> {
    val p = mutableListOf<(Component)->Boolean>()
    p.add({ component -> clazz.isInstance(component)} )
    if (id != null) p.add({ component -> component.id == id })
    if (caption != null) p.add({ component -> component.caption == caption })
    if (!styles.isNullOrBlank()) p.add({ component -> component.hasStyleName(styles!!) })
    p.addAll(predicates)

    val result = find(p.and())
    return result.filterIsInstance(clazz)
}

/**
 * Finds a list of VISIBLE components in the current UI which matches all of given predicates. This component and all of its descendants are searched.
 * @param id the required [Component.getId]; if null, no particular id is matched.
 * @param caption the required [Component.getCaption]; if null, no particular caption is matched.
 * @param predicates the predicates the component needs to match, not null. May be empty - in such case all components will match.
 * @return the list of matching components, may be empty.
 */
inline fun <reified T: Component> _find(id: String? = null, caption: String? = null, styles: String? = null, vararg predicates: (Component)->Boolean = arrayOf()): List<T> =
        UI.getCurrent()._find(T::class.java, id, caption, styles, *predicates)

/**
 * Clicks the button, but only if it is actually possible to do so by the user. If the button is read-only or disabled, throws an exception.
 * @param id the required [Component.getId]; if null, no particular id is matched.
 * @param caption the required [Component.getCaption]; if null, no particular caption is matched.
 * @param predicates the predicates the component needs to match, not null. May be empty - in such case all components will match.
 * @return the button if it was visible, enabled, not read-only and it was clicked
 * @throws IllegalArgumentException if the button was not visible, not enabled, read-only or if no button (or too many buttons) matched.
 */
fun Button._click() {
    if (!isEffectivelyVisible()) {
        throw IllegalArgumentException("The button ${toPrettyString()} is not effectively visible - either it is hidden, or its ascendant is hidden")
    }
    if (!isEnabled) {
        throw IllegalArgumentException("The button ${toPrettyString()} is not enabled")
    }
    if (!isConnectorEnabled) {
        throw IllegalArgumentException("The button ${toPrettyString()} is nested in a disabled component")
    }
    if (this is HasValue<*> && this.isReadOnly) {
        throw IllegalArgumentException("The button ${toPrettyString()} is read-only")
    }
    click()
}

private fun Component.isEffectivelyVisible(): Boolean = isVisible && (parent == null || parent.isEffectivelyVisible())

private fun Component.find(predicate: (Component)->Boolean): List<Component> = walk().filter { predicate(it) }

private fun <T: Component> Iterable<(T)->Boolean>.and(): (T)->Boolean = { component -> all { it(component) } }
