package io.coodoo.workhorse.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public class JobScheduleExecutionTimeDTO {

    public Long jobId;
    public String jobName;
    public String schedule;
    public String scheduleDescription;
    public List<LocalDateTime> executions;

}
