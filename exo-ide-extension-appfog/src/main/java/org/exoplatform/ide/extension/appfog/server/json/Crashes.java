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

import java.util.Arrays;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class Crashes {
    private Crash[] crashes;

    public Crash[] getCrashes() {
        return crashes;
    }

    public void setCrashes(Crash[] crashes) {
        this.crashes = crashes;
    }

    @Override
    public String toString() {
        return "Crashes{" +
               "crashes=" + (crashes == null ? null : Arrays.asList(crashes)) +
               '}';
    }

    public static class Crash {
        private String instance;
        private long   since;

        public String getInstance() {
            return instance;
        }

        public void setInstance(String instance) {
            this.instance = instance;
        }

        public long getSince() {
            return since;
        }

        public void setSince(long since) {
            this.since = since;
        }

        @Override
        public String toString() {
            return "Crash{" +
                   "instance='" + instance + '\'' +
                   ", since=" + since +
                   '}';
        }
    }
}
