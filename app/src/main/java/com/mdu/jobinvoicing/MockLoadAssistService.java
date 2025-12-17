package com.mdu.jobinvoicing;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MockLoadAssistService extends Service {

    private final IBinder binder = new LocalBinder();
    private OnReceiptCompletedListener listener;

    public class LocalBinder extends Binder {
        MockLoadAssistService getService() { return MockLoadAssistService.this; }
    }

    public void setOnReceiptCompletedListener(OnReceiptCompletedListener listener) {
        this.listener = listener;
    }

    @Override
    public IBinder onBind(Intent intent) {
        simulateTickets();
        return binder;
    }

    private void simulateTickets() {
        new Thread(() -> {
            try {
                InputStream is = getAssets().open("receipt.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                boolean firstLine = true;
                Handler handler = new Handler(getMainLooper());

                while ((line = reader.readLine()) != null) {
                    if (firstLine) { firstLine = false; continue; } // skip header
                    String[] parts = line.split(",");

                    LoadTicket ticket = new LoadTicket(
                            parts[0], parts[1], parts[2], parts[3], parts[4],
                            parts[5], parts[6], parts[7], parts[8], parts[9], parts[10]
                    );

                    Thread.sleep(2000); // simulate delay

                    // Add to repository and notify listener
                    TicketRepository.addTicket(ticket);

                    if (listener != null) {
                        handler.post(() -> listener.onReceiptCompleted(ticket));
                    }
                }
            } catch (IOException | InterruptedException e) {
                Log.e("MockService", "Error reading CSV", e);
            }
        }).start();
    }
}
