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

import android.view.View
//import org.daai.wifiassistant.MTR
import org.daai.wifiassistant.about.AboutFragment
import org.daai.wifiassistant.about.WifiInfoFragment
import org.daai.wifiassistant.export.Export
import org.daai.wifiassistant.settings.SettingsFragment
import org.daai.wifiassistant.vendor.VendorFragment
import org.daai.wifiassistant.wifi.accesspoint.AccessPointsFragment
import org.daai.wifiassistant.wifi.channelavailable.ChannelAvailableFragment
import org.daai.wifiassistant.wifi.channelgraph.ChannelGraphFragment
import org.daai.wifiassistant.wifi.channelrating.ChannelRatingFragment
import org.daai.wifiassistant.wifi.netcheck.IpFragment
import org.daai.wifiassistant.wifi.netcheck.MtrFragment
import org.daai.wifiassistant.wifi.netcheck.NetcheckFragment
import org.daai.wifiassistant.wifi.netcheck.NsFragment
import org.daai.wifiassistant.wifi.timegraph.TimeGraphFragment

val pass_url="https://tools.sre123.com/Tools/pass/?fr=netcheck"
val info_url="https://tools.sre123.com/Tools/info/?fr=netcheck"
val navigationItemAccessPoints: NavigationItem = FragmentItem(AccessPointsFragment())
val navigationItemChannelRating: NavigationItem = FragmentItem(ChannelRatingFragment())
val navigationItemChannelGraph: NavigationItem = FragmentItem(ChannelGraphFragment())
val navigationItemTimeGraph: NavigationItem = FragmentItem(TimeGraphFragment())
val navigationItemExport: NavigationItem = ExportItem(Export())
val navigationItemChannelAvailable: NavigationItem = FragmentItem(ChannelAvailableFragment(), false)
val navigationItemNetcheck: NavigationItem = FragmentItem(NetcheckFragment(), false)
val navigationItemIp: NavigationItem = FragmentItem(IpFragment(), false)
val navigationItemNs: NavigationItem = FragmentItem(NsFragment(), false)
//val navigationItemMtr: NavigationItem = JumpActivity()
val navigationItemMtr: NavigationItem = FragmentItem(MtrFragment(),false)
val navigationItemVendors: NavigationItem = FragmentItem(VendorFragment(), false, View.GONE)
val navigationItemSettings: NavigationItem = FragmentItem(SettingsFragment(), false, View.GONE)
val navigationItemWifPass: NavigationItem = FragmentItem(WifiInfoFragment(pass_url), false, View.GONE)
val navigationItemWifInfo: NavigationItem = FragmentItem(WifiInfoFragment(info_url), false, View.GONE)
val navigationItemAbout: NavigationItem = FragmentItem(AboutFragment(), false, View.GONE)
val navigationItemPortAuthority: NavigationItem = PortAuthorityItem()
