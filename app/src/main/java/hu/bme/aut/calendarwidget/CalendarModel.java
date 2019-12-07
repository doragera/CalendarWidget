package hu.bme.aut.calendarwidget;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
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

    List<String> getVisibleCalendarIDs() {
        List<String> ids = new ArrayList<String>();
        for (CalendarInfo cal : calendars.values()) {
            if (cal.syncEvents && cal.visible)
                ids.add(cal.id);
        }
        return ids;
    }

//    void remove(String id) {
//        synchronized (calendars) {
//            calendars.remove(id);
//        }
//    }
//
//    CalendarInfo get(String id) {
//        synchronized (calendars) {
//            return calendars.get(id);
//        }
//    }
//
    void add(String accName, CalendarInfo calendarToAdd) {
        synchronized (calendars) {
            calendars.put(accName, calendarToAdd);
        }
    }
//
//    void add(CalendarListEntry calendarToAdd) {
//        synchronized (calendars) {
//            CalendarInfo found = get(calendarToAdd.getId());
//            if (found == null) {
//                calendars.put(calendarToAdd.getId(), new CalendarInfo(calendarToAdd));
//            } else {
//                found.update(calendarToAdd);
//            }
//        }
//    }
//
//    void reset(List<CalendarListEntry> calendarsToAdd) {
//        synchronized (calendars) {
//            calendars.clear();
//            for (CalendarListEntry calendarToAdd : calendarsToAdd) {
//                add(calendarToAdd);
//            }
//        }
//    }
//
//    public CalendarInfo[] toSortedArray() {
//        synchronized (calendars) {
//            List<CalendarInfo> result = new ArrayList<CalendarInfo>();
//            for (CalendarInfo calendar : calendars.values()) {
//                result.add(calendar.clone());
//            }
//            Collections.sort(result);
//            return result.toArray(new CalendarInfo[0]);
//        }
//    }
}
