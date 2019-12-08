package hu.bme.aut.calendarwidget;

public class CalendarWidgetWeekProvider extends CalendarWidgetProvider {
    @Override
    protected int dayFrom() {
        return 1;
    }

    @Override
    protected int dayTo() {
        return 7;
    }

    @Override
    protected int layoutId() {
        return R.layout.widget_layout_week;
    }
}
