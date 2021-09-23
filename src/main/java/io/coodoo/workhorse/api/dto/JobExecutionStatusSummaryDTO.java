package io.coodoo.workhorse.api.dto;

import io.coodoo.workhorse.core.entity.ExecutionStatus;
import io.coodoo.workhorse.core.entity.JobExecutionStatusSummary;

/**
 * @author coodoo GmbH (coodoo.de)
 */
public class JobExecutionStatusSummaryDTO {

    public ExecutionStatus status;
    public Long count;
    public JobDTO job;

    public JobExecutionStatusSummaryDTO() {}

    public JobExecutionStatusSummaryDTO(JobExecutionStatusSummary jobExecutionStatusSummary) {
        this.status = jobExecutionStatusSummary.getStatus();
        this.count = jobExecutionStatusSummary.getCount();
        this.job = new JobDTO(jobExecutionStatusSummary.getJob());
    }

}
