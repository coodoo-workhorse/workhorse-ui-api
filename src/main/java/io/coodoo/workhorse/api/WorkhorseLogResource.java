package io.coodoo.workhorse.api;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.coodoo.workhorse.api.dto.LogView;
import io.coodoo.workhorse.api.dto.QueryParamListingParameters;
import io.coodoo.workhorse.core.boundary.WorkhorseLogService;
import io.coodoo.workhorse.core.boundary.WorkhorseService;
import io.coodoo.workhorse.core.entity.Job;
import io.coodoo.workhorse.core.entity.WorkhorseLog;
import io.coodoo.workhorse.persistence.interfaces.listing.ListingResult;

/**
 * Rest interface for the workhorse logs
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Path("/workhorse/logs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WorkhorseLogResource {

    @Inject
    WorkhorseLogService workhorseLogService;

    @Inject
    WorkhorseService workhorseService;

    @GET
    @Path("/")
    public ListingResult<WorkhorseLog> getLogs(@BeanParam QueryParamListingParameters listingParameters) {
        listingParameters.mapQueryParams();
        return workhorseLogService.getWorkhorseLogListing(listingParameters);
    }

    @Deprecated
    @GET
    @Path("/view")
    public ListingResult<LogView> getLogViews(@BeanParam QueryParamListingParameters listingParameters) {

        listingParameters.mapQueryParams();
        ListingResult<WorkhorseLog> workhorseLogListing = workhorseLogService.getWorkhorseLogListing(listingParameters);

        List<LogView> results = new ArrayList<>();

        for (WorkhorseLog log : workhorseLogListing.getResults()) {
            Job job = null;
            if (log.getJobId() != null) {
                job = workhorseService.getJobById(log.getJobId());
            }
            results.add(new LogView(log, job));
        }

        return new ListingResult<LogView>(results, workhorseLogListing.getMetadata());
    }

    @GET
    @Path("/{id}")
    public WorkhorseLog getLog(@PathParam("id") Long id) {
        return workhorseLogService.getLog(id);
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/")
    public WorkhorseLog createLogMessage(String message) {
        return workhorseLogService.logMessage(message, null, true);
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/{jobId}")
    public WorkhorseLog createLogMessage(@PathParam("jobId") Long jobId, String message) {
        return workhorseLogService.logMessage(message, jobId, true);
    }

}
