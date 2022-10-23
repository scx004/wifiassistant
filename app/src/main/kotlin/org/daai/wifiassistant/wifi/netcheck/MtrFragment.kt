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

//import org.daai.wifiassistant.MTR
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.ListFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.daai.wifiassistant.MainContext
import org.daai.wifiassistant.R
import org.daai.wifiassistant.databinding.MtrContentBinding
import org.daai.wifiassistant.network.TracerouteContainerMtr
import org.daai.wifiassistant.network.TracerouteWithPingMtr1

class MtrFragment : ListFragment() {

    private lateinit var  traceListAdapter: MtrAdapter
    private val tracerouteWithPingMtr1: TracerouteWithPingMtr1= TracerouteWithPingMtr1(this)
    private val maxTtl = 30
    var option: String? = null

    val settings = MainContext.INSTANCE.settings

//    private val traces: MutableList<TracerouteContainerMtr> = TODO()
    private val traces: MutableList<TracerouteContainerMtr> =mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        traceListAdapter = MtrAdapter(requireActivity(), channelAvailable1())
        val binding = MtrContentBinding.inflate(inflater, container, false)
//        listAdapter = traceListAdapter

        binding.buttonLaunch.setOnClickListener(View.OnClickListener {
            org.daai.wifiassistant.network.Data.setCT(settings.mtr_packetcount())
            org.daai.wifiassistant.network.Data.setWT(settings.mtr_timeout())
//            Log.d("11111", settings.mtr_packetcount().toString()+"111"+settings.mtr_timeout())
            if (binding.editTextPing.text.length == 0) {
                Toast.makeText(this.context, getString(R.string.no_text), Toast.LENGTH_SHORT).show()
            } else {
                traceListAdapter.clear()
                traceListAdapter.notifyDataSetChanged()
                startProgressBar(binding)

                tracerouteWithPingMtr1.executeTraceroute(
                    binding.editTextPing.text.toString(),
                    maxTtl,binding)
            }
        })

        traceListAdapter = context?.let { MtrAdapter(it,traces) }!!
        binding.listViewTraceroute.setAdapter(traceListAdapter)
//        listViewTraceroute.
        return binding.root
    }

    fun refreshList(trace: TracerouteContainerMtr) {
        val fTrace = trace
        CoroutineScope(Dispatchers.Main).launch {
            traces.add(fTrace)
            traceListAdapter.notifyDataSetChanged()
            }
    }
    fun startProgressBar(binding: MtrContentBinding) {
        binding.progressBarPing.visibility = View.VISIBLE
    }

    fun stopProgressBar(binding: MtrContentBinding) {
        binding.progressBarPing.visibility = View.GONE
    }
    private fun channelAvailable1(): MutableList<TracerouteContainerMtr> =
        mutableListOf()


}


