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
package org.exoplatform.ide.client.framework.util;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.client.framework.project.Language;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.ui.IconImageBundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 24, 2011 evgen $
 */
public class ProjectResolver {

    private static Map<String, ImageResource> types = new HashMap<String, ImageResource>();

    private static Map<Language, List<ProjectType>> projectTypes = new HashMap<Language, List<ProjectType>>();

    private static Map<ProjectType, ImageResource> projectImages = new HashMap<ProjectType, ImageResource>();

    private static Map<ProjectType, ImageResource> projectLargeImages = new HashMap<ProjectType, ImageResource>();

    private static Map<Language, ImageResource> languageImages = new HashMap<Language, ImageResource>();

    /** List contains sorted project types. */
    private static List<ProjectType> sortedTypes = new ArrayList<ProjectType>();

    /** Ruby on Rails */
    @Deprecated
    public static final String RAILS = "Rails";

    /** Java Spring Framework */
    @Deprecated
    public static final String SPRING = "Spring";

    /** eXo Development Framework : Chromattic, Groovy REST */
    @Deprecated
    public static final String EXO_APP = "eXo";

    /** Static Web Project: HTML, JS, CSS */
    @Deprecated
    public static final String STATIC_WEB = "Javascript";

    /** Servlet and JSP API based project */
    @Deprecated
    public static final String SERVLET_JSP = "Java Web";

    /** PHP Project */
    @Deprecated
    public static final String PHP = "PHP";

    /** Empty Project */
    @Deprecated
    public static final String UNDEFINED = "Undefined";

    @Deprecated
    public static final String APP_ENGINE_JAVA = "App Engine Java";

    @Deprecated
    public static final String APP_ENGINE_PYTHON = "App Engine Python";

    @Deprecated
    public static final String AWS = "Amazon Web Services";

    public static List<String> deprecatedTypes = Arrays.asList(APP_ENGINE_JAVA, APP_ENGINE_PYTHON, EXO_APP, PHP, RAILS,
                                                               SERVLET_JSP, SPRING, STATIC_WEB);

    static {
        if (IconImageBundle.INSTANCE != null) {
            types.put(RAILS, IconImageBundle.INSTANCE.rubyProject());
            types.put(SPRING, IconImageBundle.INSTANCE.springProject());
            types.put(STATIC_WEB, IconImageBundle.INSTANCE.jsProject());
            types.put(SERVLET_JSP, IconImageBundle.INSTANCE.javaProject());
            types.put(PHP, IconImageBundle.INSTANCE.phpProject());
            types.put(UNDEFINED, IconImageBundle.INSTANCE.defaultProject());
            types.put(APP_ENGINE_JAVA, IconImageBundle.INSTANCE.gaeJavaProject());
            types.put(APP_ENGINE_PYTHON, IconImageBundle.INSTANCE.gaePythonProject());

            projectImages.put(ProjectType.JAVA, IconImageBundle.INSTANCE.javaProject());
            projectImages.put(ProjectType.SPRING, IconImageBundle.INSTANCE.springProject());
            projectImages.put(ProjectType.JSP, IconImageBundle.INSTANCE.jspProject());
            projectImages.put(ProjectType.JAVASCRIPT, IconImageBundle.INSTANCE.jsProject());
            projectImages.put(ProjectType.PHP, IconImageBundle.INSTANCE.phpProject());
            projectImages.put(ProjectType.RUBY, IconImageBundle.INSTANCE.rubyProject());
            projectImages.put(ProjectType.RUBY_ON_RAILS, IconImageBundle.INSTANCE.rubyProject());
            projectImages.put(ProjectType.PYTHON, IconImageBundle.INSTANCE.pythonProject());
            projectImages.put(ProjectType.NODE_JS, IconImageBundle.INSTANCE.nodejsProject());
            projectImages.put(ProjectType.DJANGO, IconImageBundle.INSTANCE.djangoProject());

            projectLargeImages.put(ProjectType.JAR, IconImageBundle.INSTANCE.jarProject48());
            projectLargeImages.put(ProjectType.WAR, IconImageBundle.INSTANCE.jarProject48());
            projectLargeImages.put(ProjectType.JSP, IconImageBundle.INSTANCE.jspProject48());
            projectLargeImages.put(ProjectType.SPRING, IconImageBundle.INSTANCE.springProject48());
            projectLargeImages.put(ProjectType.JAVASCRIPT, IconImageBundle.INSTANCE.jsProject48());
            projectLargeImages.put(ProjectType.RUBY_ON_RAILS, IconImageBundle.INSTANCE.rubyProject48());
            projectLargeImages.put(ProjectType.PYTHON, IconImageBundle.INSTANCE.pythonProject48());
            projectLargeImages.put(ProjectType.PHP, IconImageBundle.INSTANCE.phpProject48());
            projectLargeImages.put(ProjectType.NODE_JS, IconImageBundle.INSTANCE.nodejsProject48());
            projectLargeImages.put(ProjectType.MultiModule, IconImageBundle.INSTANCE.multiModule48());
            projectLargeImages.put(ProjectType.ANDROID, IconImageBundle.INSTANCE.androidProject48());
        }

        projectTypes.put(Language.JAVA, Arrays.asList(ProjectType.JAVA, ProjectType.JSP, ProjectType.SPRING,//
                                                     ProjectType.JAR, ProjectType.WAR));
        projectTypes.put(Language.JAVASCRIPT, Arrays.asList(ProjectType.JAVASCRIPT));
        projectTypes.put(Language.PHP, Arrays.asList(ProjectType.PHP));
        projectTypes.put(Language.NODE_JS, Arrays.asList(ProjectType.NODE_JS));
        projectTypes.put(Language.PYTHON, Arrays.asList(ProjectType.PYTHON, ProjectType.DJANGO));
        projectTypes.put(Language.RUBY, Arrays.asList(ProjectType.RUBY, ProjectType.RUBY_ON_RAILS));

        languageImages.put(Language.JAVA, IconImageBundle.INSTANCE.javaType());
        languageImages.put(Language.PHP, IconImageBundle.INSTANCE.phpType());
        languageImages.put(Language.RUBY, IconImageBundle.INSTANCE.rubyType());
        languageImages.put(Language.PYTHON, IconImageBundle.INSTANCE.pythonType());
        languageImages.put(Language.GROOVY, IconImageBundle.INSTANCE.groovyType());
        languageImages.put(Language.JAVASCRIPT, IconImageBundle.INSTANCE.jsType());

        sortedTypes.add(ProjectType.JAR);
        sortedTypes.add(ProjectType.WAR);
        sortedTypes.add(ProjectType.JSP);
        sortedTypes.add(ProjectType.SPRING);
        sortedTypes.add(ProjectType.JAVASCRIPT);
        sortedTypes.add(ProjectType.RUBY_ON_RAILS);
        sortedTypes.add(ProjectType.PYTHON);
        sortedTypes.add(ProjectType.PHP);
    }

