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
package org.exoplatform.ide.extension.appfog.client.control;

import org.exoplatform.ide.extension.appfog.client.AppfogClientBundle;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.info.ApplicationInfoEvent;

/**
 * Control to get application information.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationInfoControl extends AbstractAppfogControl {

    private static final String ID = AppfogExtension.LOCALIZATION_CONSTANT.applicationInfoControlId();

    private static final String TITLE = AppfogExtension.LOCALIZATION_CONSTANT.applicationInfoControlTitle();

    private static final String PROMPT = AppfogExtension.LOCALIZATION_CONSTANT.applicationInfoControlPrompt();

    public ApplicationInfoControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(AppfogClientBundle.INSTANCE.applicationInfo(), AppfogClientBundle.INSTANCE.applicationInfoDisabled());
        setEvent(new ApplicationInfoEvent());
    }

}
