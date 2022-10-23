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
package org.daai.wifiassistant.settings

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import org.daai.annotation.OpenClass
import com.vrem.util.*
import org.daai.wifiassistant.R
import org.daai.wifiassistant.navigation.NavigationGroup
import org.daai.wifiassistant.navigation.NavigationMenu
import org.daai.wifiassistant.wifi.accesspoint.AccessPointViewType
import org.daai.wifiassistant.wifi.accesspoint.ConnectionViewType
import org.daai.wifiassistant.wifi.band.WiFiBand
import org.daai.wifiassistant.wifi.graphutils.GraphLegend
import org.daai.wifiassistant.wifi.model.GroupBy
import org.daai.wifiassistant.wifi.model.Security
import org.daai.wifiassistant.wifi.model.SortBy
import org.daai.wifiassistant.wifi.model.Strength
import java.util.*

@OpenClass
class Settings(private val repository: Repository) {

    fun initializeDefaultValues(): Unit = repository.initializeDefaultValues()

    fun registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener): Unit =
            repository.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)

    fun scanSpeed(): Int {
        val defaultValue = repository.stringAsInteger(R.string.scan_speed_default, SCAN_SPEED_DEFAULT)
        val scanSpeed = repository.stringAsInteger(R.string.scan_speed_key, defaultValue)
        return if (versionP()) {
            if (wiFiThrottleDisabled()) scanSpeed.coerceAtLeast(SCAN_SPEED_DEFAULT) else scanSpeed
        } else scanSpeed
    }

    fun mtr_packetcount(): Int {
        val defaultValue = repository.stringAsInteger(R.string.mtr_by_packetcount_def, 5)
        val scanSpeed = repository.stringAsInteger(R.string.mtr_by_packetcount, defaultValue)
        return scanSpeed
    }

    fun mtr_timeout(): Float {
        val defaultValue = repository.stringAsFloat(R.string.mtr_by_timeout_def, 0.2f)
        val scanSpeed = repository.stringAsFloat(R.string.mtr_by_timeout, defaultValue)
        return scanSpeed
    }

    fun wiFiThrottleDisabled(): Boolean {
        if (versionP()) {
            val defaultValue = repository.resourceBoolean(R.bool.wifi_throttle_disabled_default)
            return repository.boolean(R.string.wifi_throttle_disabled_key, defaultValue)
        }
        return false
    }

    fun cacheOff(): Boolean =
        repository.boolean(R.string.cache_off_key, repository.resourceBoolean(R.bool.cache_off_default))

    fun graphMaximumY(): Int {
        val defaultValue = repository.stringAsInteger(R.string.graph_maximum_y_default, GRAPH_Y_DEFAULT)
        val result = repository.stringAsInteger(R.string.graph_maximum_y_key, defaultValue)
        return result * GRAPH_Y_MULTIPLIER
    }

    fun wiFiBand(wiFiBand: WiFiBand): Unit = repository.save(R.string.wifi_band_key, wiFiBand.ordinal)

//    fun countryCode(): String = repository.string(R.string.country_code_key, defaultCountryCode())
    fun countryCode(): String = repository.string(R.string.country_code_key, defaultCountryCode())

    fun languageLocale(): Locale {
        val defaultLanguageTag = defaultLanguageTag()
        val languageTag = repository.string(R.string.language_key, defaultLanguageTag)
        return findByLanguageTag(languageTag)
    }



    fun sortBy(): SortBy = find(SortBy.values(), R.string.sort_by_key, SortBy.STRENGTH)

    fun groupBy(): GroupBy = find(GroupBy.values(), R.string.group_by_key, GroupBy.NONE)

    fun accessPointView(): AccessPointViewType = find(AccessPointViewType.values(), R.string.ap_view_key, AccessPointViewType.COMPLETE)

    fun connectionViewType(): ConnectionViewType = find(ConnectionViewType.values(), R.string.connection_view_key, ConnectionViewType.COMPACT)

    fun channelGraphLegend(): GraphLegend = find(GraphLegend.values(), R.string.channel_graph_legend_key, GraphLegend.HIDE)

    fun timeGraphLegend(): GraphLegend = find(GraphLegend.values(), R.string.time_graph_legend_key, GraphLegend.LEFT)

    fun wiFiBand(): WiFiBand = find(WiFiBand.values(), R.string.wifi_band_key, WiFiBand.GHZ2)

    fun wiFiOffOnExit(): Boolean =
            if (minVersionQ()) {
                false
            } else {
                repository.boolean(R.string.wifi_off_on_exit_key, repository.resourceBoolean(R.bool.wifi_off_on_exit_default))
            }

    fun keepScreenOn(): Boolean = repository.boolean(R.string.keep_screen_on_key, repository.resourceBoolean(R.bool.keep_screen_on_default))

    fun themeStyle(): ThemeStyle = find(ThemeStyle.values(), R.string.theme_key, ThemeStyle.LIGHT)

    fun selectedMenu(): NavigationMenu = find(NavigationMenu.values(), R.string.selected_menu_key, NavigationMenu.ACCESS_POINTS)

    fun saveSelectedMenu(navigationMenu: NavigationMenu) {
        if (NavigationGroup.GROUP_FEATURE.navigationMenus.contains(navigationMenu)) {
            repository.save(R.string.selected_menu_key, navigationMenu.ordinal)
        }
    }

    fun findSSIDs(): Set<String> = repository.stringSet(R.string.filter_ssid_key, setOf())

    fun saveSSIDs(values: Set<String>): Unit = repository.saveStringSet(R.string.filter_ssid_key, values)

//    fun mtrbypacketcount(): Set<String> = repository.stringSet(R.string.mtr_by_packet, setOf())
//
//    fun mtrbytimeout(): Set<String>  = repository.stringSet(R.string.mtr_by_time, setOf())

    fun findWiFiBands(): Set<WiFiBand> = findSet(WiFiBand.values(), R.string.filter_wifi_band_key, WiFiBand.GHZ2)

    fun saveWiFiBands(values: Set<WiFiBand>): Unit = saveSet(R.string.filter_wifi_band_key, values)

    fun findStrengths(): Set<Strength> = findSet(Strength.values(), R.string.filter_strength_key, Strength.FOUR)

    fun saveStrengths(values: Set<Strength>): Unit = saveSet(R.string.filter_strength_key, values)

    fun findSecurities(): Set<Security> = findSet(Security.values(), R.string.filter_security_key, Security.NONE)

    fun saveSecurities(values: Set<Security>): Unit = saveSet(R.string.filter_security_key, values)

    private fun <T : Enum<T>> find(values: Array<T>, key: Int, defaultValue: T): T {
        val value = repository.stringAsInteger(key, defaultValue.ordinal)
        return findOne(values, value, defaultValue)
    }

    private fun <T : Enum<T>> findSet(values: Array<T>, key: Int, defaultValue: T): Set<T> {
        val ordinalDefault = ordinals(values)
        val ordinalSaved = repository.stringSet(key, ordinalDefault)
        return findSet(values, ordinalSaved, defaultValue)
    }

    private fun <T : Enum<T>> saveSet(key: Int, values: Set<T>): Unit = repository.saveStringSet(key, ordinals(values))

    fun minVersionQ(): Boolean = buildMinVersionQ()

    fun versionP(): Boolean = buildVersionP()

    companion object {
        private const val SCAN_SPEED_DEFAULT = 5
        private const val GRAPH_Y_MULTIPLIER = -10
        private const val GRAPH_Y_DEFAULT = 2
    }

}