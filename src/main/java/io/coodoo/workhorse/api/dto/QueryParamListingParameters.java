package io.coodoo.workhorse.api.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import io.coodoo.workhorse.persistence.interfaces.listing.ListingParameters;

/**
 * Listing query parameters and settings
 * 
 * @author coodoo GmbH (coodoo.io)
 */
public class QueryParamListingParameters extends ListingParameters {

    @Context
    private UriInfo uriInfo;

    @QueryParam("index")
    private Integer indexQueryParam;

    @QueryParam("page")
    private Integer pageQueryParam;

    @QueryParam("limit")
    private Integer limitQueryParam;

    @QueryParam("sort")
    private String sortAttributeQueryParam;

    @QueryParam("filter")
    private String filterQueryParam;

    public UriInfo getUriInfo() {
        return uriInfo;
    }

    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    /**
     * @return Map of attribute specific filters of parameters that start with "filter-"
     */
    @Override
    public Map<String, String> getFilterAttributes() {

        // collects filter from URI if given
        if (uriInfo != null) {

            MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);

            for (Map.Entry<String, List<String>> queryParameter : queryParameters.entrySet()) {

                String filterAttribute = queryParameter.getKey();
                if (filterAttribute == null || filterAttribute.isEmpty() || !filterAttribute.startsWith("filter-")) {
                    continue;
                }
                filterAttribute = filterAttribute.substring("filter-".length(), filterAttribute.length());

                addFilterAttributes(filterAttribute, queryParameter.getValue().get(0));
            }
        }
        return super.getFilterAttributes();
    }

    public void mapQueryParams() {
        super.setIndex(indexQueryParam);
        super.setPage(pageQueryParam);
        super.setLimit(limitQueryParam);
        super.setSortAttribute(sortAttributeQueryParam);
        super.setFilter(filterQueryParam);
    }
}
