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

        assertTrue(validator.validate("com.magic 23 1447994982 host=chris"));
        assertTrue(validator.validate("com.magic -23 1447994982 host=chris"));
        assertTrue(validator.validate("com.magic 23.99 1447994982 hostname=chris-test"));
        assertTrue(validator.validate("com.magic.value 2.34343E10 1447994982 host=chris-test.com"));
        assertTrue(validator.validate("com.magic.value 2.34343E-10 1447994982 host=chris-test.com"));
        assertTrue(validator.validate("com.magic.value 2.34343e10 1447994982 hostname=chris.test.yammer.com"));
        assertTrue(validator.validate("com.magic.value 2.34343e-10 1447994982 hostname=chris.test.yammer.com"));

        // Empty/Null cases
        assertFalse(validator.validate(""));
        assertFalse(validator.validate(" "));
        assertFalse(validator.validate("random steats of strings "));
        assertFalse(validator.validate(null));

        // Bad Metric Names
        assertFalse(validator.validate("23 1447994982 host=chris"));
        assertFalse(validator.validate(".com.magic 23 1447994982 host=chris"));
        assertFalse(validator.validate("-com.magic 23 1447994982 host=chris"));
        assertFalse(validator.validate("commagic 23 1447994982 host=chris"));
        assertFalse(validator.validate("commagic- 23 1447994982 host=chris"));

        // Bad Metric Values
        assertFalse(validator.validate("com.magic 1447994982 host=chris"));
        assertFalse(validator.validate("com.magic number 1447994982 host=chris"));
        assertFalse(validator.validate("com.magic 23 0 1447994982 host=chris"));
        assertFalse(validator.validate("com.magic 23E0 1447994982 host=chris"));
        assertFalse(validator.validate("com.magic 23E-0 1447994982 host=chris"));

        // Bad Timestamps
        assertFalse(validator.validate("com.magic 23 host=chris"));
        assertFalse(validator.validate("com.magic 23 144799498212 host=chris"));
        assertFalse(validator.validate("com.magic.value 2.34343e10 1447994982000 host=chris")); // Milliseconds
        assertFalse(validator.validate("com.magic.value 2.34343e10 144799498 host=chris")); // Missing a digit
        assertFalse(validator.validate("com.magic.value 2.34343e10 14479949820 host=chris")); // Extra digit

        /** Bad Hosts */
        assertFalse(validator.validate("com.magic.value 2.34343e10 1447994982"));
        assertFalse(validator.validate("com.magic.value 2.34343e10 1447994982 host="));
        assertFalse(validator.validate("com.magic.value 2.34343e10 1447994982 host= "));
        assertFalse(validator.validate("com.magic.value 2.34343e10 1447994982 host=."));
        assertFalse(validator.validate("com.magic.value 2.34343e10 1447994982 host=-"));

    }
}
