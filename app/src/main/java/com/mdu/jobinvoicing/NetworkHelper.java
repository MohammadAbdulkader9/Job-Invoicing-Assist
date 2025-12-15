package com.mdu.jobinvoicing;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

public class NetworkHelper {

    private final Context context;
    private final ImageView statusDot;
    private final TextView statusText;
    private ConnectivityManager.NetworkCallback networkCallback;

    public NetworkHelper(Context context, ImageView statusDot, TextView statusText) {
        this.context = context;
        this.statusDot = statusDot;
        this.statusText = statusText;
    }

    public void startMonitoring() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return;

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                new Handler(Looper.getMainLooper()).post(() -> updateStatus(true));
            }

            @Override
            public void onLost(Network network) {
                new Handler(Looper.getMainLooper()).post(() -> updateStatus(false));
            }
        };

        cm.registerDefaultNetworkCallback(networkCallback);

        // Set initial status
        updateStatus(isOnline());
    }

    public void stopMonitoring() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null && networkCallback != null) {
            cm.unregisterNetworkCallback(networkCallback);
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
    }

    private void updateStatus(boolean online) {
        if (statusDot != null && statusText != null) {
            if (online) {
                statusDot.setColorFilter(Color.parseColor("#4CAF50")); // green
                statusText.setText("Online");
            } else {
                statusDot.setColorFilter(Color.parseColor("#9E9E9E")); // grey
                statusText.setText("Offline");
            }
        }
    }
}
