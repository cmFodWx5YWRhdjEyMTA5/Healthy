package com.amsu.healthy.utils.wifiTransmit;

import android.app.Activity;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

public class WifiAutoConnectManager {

	private static final String TAG = WifiAutoConnectManager.class
			.getSimpleName();

	WifiManager wifiManager;
	private Activity mActivity;


	// 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}

	// 构造函数
	public WifiAutoConnectManager(WifiManager wifiManager) {
		this.wifiManager = wifiManager;
	}

	// 构造函数
	public WifiAutoConnectManager(Activity activity,WifiManager wifiManager) {
		mActivity = activity;
		this.wifiManager = wifiManager;
	}

	// 提供一个外部接口，传入要连接的无线网
	public void connect(String ssid, String password, WifiCipherType type) {
		Thread thread = new Thread(new ConnectRunnable(ssid, password, type));
		thread.start();
	}

	// 查看以前是否也配置过这个网络
	private WifiConfiguration isExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
		if (existingConfigs!=null) {
			for (WifiConfiguration existingConfig : existingConfigs) {
				if (existingConfig.SSID!=null && existingConfig.SSID.equals("\"" + SSID + "\"")) {
					return existingConfig;
				}
			}
		}
		return null;
	}

	private WifiConfiguration createWifiInfo(String SSID, String Password,
											 WifiCipherType Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		// nopass
		if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
			// config.wepKeys[0] = "";
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			// config.wepTxKeyIndex = 0;
		}
		// wep
		if (Type == WifiCipherType.WIFICIPHER_WEP) {
			if (!TextUtils.isEmpty(Password)) {
				if (isHexWepKey(Password)) {
					config.wepKeys[0] = Password;
				} else {
					config.wepKeys[0] = "\"" + Password + "\"";
				}
			}
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		// wpa
		if (Type == WifiCipherType.WIFICIPHER_WPA) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// 此处需要修改否则不能自动重联
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	// 打开wifi功能
	private boolean openWifi() {
		boolean bRet = true;
		if (!wifiManager.isWifiEnabled()) {
			bRet = wifiManager.setWifiEnabled(true);
		}
		return bRet;
	}

	class ConnectRunnable implements Runnable {
		private String ssid;

		private String password;

		private WifiCipherType type;
		private boolean connected;

		public ConnectRunnable(String ssid, String password, WifiCipherType type) {
			this.ssid = ssid;
			this.password = password;
			this.type = type;
		}

		@Override
		public void run() {
			try {
				// 打开wifi
				openWifi();
				Thread.sleep(200);
				// 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
				// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
				while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
					try {
						// 为了避免程序一直while循环，让它睡个100毫秒检测……
						Thread.sleep(100);
					} catch (InterruptedException ie) {
					}
				}

				while (wifiManager.getConfiguredNetworks()==null || wifiManager.getConfiguredNetworks().size()==0) {
					try {
						// 为了避免程序一直while循环，让它睡个100毫秒检测……
						Thread.sleep(100);
					} catch (InterruptedException ie) {
					}
				}

				WifiConfiguration tempConfig = isExsits(ssid);

				Log.i(TAG,"tempConfig:"+ tempConfig);

				connected = false;

				if (tempConfig != null) {
					//wifiManager.removeNetwork(tempConfig.networkId);
					boolean enableNetwork = wifiManager.enableNetwork(tempConfig.networkId, true);
					Log.i(TAG,"enableNetwork:"+ enableNetwork);

					connected = enableNetwork;
				}
				else {
					WifiConfiguration wifiConfig = createWifiInfo(ssid, password,type);
					Log.i(TAG,"wifiConfig:"+wifiConfig);
					//
					if (wifiConfig == null) {
						Log.i(TAG,"wifiConfig is null!");
						return;
					}

					int netID = wifiManager.addNetwork(wifiConfig);
					Log.i(TAG,"netID:"+netID);

					boolean enabled = wifiManager.enableNetwork(netID, true);
					Log.i(TAG,"enabled:"+enabled);


					//connected = enabled;

					connected = wifiManager.reconnect();
				}

				long currentTimeMillis = System.currentTimeMillis();

				Log.i(TAG,"getSSID:"+ wifiManager.getConnectionInfo().getSSID());

				while (wifiManager.getConnectionInfo()==null && !("\""+ssid+"\"").equals(wifiManager.getConnectionInfo().getSSID())){
					Log.i(TAG,"getSSID:"+ wifiManager.getConnectionInfo().getSSID());
					Log.i(TAG,"connectionInfo:"+wifiManager.getConnectionInfo());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
					}

					if(System.currentTimeMillis()-currentTimeMillis>5000){  //超过5s就默认连接失败
						connected = false;
						break;
					}
				}

				if (connectStateResultChanged!=null){
					if (mActivity!=null && !mActivity.isFinishing() && !mActivity.isDestroyed())
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							connectStateResultChanged.onConnectStateChanged(connected);
						}
					});
				}

			} catch (Exception e) {
				// TODO: handle exception
				Log.i(TAG,"e:"+e);
				e.printStackTrace();
			}
		}
	}

	private static boolean isHexWepKey(String wepKey) {
		final int len = wepKey.length();

		// WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
		if (len != 10 && len != 26 && len != 58) {
			return false;
		}

		return isHex(wepKey);
	}

	private static boolean isHex(String key) {
		for (int i = key.length() - 1; i >= 0; i--) {
			final char c = key.charAt(i);
			if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
					&& c <= 'f')) {
				return false;
			}
		}

		return true;
	}

	public interface ConnectStateResultChanged{
		void onConnectStateChanged(boolean isConnected);
	}

	ConnectStateResultChanged connectStateResultChanged;

	public void setConnectStateResultChanged(ConnectStateResultChanged connectStateResultChanged){
		this.connectStateResultChanged = connectStateResultChanged;
	}

}