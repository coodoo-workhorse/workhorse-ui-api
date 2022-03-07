package io.coodoo.workhorse.api.dto;

import io.coodoo.workhorse.core.entity.Job;
import io.coodoo.workhorse.util.WorkhorseApiUtil;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class JobDTO extends Job {

    public String scheduleDescription;

    public JobDTO() {}

    public JobDTO(Job job) {
        super();
        this.scheduleDescription = WorkhorseApiUtil.cronExpressionDescription(job.getSchedule());
    }
}
