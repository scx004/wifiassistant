/*

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.daai.wifiassistant.navigation.items

import android.content.ActivityNotFoundException
import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import org.daai.wifiassistant.MainActivity
import org.daai.wifiassistant.MainContext
import org.daai.wifiassistant.R
import org.daai.wifiassistant.export.Export
import org.daai.wifiassistant.navigation.NavigationMenu
import org.daai.wifiassistant.wifi.model.WiFiDetail

internal class ExportItem(private val export: Export) : NavigationItem {

    override fun activate(mainActivity: MainActivity, menuItem: MenuItem, navigationMenu: NavigationMenu) {
        val wiFiDetails: List<WiFiDetail> = MainContext.INSTANCE.scannerService.wiFiData().wiFiDetails
        if (wiFiDetails.isEmpty()) {
            Toast.makeText(mainActivity, R.string.no_data, Toast.LENGTH_LONG).show()
            return
        }
        val intent: Intent = export.export(mainActivity, wiFiDetails)
        if (!exportAvailable(mainActivity, intent)) {
            Toast.makeText(mainActivity, R.string.export_not_available, Toast.LENGTH_LONG).show()
            return
        }
        try {
            mainActivity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(mainActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun exportAvailable(mainActivity: MainActivity, chooser: Intent): Boolean =
            chooser.resolveActivity(mainActivity.packageManager) != null

}