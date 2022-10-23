package org.daai.wifiassistant.network;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.daai.wifiassistant.R;
import org.daai.wifiassistant.databinding.MtrContentBinding;
import org.daai.wifiassistant.wifi.netcheck.MtrFragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
public class TracerouteWithPingMtr1 {

	private static final String PING = "PING";
	private static final String PING6 = "PING6";
	private static final String FROM_PING = "From";
	private static final String SMALL_FROM_PING = "from";
	private static final String PARENTHESE_OPEN_PING = "(";
	private static final String PARENTHESE_CLOSE_PING = ")";
	private static final String TIME_PING = "min/avg/max/mdev = ";
	private static final String LossRate = "packet loss";
	private static final String EXCEED_PING = "exceed";
	private static final String UNREACHABLE_PING = "100%";

	private TracerouteContainerMtr latestTrace;
	private int ttl;
	private int finishedTasks;
	private String urlToPing;
	private String ipToPing;
	private float elapsedTime;
	private MtrFragment context;
	private MtrFragment TraceActivity;

	// timeout handling
	private static final int TIMEOUT = 30000;
	private Handler handlerTimeout;
	private static Runnable runnableTimeout;
	private   MtrContentBinding binding;

	public TracerouteWithPingMtr1(MtrFragment context) {
		this.context = context;
		this.TraceActivity=context;
	}

	/**
	 * Launches the Traceroute
	 *
	 * @param url
	 *            The url to trace
	 * @param maxTtl
	 *            The max time to live to set (ping param)
	 */
	public void executeTraceroute(String url, int maxTtl, MtrContentBinding binding) {
		this.ttl = 1;
		this.finishedTasks = 0;
		this.urlToPing = url;
		this.binding = binding;

		new ExecutePingAsyncTask(maxTtl).execute();
	}

	/**
	 * Allows to timeout the ping if TIMEOUT exceeds. (-w and -W are not always supported on Android)
	 */
	private class TimeOutAsyncTask extends AsyncTask<Void, Void, Void> {

		private ExecutePingAsyncTask task;
		private int ttlTask;

		public TimeOutAsyncTask(ExecutePingAsyncTask task, int ttlTask) {
			this.task = task;
			this.ttlTask = ttlTask;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (handlerTimeout == null) {
				handlerTimeout = new Handler();
			}

			// stop old timeout
			if (runnableTimeout != null) {
				handlerTimeout.removeCallbacks(runnableTimeout);
			}
			// define timeout
			runnableTimeout = new Runnable() {
				@Override
				public void run() {
					if (task != null) {
						Log.e(TraceActivity.getTag(), ttlTask + " task.isFinished()" + finishedTasks + " " + (ttlTask == finishedTasks));
						if (ttlTask == finishedTasks) {
//							Toast.makeText(context, context.getString(R.string.timeout), Toast.LENGTH_SHORT).show();
							task.setCancelled(true);
							task.cancel(true);
//							context.stopProgressBar();
							context.stopProgressBar(binding);
						}
					}
				}
			};
			// launch timeout after a delay
			handlerTimeout.postDelayed(runnableTimeout, TIMEOUT);

			super.onPostExecute(result);
		}
	}

	/**
	 * The task that ping an ip, with increasing time to live (ttl) value
	 */
	private class ExecutePingAsyncTask extends AsyncTask<Void, Void, String> {

		private boolean isCancelled;
		private int maxTtl;

		public ExecutePingAsyncTask(int maxTtl) {
			this.maxTtl = maxTtl;
		}

		/**
		 * Launches the ping, launches InetAddress to retrieve url if there is one, store trace
		 */
		@Override
		protected String doInBackground(Void... params) {
			if (hasConnectivity()) {
				try {
					String res = launchPing(urlToPing);

					TracerouteContainerMtr trace;
					String ip = parseIpFromPing(res);

					if (res.contains(UNREACHABLE_PING) && !res.contains(EXCEED_PING)) {
						// Create the TracerouteContainer object when ping
						// failed
						trace = new TracerouteContainerMtr("", ip,"100%",elapsedTime,false);
					} else {
						// Create the TracerouteContainer object when succeed
						trace = new TracerouteContainerMtr("", ip, !res.contains(EXCEED_PING)&&ttl == maxTtl?parseLossRateFromPing(res):"loss 0%",ttl == maxTtl ? Float.parseFloat(parseTimeFromPing(res))
								: elapsedTime, true);
					}

					// Get the host name from ip (unix ping do not support
					// hostname resolving)
//					Log.d(TraceActivity.tag, "hostname : " );
					InetAddress inetAddr = InetAddress.getByName(trace.getIp());
					String hostname = inetAddr.getHostName();
					String canonicalHostname = inetAddr.getCanonicalHostName();
					trace.setHostname(hostname);
					latestTrace = trace;
					Log.d(TraceActivity.getTag(), "hostname : " + hostname);
					Log.d(TraceActivity.getTag(), "canonicalHostname : " + canonicalHostname);

					// Store the TracerouteContainer object
					Log.d(TraceActivity.getTag(), trace.toString());

					// Not refresh list if this ip is the final ip but the ttl is not maxTtl
					// this row will be inserted later
					if (!ip.equals(ipToPing) || ttl == maxTtl) {

						String a = trace.getIp()+","+trace.getHostname()+","+trace.getLossRate()+","+trace.getMs();
//						Log.d("3331", a);
						context.getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
//								binding.textViewResult.setText(binding.textViewResult.getText()+"\n"+a);
//								traceListAdapter.notifyDataSetChanged()
								context.refreshList(trace);
							}
						});
					}

