package de.chribi.predictable.util;


import java.util.Date;

public class DefaultDateTimeHandler implements DateTimeProvider {
    @Override
    public Date getCurrentDateTime() {
        return new Date();
    }
}
