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
package org.exoplatform.ide.client.framework.preference;

import org.exoplatform.ide.client.framework.ui.api.View;

/**
 * Has method for recieving preference's view. {@link PreferencePerformer} is implemented by view's presenter. The
 * {@link PreferencePerformer#getPreference()} must contain view creation, binding with presenter and view's filling.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 19, 2012 9:58:29 AM anya $
 */
public interface PreferencePerformer {
    /** @return {@link View} preference's view */
    View getPreference();
}
