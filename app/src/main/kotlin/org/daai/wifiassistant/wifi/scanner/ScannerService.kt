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
package org.daai.wifiassistant.wifi.scanner

import android.os.Handler
import org.daai.wifiassistant.MainActivity
import org.daai.wifiassistant.permission.PermissionService
import org.daai.wifiassistant.settings.Settings
import org.daai.wifiassistant.wifi.manager.WiFiManagerWrapper
import org.daai.wifiassistant.wifi.model.WiFiData

interface UpdateNotifier {
    fun update(wiFiData: WiFiData)
}

interface ScannerService {
    fun update()
    fun wiFiData(): WiFiData
    fun register(updateNotifier: UpdateNotifier): Boolean
    fun unregister(updateNotifier: UpdateNotifier): Boolean
    fun pause()
    fun running(): Boolean
    fun resume()
    fun resumeWithDelay()
    fun stop()
    fun toggle()
}

fun makeScannerService(
    mainActivity: MainActivity,
    wiFiManagerWrapper: WiFiManagerWrapper,
    handler: Handler,
    settings: Settings
): ScannerService {
    val cache = Cache()
    val transformer = Transformer(cache)
    val permissionService = PermissionService(mainActivity)
    val scanner = Scanner(wiFiManagerWrapper, settings, permissionService, transformer)
    scanner.periodicScan = PeriodicScan(scanner, handler, settings)
    scanner.scannerCallback = ScannerCallback(wiFiManagerWrapper, cache)
    scanner.scanResultsReceiver = ScanResultsReceiver(mainActivity, scanner.scannerCallback)
    return scanner
}
