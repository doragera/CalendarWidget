package hu.bme.aut.calendarwidget;

class CalendarInfo {

    final String id;
    final String displayName;
    final Boolean visible;
    final boolean syncEvents;

    public CalendarInfo(String id, String displayName, boolean visible, boolean syncEvents) {
        this.id = id;
        this.displayName = displayName;
        this.visible = visible;
        this.syncEvents = syncEvents;
    }
}
