package io.coodoo.workhorse.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.coodoo.workhorse.api.dto.ExecutionInfo;
import io.coodoo.workhorse.api.dto.GroupInfo;
import io.coodoo.workhorse.api.dto.JobCountView;
import io.coodoo.workhorse.api.dto.JobExecutionView;
import io.coodoo.workhorse.api.dto.JobScheduleExecutionTimeDTO;
import io.coodoo.workhorse.core.boundary.WorkhorseService;
import io.coodoo.workhorse.core.entity.Execution;
import io.coodoo.workhorse.core.entity.Job;
import io.coodoo.workhorse.core.entity.WorkhorseInfo;
import io.coodoo.workhorse.persistence.interfaces.listing.ListingParameters;
import io.coodoo.workhorse.persistence.interfaces.listing.ListingResult;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@Path("/workhorse")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkhorseResource {

    @Inject
    WorkhorseService workhorseService;

    @GET
    @Path("scheduled-job/{jobId}/stop")
    public void stopScheduledJob(@PathParam("jobId") Long jobId) {
        workhorseService.stopScheduledJob(jobId);
    }

    @GET
    @Path("/infos")
    public List<WorkhorseInfo> getJobEngineInfos() {
        return workhorseService.getAllJobs().stream().map(job -> workhorseService.getWorkhorseInfo(job.getId()))
                .collect(Collectors.toList());
    }

    @GET
    @Path("/infos/{jobId}")
    public WorkhorseInfo getJobEngineInfo(@PathParam("jobId") Long jobId) {
        return workhorseService.getWorkhorseInfo(jobId);
    }

    @GET
    @Path("/start")
    public Response start() {
        workhorseService.start();
        return Response.ok().build();
    }

    @GET
    @Path("/stop")
    public Response stop() {
        workhorseService.stop();
        return Response.ok().build();
    }

    @GET
    @Path("/is-running")
    public Response isRunning() {
        return Response.ok(workhorseService.isRunning()).build();
    }

    @GET
    @Path("/jobs")
    public ListingResult<Job> getJobs(@BeanParam ListingParameters listingParameters) {

        return workhorseService.getJobListing(listingParameters);
    }

    @PUT
    @Path("/jobs/{jobId}")
    public Job updateJob(@PathParam("jobId") Long jobId, Job job) {
        return workhorseService.updateJob(jobId, job.getName(), job.getDescription(), job.getWorkerClassName(),
                job.getSchedule(), job.getStatus(), job.getThreads(), job.getMaxPerMinute(), job.getFailRetries(),
                job.getRetryDelay(), job.getMinutesUntilCleanUp(), job.isUniqueQueued());
    }

    @GET
    @Path("/jobs-count")
    public ListingResult<JobCountView> getJobsWithCounts(@BeanParam ListingParameters listingParameters) {

        ListingResult<Job> jobsListing = workhorseService.getJobListing(listingParameters);
        List<JobCountView> results = jobsListing.getResults().stream().map(job -> new JobCountView(job))
                .collect(Collectors.toList());
        return new ListingResult<>(results, jobsListing.getMetadata());
    }

    @GET
    @Path("/executions/{jobExecutionId}")
    public Execution getJobExecution(@PathParam("jobExecutionId") Long jobExecutionId) {
        return workhorseService.getExecutionById(null, jobExecutionId);
    }

    @POST
    @Path("/jobs/{jobId}/executions")
    public Execution createJobExecution(@PathParam("jobId") Long jobId, Execution jobExecution) {
        return workhorseService.createExecution(jobId, jobExecution.getParameters(), jobExecution.isPriority(),
                jobExecution.getPlannedFor(), jobExecution.getExpiresAt(), jobExecution.getBatchId(),
                jobExecution.getChainId(), null, false);
    }

    @PUT
    @Path("/jobs/{jobId}/executions/{jobExecutionId}")
    public Execution updateJobExecution(@PathParam("jobId") Long jobId,
            @PathParam("jobExecutionId") Long jobExecutionId, Execution jobExecution) {
        return workhorseService.updateExecution(jobId, jobExecutionId, jobExecution.getStatus(),
                jobExecution.getParameters(), jobExecution.isPriority(), jobExecution.getPlannedFor(),
                jobExecution.getFailRetry());
    }

    @DELETE
    @Path("/jobs/{jobId}/executions/{jobExecutionId}")
    public void deleteJobExecution(@PathParam("jobId") Long jobId, @PathParam("jobExecutionId") Long jobExecutionId) {
        workhorseService.deleteExecution(jobId, jobExecutionId);
    }

    @GET
    @Path("/jobs/{jobId}/execution-views")
    public ListingResult<JobExecutionView> getExecutionViews(@PathParam("jobId") Long jobId,
            @BeanParam ListingParameters listingParameters) {

        if (jobId != null && jobId > 0) {
            listingParameters.addFilterAttributes("jobId", jobId.toString());
        }

        Job job = workhorseService.getJobById(jobId);

        ListingResult<Execution> executionListing = workhorseService.getExecutionListing(listingParameters);

        List<JobExecutionView> results = executionListing.getResults().stream()
                .map(jobExecution -> new JobExecutionView(job, jobExecution)).collect(Collectors.toList());

        return new ListingResult<>(results, executionListing.getMetadata());
    }

    @GET
    @Path("/jobs/{jobId}")
    public Job getJob(@PathParam("jobId") Long jobId) {
        return workhorseService.getJobById(jobId);
    }

    @GET
    @Path("/jobs/{jobId}/activate")
    public void activateJob(@PathParam("jobId") Long jobId) {
        workhorseService.activateJob(jobId);
    }

    @GET
    @Path("/jobs/{jobId}/deactivate")
    public void deactivateJob(@PathParam("jobId") Long jobId) {
        workhorseService.deactivateJob(jobId);
    }

    @GET
    @Path("/jobs/{jobId}/executions/{jobExecutionId}")
    public Execution getJobExecution(@PathParam("jobId") Long jobId, @PathParam("jobExecutionId") Long jobExecutionId) {
        return workhorseService.getExecutionById(jobId, jobExecutionId);
    }

    @GET
    @Path("/jobs/{jobId}/batch/{batchId}")
    public GroupInfo getBatchInfo(@PathParam("jobId") Long jobId, @PathParam("batchId") Long batchId) {

        List<Execution> batchExecutions = workhorseService.getExecutionBatch(jobId, batchId);
        List<ExecutionInfo> batchInfo = batchExecutions.stream()
                .map(execution -> new ExecutionInfo(execution.getId(), execution.getStatus(), execution.getStartedAt(),
                        execution.getEndedAt(), execution.getDuration(), execution.getFailRetryExecutionId()))
                .collect(Collectors.toList());

        return new GroupInfo(batchId, batchInfo);
    }

    @GET
    @Path("/jobs/{jobId}/batch/{batchId}/executions")
    public List<Execution> getBatchExecutions(@PathParam("jobId") Long jobId, @PathParam("batchId") Long batchId) {
        return workhorseService.getExecutionBatch(jobId, batchId);
    }

    @GET
    @Path("/jobs/{jobId}/chain/{chainId}")
    public GroupInfo getChainInfo(@PathParam("jobId") Long jobId, @PathParam("chainId") Long chainId) {

        List<Execution> chainExecutions = workhorseService.getExecutionChain(jobId, chainId);
        List<ExecutionInfo> batchInfo = chainExecutions.stream()
                .map(execution -> new ExecutionInfo(execution.getId(), execution.getStatus(), execution.getStartedAt(),
                        execution.getEndedAt(), execution.getDuration(), execution.getFailRetryExecutionId()))
                .collect(Collectors.toList());

        return new GroupInfo(chainId, batchInfo);
    }

    @GET
    @Path("/jobs/{jobId}/chain/{chainId}/executions")
    public List<Execution> getChainJobExecutions(@PathParam("jobId") Long jobId, @PathParam("chainId") Long chainId) {
        return workhorseService.getExecutionChain(jobId, chainId);
    }

    @POST
    @Path("/jobs/{jobId}/scheduled-job-execution")
    public Job scheduledJobExecutionCreation(@PathParam("jobId") Long jobId, Job job) throws Exception {

        workhorseService.triggerScheduledExecutionCreation(workhorseService.getJobById(jobId));
        return job;
    }

    @GET
    @Path("/jobs/next-scheduled-times")
    public List<LocalDateTime> getNextScheduledTimes(@QueryParam("schedule") String schedule,
            @QueryParam("times") Integer times, @QueryParam("start") String start) {

        Integer scheduleTimes = times != null ? times : 5;
        LocalDateTime startTime = start != null ? LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME) : null;

        return workhorseService.getNextScheduledTimes(schedule, scheduleTimes, startTime);
    }

    @GET
    @Path("/jobs/schedule-executions")
    public List<JobScheduleExecutionTimeDTO> getAllScheduleExecutionTimes(@QueryParam("start") String start,
            @QueryParam("end") String end) {

        LocalDateTime startTime = start != null ? LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME) : null;
        LocalDateTime endTime = end != null ? LocalDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME) : null;

        List<JobScheduleExecutionTimeDTO> scheduledTimes = new ArrayList<>();
        for (Job job : workhorseService.getAllScheduledJobs()) {
            try {
                JobScheduleExecutionTimeDTO dto = new JobScheduleExecutionTimeDTO();
                dto.jobId = job.getId();
                dto.jobName = job.getName();
                dto.schedule = job.getSchedule();
                dto.executions = workhorseService.getScheduledTimes(job.getSchedule(), startTime, endTime);
                scheduledTimes.add(dto);
            } catch (RuntimeException e) {
            }
        }
        return scheduledTimes;
    }

    // --------------------------------------------------------------------------------------------------------------
    // Deprecated
    // --------------------------------------------------------------------------------------------------------------

    @GET
    @Path("/execution-counts/{minutes}")
    public Object getJobExecutionCount(@PathParam("minutes") Integer minutes) {

        return null;
    }

    @GET
    @Path("/jobs/{jobId}/execution-counts/{minutes}")
    public Object getJobExecutionCountByJob(@PathParam("jobId") Long jobId, @PathParam("minutes") Integer minutes) {
        return null;
    }

}
