/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.reference.browser.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_tabstray.*
import mozilla.components.browser.session.Session
import mozilla.components.browser.session.SessionManager
import mozilla.components.feature.tabs.tabstray.TabsFeature
import mozilla.components.support.base.feature.UserInteractionHandler
import org.mozilla.reference.browser.R
import org.mozilla.reference.browser.ext.isFreshTab
import org.mozilla.reference.browser.ext.nav
import org.mozilla.reference.browser.ext.requireComponents

/**
 * A fragment for displaying the tabs tray.
 */
class TabsTrayFragment : Fragment(), UserInteractionHandler, SessionManager.Observer {
    private var tabsFeature: TabsFeature? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_tabstray, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabsFeature = TabsFeature(
            tabsTray,
            requireComponents.core.sessionManager,
            requireComponents.useCases.tabsUseCases,
            ::closeTabsTray)

        tabsPanel.initialize(tabsFeature, ::closeTabsTray, ::openFreshTabFragment, ::openBrowserFragment)
    }

    override fun onStart() {
        super.onStart()

        tabsFeature?.start()
        requireComponents.core.sessionManager.register(this)
    }

    override fun onStop() {
        super.onStop()

        tabsFeature?.stop()
        requireComponents.core.sessionManager.unregister(this)
    }

    override fun onSessionRemoved(session: Session) {
        super.onSessionRemoved(session)
        requireComponents.core.sessionManager.apply {
            if (size == 0) {
                add(Session(""), selected = true)
            }
        }
    }

    override fun onAllSessionsRemoved() {
        super.onAllSessionsRemoved()
        requireComponents.core.sessionManager.apply {
            add(Session(""), selected = true)
        }
    }

    override fun onBackPressed(): Boolean {
        closeTabsTray()
        return true
    }

    private fun closeTabsTray() {
        if (tabsPanel.isTrayEmpty()) {
            requireComponents.useCases.tabsUseCases.addTab.invoke("")
            openFreshTabFragment()
        } else {
            val selectedSession = requireComponents.core.sessionManager.selectedSession
            if (selectedSession != null && selectedSession.isFreshTab()) {
                openFreshTabFragment()
            } else {
                openBrowserFragment()
            }
        }
    }

    private fun openFreshTabFragment() {
        val direction = TabsTrayFragmentDirections.actionTabsTrayFragmentToFreshTabFragment()
        nav(R.id.tabsTrayFragment, direction)
    }

    private fun openBrowserFragment() {
        val direction =
            TabsTrayFragmentDirections.actionTabsTrayFragmentToBrowserFragment(sessionId = null)
        nav(R.id.tabsTrayFragment, direction)
    }
}