    public static Set<String> getProjectsTypes() {
        return types.keySet();
    }

    /**
     * Returns index of project type.
     *
     * @param type
     *         {@link ProjectType}
     * @return index of project type
     */
    public static int getIndexOfProjectType(ProjectType type) {
        if (sortedTypes.contains(type)) {
            return sortedTypes.indexOf(type);
        } else {
            return 999;
        }
    }

    /**
     * @param type
     * @return
     * @use {@link #getImageForProject(ProjectType)}
     */
    @Deprecated
    public static ImageResource getImageForProject(String type) {
        if (types.containsKey(type)) {
            return types.get(type);
        } else
            return types.get(UNDEFINED);
    }

    public static ImageResource getImageForProject(ProjectType type) {
        if (projectImages.containsKey(type)) {
            return projectImages.get(type);
        } else
            return types.get(UNDEFINED);
    }

    public static ImageResource getLargeImageForProject(ProjectType type) {
        if (projectLargeImages.containsKey(type)) {
            return projectLargeImages.get(type);
        } else
            return types.get(UNDEFINED);
    }

    public static ImageResource getImageForLanguage(Language language) {
        if (languageImages.containsKey(language)) {
            return languageImages.get(language);
        } else
            return null;
    }

    public static List<ProjectType> getProjectTypesByLanguage(Language language) {
        return projectTypes.get(language);
    }

    /**
     * TODO temporary method.
     * <p/>
     * Is used to support projects with deprecated types.
     */
    public static ArrayList<String> resolveProjectTarget(String projectType) {
        ArrayList<String> targets = new ArrayList<String>();
        if (APP_ENGINE_JAVA.equals(projectType) || APP_ENGINE_PYTHON.equals(projectType)) {
            targets.add("GAE");
        }

        if (PHP.equals(projectType)) {
            targets.add("OpenShift");
        }

        if (RAILS.equals(projectType) || SPRING.equals(projectType) || SERVLET_JSP.equals(projectType)) {
            targets.add("CloudFoundry");
        }

        if (SERVLET_JSP.equals(projectType)) {
            targets.add("CloudBees");
        }

        if (RAILS.equals(projectType)) {
            targets.add("Heroku");
        }

        return targets;
    }

}
