package io.coodoo.workhorse.api.dto;

import io.coodoo.workhorse.core.entity.WorkhorseConfig;
import io.coodoo.workhorse.util.WorkhorseUtil;

public class WorkhorseConfigDTO {

    public WorkhorseConfig workhorseConfig;
    public String workhorseVersion = WorkhorseUtil.getVersion();

    public WorkhorseConfigDTO() {}

    public WorkhorseConfigDTO(WorkhorseConfig workhorseConfig, String workhorseVersion) {
        this.workhorseConfig = workhorseConfig;
        this.workhorseVersion = workhorseVersion;
    }

}
