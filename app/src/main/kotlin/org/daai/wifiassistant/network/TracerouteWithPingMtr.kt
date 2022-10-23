package org.daai.wifiassistant.network

//import org.daai.wifiassistant.MTR;
import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import org.daai.wifiassistant.R
import org.daai.wifiassistant.wifi.netcheck.MtrFragment
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal
import java.net.Inet6Address
import java.net.InetAddress


class TracerouteWithPingMtr(private val context: MtrFragment) {
	private var latestTrace: TracerouteContainerMtr? = null
	private var ttl = 0
	private var finishedTasks = 0
	private var urlToPing: String? = null
	private var ipToPing: String? = null
	private var elapsedTime = 0f
	private var handlerTimeout: Handler? = null

	/**
	 * Launches the Traceroute
	 *
	 * @param url
	 * The url to trace
	 * @param maxTtl
	 * The max time to live to set (ping param)
	 */
	fun executeTraceroute(url: String?, maxTtl: Int) {
		ttl = 1
		finishedTasks = 0
		urlToPing = url
		ExecutePingAsyncTask(maxTtl).execute()
	}

	/**
	 * Allows to timeout the ping if TIMEOUT exceeds. (-w and -W are not always supported on Android)
	 */
	private inner class TimeOutAsyncTask(
		private val task: ExecutePingAsyncTask?,
		private val ttlTask: Int
	) :
		AsyncTask<Void?, Void?, Void?>() {
//		protected override fun doInBackground(vararg arg0: Void): Void? {
//			return null
//		}
		override fun doInBackground(vararg p0: Void?): Void? {
			return null
		}
		override fun onPostExecute(result: Void?) {
			if (handlerTimeout == null) {
				handlerTimeout = Handler()
			}

			// stop old timeout
			if (runnableTimeout != null) {
				handlerTimeout!!.removeCallbacks(runnableTimeout!!)
			}
			// define timeout
			runnableTimeout = Runnable {
				if (task != null) {
					if (ttlTask == finishedTasks) {
						//							Toast.makeText(context, context.getString(R.string.timeout), Toast.LENGTH_SHORT).show();
						task.isCancelled = true
						task.cancel(true)
						//							context.stopProgressBar();
					}
				}
			}
			// launch timeout after a delay
			handlerTimeout!!.postDelayed(runnableTimeout!!, TIMEOUT.toLong())
			super.onPostExecute(result)
		}


	}

