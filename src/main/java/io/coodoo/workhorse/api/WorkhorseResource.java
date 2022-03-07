package io.coodoo.workhorse.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.coodoo.workhorse.api.dto.JobExecutionStatusSummariesDTO;
import io.coodoo.workhorse.api.dto.JobThreadDTO;
import io.coodoo.workhorse.core.boundary.WorkhorseService;
import io.coodoo.workhorse.core.control.JobThread;
import io.coodoo.workhorse.core.entity.ExecutionStatus;
import io.coodoo.workhorse.core.entity.JobExecutionStatusSummary;
import io.coodoo.workhorse.core.entity.WorkhorseInfo;
import io.coodoo.workhorse.util.WorkhorseUtil;

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
    @Path("/infos")
    public List<WorkhorseInfo> getJobEngineInfos() {
        return workhorseService.getAllJobs().stream().map(job -> workhorseService.getWorkhorseInfo(job.getId())).collect(Collectors.toList());
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
    @Path("/monitoring/job-execution-summary/{status}")
    public JobExecutionStatusSummariesDTO getJobExecutionStatusSummaries(@PathParam("status") ExecutionStatus status,
                    @QueryParam("last-minutes") Integer lastMinutes) {

        LocalDateTime since = null;
        Long count = 0L;

        if (lastMinutes != null) {
            since = WorkhorseUtil.timestamp().minusMinutes(lastMinutes);
        }
        List<JobExecutionStatusSummary> jobExecutionStatusSummaryies = workhorseService.getJobExecutionStatusSummaries(status, since);

        for (JobExecutionStatusSummary jobExecutionStatusSummary : jobExecutionStatusSummaryies) {
            count = count + jobExecutionStatusSummary.getCount();
        }
        return new JobExecutionStatusSummariesDTO(status, count, jobExecutionStatusSummaryies);
    }

    @GET
    @Path("/monitoring/job-threads")
    public List<JobThreadDTO> getJobThreads() {

        List<JobThreadDTO> jobThreads = new ArrayList<>();
        Map<Long, Set<JobThread>> jobThreadsMap = workhorseService.getJobThreads();

        for (Set<JobThread> setOfJobThread : jobThreadsMap.values()) {
            for (JobThread jobThread : setOfJobThread) {
                jobThreads.add(new JobThreadDTO(jobThread));
            }
        }
        return jobThreads;
    }

}
