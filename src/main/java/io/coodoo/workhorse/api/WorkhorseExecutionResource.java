package io.coodoo.workhorse.api;

import java.time.LocalDateTime;
import java.util.Random;

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
import javax.ws.rs.core.MediaType;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import io.coodoo.workhorse.api.dto.GroupInfo;
import io.coodoo.workhorse.api.dto.QueryParamListingParameters;
import io.coodoo.workhorse.core.boundary.WorkhorseService;
import io.coodoo.workhorse.core.entity.Execution;
import io.coodoo.workhorse.core.entity.ExecutionLog;
import io.coodoo.workhorse.core.entity.ExecutionStatusCounts;
import io.coodoo.workhorse.core.entity.Job;
import io.coodoo.workhorse.persistence.interfaces.listing.ListingResult;
import io.coodoo.workhorse.util.WorkhorseUtil;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@Path("/workhorse/jobs/{jobId}/executions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkhorseExecutionResource {

    @Inject
    WorkhorseService workhorseService;

    @GET
    @Path("/")
    public ListingResult<Execution> getExecutions(@PathParam("jobId") Long jobId, @BeanParam QueryParamListingParameters listingParameters) {
        listingParameters.mapQueryParams();
        if (jobId != null && jobId > 0) {
            listingParameters.addFilterAttributes("jobId", jobId.toString());
        }

        if (listingParameters.getSortAttribute() == null || listingParameters.getSortAttribute().isEmpty()) {
            listingParameters.setSortAttribute("-createdAt");
        }
        return workhorseService.getExecutionListing(jobId, listingParameters);
    }

    @GET
    @Path("/{executionId}")
    public Execution getJobExecution(@PathParam("jobId") Long jobId, @PathParam("executionId") Long executionId) {
        return workhorseService.getExecutionById(null, executionId);
    }

    @POST
    @Path("")
    public Execution createJobExecution(@PathParam("jobId") Long jobId, Execution jobExecution) {
        return workhorseService.createExecution(jobId, jobExecution.getParameters(), jobExecution.isPriority(), jobExecution.getPlannedFor(),
                        jobExecution.getExpiresAt(), jobExecution.getBatchId(), jobExecution.getChainId());
    }

    @PUT
    @Path("/{executionId}")
    public Execution updateJobExecution(@PathParam("jobId") Long jobId, @PathParam("executionId") Long executionId, Execution jobExecution) {
        return workhorseService.updateExecution(jobId, executionId, jobExecution.getStatus(), jobExecution.getParameters(), jobExecution.isPriority(),
                        jobExecution.getPlannedFor(), jobExecution.getFailRetry());
    }

    @DELETE
    @Path("/{executionId}")
    public void deleteJobExecution(@PathParam("jobId") Long jobId, @PathParam("executionId") Long executionId) {
        workhorseService.deleteExecution(jobId, executionId);
    }

    @GET
    @Path("/{executionId}/redo")
    public Execution redoJobExecution(@PathParam("jobId") Long jobId, @PathParam("executionId") Long executionId) {
        return workhorseService.redoJobExecution(jobId, executionId);
    }

    @GET
    @Path("/{executionId}/log")
    public ExecutionLog getJobExecutionLog(@PathParam("jobId") Long jobId, @PathParam("executionId") Long executionId) {
        return workhorseService.getExecutionLog(jobId, executionId);
    }

    @GET
    @Path("/batch/{batchId}")
    public GroupInfo getBatchInfo(@PathParam("jobId") Long jobId, @PathParam("batchId") Long batchId) {
        return new GroupInfo(batchId, workhorseService.getExecutionBatch(jobId, batchId));
    }

    @GET
    @Path("/chain/{chainId}")
    public GroupInfo getChainInfo(@PathParam("jobId") Long jobId, @PathParam("chainId") Long chainId) {
        return new GroupInfo(chainId, workhorseService.getExecutionChain(jobId, chainId));
    }

    @GET
    @Path("/status-counts/{last-minutes}")
    public ExecutionStatusCounts getStatusCount(@PathParam("jobId") Long pathJobId, @PathParam("last-minutes") Integer lastMinutes) {

        Long jobId = null;
        if (pathJobId != null && pathJobId >= 0) {
            jobId = pathJobId;
        }
        LocalDateTime from = null;
        if (lastMinutes != null) {
            from = WorkhorseUtil.timestamp().minusMinutes(lastMinutes);
        }
        return workhorseService.getExecutionStatusCounts(jobId, from);
    }

    @GET
    @Path("/random-parameters")
    @Produces(MediaType.TEXT_PLAIN)
    public String getRandomParameters(@PathParam("jobId") Long jobId) {

        try {
            Job job = workhorseService.getJobById(jobId);
            if (job == null) {
                return null;
            }
            String parametersClassName = job.getParametersClassName();
            if (parametersClassName == null) {
                return null;
            }
            Class<?> parametersClass = Class.forName(parametersClassName);

            EasyRandomParameters parameters = new EasyRandomParameters();
            parameters.seed(new Random().nextLong());
            parameters.stringLengthRange(3, 12);
            parameters.collectionSizeRange(2, 5);
            parameters.randomizationDepth(5);
            parameters.ignoreRandomizationErrors(true);
            parameters.overrideDefaultInitialization(false);

            EasyRandom generator = new EasyRandom(parameters);
            Object parametersRandom = generator.nextObject(parametersClass);

            return WorkhorseUtil.parametersToJson(parametersRandom);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @GET
    @Path("/latest-parameters")
    @Produces(MediaType.TEXT_PLAIN)
    public String getLatestParameters(@PathParam("jobId") Long jobId) {

        Job job = workhorseService.getJobById(jobId);
        if (job == null) {
            return null;
        }
        String parametersClassName = job.getParametersClassName();
        if (parametersClassName == null) {
            return null;
        }

        QueryParamListingParameters listingParameters = new QueryParamListingParameters();
        listingParameters.setLimit(5); // for good measure, could have also gone for "parameters":"NOT NULL" but ja...
        listingParameters.setSortAttribute("-createdAt");
        listingParameters.addFilterAttributes("jobId", jobId.toString());

        ListingResult<Execution> listingResult = workhorseService.getExecutionListing(jobId, listingParameters);

        for (Execution execution : listingResult.getResults()) {
            if (execution.getParameters() != null) {
                return execution.getParameters();
            }
        }
        return null;
    }
}
