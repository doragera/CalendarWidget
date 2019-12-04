package hu.bme.aut.calendarwidget;

import com.google.api.client.util.Objects;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;

class CalendarInfo {

    private String name;
    private String displayName;
    private Boolean visible;
    private Boolean syncevents;

    public CalendarInfo(String name, String displayName, Boolean visible, Boolean syncevents) {
        this.name = name;
        this.displayName = displayName;
        this.visible = visible;
        this.syncevents = syncevents;
    }

//    CalendarInfo(Calendar calendar) {
//        update(calendar);
//    }
//
//    CalendarInfo(CalendarListEntry calendar) {
//        update(calendar);
//    }
//
//    @Override
//    public String toString() {
//        return Objects.toStringHelper(CalendarInfo.class).add("id", id).add("summary", summary)
//                .toString();
//    }
//
//    public int compareTo(CalendarInfo other) {
//        return summary.compareTo(other.summary);
//    }
//
//    @Override
//    public CalendarInfo clone() {
//        try {
//            return (CalendarInfo) super.clone();
//        } catch (CloneNotSupportedException exception) {
//            // should not happen
//            throw new RuntimeException(exception);
//        }
//    }
//
//    void update(Calendar calendar) {
//        id = calendar.getId();
//        summary = calendar.getSummary();
//    }
//
//    void update(CalendarListEntry calendar) {
//        id = calendar.getId();
//        summary = calendar.getSummary();
//    }
}
