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
package com.codenvy.ide.ext.git.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class GitHelper {
    public static void addToGitIgnore(File dir, String... rules) throws IOException {
        if (rules == null || rules.length == 0) {
            return;
        }

        Set<String> toAdd = new LinkedHashSet<String>(Arrays.asList(rules));

        File f = new File(dir, ".gitignore");
        FileWriter w = null;
        try {
            if (f.exists() && f.length() > 0) {
                BufferedReader r = null;
                try {
                    r = new BufferedReader(new FileReader(f));
                    for (String l = r.readLine(); l != null; l = r.readLine()) {
                        toAdd.remove(l.trim());
                    }
                } finally {
                    if (r != null) {
                        r.close();
                    }
                }

                w = new FileWriter(f, true);
                w.write('\n');
            } else {
                w = new FileWriter(f);
            }

            for (String l : toAdd) {
                w.write(l);
                w.write('\n');
            }
        } finally {
            if (w != null) {
                w.flush();
                w.close();
            }
        }
    }
}
