/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.reference.browser.freshtab.toolbar

import android.content.Context
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mozilla.components.browser.menu.BrowserMenuBuilder
import mozilla.components.browser.menu.BrowserMenuItem
import mozilla.components.browser.menu.item.BrowserMenuItemToolbar
import mozilla.components.browser.menu.item.SimpleBrowserMenuItem
import mozilla.components.browser.session.SessionManager
import org.mozilla.reference.browser.R
import org.mozilla.reference.browser.freshtab.FreshTabIntegration
import org.mozilla.reference.browser.freshtab.FreshTabToolbar

interface ToolbarMenuInteractor {

    fun onForwardClicked()

    fun onNewTabClicked()

    fun onNewForgetTabClicked()

    fun onReportIssueClicked()

    fun onSettingsClicked()

    fun onHistoryClicked()

    fun onBookmarksClicked()

    fun onClearDataClicked()
}

interface SearchBarInteractor {

    fun onSearchBarClicked()
}

class ToolbarFeature(
    private val context: Context,
    private val scope: CoroutineScope,
    private val sessionManager: SessionManager,
    private val freshTabToolbar: FreshTabToolbar,
    private val toolbarMenuInteractor: ToolbarMenuInteractor,
    private val searchBarInteractor: SearchBarInteractor
) {

    private val menuToolbar by lazy {

        val forward = BrowserMenuItemToolbar.TwoStateButton(
            primaryImageResource = mozilla.components.ui.icons.R.drawable.mozac_ic_forward,
            primaryImageTintResource = R.color.icons,
            primaryContentDescription = context.getString(R.string.toolbar_menu_item_forward),
            isInPrimaryState = {
                sessionManager.selectedSession?.canGoForward == true
            },
            secondaryImageTintResource = R.color.disabled_icons,
            disableInSecondaryState = true,
            listener = toolbarMenuInteractor::onForwardClicked
        )

        val refresh = BrowserMenuItemToolbar.Button(
            mozilla.components.ui.icons.R.drawable.mozac_ic_refresh,
            iconTintColorResource = R.color.disabled_icons,
            contentDescription = context.getString(R.string.toolbar_menu_item_refresh),
            isEnabled = { false },
            listener = {}
        )

        val share = BrowserMenuItemToolbar.Button(
            mozilla.components.ui.icons.R.drawable.mozac_ic_share,
            iconTintColorResource = R.color.disabled_icons,
            contentDescription = context.getString(R.string.toolbar_menu_item_share),
            isEnabled = { false },
            listener = {}
        )

        val bookmark = BrowserMenuItemToolbar.Button(
            R.drawable.ic_bookmark,
            iconTintColorResource = R.color.disabled_icons,
            contentDescription = context.getString(R.string.toolbar_menu_item_bookmark),
            isEnabled = { false },
            listener = {}
        )

        BrowserMenuItemToolbar(listOf(forward, refresh, bookmark, share))
    }

    private val menuItems: List<BrowserMenuItem> by lazy {
        listOf(
            menuToolbar,
            SimpleBrowserMenuItem(context.getString(R.string.toolbar_menu_item_new_tab)) {
                toolbarMenuInteractor.onNewTabClicked()
            },

            SimpleBrowserMenuItem(context.getString(R.string.toolbar_menu_item_forget_tab)) {
                toolbarMenuInteractor.onNewForgetTabClicked()
            },

            SimpleBrowserMenuItem(context.getString(R.string.toolbar_menu_item_report_issue)) {
                toolbarMenuInteractor.onReportIssueClicked()
            },

            SimpleBrowserMenuItem(context.getString(R.string.toolbar_menu_item_settings)) {
                toolbarMenuInteractor.onSettingsClicked()
            },

            SimpleBrowserMenuItem(context.getString(R.string.toolbar_menu_item_history)) {
                toolbarMenuInteractor.onHistoryClicked()
            },

            SimpleBrowserMenuItem(context.getString(R.string.toolbar_menu_item_bookmarks)) {
                toolbarMenuInteractor.onBookmarksClicked()
            },

            SimpleBrowserMenuItem(context.getString(R.string.toolbar_menu_item_clear_data)) {
                toolbarMenuInteractor.onClearDataClicked()
            }
        )
    }

    init {
        freshTabToolbar.setMenuBuilder(BrowserMenuBuilder(menuItems))
        freshTabToolbar.setSearchBarClickListener(View.OnClickListener {
            freshTabToolbar.setExpanded(false)
            scope.launch(Dispatchers.Main) {
                delay(FreshTabIntegration.FRESH_TAB_TOOLBAR_EXPAND_INTERACTION_DELAY)
                searchBarInteractor.onSearchBarClicked()
            }
        })
    }
}
