package hu.bme.aut.calendarwidget;

import com.google.common.collect.ArrayListMultimap;

import java.util.List;
import java.util.Set;

class CalendarModel {

    private final ArrayListMultimap<String, CalendarInfo> calendars = ArrayListMultimap.create();

    int size() {
        synchronized (calendars) {
            return calendars.size();
        }
    }

    Set<String> getAccounts() {
        return calendars.keySet();
    }

    List<CalendarInfo> getCalendars(String accName) {
        return calendars.get(accName);
    }

    void add(String accName, CalendarInfo calendarToAdd) {
        synchronized (calendars) {
            calendars.put(accName, calendarToAdd);
        }
    }
}
