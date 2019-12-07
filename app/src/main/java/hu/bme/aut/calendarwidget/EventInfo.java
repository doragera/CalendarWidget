package hu.bme.aut.calendarwidget;

class EventInfo implements Comparable {
     String eventID;
     String title;
     Long begin;
     Long end;
     String color;

    public EventInfo(String eventID, String title, Long begin, Long end, String color) {
        this.eventID = eventID;
        this.title = title;
        this.begin = begin;
        this.end = end;
        this.color = color;
    }

    @Override
    public int compareTo(Object o) {
        return (int) (this.begin-((EventInfo)o).begin);
    }
}
