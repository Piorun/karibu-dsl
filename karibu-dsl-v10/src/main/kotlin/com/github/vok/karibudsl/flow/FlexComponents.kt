package com.github.vok.karibudsl.flow

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.FlexLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * Sets the component's [FlexComponent.getFlexGrow]. Only works when the component is nested in a [FlexComponent].
 *
 * This defines the ability for a flex item to grow if necessary. It accepts a unitless value that serves as a proportion. It dictates what amount of the available space inside the flex container the item should take up.
 *
 * If all items have flex-grow set to 1, the remaining space in the container will be distributed equally to all children. If one of the children has a value of 2, the remaining space would take up twice as much space as the others (or it will try to, at least).
 *
 * Negative numbers are invalid.
 *
 * Get more information at [Guide to Flexbox](https://css-tricks.com/snippets/css/a-guide-to-flexbox/)
 */
var (@VaadinDsl Component).flexGrow: Double
    get() {
        val value = element.style.get("flexGrow")
        return if (value.isNullOrBlank()) 0.0 else value!!.toDouble()
    }
    set(value) {
        when {
            value == 0.0 -> element.style.remove("flexGrow")
            value > 0.0 -> element.style.set("flexGrow", value.toString())
            else -> throw IllegalArgumentException("Flex grow property cannot be negative: $flexGrow")
        }
    }

/**
 * Checks if the component expands when nested in [FlexComponent]. Alias for setting [flexGrow] to 1.0; see [flexGrow] for more information.
 */
var (@VaadinDsl Component).isExpand: Boolean
    get() = flexGrow > 0
    set(value) { flexGrow = if (value) 1.0 else 0.0 }

/**
 * The `flex-shrink` CSS property specifies the flex shrink factor of a flex item. Flex items will shrink to fill the container according
 * to the `flex-shrink` number, when the default size of flex items is larger than the flex container.
 *
 * Get more information at [flex-shrink](https://developer.mozilla.org/en-US/docs/Web/CSS/flex-shrink)
 */
var (@VaadinDsl Component).flexShrink: Double
    get() = element.style.get("flexShrink")?.toDouble() ?: 1.0
    set(value) {
        when (value) {
            1.0 -> element.style.remove("flexShrink")
            else -> element.style.set("flexShrink", value.toString())
        }
    }

/**
 * This defines the default size of an element before the remaining space is distributed. It can be a length (e.g. `20%`, `5rem`, etc.) or a keyword.
 * The `auto` keyword means "look at my width or height property".
 * The `content` keyword means "size it based on the item's content" - this keyword isn't well supported yet, so it's hard to test and harder
 * to know what its brethren `max-content`, `min-content`, and `fit-content` do.
 *
 * If set to `0`, the extra space around content isn't factored in. If set to `auto`, the extra space is distributed based on its flex-grow value.
 */
var (@VaadinDsl Component).flexBasis: String?
    get() = element.style.get("flexBasis")
    set(value) { element.style.set("flexBasis", value) }

/**
 * This allows the default alignment (or the one specified by [FlexComponent.getAlignItems] to be overridden for individual flex items.
 *
 * Please see the [FlexComponent.getAlignItems] explanation to understand the available values.
 */
var (@VaadinDsl Component).alignSelf: FlexComponent.Alignment
    get() = (parent.get() as FlexComponent<*>).getAlignSelf(this)
    set(value) { (parent.get() as FlexComponent<*>).setAlignSelf(value, this) }

/**
 * This aligns a flex container's lines within when there is extra space in the cross-axis, similar to how [FlexComponent.getJustifyContentMode] aligns individual
 * items within the main-axis.
 */
var (@VaadinDsl FlexLayout).alignContent: AlignContent
    get() = (this as FlexComponent<*>).alignContent
    set(value) { (this as FlexComponent<*>).alignContent = value }

// private since it would confuse users of VerticalLayout and HorizontalLayout which also implement FlexComponent.
private var (@VaadinDsl FlexComponent<*>).alignContent: AlignContent
    get() = AlignContent.fromFlexValue(element.style.get("alignContent"))
    set(value) { element.style.set("alignContent", value.flexValue) }

enum class AlignContent(val flexValue: String) {
    /**
     * lines packed to the start of the container
     */
    Start("flex-start"),
    /**
     * lines packed to the end of the container
     */
    End("flex-end"),
    /**
     * lines packed to the center of the container
     */
    Center("center"),
    /**
     * lines evenly distributed; the first line is at the start of the container while the last one is at the end
     */
    SpaceBetween("space-between"),
    /**
     * lines evenly distributed with equal space around each line
     */
    SpaceAround("space-around"),
    /**
     * (default): lines stretch to take up the remaining space
     */
    Stretch("stretch");

    companion object {
        fun fromFlexValue(flexValue: String?): AlignContent = values().firstOrNull { it.flexValue == flexValue } ?: Stretch
    }
}

/**
 * Gets or sets a vertical alignment for individual components inside the [HorizontalLayout].
 * This individual alignment for the component overrides any alignment set
 * at the [HorizontalLayout.setDefaultVerticalComponentAlignment].
 *
 * It effectively sets the `"alignSelf"` style value.
 *
 * The default alignment for individual components is
 * [FlexComponent.Alignment.AUTO].
 *
 * It's the same as the [Component.alignSelf] property.
 *
 * Note: this only works with [HorizontalLayout] - it will throw an exception if this component is nested in any other type of layout.
 */
var (@VaadinDsl Component).verticalAlignSelf: FlexComponent.Alignment
    get() = (parent.get() as HorizontalLayout).getVerticalComponentAlignment(this)
    set(value) { (parent.get() as HorizontalLayout).setVerticalComponentAlignment(value, this) }

/**
 * Gets or sets a horizontal alignment for individual components inside the [VerticalLayout].
 * This individual alignment for the component overrides any alignment set
 * at the [VerticalLayout.setDefaultHorizontalComponentAlignment].
 *
 * The default alignment for individual components is [FlexComponent.Alignment.AUTO].
 *
 * It's the same as the [FlexComponent.setAlignSelf] method.
 *
 * Note: this only works with [VerticalLayout] - it will throw an exception if this component is nested in any other type of layout.
 */
var (@VaadinDsl Component).horizontalAlignSelf: FlexComponent.Alignment
    get() = (parent.get() as VerticalLayout).getHorizontalComponentAlignment(this)
    set(value) { (parent.get() as VerticalLayout).setHorizontalComponentAlignment(value, this) }

/**
 * This aligns the container's components within when there is extra space. See [justify-content](https://css-tricks.com/snippets/css/a-guide-to-flexbox/#article-header-id-6)
 * for more details but rotate the sample image 90 degrees clockwise :-D
 *
 * Note: contrary to [VerticalLayout.setDefaultHorizontalComponentAlignment] this setting can not be overridden by individual children. This is a limitation of
 * the flex layout. That's why this property does not have the `default` prefix. Calling [verticalAlignSelf] on children will throw an exception.
 *
 * Note: This is just an alias for [FlexComponent.justifyContentMode] but the naming
 * of this property is more consistent with [horizontalAlignSelf] and [VerticalLayout.setDefaultHorizontalComponentAlignment].
 */
var (@VaadinDsl VerticalLayout).verticalContentAlignment: FlexComponent.JustifyContentMode
    get() = justifyContentMode
    set(value) { justifyContentMode = value }

/**
 * This aligns the container's components within when there is extra space. See [justify-content](https://css-tricks.com/snippets/css/a-guide-to-flexbox/#article-header-id-6)
 * for more details.
 *
 * Note: contrary to [HorizontalLayout.setDefaultVerticalComponentAlignment] this setting can not be overridden by individual children. This is a limitation of
 * the flex layout. That's why this property does not have the `default` prefix. Calling [horizontalAlignSelf] on children will throw an exception.
 *
 * Note: This is just an alias for [FlexComponent.justifyContentMode] but the naming
 * of this property is more consistent with [verticalAlignSelf] and [HorizontalLayout.setDefaultVerticalComponentAlignment].
 */
var (@VaadinDsl HorizontalLayout).horizontalComponentAlignment: FlexComponent.JustifyContentMode
    get() = justifyContentMode
    set(value) { justifyContentMode = value }

class HorizontalLayoutContentAlign(private val owner: HorizontalLayout) {
    /**
     * This aligns the container's components within when there is extra space. See [justify-content](https://css-tricks.com/snippets/css/a-guide-to-flexbox/#article-header-id-6)
     * for more details.
     *
     * Note: contrary to [v] this setting can not be overridden by individual children. This is a limitation of
     * the flex layout. Calling [horizontalAlignSelf] on children will throw an exception.
     *
     * Note: This is just an alias for [horizontalComponentAlignment].
     */
    var h: FlexComponent.JustifyContentMode
        get() = owner.horizontalComponentAlignment
        set(value) { owner.horizontalComponentAlignment = value }

    /**
     * The default vertical alignment to be used by all components without
     * individual alignments inside the layout. Individual components can be
     * aligned by using the [verticalAlignSelf] property.
     *
     * It effectively sets the `"alignItems"` style value.
     *
     * The default alignment is [vStart].
     *
     * It's the same as the [HorizontalLayout.getDefaultVerticalComponentAlignment] method.
     */
    var v: FlexComponent.Alignment
        get() = owner.defaultVerticalComponentAlignment
        set(value) { owner.defaultVerticalComponentAlignment = value }

    /**
     * Items are positioned at the beginning of the container.
     */
    val vStart = FlexComponent.Alignment.START

    /**
     * Items are positioned at the end of the container.
     */
    val vEnd = FlexComponent.Alignment.END

    /**
     * Items are positioned at the center of the container.
     */
    val vCenter = FlexComponent.Alignment.CENTER

    /**
     * Items are stretched to fit the container.
     */
    val vStretch = FlexComponent.Alignment.STRETCH

    /**
     * Items are positioned at the baseline of the container.
     */
    val vBaseline = FlexComponent.Alignment.BASELINE

    /**
     * Items are positioned at the beginning of the container.
     */
    val hStart = FlexComponent.JustifyContentMode.START

    /**
     * Items are positioned at the end of the container.
     */
    val hEnd = FlexComponent.JustifyContentMode.END

    /**
     * Items are positioned at the center of the container.
     */
    val hCenter = FlexComponent.JustifyContentMode.CENTER

    /**
     * Items are positioned with space between the lines.
     */
    val hBetween = FlexComponent.JustifyContentMode.BETWEEN

    /**
     * Items are positioned with space before, between, and after the lines.
     */
    val hAround = FlexComponent.JustifyContentMode.AROUND

    /**
     * Items have equal space around them.
     */
    val hEvenly = FlexComponent.JustifyContentMode.EVENLY

    /**
     * Centers all children inside of the layout. Equal to setting [vCenter] and [hCenter].
     */
    fun center() { v = vCenter; h = hCenter }
}

/**
 * Configures the general rules for positioning of child components inside of this [HorizontalLayout].
 *
 * Example of usage:
 * ```
 * horizontalLayout {
 *   contentAlign { h = hEnd; v = vCenter }
 * }
 * ```
 * Important notes:
 * * [HorizontalLayout] only supports one row of components; if you have multiple rows you need to use [FlexLayout].
 * * Never use [com.vaadin.flow.component.HasSize.setSizeFull] nor set the [com.vaadin.flow.component.HasSize.setWidth] to `100%` - it will
 * not work as you expect. With Vaadin 8 the child would fill the slot allocated by HorizontalLayout. However with Vaadin 10 and flex layout
 * there are no slots; setting the width to `100%` would make the component match the width of parent - it would set it to be as wide as
 * the HorizontalLayout is.
 *
 * To alter the layout further, call the following properties on children:
 *
 * * Most important: [flexGrow] (and its brother [isExpand]) expands that particular child to take up all of the remaining space. The child
 * is automatically enlarged.
 * * [verticalAlignSelf] to align child vertically; it is not possible to align particular child horizontally
 * * [flexShrink] - when there is not enough room for all children then they are shrank
 * * [flexBasis]
 */
fun (@VaadinDsl HorizontalLayout).contentAlign(block: HorizontalLayoutContentAlign.()->Unit) {
    HorizontalLayoutContentAlign(this).block()
}

class VerticalLayoutContentAlign(private val owner: VerticalLayout) {
    /**
     * The default horizontal alignment to be used by all components without
     * individual alignments inside the layout. Individual components can be
     * aligned by using the [horizontalAlignSelf] property.
     *
     * It effectively sets the `"alignItems"` style value.
     *
     * The default alignment is [hStart].
     *
     * It's the same as the [VerticalLayout.setDefaultHorizontalComponentAlignment] method.
     */
    var h: FlexComponent.Alignment
        get() = owner.defaultHorizontalComponentAlignment
        set(value) { owner.defaultHorizontalComponentAlignment = value }

    /**
     * This aligns the container's components within when there is extra space. See [justify-content](https://css-tricks.com/snippets/css/a-guide-to-flexbox/#article-header-id-6)
     * for more details.
     *
     * Note: contrary to [h] this setting can not be overridden by individual children. This is a limitation of
     * the flex layout. Calling [verticalAlignSelf] on children will throw an exception.
     *
     * Note: This is just an alias for [verticalContentAlignment].
     */
    var v: FlexComponent.JustifyContentMode
        get() = owner.verticalContentAlignment
        set(value) { owner.verticalContentAlignment = value }

    /**
     * Items are positioned at the beginning of the container.
     */
    val hStart = FlexComponent.Alignment.START

    /**
     * Items are positioned at the end of the container.
     */
    val hEnd = FlexComponent.Alignment.END

    /**
     * Items are positioned at the center of the container.
     */
    val hCenter = FlexComponent.Alignment.CENTER

    /**
     * Items are stretched to fit the container.
     */
    val hStretch = FlexComponent.Alignment.STRETCH

    /**
     * Items are positioned at the beginning of the container.
     */
    val vStart = FlexComponent.JustifyContentMode.START

    /**
     * Items are positioned at the end of the container.
     */
    val vEnd = FlexComponent.JustifyContentMode.END

    /**
     * Items are positioned at the center of the container.
     */
    val vCenter = FlexComponent.JustifyContentMode.CENTER

    /**
     * Items are positioned with space between the lines.
     */
    val vBetween = FlexComponent.JustifyContentMode.BETWEEN

    /**
     * Items are positioned with space before, between, and after the lines.
     */
    val vAround = FlexComponent.JustifyContentMode.AROUND

    /**
     * Items have equal space around them.
     */
    val vEvenly = FlexComponent.JustifyContentMode.EVENLY

    /**
     * Centers all children inside of the layout. Equal to setting [vCenter] and [hCenter].
     */
    fun center() { v = vCenter; h = hCenter }
}

/**
 * Configures the general rules for positioning of child components inside of this [VerticalLayout].
 *
 * Example of usage:
 * ```
 * verticalLayout {
 *   contentAlign { h = hEnd; v = vCenter }
 * }
 * ```
 * Important notes:
 * * [VerticalLayout] only supports one row of components; if you have multiple columns you need to use [FlexLayout].
 * * Never use [com.vaadin.flow.component.HasSize.setSizeFull] nor set the [com.vaadin.flow.component.HasSize.setHeight] to `100%` - it will
 * not work as you expect. With Vaadin 8 the child would fill the slot allocated by VerticalLayout. However with Vaadin 10 and flex layout
 * there are no slots; setting the height to `100%` would make the component match the height of parent - it would set it to be as tall as
 * the VerticalLayout is.
 *
 * To alter the layout further, call the following properties on children:
 *
 * * Most important: [flexGrow] (and its brother [isExpand]) expands that particular child to take up all of the remaining space. The child
 * is automatically enlarged.
 * * [verticalAlignSelf] to align child vertically; it is not possible to align particular child horizontally
 * * [flexShrink] - when there is not enough room for all children then they are shrank
 * * [flexBasis]
 */
fun (@VaadinDsl VerticalLayout).contentAlign(block: VerticalLayoutContentAlign.()->Unit) {
    VerticalLayoutContentAlign(this).block()
}

/**
 * By default, flex items will all try to fit onto one line. You can change that and allow the items to wrap as needed with this property.
 */
enum class FlexWrap(val flexValue: String) {
    /**
     * nowrap (default): all flex items will be on one line
     */
    Nowrap("nowrap"),
    /**
     * wrap: flex items will wrap onto multiple lines, from top to bottom.
     */
    Wrap("wrap"),
    /**
     * wrap-reverse: flex items will wrap onto multiple lines from bottom to top.
     */
    WrapReverse("wrap-reverse");
    companion object {
        fun findByFlex(flexValue: String?) = when {
            flexValue == null -> null
            else -> values().firstOrNull { it.flexValue == flexValue }
        }
    }
}

/**
 * By default, flex items will all try to fit onto one line. You can change that and allow the items to wrap as needed with this property.
 */
var (@VaadinDsl FlexLayout).flexWrap: FlexWrap
    get() = FlexWrap.findByFlex(element.style.get("flexWrap")) ?: FlexWrap.Wrap
    set(value) {
        when (value) {
            FlexWrap.Nowrap -> element.style.remove("flexWrap")
            else -> element.style.set("flexWrap", value.flexValue)
        }
    }
