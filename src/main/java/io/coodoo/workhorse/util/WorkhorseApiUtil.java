package io.coodoo.workhorse.util;

import it.burning.cron.CronExpressionDescriptor;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class WorkhorseApiUtil {

    private WorkhorseApiUtil() {}

    public static String cronExpressionDescription(String schedule) {

        if (schedule != null && !schedule.isEmpty()) {
            try {
                return CronExpressionDescriptor.getDescription(schedule);
            } catch (Exception e) {
                return WorkhorseUtil.getMessagesFromException(e);
            }
        }
        return null;
    }

}
