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
package org.daai.wifiassistant.wifi.netcheck

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.daai.wifiassistant.R
import org.daai.wifiassistant.databinding.MtrDetailsBinding

internal class MtrAdapterBinding {
    val root: View
    val textViewNumber: TextView
    val textViewIp: TextView
    val textViewLossRate: TextView
    val textViewTime: TextView
    val imageViewStatusPing: ImageView

    internal constructor(binding: MtrDetailsBinding) {
        root = binding.root
        textViewNumber = binding.textViewNumber
        textViewIp = binding.textViewIp
        textViewLossRate = binding.textViewLossRate
        textViewTime = binding.textViewTime
        imageViewStatusPing = binding.imageViewStatusPing

    }

    internal constructor(view: View) {
        root = view
        textViewNumber = view.findViewById(R.id.textViewNumber)
        textViewIp = view.findViewById(R.id.textViewIp)
        textViewLossRate = view.findViewById(R.id.textViewLossRate)
        textViewTime = view.findViewById(R.id.textViewTime)
        imageViewStatusPing = view.findViewById(R.id.imageViewStatusPing)
    }

}
