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

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import org.daai.wifiassistant.MainContext
import org.daai.wifiassistant.R
import org.daai.wifiassistant.databinding.MtrDetailsBinding
import org.daai.wifiassistant.network.TracerouteContainerMtr


internal class MtrAdapter(context: Context, traces: MutableList<TracerouteContainerMtr>) :
    ArrayAdapter<TracerouteContainerMtr>(context, R.layout.mtr_details, traces) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val binding = view?.let { MtrAdapterBinding(it) }?: MtrAdapterBinding(create(parent))
        val rootView = binding.root
        val currentTrace = getItem(position)
//        getItem(position)?.let {
            binding.textViewNumber.text = position.toString()

        if (position % 2 == 1) {
            if (view != null) {
                view.setBackgroundResource(R.drawable.table_odd_lines)
            }
        } else {
            if (view != null) {
                view.setBackgroundResource(R.drawable.table_pair_lines)
            }
        }

        if (currentTrace != null) {
            if (currentTrace.isSuccessful) {
                binding.imageViewStatusPing.setImageResource(R.drawable.zhuangtai)
            } else {
                binding.imageViewStatusPing.setImageResource(R.drawable.zhuangtaix)
            }
        }

            var vtv: String? = ""
            vtv =
                if (currentTrace?.hostname == currentTrace?.ip) currentTrace?.ip else currentTrace?.hostname + " (" + currentTrace?.ip + ")"
            binding.textViewIp.text = vtv
            binding.textViewLossRate.text = currentTrace?.lossRate
            binding.textViewTime.text = currentTrace?.ms1.toString() + "ms"
            println("333: " + position+ " " + vtv + " "+currentTrace?.lossRate+" "+currentTrace?.ms1+"ms")
//        }
        return rootView
    }

    private fun create(parent: ViewGroup): MtrDetailsBinding =
        MtrDetailsBinding.inflate(MainContext.INSTANCE.layoutInflater, parent, false)

}