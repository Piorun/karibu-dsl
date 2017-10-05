package com.vaadin.starter.beveragebuddy.backend

import java.io.Serializable

/**
 * Represents a beverage category.
 * @property id
 * @property name the category name
 */
// must be open - Flow requires it to create ModelProxy
open class Category(open var id: Long? = null, var name: String = "") : Serializable {

    companion object {
        val UNDEFINED = Category(name = "(undefined)")
    }

    override fun toString() = "Category(id=$id, name='$name')"

    fun copy() = Category(id, name)
}
