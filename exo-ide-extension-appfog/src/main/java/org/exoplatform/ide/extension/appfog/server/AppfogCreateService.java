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

import org.exoplatform.ide.extension.appfog.server.json.CreateService;
import org.exoplatform.ide.extension.appfog.shared.Infra;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogCreateService extends CreateService {
    private Infra infra;

    public AppfogCreateService(String name, String type, String tier, String vendor, String version, Infra infra) {
        super(name, type, tier, vendor, version);
        this.infra = infra;
    }

    public AppfogCreateService(String name, String type, String vendor, String version, Infra infra) {
        super(name, type, vendor, version);
        this.infra = infra;
    }

    public Infra getInfra() {
        return infra;
    }

    public void setInfra(Infra infra) {
        this.infra = infra;
    }

    @Override
    public String toString() {
        return "AppfogCreateService{" +
               "infra=" + infra +
               '}';
    }
}
