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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.ListFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.daai.wifiassistant.MainContext
import org.daai.wifiassistant.databinding.NetcheckContentBinding
import org.daai.wifiassistant.wifi.band.WiFiChannelCountry
import org.daai.wifiassistant.wifi.band.WiFiChannelCountry.Companion.find
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Inet6Address
import java.net.InetAddress

//import javax.print.attribute.standard.JobState.COMPLETED


//import org.daai.wifiassistant.wifi.channelavailable.ChannelAvailableAdapter

class NetcheckFragment : ListFragment() {
    private lateinit var NetcheckAdapter: NetcheckAdapter


    var handler: Handler? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = NetcheckContentBinding.inflate(inflater, container, false)
        NetcheckAdapter = NetcheckAdapter(requireActivity(), channelAvailable())
        listAdapter = NetcheckAdapter

        binding.startButton.setOnClickListener{
            val host: String = binding.checktext.getText().toString()
            val pingarg: String = binding.textPort.getText().toString()
            binding.progressBar.setVisibility(View.VISIBLE)
            Thread {
                try {
                    val addrs = InetAddress.getAllByName(host)
                    for (ip in addrs) {
                        println("Ping:$ip")
                        if (ip is Inet6Address) {
                            ping6(binding,ip.toString().split("/").toTypedArray()[1], pingarg)
                        } else {
                            ping(binding,ip.toString().split("/").toTypedArray()[1], pingarg)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    Thread {
                        try {
                            binding.progressBar.setVisibility(View.GONE)
                        } catch (e: Exception) {
                        }
                    }.start()
                }
            }.start()
        }
        binding.cleanText.setOnClickListener{
            binding.textViewResult.text=""
        }
        return binding.root
    }

    fun ping(binding:NetcheckContentBinding,host: String, pingarg: String): String? {
//        val textView_result: TextView  = (R.id.textView_result) as TextView
        var pingarg = pingarg
        var result = "ping v4$host"

        try {
            if (pingarg == "") {
                pingarg = " -c 5 -w 100 "
            }
            val p = Runtime.getRuntime().exec("ping $pingarg $host")
            // 读取ping的内容，可不加。
            val input = p.inputStream
            val `in` = BufferedReader(InputStreamReader(input))
            val stringBuffer = StringBuffer()
            var content = ""
            while (`in`.readLine().also { content = it } != null) {
                stringBuffer.append(
                    """
                    $content
                    
                    """.trimIndent()
                )
                val ping_content = content
                println(ping_content)

                CoroutineScope(Dispatchers.Main).launch {
                    binding.textViewResult.text =
                        binding.textViewResult.text.toString() + "\n" + ping_content
                }
            }
            result = stringBuffer.toString()
            // PING的状态
            val status = p.waitFor()
            if (status == 0) {
                result = "$result\nsuccessful~"
                return result
            } else {
                result = "$result\nfailed~ cannot reach the IP address"
            }
        } catch (e: IOException) {
            result = "$result\nfailed~ IOException"
        } catch (e: InterruptedException) {
            result = "$result\nfailed~ InterruptedException"
        } finally {
            return result
        }
    }

    fun ping6(binding:NetcheckContentBinding,host: String, pingarg: String): String? {
        var pingarg = pingarg
//        val textView_result: TextView  = (org.daai.wifiassistant.R.id.textView_result) as TextView
//        val app_path: String = getApplicationContext().getFilesDir().getAbsolutePath()
        //        varifyFile(getApplicationContext(), "ping7");
        var result = "ping v6 $host"
        try {
            if (pingarg == "") {
                pingarg = " -c 5 -w 100 "
            }
            val p = Runtime.getRuntime().exec("ping6 $pingarg $host")
            val input = p.inputStream
            val `in` = BufferedReader(InputStreamReader(input))
            val stringBuffer = StringBuffer()
            var content = result
            while (`in`.readLine().also { content = it } != null) {
                stringBuffer.append(
                    """
                    $content
                    
                    """.trimIndent()
                )
                val ping_content = content
                CoroutineScope(Dispatchers.Main).launch {
                    binding.textViewResult.text =
                        binding.textViewResult.text.toString() + "\n" + ping_content
                }
            }
            result = stringBuffer.toString()
            // PING的状态
            val status = p.waitFor()
            if (status == 0) {
                result = "$result\nsuccessful~"
                return result
            } else {
                result =
                    "$result\nfailed~ cannot reach the IP address, If is Android 9  Ping v6 Error!"
                CoroutineScope(Dispatchers.Main).launch {
                    binding.textViewResult.text =
                        binding.textViewResult.text.toString() + "\n" + result
                }
            }
        } catch (e: IOException) {
            result = "$result\nfailed~ IOException"
        } catch (e: InterruptedException) {
            result = "$result\nfailed~ InterruptedException"
        } finally {
            return result
        }
    }
    override fun onResume() {
        super.onResume()
        NetcheckAdapter.clear()
        NetcheckAdapter.addAll(channelAvailable())
    }

    private fun channelAvailable(): MutableList<WiFiChannelCountry> =
            mutableListOf(find(MainContext.INSTANCE.settings.countryCode()))
}