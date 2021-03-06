/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.reference.browser.topsites.ui

import kotlinx.coroutines.CoroutineScope
import mozilla.components.browser.icons.BrowserIcons
import mozilla.components.support.base.feature.LifecycleAwareFeature
import org.mozilla.reference.browser.library.history.usecases.HistoryUseCases

class TopSitesFeature(
    private val topSitesView: TopSitesView,
    private val scope: CoroutineScope,
    private val historyUseCases: HistoryUseCases,
    private val browserIcons: BrowserIcons
) : LifecycleAwareFeature {

    override fun start() {
        topSitesView.initialize(scope, historyUseCases, browserIcons)
    }

    override fun stop() {
        // no-op
    }

    fun updateTopSites() {
        topSitesView.updateTopSites()
    }
}