	/**
	 * The task that ping an ip, with increasing time to live (ttl) value
	 */
	private inner class ExecutePingAsyncTask(private val maxTtl: Int) :
		AsyncTask<Void?, Void?, String>() {
		private var isCancelled1 = false

		/**
		 * Launches the ping, launches InetAddress to retrieve url if there is one, store trace
		 */
		 override fun doInBackground(vararg p0: Void?): String? {
			if (hasConnectivity()) {
				try {
					val res = launchPing(urlToPing)
					val trace: TracerouteContainerMtr
					val ip = parseIpFromPing(res)
					trace = if (res.contains(UNREACHABLE_PING) && !res.contains(EXCEED_PING)) {
						// Create the TracerouteContainer object when ping
						// failed
						TracerouteContainerMtr("", ip, "100%", elapsedTime, false)
					} else {
						// Create the TracerouteContainer object when succeed
						TracerouteContainerMtr(
							"",
							ip,
							if (!res.contains(EXCEED_PING) && ttl == maxTtl) parseLossRateFromPing(
								res
							) else "loss 0%",
							if (ttl == maxTtl) parseTimeFromPing(res).toFloat() else elapsedTime,
							true
						)
					}

					// Get the host name from ip (unix ping do not support
					// hostname resolving)
//					Log.d(TraceActivity.tag, "hostname : " );
					val inetAddr = InetAddress.getByName(trace.ip)
					val hostname = inetAddr.hostName
					val canonicalHostname = inetAddr.canonicalHostName
					trace.hostname = hostname
					latestTrace = trace
					// Not refresh list if this ip is the final ip but the ttl is not maxTtl
					// this row will be inserted later
					if (ip != ipToPing || ttl == maxTtl) {
						context.refreshList(trace)
					}
					return res
				} catch (e: Exception) {
//					context.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							onException(e);
//						}
//					});
				}
			} else {
				return context.getString(R.string.no_connectivity)
			}
			return ""
		}

		/**
		 * Launches ping command
		 *
		 * @param url
		 * The url to ping
		 * @return The ping string
		 */
		@SuppressLint("NewApi")
		@Throws(Exception::class)
		private fun launchPing(url: String?): String {
			// Build ping command with parameters
			val p: Process
			var command = ""
			val pc = Data.getpc()
			val wt = Data.getwt()
			val b = BigDecimal(wt.toString())
			val d = b.toDouble()

//			String format = "ping -c 5 -i 0.2 -t %d ";
//			String format6 = "ping6 -c 5 -i 0.2 -t %d ";
			val format = "ping -c %d -i %f -t %d "
			val format6 = "ping6 -c %d -i %f -t %d "
			val addrs = InetAddress.getByName(url)
			command = if (addrs is Inet6Address) {
				String.format(format6, pc, d, ttl)
				//					command = String.format(format6,ttl);
			} else {
				String.format(format, pc, d, ttl)
				//					command = String.format(format,ttl);
			}
			val ip = addrs.toString().split("/").toTypedArray()[1]

//			Log.d(TraceActivity.tag, "Will launch3 : " + command + ip);
			Log.d("111", command + ip)
			val startTime = System.nanoTime()
			elapsedTime = 0f
			// timeout task
			TimeOutAsyncTask(this, ttl).execute()
			// Launch command
			p = Runtime.getRuntime().exec(command + ip)
			val stdInput = BufferedReader(InputStreamReader(p.inputStream))

			// Construct the response from ping
			var s: String
			var res = ""
			while (stdInput.readLine().also { s = it } != null) {
				res += """
					$s
					
					""".trimIndent()
				//				if (s.contains(FROM_PING) || s.contains(SMALL_FROM_PING)) {
//					// We store the elapsedTime when the line from ping comes
//					elapsedTime = (System.nanoTime() - startTime - 4000000) / 5.0f / 1000000.0f;
				Log.d(
					"111",
					(System.nanoTime() - startTime - 4000000.0f).toString() + ":" + (System.nanoTime() - startTime - 4000000.0f) / 4.0f + ":" + (System.nanoTime() - startTime - 4000000.0f) / 4.0f / 1000000.0f
				)
				//				}
			}
			//			Log.d(TraceActivity.tag, "Will launch33 : " + res);
//			Log.d(TraceActivity.tag, "Will launch33 : " + parseexceedTimeFromPing(res));

//			elapsedTime=(Float.parseFloat(parseexceedTimeFromPing(res))-200*4)/5.0f;
			elapsedTime =
				(parseexceedTimeFromPing(res).toFloat() - wt * 1000 * (pc - 1)) / pc / 1.0f
			p.destroy()
			require(res != "")

			// Store the wanted ip adress to compare with ping result
			if (ttl == 1) {
				ipToPing = parseIpToPingFromPing(res)
			}
			return res
		}

		/**
		 * Treat the previous ping (launches a ttl+1 if it is not the final ip, refresh the list on view etc...)
		 */
		override fun onPostExecute(result: String) {
			if (!isCancelled1) {
				try {
					if ("" != result) {
						if (context.getString(R.string.no_connectivity) == result) {
//							Toast.makeText(context, context.getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();
						} else {
							if (latestTrace != null && latestTrace!!.ip == ipToPing) {
								if (ttl < maxTtl) {
									ttl = maxTtl
									ExecutePingAsyncTask(maxTtl).execute()
								} else {
//									context.stopProgressBar();
								}
							} else {
								if (ttl < maxTtl) {
									ttl++
									ExecutePingAsyncTask(maxTtl).execute()
								}
							}
							//							context.refreshList(traces);
						}
					}
					finishedTasks++
				} catch (e: Exception) {
//					context.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							onException(e);
//						}
//					});
				}
			}
			super.onPostExecute(result)
		}

		/**
		 * Handles exception on ping
		 *
		 * @param e
		 * The exception thrown
		 */
		private fun onException(e: Exception) {
			if (e is IllegalArgumentException) {
//				Toast.makeText(context, context.getString(R.string.no_ping), Toast.LENGTH_SHORT).show();
			} else {
//				Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show();
			}

//			context.stopProgressBar();
			finishedTasks++
		}

		fun setCancelled(isCancelled1: Boolean) {
			this.isCancelled1 = isCancelled1
		}

//		override fun doInBackground(vararg p0: Void?): String {
//			TODO("Not yet implemented")
//		}
	}

