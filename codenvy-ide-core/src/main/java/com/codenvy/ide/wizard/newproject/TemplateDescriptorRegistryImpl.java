/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.template.TemplateDescriptorRegistry;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;

/**
 * Implementation of {@link TemplateDescriptorRegistry}.
 *
 * @author Artem Zatsarynnyy
 */
public class TemplateDescriptorRegistryImpl implements TemplateDescriptorRegistry {
    private final StringMap<ProjectTemplateDescriptor> templateDescriptors;

    protected TemplateDescriptorRegistryImpl() {
        this.templateDescriptors = Collections.createStringMap();
    }

    @Override
    public ProjectTemplateDescriptor getDescriptor(String id) {
        return templateDescriptors.get(id);
    }

    @Override
    public Array<ProjectTemplateDescriptor> getDescriptors() {
        return templateDescriptors.getValues();
    }

    @Override
    public Array<ProjectTemplateDescriptor> getDescriptors(ProjectTypeDescriptor projectTypeDescriptor) {
        Array<ProjectTemplateDescriptor> descriptors = Collections.createArray();
        for (ProjectTemplateDescriptor descriptor : templateDescriptors.getValues().asIterable()) {
            if (descriptor.getProjectTypeId().equals(projectTypeDescriptor.getProjectTypeId())) {
                descriptors.add(descriptor);
            }
        }
        return descriptors;
    }

    @Override
    public void registerTemplate(ProjectTemplateDescriptor templateDescriptor) {
        templateDescriptors.put(templateDescriptor.getTemplateId(), templateDescriptor);
    }

    @Override
    public void registerTemplates(Array<ProjectTemplateDescriptor> templateDescriptors) {
        for (ProjectTemplateDescriptor descriptor : templateDescriptors.asIterable()) {
            this.templateDescriptors.put(descriptor.getTemplateId(), descriptor);
        }
    }
}
