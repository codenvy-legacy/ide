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
package org.exoplatform.ide.client.framework.template;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 27, 2012 2:35:55 PM anya $
 */
public interface TemplateAutoBeanFactory extends AutoBeanFactory {
    TemplateAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(TemplateAutoBeanFactory.class);

    /**
     * A factory method for folder template.
     *
     * @return an {@link AutoBean} of type {@link FolderTemplate}
     */
    AutoBean<FolderTemplate> folderTemplate();

    /**
     * A factory method for folder template.
     *
     * @return an {@link AutoBean} of type {@link FolderTemplate}
     */
    AutoBean<Template> template();

    /**
     * A factory method for folder template.
     *
     * @return an {@link AutoBean} of type {@link FileTemplate}
     */
    AutoBean<FileTemplate> fileTemplate();

    /**
     * A factory method for project template.
     *
     * @return an {@link AutoBean} of type {@link ProjectTemplate}
     */
    AutoBean<ProjectTemplate> projectTemplate();
}
