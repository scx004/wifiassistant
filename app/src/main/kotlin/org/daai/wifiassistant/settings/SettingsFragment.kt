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

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.vrem.util.buildMinVersionQ
import com.vrem.util.buildVersionP
import org.daai.wifiassistant.R

open class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
        findPreference<Preference>(getString(R.string.wifi_throttle_disabled_key))!!.isVisible = buildVersionP()
        findPreference<Preference>(getString(R.string.wifi_off_on_exit_key))!!.isVisible = !buildMinVersionQ()

        val displayOptions: PreferenceCategory? =
            findPreference("abcdef") as PreferenceCategory?
        if (displayOptions != null) {
            preferenceScreen.removePreference(displayOptions)
        }
        val displayOptions1: PreferenceCategory? =
            findPreference("abcdefef") as PreferenceCategory?
        if (displayOptions1 != null) {
            preferenceScreen.removePreference(displayOptions1)
        }

    }

}