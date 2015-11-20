package com.yammer.metrics.pipeline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validate the Wavefront metrics as best as we can.
 */
public class WavefrontValidator implements MetricsValidator {

    private final static String METRIC_NAME_PATTERN = "^\\w+[\\w+\\-]+\\.{1}[\\w\\-\\.]+";
    private final static String SINGLE_SPACE = " {1}";
    private final static String METRIC_VALUE_PATTERN = "\\-?\\d+(\\.\\d*([Ee]\\-?)?\\d*)?";
    private final static String TIMESTAMP_PATTERN = "timestamp=\\d{10}";
    private final static String HOSTNAME_PATTERN = "host(name)?\\=\\w+[\\w\\-\\.]+";
    private final static String END = "$";


//    private final static String regex = "^\\p{Alpha}+\\..*";
    private final static String regex = METRIC_NAME_PATTERN + SINGLE_SPACE + METRIC_VALUE_PATTERN + SINGLE_SPACE + TIMESTAMP_PATTERN + SINGLE_SPACE + HOSTNAME_PATTERN + END;
    private final static Pattern pattern = Pattern.compile(regex);

    public boolean validate(String metric) {
        if(metric != null && !metric.trim().isEmpty()){
            final Matcher matcher = pattern.matcher(metric);
            return matcher.matches();
        }else{
            return false;
        }
    }
}
