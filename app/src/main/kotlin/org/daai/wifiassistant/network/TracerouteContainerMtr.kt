package org.daai.wifiassistant.network

import java.io.Serializable

class TracerouteContainerMtr(
    var hostname: String,
    var ip: String,
    val lossRate: String,
    var ms1: Float,
    var isSuccessful: Boolean
) :
    Serializable {

    fun getMs(): Float {
        //return ms;
        return Math.round(ms1 * 100).toFloat() / 100
    }

    fun setMs(ms1: Float) {
        this.ms1 = ms1
    }

    override fun toString(): String {
        return "Traceroute : \nHostname : $hostname\nip : $ip\nMilliseconds : $ms1"
    }

    companion object {
        private const val serialVersionUID = 1034744411998219581L
    }
}
