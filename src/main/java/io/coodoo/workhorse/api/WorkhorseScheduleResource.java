package io.coodoo.workhorse.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.coodoo.workhorse.api.dto.JobScheduleExecutionTimeDTO;
import io.coodoo.workhorse.core.boundary.WorkhorseService;
import io.coodoo.workhorse.core.entity.Job;
import io.coodoo.workhorse.util.WorkhorseApiUtil;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@Path("/workhorse/schedules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkhorseScheduleResource {

    @Inject
    WorkhorseService workhorseService;

    @GET
    @Path("/description")
    public Response description(@QueryParam("schedule") String schedule) {
        return Response.ok(WorkhorseApiUtil.cronExpressionDescription(schedule)).build();
    }

    @GET
    @Path("/next-scheduled-times")
    public List<LocalDateTime> getNextScheduledTimes(@QueryParam("schedule") String schedule, @QueryParam("times") Integer times,
                    @QueryParam("start") String start) {

        Integer scheduleTimes = times != null ? times : 5;
        LocalDateTime startTime = start != null ? LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME) : null;

        return workhorseService.getNextScheduledTimes(schedule, scheduleTimes, startTime);
    }

    @GET
    @Path("/all-scheduled-times")
    public List<JobScheduleExecutionTimeDTO> getAllScheduledTimes(@QueryParam("start") String start, @QueryParam("end") String end) {

        LocalDateTime startTime = start != null ? LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME) : null;
        LocalDateTime endTime = end != null ? LocalDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME) : null;

        List<JobScheduleExecutionTimeDTO> scheduledTimes = new ArrayList<>();
        for (Job job : workhorseService.getAllScheduledJobs()) {
            try {
                JobScheduleExecutionTimeDTO dto = new JobScheduleExecutionTimeDTO();
                dto.jobId = job.getId();
                dto.jobName = job.getName();
                dto.schedule = job.getSchedule();
                dto.scheduleDescription = WorkhorseApiUtil.cronExpressionDescription(job.getSchedule());
                dto.executions = workhorseService.getScheduledTimes(job.getSchedule(), startTime, endTime);
                scheduledTimes.add(dto);
            } catch (RuntimeException e) {
            }
        }
        return scheduledTimes;
    }

}
