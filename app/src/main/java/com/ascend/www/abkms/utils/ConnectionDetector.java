package com.ascend.www.abkms.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector
	{

		private Context _context;

		public ConnectionDetector(Context context)
			{
				this._context = context;
			}
		public boolean isConnectingToInternet()
			{
				Boolean isConnected = false;
				ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
				if (connectivity != null)
					{
						NetworkInfo[] info = connectivity.getAllNetworkInfo();
						if (info != null)
							for (int i = 0; i < info.length; i++)
								if (info[i].getState() == NetworkInfo.State.CONNECTED)
									{
										isConnected = true;
									}

					}else {
					isConnected = false;
				}


				ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
				if ((connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null &&
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
						|| (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null &&
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED))
				{
					isConnected = true;
				} else {
					isConnected = false;
				}

				return isConnected;
			}

	}
