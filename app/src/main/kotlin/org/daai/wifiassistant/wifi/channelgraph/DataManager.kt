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
package org.daai.wifiassistant.wifi.channelgraph

import com.jjoe64.graphview.series.TitleLineGraphSeries
import org.daai.annotation.OpenClass
import org.daai.wifiassistant.wifi.band.WiFiChannelPair
import org.daai.wifiassistant.wifi.graphutils.GraphDataPoint
import org.daai.wifiassistant.wifi.graphutils.GraphViewWrapper
import org.daai.wifiassistant.wifi.graphutils.MIN_Y
import org.daai.wifiassistant.wifi.model.WiFiDetail

internal fun WiFiChannelPair.inRange(wiFiDetail: WiFiDetail): Boolean =
        wiFiDetail.wiFiSignal.centerFrequency in first.frequency..second.frequency

@OpenClass
internal class DataManager {
    fun newSeries(wiFiDetails: List<WiFiDetail>, wiFiChannelPair: WiFiChannelPair): Set<WiFiDetail> =
            wiFiDetails.filter { wiFiChannelPair.inRange(it) }.toSet()

    fun graphDataPoints(wiFiDetail: WiFiDetail, levelMax: Int): Array<GraphDataPoint> {
        val wiFiSignal = wiFiDetail.wiFiSignal
        val guardBand = wiFiSignal.wiFiWidth.guardBand
        val frequencyStart = wiFiSignal.frequencyStart
        val frequencyEnd = wiFiSignal.frequencyEnd
        val level = wiFiSignal.level.coerceAtMost(levelMax)
        return arrayOf(
                GraphDataPoint(frequencyStart, MIN_Y),
                GraphDataPoint(frequencyStart + guardBand, level),
                GraphDataPoint(wiFiSignal.centerFrequency, level),
                GraphDataPoint(frequencyEnd - guardBand, level),
                GraphDataPoint(frequencyEnd, MIN_Y)
        )
    }

    fun addSeriesData(graphViewWrapper: GraphViewWrapper, wiFiDetails: Set<WiFiDetail>, levelMax: Int) {
        wiFiDetails.forEach {
            val dataPoints = graphDataPoints(it, levelMax)
            if (graphViewWrapper.newSeries(it)) {
                graphViewWrapper.addSeries(it, TitleLineGraphSeries(dataPoints), true)
            } else {
                graphViewWrapper.updateSeries(it, dataPoints, true)
            }
        }
    }

}