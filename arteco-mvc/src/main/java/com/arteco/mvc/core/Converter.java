package com.arteco.mvc.core;

/**
 * Developed by Arteco Consulting Sl.
 * Author rarnau on 11/11/16.
 */
public interface Converter {

    <T> T convert(Object value, Class<T> targetClass);

}
