package com.yammer.metrics.pipeline;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Initially Created by Chris Shellenbarger in November, 2015.
 */
public class WavefrontValidatorTest {

    @Test
    public void testValidate() throws Exception {

        final MetricsValidator validator = new WavefrontValidator();

        assertTrue(validator.validate("com.magic 23 timestamp=1447994982 host=chris"));
        assertTrue(validator.validate("com.magic -23 timestamp=1447994982 host=chris"));
        assertTrue(validator.validate("com.magic 23.99 timestamp=1447994982 hostname=chris-test"));
        assertTrue(validator.validate("com.magic.value 2.34343E10 timestamp=1447994982 host=chris-test.com"));
        assertTrue(validator.validate("com.magic.value 2.34343E-10 timestamp=1447994982 host=chris-test.com"));
        assertTrue(validator.validate("com.magic.value 2.34343e10 timestamp=1447994982 hostname=chris.test.yammer.com"));
        assertTrue(validator.validate("com.magic.value 2.34343e-10 timestamp=1447994982 hostname=chris.test.yammer.com"));

        // Empty/Null cases
        assertFalse(validator.validate(""));
        assertFalse(validator.validate(" "));
        assertFalse(validator.validate("random steats of strings "));
        assertFalse(validator.validate(null));

        // Bad Metric Names
        assertFalse(validator.validate("23 timestamp=1447994982 host=chris"));
        assertFalse(validator.validate(".com.magic 23 timestamp=1447994982 host=chris"));
        assertFalse(validator.validate("-com.magic 23 timestamp=1447994982 host=chris"));
        assertFalse(validator.validate("commagic 23 timestamp=1447994982 host=chris"));
        assertFalse(validator.validate("commagic- 23 timestamp=1447994982 host=chris"));

        // Bad Metric Values
        assertFalse(validator.validate("com.magic timestamp=1447994982 host=chris"));
        assertFalse(validator.validate("com.magic number timestamp=1447994982 host=chris"));
        assertFalse(validator.validate("com.magic 23 0 timestamp=1447994982 host=chris"));
        assertFalse(validator.validate("com.magic 23E0 timestamp=1447994982 host=chris"));
        assertFalse(validator.validate("com.magic 23E-0 timestamp=1447994982 host=chris"));

        // Bad Timestamps
        assertFalse(validator.validate("com.magic 23 host=chris"));
        assertFalse(validator.validate("com.magic 23 timestam=1447994982 host=chris"));
        assertFalse(validator.validate("com.magic.value 2.34343e10 timestamp=1447994982000 host=chris")); // Milliseconds
        assertFalse(validator.validate("com.magic.value 2.34343e10 timestamp=144799498 host=chris")); // Missing a digit
        assertFalse(validator.validate("com.magic.value 2.34343e10 timestamp=14479949820 host=chris")); // Extra digit

        /** Bad Hosts */
        assertFalse(validator.validate("com.magic.value 2.34343e10 timestamp=1447994982"));
        assertFalse(validator.validate("com.magic.value 2.34343e10 timestamp=1447994982 host="));
        assertFalse(validator.validate("com.magic.value 2.34343e10 timestamp=1447994982 host= "));
        assertFalse(validator.validate("com.magic.value 2.34343e10 timestamp=1447994982 host=."));
        assertFalse(validator.validate("com.magic.value 2.34343e10 timestamp=1447994982 host=-"));

    }
}