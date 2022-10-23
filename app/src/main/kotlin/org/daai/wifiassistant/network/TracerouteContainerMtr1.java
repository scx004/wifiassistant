package org.daai.wifiassistant.network;

import java.io.Serializable;

public class TracerouteContainerMtr1 implements Serializable {

	private static final long serialVersionUID = 1034744411998219581L;

	private String hostname;
	private String ip;
	private String lossRate;
	private float ms;
	private boolean isSuccessful;

	public TracerouteContainerMtr1(String hostname, String ip, String lossRate,float ms, boolean isSuccessful) {
		this.hostname = hostname;
		this.ip = ip;
		this.ms = ms;
		this.lossRate=lossRate;
		this.isSuccessful = isSuccessful;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getIp() {
		return ip;
	}
	public String getLossRate() {
		return lossRate;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}

	public float getMs() {
		//return ms;
		return  (float)(Math.round(ms*100))/100;
	}

	public void setMs(float ms) {
		this.ms = ms;
	}


	public boolean isSuccessful() {
		return isSuccessful;
	}

	public void setSuccessful(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	@Override
	public String toString() {
		return "Traceroute : \nHostname : " + hostname + "\nip : " + ip + "\nMilliseconds : " + ms;
	}

}
