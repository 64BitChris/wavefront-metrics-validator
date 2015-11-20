package com.yammer.metrics.pipeline;

import org.springframework.core.convert.converter.Converter;

import java.io.UnsupportedEncodingException;

/**
 * Simple byte array to String converter; allowing the character set
 * to be specified.
 *
 * @author Gary Russell via Spring Integration Examples.
 * @since 2.1
 */
public class ByteArrayToStringConverter implements Converter<byte[], String> {

    private String charSet = "UTF-8";

    public String convert(byte[] bytes) {
        try {
            System.out.println("Converting....");
            return new String(bytes, this.charSet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new String(bytes);
        }
    }

    /**
     * @return the charSet
     */
    public String getCharSet() {
        return charSet;
    }

    /**
     * @param charSet the charSet to set
     */
    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

}
