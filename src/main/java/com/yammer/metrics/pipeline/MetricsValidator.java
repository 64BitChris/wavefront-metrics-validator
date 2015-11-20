package com.yammer.metrics.pipeline;

/**
 * Determine if a metric String is valid or not.
 */
@FunctionalInterface
public interface MetricsValidator {

    /**
     * Validate the metric String.
     * @param metric The Metric string to validate.
     * @return true if the metric is considered valid.
     */
    boolean validate(String metric);
}
