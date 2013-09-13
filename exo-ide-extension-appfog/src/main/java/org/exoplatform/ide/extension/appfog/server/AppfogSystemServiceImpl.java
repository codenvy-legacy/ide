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
package org.exoplatform.ide.extension.appfog.server;

import org.exoplatform.ide.extension.appfog.shared.AppfogSystemService;
import org.exoplatform.ide.extension.appfog.shared.Infra;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogSystemServiceImpl implements AppfogSystemService {
    private String vendor;
    private String type;
    private String version;
    private String description;
    private Infra  infra;

    public AppfogSystemServiceImpl() {
    }

    public AppfogSystemServiceImpl(String vendor, String type, String version, String description, Infra infra) {
        this.vendor = vendor;
        this.type = type;
        this.version = version;
        this.description = description;
        this.infra = infra;
    }

    @Override
    public String getVendor() {
        return vendor;
    }

    @Override
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Infra getInfra() {
        return infra;
    }

    @Override
    public void setInfra(Infra infra) {
        this.infra = infra;
    }

    @Override
    public String toString() {
        return "AppfogSystemServiceImpl{" +
               "vendor='" + vendor + '\'' +
               ", type='" + type + '\'' +
               ", version='" + version + '\'' +
               ", description='" + description + '\'' +
               ", infra=" + infra +
               '}';
    }
}
