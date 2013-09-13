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
package org.exoplatform.ide.client.outline;

import org.exoplatform.ide.client.framework.outline.OutlineItemCreator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorFactory Feb 22, 2011 11:06:05 AM evgen $
 */
public class OutlineItemCreatorFactory {
    private static Map<String, OutlineItemCreator> outlineItemCreators = new HashMap<String, OutlineItemCreator>();

    static {
        // addOutlineItemCreator(MimeType.UWA_WIDGET, new HtmlOutlineItemCreator());
    }

    public static void addOutlineItemCreator(String mimeType, OutlineItemCreator outlineItemCreator) {
        outlineItemCreators.put(mimeType, outlineItemCreator);
    }

    public static OutlineItemCreator getOutlineItemCreator(String mimeType) {
        if (outlineItemCreators.containsKey(mimeType)) {
            return outlineItemCreators.get(mimeType);
        }

        return null;
    }

}
