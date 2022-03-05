package io.coodoo.workhorse.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.coodoo.workhorse.api.dto.JobDTO;
import io.coodoo.workhorse.api.dto.QueryParamListingParameters;
import io.coodoo.workhorse.core.boundary.WorkhorseService;
import io.coodoo.workhorse.core.entity.Job;
import io.coodoo.workhorse.persistence.interfaces.listing.ListingResult;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@Path("/workhorse/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkhorseJobResource {

    @Inject
    WorkhorseService workhorseService;

    @GET
    @Path("/")
    // FIXME: ohne DTO!
    public ListingResult<JobDTO> getJobs(@BeanParam QueryParamListingParameters listingParameters) {
        listingParameters.mapQueryParams();
        ListingResult<Job> listingResult = (ListingResult<Job>) workhorseService.getJobListing(listingParameters);
        List<JobDTO> dtos = listingResult.getResults().stream().map(JobDTO::new).collect(Collectors.toList());
        return new ListingResult<JobDTO>(dtos, listingResult.getMetadata());
    }

    @GET
    @Path("/{jobId}")
    public Response getJob(@PathParam("jobId") Long jobId) {
        Job job = workhorseService.getJobById(jobId);
        if (job == null) {
            return Response.status(Status.BAD_REQUEST).entity("Job does not exist.").build();
        }
        return Response.ok(new JobDTO(job)).build();
    }

    @PUT
    @Path("/{jobId}")
    public JobDTO updateJob(@PathParam("jobId") Long jobId, Job job) {
        Job updatedJob = workhorseService.updateJob(jobId, job.getName(), job.getDescription(), job.getWorkerClassName(), job.getSchedule(), job.getStatus(),
                        job.getThreads(), job.getMaxPerMinute(), job.getFailRetries(), job.getRetryDelay(), job.getMinutesUntilCleanUp(), job.isUniqueQueued());
        return new JobDTO(updatedJob);
    }

    @DELETE
    @Path("/{jobId}")
    public void deleteJob(@PathParam("jobId") Long jobId) {
        workhorseService.deleteJob(jobId);
    }

}