					return res;
				} catch (final Exception e) {
					context.getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onException(e);
						}
					});
				}
			} else {
				return context.getString(R.string.no_connectivity);
			}
			return "";
		}

		/**
		 * Launches ping command
		 *
		 * @param url
		 *            The url to ping
		 * @return The ping string
		 */
		@SuppressLint("NewApi")
		private String launchPing(String url) throws Exception {
			// Build ping command with parameters
			Process p;
			String command = "";

			int pc=Data.getpc();
			float wt = Data.getwt();
			BigDecimal b = new BigDecimal(String.valueOf(wt));
			double d = b.doubleValue();

//			String format = "ping -c 5 -i 0.2 -t %d ";
//			String format6 = "ping6 -c 5 -i 0.2 -t %d ";

			String format = "ping -c %d -i %f -t %d ";
			String format6 = "ping6 -c %d -i %f -t %d ";

			InetAddress addrs = InetAddress.getByName(url);

			if ((addrs instanceof Inet6Address) ) {
				command = String.format(format6, pc,d,ttl);
//					command = String.format(format6,ttl);
			} else {
				command = String.format(format, pc,d,ttl);
//					command = String.format(format,ttl);
			}

			String ip= addrs.toString().split("/")[1];

//			Log.d("11111", "Will launch3 : " + command + ip);

			long startTime = System.nanoTime();
			elapsedTime = 0;
			// timeout task
			new TimeOutAsyncTask(this, ttl).execute();
			// Launch command
			p = Runtime.getRuntime().exec(command + ip);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

			// Construct the response from ping
			String s;
			String res = "";
			while ((s = stdInput.readLine()) != null) {
				res += s + "\n";
//				if (s.contains(FROM_PING) || s.contains(SMALL_FROM_PING)) {
//					// We store the elapsedTime when the line from ping comes
//					elapsedTime = (System.nanoTime() - startTime - 4000000) / 5.0f / 1000000.0f;
//					//Log.d("111",(System.nanoTime() - startTime - 4000000.0f)+":"+(System.nanoTime() - startTime - 4000000.0f) / 4.0f+":"+(System.nanoTime() - startTime - 4000000.0f) / 4.0f / 1000000.0f);
//				}
			}
//			Log.d(TraceActivity.tag, "Will launch33 : " + res);
//			Log.d(TraceActivity.tag, "Will launch33 : " + parseexceedTimeFromPing(res));

//			elapsedTime=(Float.parseFloat(parseexceedTimeFromPing(res))-200*4)/5.0f;
			elapsedTime=(Float.parseFloat(parseexceedTimeFromPing(res))-wt*1000*(pc-1))/pc/1.0f;
			p.destroy();

			if (res.equals("")) {
				throw new IllegalArgumentException();
			}

			// Store the wanted ip adress to compare with ping result
			if (ttl == 1) {
				ipToPing = parseIpToPingFromPing(res);
			}

			return res;
		}

		/**
		 * Treat the previous ping (launches a ttl+1 if it is not the final ip, refresh the list on view etc...)
		 */
		@Override
		protected void onPostExecute(String result) {
			if (!isCancelled) {
				try {
					if (!"".equals(result)) {
						if (context.getString(R.string.no_connectivity).equals(result)) {
//							Toast.makeText(context, context.getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();
						} else {
							Log.d(TraceActivity.getTag(), result);

							if (latestTrace != null && latestTrace.getIp().equals(ipToPing)) {
								if (ttl < maxTtl) {
									ttl = maxTtl;
									new ExecutePingAsyncTask(maxTtl).execute();
								} else {
									context.stopProgressBar(binding);
								}
							} else {
								if (ttl < maxTtl) {
									ttl++;
									new ExecutePingAsyncTask(maxTtl).execute();
								}
							}
//							context.refreshList(traces);
						}
					}
					finishedTasks++;
				} catch (final Exception e) {

							onException(e);
				}
			}

			super.onPostExecute(result);
		}

		/**
		 * Handles exception on ping
		 *
		 * @param e
		 *            The exception thrown
		 */
		private void onException(Exception e) {
			Log.e(TraceActivity.getTag()+"123", e.toString());

			if (e instanceof IllegalArgumentException) {
//				Toast.makeText(context, context.getString(R.string.no_ping), Toast.LENGTH_SHORT).show();
			} else {
//				Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show();
			}

			context.stopProgressBar(binding);

			finishedTasks++;
		}

		public void setCancelled(boolean isCancelled) {
			this.isCancelled = isCancelled;
		}

	}

	/**
	 * Gets the ip from the string returned by a ping
	 *
	 * @param ping
	 *            The string returned by a ping command
	 * @return The ip contained in the ping
	 */
	private String parseIpFromPing(String ping) {
		String ip = "";
		if (ping.contains(FROM_PING)) {
			// Get ip when ttl exceeded
			int index = ping.indexOf(FROM_PING);

			ip = ping.substring(index + 5);

			if (ip.contains(PARENTHESE_OPEN_PING)) {
				// Get ip when in parenthese
				int indexOpen = ip.indexOf(PARENTHESE_OPEN_PING);
				int indexClose = ip.indexOf(PARENTHESE_CLOSE_PING);

				ip = ip.substring(indexOpen + 1, indexClose);


			} else {
				// Get ip when after from
				ip = ip.substring(0, ip.indexOf("\n"));


				if (ip.contains(" ")) {
					index = ip.indexOf(" ");
				}

				ip = ip.substring(0, index);
				if(ip.substring(ip.length()-1).equals(":"))
				{
					ip = ip.substring(0, ip.length()-1);
				}

			}

		} else {
			// Get ip when ping succeeded
			int indexOpen = ping.indexOf(PARENTHESE_OPEN_PING);
			int indexClose = ping.indexOf(PARENTHESE_CLOSE_PING);

			ip = ping.substring(indexOpen + 1, indexClose);
		}
//		Log.d(TraceActivity.tag, "parseIpFromPing4 : " + ip);
		return ip;
	}

	/**
	 * Gets the final ip we want to ping (example: if user fullfilled google.fr, final ip could be 8.8.8.8)
	 *
	 * @param ping
	 *            The string returned by a ping command
	 * @return The ip contained in the ping
	 */
	private String parseIpToPingFromPing(String ping) {
		String ip = "";
		if (ping.contains(PING) || ping.contains(PING6)) {
			// Get ip when ping succeeded
			int indexOpen = ping.indexOf(PARENTHESE_OPEN_PING);
			int indexClose = ping.indexOf(PARENTHESE_CLOSE_PING);

			ip = ping.substring(indexOpen + 1, indexClose);
		}

		return ip;
	}


	/**
	 * Gets the time from ping command (if there is)
	 *
	 * @param ping
	 *            The string returned by a ping command
	 * @return The time contained in the ping
	 */
	private String parseexceedTimeFromPing(String ping) {
		String time = "";
		if (ping.contains("packet loss, time")) {
			int index = ping.indexOf("packet loss, time");

			time = ping.substring(index + 18);
			time=time.split("ms")[0];
//			index = time.indexOf("/");
//			time = time.substring(0, index);
		}

		return time;
	}

	private String parseTimeFromPing(String ping) {
		String time = "";
		if (ping.contains(TIME_PING)) {
			int index = ping.indexOf(TIME_PING);

			time = ping.substring(index + 19);
			time=time.split("/")[1];
//			index = time.indexOf("/");
//			time = time.substring(0, index);
		}

		return time;
	}
	private String parseLossRateFromPing(String ping) {
		String time = "";
		String[] time1=null;
		if (ping.contains(LossRate)) {
			int index = ping.indexOf(LossRate);
			time1=ping.split(LossRate)[0].split(" ");
			time=time1[time1.length-1];
//			time = ping.substring(index + 10);
//			index = time.indexOf(" ");
//			time = time.substring(0, index);
		}

		return "loss "+time;
	}

	/**
	 * Check for connectivity (wifi and mobile)
	 *
	 * @return true if there is a connectivity, false otherwise
	 */
	public boolean hasConnectivity() {
//		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
//		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		return true;
	}
//	public reshList(trace TracerouteContainerMtr) {
//
//	}
}
