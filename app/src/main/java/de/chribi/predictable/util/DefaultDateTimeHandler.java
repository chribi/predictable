package de.chribi.predictable.util;


import org.joda.time.DateTimeZone;

import java.util.Date;

public class DefaultDateTimeHandler implements DateTimeProvider {
    @Override
    public Date getCurrentDateTime() {
        return new Date();
    }

    @Override
    public DateTimeZone getCurrentTimeZone() {
        return DateTimeZone.getDefault();
    }
}
