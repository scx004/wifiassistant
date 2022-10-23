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

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.ListFragment
import org.daai.wifiassistant.databinding.NsContentBinding
import org.daai.wifiassistant.settings.ThemeStyle
import java.net.InetAddress


class NsFragment : ListFragment() {

    var handler: Handler? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = NsContentBinding.inflate(inflater, container, false)
//        NetcheckAdapter = NetcheckAdapter(requireActivity(), channelAvailable())
//        listAdapter = NetcheckAdapter
        binding.startButton.setOnClickListener{
            startfun(binding,view)
        }
        binding.cleanText.setOnClickListener{
            binding.textViewResult.text=""
        }
        return binding.root
    }
    var mHandler = Handler()
    fun startfun(binding:NsContentBinding,view: View?) {
        val checktext_s = binding.checktext.text.toString()
        if (checktext_s.indexOf(",") < 0) {
            Thread {
                try {
                    val result: String = NsLookup(checktext_s)
                    mHandler.post {
                        binding.textViewResult.text= "域名解析:\n"+result
                    }
                } catch (e: Exception) {
                    print("netcheck error:" + e.message)
                }
            }.start()
        }
    }
    fun NsLookup(host: String?): String {
        var result = ""
        try {
            val addrs = InetAddress.getAllByName(host)
            for (adr in addrs) {
                result = result+"\n"+adr
            }
        } catch (e: java.lang.Exception) {
            result = "ERROR: network error!"
            Log.e("nslookup", e.message!!)
        } finally {
            //Log.e("nslookup" ,result);
            return result
        }
    }
}

