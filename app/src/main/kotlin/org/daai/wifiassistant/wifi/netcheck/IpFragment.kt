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

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.ListFragment
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.daai.wifiassistant.databinding.IpContentBinding
import org.daai.wifiassistant.netease.LDNetDiagnoUtils.LDNetUtil
import org.daai.wifiassistant.netease.LDNetDiagnoUtils.LDNetUtil.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class IpFragment : ListFragment() {
//    private lateinit var NetcheckAdapter: MtrAdapter

    var handler: Handler? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = IpContentBinding.inflate(inflater, container, false)
//        NetcheckAdapter = NetcheckAdapter(requireActivity(), channelAvailable())
//        listAdapter = NetcheckAdapter

        binding.startButton.setOnClickListener{
            startfun(binding,view)
        }

        return binding.root
    }
    var mHandler = Handler()
    private var ii = 0
    fun startfun(binding:IpContentBinding,view: View?) {
        ii = 0
        val curlcurl = "https://tools.sre123.com/dnsipurl"
        val ipmap: HashMap<String, String> = hashMapOf<String, String>()
        val okHttpClient1: OkHttpClient =  OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(7, TimeUnit.SECONDS)
            .writeTimeout(7, TimeUnit.SECONDS)
            .build()
        val request1: Request = Request.Builder()
            .url(curlcurl)
            .addHeader("User-Agent", "bianmin")
            .build()
        val call1: Call = okHttpClient1.newCall(request1)
        Thread {
            try {
                val response: Response = call1.execute()
                val res: String = response.body?.string().toString()
                var isres  =false
                for (a in res.split("\n").toTypedArray()) {
                    val resarr   =
                        a.replaceFirst("var ".toRegex(), "").replace("[;' ]".toRegex(), "")
                            .split("=")
                            .toTypedArray()
                    if (resarr.size > 1) {
                        val  key1  = resarr[0]
                        val  value1 = resarr[1]
                        ipmap.put(key1, value1)
                        isres = true
                    } else {
                        binding.gwinip.text="格式出错!"
                        isres = false
                    }
                }
                for(key in ipmap.keys){
                    println("Key = ${key} , Value = ${ipmap[key]}")
                }

                if (true) {
                    mHandler.post { //
                        // System.out.println("111"+LDNetUtil.NETWORKTYPE_WIFI);
                        if (getNetWorkType(this.context)
                                .equals(NETWORKTYPE_WIFI) && isNetworkConnected(
                                this.context
                            )
                        ) {
                            binding.inip.text=getLocalIpByWifi(this.context)
                        } else {
                            binding.inip.text=getLocalIpBy3G()
                        }
                        if (ipmap["ip"] != null) {
                            binding.ipv4.text=ipmap["ip"].toString()
                        }
                        if (getNetWorkType(this.context)
                                .equals(NETWORKTYPE_WIFI) && isNetworkConnected(
                                this.context
                            )
                        ) {
                            binding.gwinip.text=pingGateWayInWifi(this.context)
                        } else {
                            binding.gwinip.text="非WIFI，无网关"
                        }
                        if (ipmap["ip_province"] != null && ipmap["ip_city"] != null) {
                            binding.addr.text=ipmap["ip_province"].toString() + ipmap["ip_city"].toString()
                        }
                        if (ipmap["ip_isp"] != null) {
                            binding.ipinfo.text=ipmap["ip_isp"].toString()
                        }
                        if (ipmap["dns"] != null) {
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.dnsip.text = ipmap["dns"].toString()
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                ii++
                if (ii == 2) {
                    mHandler.post {
//                        stopProgressBar()
                    }
                }
            }
        }.start()
        val ipv6test="https://tools.sre123.com/ipv6"
        val request2: Request = Request.Builder()
            .url(ipv6test)
            .addHeader("User-Agent", "bianmin")
            .build()
        val call2: Call = okHttpClient1.newCall(request2)
        Thread {
            try {
                val response: Response = call2.execute()
                val res: String = response.body?.string().toString()
                mHandler.post {
                    var ipv6_res: String = jsgetip(res)
                    if (ipv6_res.indexOf(",") > 0) ipv6_res = ipv6_res.split(",").toTypedArray()[0]
                    binding.ipv6.text=ipv6_res
//                    qr(ipv6_res)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                mHandler.post {
                    binding.ipv6.text="不支持IPv6或超时，请重试!"
                }
            } finally {
                ii++
                if (ii == 2) {
                    mHandler.post {
//                        stopProgressBar()
                    }
                }
            }
        }.start()
    }
    private fun jsgetip(jsonData: String): String {
        var jsonData = jsonData
        try {
            jsonData = jsonData.substring(9, jsonData.indexOf(")"))
            val `object`: JSONObject = JSON.parseObject(jsonData)
            return `object`["ip"].toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return jsonData
    }
//    fun qr(binding:IpContentBinding, ipv6_res: String?) {
//        val bitmap: Bitmap =
//            createQRImage(ipv6_res, binding.imageqr.getWidth(), binding.imageqr.getHeight())
//        binding.imageqr.setImageBitmap(bitmap)
//    }
//    fun createQRImage(url: String?, width: Int, height: Int): Bitmap {
//        try {
//            // 判断URL合法性
//            if (url == null || "" == url || url.length < 1) {
//                return null
//            }
//            val hints: Hashtable<*, *> = Hashtable<Any?, Any?>()
//            hints[EncodeHintType.CHARACTER_SET]  = "utf-8"
//            // 图像数据转换，使用了矩阵转换
//            val bitMatrix: BitMatrix =
//                QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints)
//            val pixels = IntArray(width * height)
//            // 下面这里按照二维码的算法，逐个生成二维码的图片，
//            // 两个for循环是图片横列扫描的结果
//            for (y in 0 until height) {
//                for (x in 0 until width) {
//                    if (bitMatrix.get(x, y)) {
//                        pixels[y * width + x] = -0x1000000
//                    } else {
//                        pixels[y * width + x] = -0x1
//                    }
//                }
//            }
//            // 生成二维码图片的格式，使用ARGB_8888
//            val bitmap = Bitmap.createBitmap(
//                width, height,
//                Bitmap.Config.ARGB_8888
//            )
//            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
//            return bitmap
//        } catch (e: WriterException) {
//            e.printStackTrace()
//        }
//        return null
//    }
}

