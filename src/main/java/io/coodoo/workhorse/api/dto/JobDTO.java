package io.coodoo.workhorse.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.coodoo.workhorse.api.WorkhorseResource;
import io.coodoo.workhorse.core.entity.Job;
import io.coodoo.workhorse.core.entity.JobStatus;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class JobDTO {

    public Long id;
    public String name;
    public String description;
    public List<String> tags;
    public String workerClassName;
    public String parametersClassName;
    public JobStatus status;
    public int threads;
    public Integer maxPerMinute;
    public int failRetries;
    public int retryDelay;
    public int minutesUntilCleanUp;
    public boolean uniqueQueued;
    public String schedule;
    public String scheduleDescription;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public boolean asynchronous;

    public JobDTO() {}

    public JobDTO(Job job) {
        super();
        this.id = job.getId();
        this.name = job.getName();
        this.description = job.getDescription();
        this.tags = job.getTags();
        this.workerClassName = job.getWorkerClassName();
        this.parametersClassName = job.getParametersClassName();
        this.status = job.getStatus();
        this.threads = job.getThreads();
        this.maxPerMinute = job.getMaxPerMinute();
        this.failRetries = job.getFailRetries();
        this.retryDelay = job.getRetryDelay();
        this.minutesUntilCleanUp = job.getMinutesUntilCleanUp();
        this.uniqueQueued = job.isUniqueQueued();
        this.schedule = job.getSchedule();
        this.scheduleDescription = WorkhorseResource.cronExpressionDescriptorMessage(job.getSchedule());
        this.createdAt = job.getCreatedAt();
        this.updatedAt = job.getUpdatedAt();
        this.asynchronous = job.isAsynchronous();
    }

}
