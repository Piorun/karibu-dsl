package com.github.vok.karibudsl

import com.vaadin.server.*
import com.vaadin.ui.UI
import java.util.*

object MockVaadin {
    // prevent GC on Vaadin Session and Vaadin UI as they are only soft-referenced from the Vaadin itself.
    private val strongRefSession = ThreadLocal<VaadinSession>()
    private val strongRefUI = ThreadLocal<UI>()
    /**
     * Creates new mock session and UI for a test. Just call this in your @Before
     */
    fun setup() {
        val config = DefaultDeploymentConfiguration(MockVaadin::class.java, Properties())
        val session = VaadinSession(VaadinServletService(VaadinServlet(), config))
        VaadinSession.setCurrent(session)
        strongRefSession.set(session)
        val ui = object : UI() {
            override fun init(request: VaadinRequest?) {
            }
        }
        strongRefUI.set(ui)
        UI.setCurrent(ui)
    }
}
