package com.mdu.jobinvoicing;

import java.util.ArrayList;
import java.util.List;

public class TicketRepository {
    private static final List<LoadTicket> tickets = new ArrayList<>();
    private static OnTicketsUpdatedListener listener;

    public static void addTicket(LoadTicket ticket) {
        tickets.add(ticket);
        if (listener != null) listener.onUpdated();
    }

    public static List<LoadTicket> getTickets() {
        return tickets;
    }

    public static void setListener(OnTicketsUpdatedListener l) {
        listener = l;
    }

    public interface OnTicketsUpdatedListener {
        void onUpdated();
    }
}
