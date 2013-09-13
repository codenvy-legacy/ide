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
package org.exoplatform.ide.extension.appfog.server.json;


/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class Stats {
    private String    state;
    private StatsInfo stats;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public StatsInfo getStats() {
        return stats;
    }

    public void setStats(StatsInfo stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "Stats [state=" + state + ", stats=" + stats + "]";
    }
}
