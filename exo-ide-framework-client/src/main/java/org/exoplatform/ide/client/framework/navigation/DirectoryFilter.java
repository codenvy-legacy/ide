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

package org.exoplatform.ide.client.framework.navigation;

import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DirectoryFilter {

    private static DirectoryFilter instance;

    public static DirectoryFilter get() {
        if (instance == null) {
            instance = new DirectoryFilter();
        }

        return instance;
    }

    private String[] parts = new String[0];

    public void setPattern(String pattern) {
        parts = pattern.split(";");
    }

    public List<Item> filter(List<Item> items) {
        List<Item> result = new ArrayList<Item>();

        for (Item item : items) {
            if (!matchWithPattern(item.getName())) {
                result.add(item);
            }
//         if (parts == null || parts.length == 0 || !matchWithPattern(item.getName()))
//         {
//            result.add(item);
//         }
        }

        return result;
    }

    public boolean matchWithPattern(String text) {
        if (parts == null) {
            return false;
        }

        if (parts.length == 0) {
            return false;
        }

        try {
            for (String p : parts) {
                p = p.trim();

                if (p == null || "".equals(p)) {
                    continue;
                }

            /*
             * Matches for *characters
             */
                if (p.startsWith("*")) {
                    String work = p.substring(1);
                    if (text.toLowerCase().endsWith(work.toLowerCase())) {
                        return true;
                    }
                    continue;
                }

            /*
             * Matches for characters*
             */
                if (p.endsWith("*")) {
                    String work = p.substring(0, p.length() - 1);
                    if (text.toLowerCase().startsWith(work.toLowerCase())) {
                        return true;
                    }
                    continue;
                }

            /*
             * Matches for whole name
             */
                if (text.equalsIgnoreCase(p)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
