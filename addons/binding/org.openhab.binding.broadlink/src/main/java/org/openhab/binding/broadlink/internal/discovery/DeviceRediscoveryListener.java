package org.openhab.binding.broadlink.internal.discovery;

public interface DeviceRediscoveryListener {
	public void onDeviceRediscovered(String newIpAddress);
	public void onDeviceRediscoveryFailure();
}
