/*
 * Copyright 2000-2013 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.vok.karibudsl.example

import com.github.vok.karibudsl.AutoView
import com.github.vok.karibudsl.addStyleNames
import com.github.vok.karibudsl.label
import com.github.vok.karibudsl.tree
import com.vaadin.data.TreeData
import com.vaadin.data.provider.HierarchicalDataProvider
import com.vaadin.data.provider.TreeDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.server.SerializablePredicate
import com.vaadin.ui.Grid
import com.vaadin.ui.Notification
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import java.util.*

@AutoView
class Trees : VerticalLayout(), View {
    init {
        isSpacing = false

        label("Trees") {
            addStyleNames(ValoTheme.LABEL_H2, ValoTheme.LABEL_COLORED)
        }
        tree<Int> {
            setSelectionMode(Grid.SelectionMode.MULTI)
            dataProvider = generateTestData()
            val strings = arrayOf("lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "quid", "securi", "etiam", "tamquam", "eu", "fugiat", "nulla", "pariatur")
            setItemCaptionGenerator { strings[it % strings.size] }
            setItemIconGenerator { VaadinIcons.values()[it % VaadinIcons.values().size] }
        }
    }

    override fun enter(event: ViewChangeEvent) {}
}

private fun generateTestData(): HierarchicalDataProvider<Int, SerializablePredicate<Int>> {
    fun TreeData<Int>.populate(parent: Int?, depth: Int) {
        if (depth > 3) return
        (0..Random().nextInt(10)).forEach {
            val id = (parent ?: 1) * 10 + it
            addItem(parent, id)
            populate(id, depth + 1)
        }
    }
    val treeData = TreeData<Int>()
    treeData.populate(null, 0)
    return TreeDataProvider<Int>(treeData)
}
