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
package org.exoplatform.ide.extension.googleappengine.client.model;

import java.util.Set;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 4:45:33 PM anya $
 */
public interface Backend {
    public String getInstanceClass();

    public Integer getInstances();

    public Integer getMaxConcurrentRequests();

    public String getName();

    public Set<Option> getOptions();

    public State getState();

    public Boolean isDynamic();

    public Boolean isFailFast();

    public Boolean isPublic();

    public void setInstanceClass(String instanceClass);

    public void setInstances(Integer number);

    public void setMaxConcurrentRequests(Integer number);

    public void setName(String name);

    public void setOptions(Set<Option> options);

    public void setState(State state);

    public void setDynamic(Boolean isDynamic);

    public void setFailFast(Boolean isFailFast);

    public void setPublic(Boolean isPublic);
}
