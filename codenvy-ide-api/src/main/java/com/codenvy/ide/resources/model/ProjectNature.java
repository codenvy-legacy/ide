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
package com.codenvy.ide.resources.model;

import com.codenvy.ide.collections.JsonStringSet;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * Project Nature concept is a composition of controller and a tag. When Nature tag is
 * added to the Project, then {@link ProjectNature#configure()} is triggered.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface ProjectNature {
    public static final String PRIMARY_NATURE_CATEGORY = "natures.primary";
    public static final String LANG_NATURE_CATEGORY    = "natures.lang";
    public static final String PAAS_NATURE_CATEGORY    = "natures.paas";

    /**
     * Returns the unique identifier of this nature.
     *
     * @return the unique nature identifier
     */
    String getNatureId();

    /**
     * Returns a displayable label for this nature.
     * Returns the empty string if no label for this nature
     * is specified in the plug-in manifest file.
     *
     * @return a displayable string label for this nature,
     *         possibly the empty string
     */
    String getLabel();

    /**
     * Returns the identifiers of the natures required by this nature.
     *
     * @return an array of nature ids that this nature requires,
     *         possibly an empty array.
     */
    JsonStringSet getRequiredNatureIds();

    /**
     * Returns the identifiers of the Nature Categories that this nature exclusively belongs to.
     * No any other Nature of this category can exist on the project
     *
     * @return a set of nature categories that this nature belongs to,
     *         possibly an empty array.
     */
    JsonStringSet getNatureCategories();

    /**
     * Configures this nature for its project. This is called by the workspace
     * when natures are added to the project using <code>Project.setDescription</code>
     * and should not be called directly by clients.  The nature extension
     * id is added to the list of natures before this method is called,
     * and need not be added here.
     * <p/>
     * Exceptions thrown by this method will be propagated back to the caller
     * of <code>Project.setDescription</code>, but the nature will remain in
     * the project description.
     *
     * @throws Exception
     *         if this method fails.
     */
    void configure(Project project, AsyncCallback<Project> callback);

    /**
     * De-configures this nature for its project.  This is called by the workspace
     * when natures are removed from the project using
     * <code>Project.setDescription</code> and should not be called directly by
     * clients.  The nature extension id is removed from the list of natures before
     * this method is called, and need not be removed here.
     *
     * @throws Exception
     *         if this method fails.
     */
    void deconfigure(Project project, AsyncCallback<Project> callback);

}
