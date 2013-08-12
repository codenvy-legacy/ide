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
package com.codenvy.ide.ext.aws.shared.beanstalk;

/**
 * Description of application event.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface Event {
    /**
     * The date when the event occurred.
     *
     * @return The date when the event occurred.
     */
    double getEventDate();

    /**
     * The event message.
     *
     * @return The event message.
     */
    String getMessage();

    /**
     * The application associated with the event.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 100<br/>
     *
     * @return The application associated with the event.
     */
    String getApplicationName();

    /**
     * The release label for the application version associated with this
     * event.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 100<br/>
     *
     * @return The release label for the application version associated with this
     *         event.
     */
    String getVersionLabel();

    /**
     * The name of the configuration associated with this event.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 100<br/>
     *
     * @return The name of the configuration associated with this event.
     */
    String getTemplateName();

    /**
     * The name of the environment associated with this event.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>4 - 23<br/>
     *
     * @return The name of the environment associated with this event.
     */
    String getEnvironmentName();

    /**
     * The severity level of this event.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Allowed Values: </b>TRACE, DEBUG, INFO, WARN, ERROR, FATAL
     *
     * @return The severity level of this event.
     * @see EventsSeverity
     */
    EventsSeverity getSeverity();
}
