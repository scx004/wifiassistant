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
package org.daai.wifiassistant.about

//import org.daai.wifiassistant.databinding.AboutContentBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.daai.wifiassistant.R
import org.daai.wifiassistant.databinding.WifiinfoContentBinding

class WifiInfoFragment(s: String) : Fragment() {
    val url=s
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: WifiinfoContentBinding = WifiinfoContentBinding.inflate(inflater, container, false)
        val activity: FragmentActivity = requireActivity()
        setTexts(binding, activity)
        wr_load(binding)
        return binding.root
    }

    private fun setTexts(binding: WifiinfoContentBinding, activity: FragmentActivity) {

    }

    fun wr_load(binding: WifiinfoContentBinding) {
//        val url="https://tools.sre123.com/Tools/ua/?fr=netcheck"
        val  option="liulanqi"
            val webView_url_help = binding.webview
            val webSettings = webView_url_help.settings
            webSettings.cacheMode = WebSettings.LOAD_DEFAULT
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.builtInZoomControls = true
            webSettings.blockNetworkImage = false
            if (option != "liulanqi" && option != "speedtest") {
                webSettings.setUserAgentString("bianmin")
            }
            webView_url_help.setBackgroundColor(resources.getColor(android.R.color.transparent))
            webView_url_help.setBackgroundResource(R.drawable.table_pair_lines)
//            webView_url_help.webViewClient = object : WebViewClient() {
//                override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap) {
////                    super.onPageStarted(view, url, favicon)
//                }
//
//                override fun onPageFinished(view: WebView?, url: String) {
//
//                }
//
//                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
//                    view?.loadUrl(url)
//                    return true
//                }
//            }
            webView_url_help.loadUrl(url)
    }

    companion object {
        private const val YEAR_FORMAT = "yyyy"
    }
}