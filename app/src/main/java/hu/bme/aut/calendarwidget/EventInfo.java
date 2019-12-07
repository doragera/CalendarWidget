package hu.bme.aut.calendarwidget;

import static java.lang.Math.min;

class EventInfo implements Comparable {
     int eventID;
     String title;
     Long begin;
     Long end;
     int color;

    public EventInfo(int eventID, String title, Long begin, Long end, int color) {
        this.eventID = eventID;
        this.title = title.substring(0, min(title.length(), 30));
        this.begin = begin;
        this.end = end;
        this.color = color;
    }

    @Override
    public int compareTo(Object o) {
        return (int) (this.begin-((EventInfo)o).begin);
    }
}
