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
        this.setId(job.getId());
        this.setName(job.getName());
        this.setDescription(job.getDescription());
        this.setTags(job.getTags());
        this.setWorkerClassName(job.getWorkerClassName());
        this.setParametersClassName(job.getParametersClassName());
        this.setStatus(job.getStatus());
        this.setThreads(job.getThreads());
        this.setMaxPerMinute(job.getMaxPerMinute());
        this.setFailRetries(job.getFailRetries());
        this.setRetryDelay(getRetryDelay());
        this.setMinutesUntilCleanUp(job.getMinutesUntilCleanUp());
        this.setUniqueQueued(job.isUniqueQueued());
        this.setSchedule(job.getSchedule());
        this.scheduleDescription = WorkhorseApiUtil.cronExpressionDescription(job.getSchedule());
        this.setAsynchronous(job.isAsynchronous());
        this.setCreatedAt(job.getCreatedAt());
        this.setUpdatedAt(job.getUpdatedAt());
    }
}
