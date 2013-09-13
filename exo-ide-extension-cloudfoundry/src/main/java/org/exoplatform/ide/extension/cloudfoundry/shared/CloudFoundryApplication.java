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
package org.exoplatform.ide.extension.cloudfoundry.shared;

import java.util.List;

/**
 * Cloud Foundry application info.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInfo.java Mar 16, 2012 12:21:15 AM azatsarynnyy $
 */
public interface CloudFoundryApplication {
    String getName();

    void setName(String name);

    List<String> getUris();

    void setUris(List<String> uris);

    int getInstances();

    void setInstances(int instances);

    int getRunningInstances();

    void setRunningInstances(int runningInstances);

    String getState();

    void setState(String state);

    List<String> getServices();

    void setServices(List<String> services);

    String getVersion();

    void setVersion(String version);

    List<String> getEnv();

    void setEnv(List<String> env);

    CloudFoundryApplicationResources getResources();

    void setResources(CloudFoundryApplicationResources resources);

    Staging getStaging();

    void setStaging(Staging staging);

    // Switch debug mode.
    String getDebug();

    void setDebug(String debug);
    // ------------------

    ApplicationMetaInfo getMeta();

    void setMeta(ApplicationMetaInfo mi);
}