	/**
	 * Gets the ip from the string returned by a ping
	 *
	 * @param ping
	 * The string returned by a ping command
	 * @return The ip contained in the ping
	 */
	private fun parseIpFromPing(ping: String): String {
		var ip = ""
		if (ping.contains(FROM_PING)) {
			// Get ip when ttl exceeded
			var index = ping.indexOf(FROM_PING)
			ip = ping.substring(index + 5)
			if (ip.contains(PARENTHESE_OPEN_PING)) {
				// Get ip when in parenthese
				val indexOpen = ip.indexOf(PARENTHESE_OPEN_PING)
				val indexClose = ip.indexOf(PARENTHESE_CLOSE_PING)
				ip = ip.substring(indexOpen + 1, indexClose)
			} else {
				// Get ip when after from
				ip = ip.substring(0, ip.indexOf("\n"))
				if (ip.contains(" ")) {
					index = ip.indexOf(" ")
				}
				ip = ip.substring(0, index)
				if (ip.substring(ip.length - 1) == ":") {
					ip = ip.substring(0, ip.length - 1)
				}
			}
		} else {
			// Get ip when ping succeeded
			val indexOpen = ping.indexOf(PARENTHESE_OPEN_PING)
			val indexClose = ping.indexOf(PARENTHESE_CLOSE_PING)
			ip = ping.substring(indexOpen + 1, indexClose)
		}
		//		Log.d(TraceActivity.tag, "parseIpFromPing4 : " + ip);
		return ip
	}

	/**
	 * Gets the final ip we want to ping (example: if user fullfilled google.fr, final ip could be 8.8.8.8)
	 *
	 * @param ping
	 * The string returned by a ping command
	 * @return The ip contained in the ping
	 */
	private fun parseIpToPingFromPing(ping: String): String {
		var ip = ""
		if (ping.contains(PING) || ping.contains(PING6)) {
			// Get ip when ping succeeded
			val indexOpen = ping.indexOf(PARENTHESE_OPEN_PING)
			val indexClose = ping.indexOf(PARENTHESE_CLOSE_PING)
			ip = ping.substring(indexOpen + 1, indexClose)
		}
		return ip
	}

	/**
	 * Gets the time from ping command (if there is)
	 *
	 * @param ping
	 * The string returned by a ping command
	 * @return The time contained in the ping
	 */
	private fun parseexceedTimeFromPing(ping: String): String {
		var time = ""
		if (ping.contains("packet loss, time")) {
			val index = ping.indexOf("packet loss, time")
			time = ping.substring(index + 18)
			time = time.split("ms").toTypedArray()[0]
			//			index = time.indexOf("/");
//			time = time.substring(0, index);
		}
		return time
	}

	private fun parseTimeFromPing(ping: String): String {
		var time = ""
		if (ping.contains(TIME_PING)) {
			val index = ping.indexOf(TIME_PING)
			time = ping.substring(index + 19)
			time = time.split("/").toTypedArray()[1]
			//			index = time.indexOf("/");
//			time = time.substring(0, index);
		}
		return time
	}

	private fun parseLossRateFromPing(ping: String): String {
		var time = ""
		var time1: Array<String>? = null
		if (ping.contains(LossRate)) {
			val index = ping.indexOf(LossRate)
			time1 = ping.split(LossRate).toTypedArray()[0].split(" ").toTypedArray()
			time = time1[time1.size - 1]
			//			time = ping.substring(index + 10);
//			index = time.indexOf(" ");
//			time = time.substring(0, index);
		}
		return "loss $time"
	}

	/**
	 * Check for connectivity (wifi and mobile)
	 *
	 * @return true if there is a connectivity, false otherwise
	 */
	fun hasConnectivity(): Boolean {
//		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
//		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		return true
	}

	companion object {
		private const val PING = "PING"
		private const val PING6 = "PING6"
		private const val FROM_PING = "From"
		private const val SMALL_FROM_PING = "from"
		private const val PARENTHESE_OPEN_PING = "("
		private const val PARENTHESE_CLOSE_PING = ")"
		private const val TIME_PING = "min/avg/max/mdev = "
		private const val LossRate = "packet loss"
		private const val EXCEED_PING = "exceed"
		private const val UNREACHABLE_PING = "100%"

		//	private MTR context;
		// timeout handling
		private const val TIMEOUT = 30000
		private var runnableTimeout: Runnable? = null
	}
}