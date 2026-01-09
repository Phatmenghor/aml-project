package com.cpbank.AML_API.utils;

import com.cpbank.AML_API.constant.AppConstant;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static String generateTransactionId() {
        return ZonedDateTime.now(ZoneId.of(AppConstant.TIMEZONE_PHNOM_PENH))
                .format(DateTimeFormatter.ofPattern(AppConstant.TRANSACTION_ID_FORMAT));
    }

    private DateTimeUtils() {}
}
