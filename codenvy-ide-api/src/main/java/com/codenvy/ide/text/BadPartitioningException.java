/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.text;

/**
 * Represents the attempt to refer to a non-existing document partitioning.
 * <p>
 * This class is not intended to be serialized.
 * </p>
 *
 * @see Document
 */
public class BadPartitioningException extends Exception {

    /**
     * Serial version UID for this class.
     * <p>
     * Note: This class is not intended to be serialized.
     * </p>
     */
    private static final long serialVersionUID = 3256439205327876408L;

    /** Creates a new bad partitioning exception. */
    public BadPartitioningException() {
    }

    /**
     * Creates a new bad partitioning exception.
     *
     * @param message
     *         message describing the exception
     */
    public BadPartitioningException(String message) {
        super(message);
    }
}
