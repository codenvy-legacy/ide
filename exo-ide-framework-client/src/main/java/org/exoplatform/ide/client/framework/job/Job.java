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
package org.exoplatform.ide.client.framework.job;

/**
 * Simple been to manage Running async REST Services
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 19, 2011 evgen $
 */
public class Job {

    public enum JobStatus {
        STARTED, FINISHED, ERROR
    }

    private String id;

    private JobStatus status;

    private String startMessage;

    private String finishMessage;

    private Throwable error;

    public Job(String id, JobStatus status) {
        this.id = id;
        this.status = status;
    }

    /** @return the startMessage */
    public String getStartMessage() {
        return startMessage;
    }

    /**
     * @param startMessage
     *         the startMessage to set
     */
    public void setStartMessage(String startMessage) {
        this.startMessage = startMessage;
    }

    /** @return the finishMessage */
    public String getFinishMessage() {
        return finishMessage;
    }

    /**
     * @param finishMessage
     *         the finishMessage to set
     */
    public void setFinishMessage(String finishMessage) {
        this.finishMessage = finishMessage;
    }

    /** @return the error */
    public Throwable getError() {
        return error;
    }

    /**
     * @param error
     *         the error to set
     */
    public void setError(Throwable error) {
        this.error = error;
    }

    /** @return the id */
    public String getId() {
        return id;
    }

    /** @return the status */
    public JobStatus getStatus() {
        return status;
    }

}
