package de.chribi.predictable.util;

import org.joda.time.DateTimeZone;

import java.util.Date;

public interface DateTimeProvider {
    Date getCurrentDateTime();
    DateTimeZone getCurrentTimeZone();
}
