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
package org.exoplatform.ide.client.framework.discovery;

/**
 * Interface describe REST-service.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: IRestService.java Mar 26, 2012 10:55:52 AM azatsarynnyy $
 */
public interface IRestService {

    /**
     * Returns full qualified name of REST-service.
     *
     * @return full qualified name
     */
    public String getFqn();

    /**
     * Set full qualified name of REST-service.
     *
     * @param fqn
     *         full qualified name
     */
    public void setFqn(String fqn);

    /**
     * Returns REST-service path.
     *
     * @return the REST-service path
     */
    public String getPath();

    /**
     * Set REST-service path.
     *
     * @param path
     *         the REST-service path to set
     */
    public void setPath(String path);

    /**
     * Returns regex of REST-service.
     *
     * @return the REST-service regex
     */
    public String getRegex();

    /**
     * Set regex of REST-service.
     *
     * @param the
     *         REST-service regex
     */
    public void setRegex(String regex);

}