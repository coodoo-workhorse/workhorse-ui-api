package io.coodoo.workhorse.api.dto;

import io.coodoo.workhorse.core.entity.WorkhorseConfig;

public class ConfigDTO {
    public String timeZone;
    public int jobQueueMax;
    public int jobQueueMin;
    public int jobQueuePollerInterval; // in seconds
    public int jobQueuePusherPoll; // in seconds

    public boolean isPusherAvailable;

    public ConfigDTO() {}

    public ConfigDTO(WorkhorseConfig workhorseConfig) {
        timeZone = workhorseConfig.getTimeZone();
        jobQueueMax = workhorseConfig.getBufferMax();
        jobQueueMin = workhorseConfig.getBufferMin();
        jobQueuePollerInterval = workhorseConfig.getBufferPollInterval();
        jobQueuePusherPoll = workhorseConfig.getBufferPushFallbackPollInterval();
    }
}
