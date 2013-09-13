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
package org.exoplatform.ide.shell.server;

import com.codenvy.commons.env.EnvironmentContext;

import org.everrest.core.ComponentLifecycleScope;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.everrest.core.impl.resource.AbstractResourceDescriptorImpl;
import org.everrest.core.method.MethodParameter;
import org.everrest.core.resource.AbstractResourceDescriptor;
import org.everrest.core.resource.ResourceMethodDescriptor;
import org.everrest.core.resource.ResourceMethodMap;
import org.everrest.core.resource.SubResourceLocatorDescriptor;
import org.everrest.core.resource.SubResourceLocatorMap;
import org.everrest.core.resource.SubResourceMethodDescriptor;
import org.everrest.core.resource.SubResourceMethodMap;
import org.exoplatform.ide.shell.shared.CLIResource;
import org.exoplatform.ide.shell.shared.CLIResourceParameter;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class CLIResourceFactory {
    private static class CLIResourceConfig {
        private final AtomicLong                                             lastModified = new AtomicLong();

        private final AtomicReference<Map<String, Map<String, CLIResource>>> cliResources =
                                                                                            new AtomicReference<Map<String, Map<String, CLIResource>>>();

        private final File                                                   file;

        CLIResourceConfig(File file) throws IOException {
            this.file = file;
            load();
        }

        CLIResource getCLIResource(String path, String method) {
            Map<String, Map<String, CLIResource>> m = cliResources.get();
            if (m != null) {
                path = normalizePath(path);
                Map<String, CLIResource> sub = m.get(path);
                if (sub != null) {
                    CLIResource cli = sub.get(method);
                    if (cli != null) {
                        CLIResource copy = CLIResource.newInstance(cli);
                        copy.setPath(copy.getPath().replace("{ws-name}", EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString()));
                        return copy;
                    }
                }
            }
            return null;
        }

        void load() throws IOException {
            FileReader reader = null;
            Map<String, Map<String, CLIResource>> newCliResources = new HashMap<String, Map<String, CLIResource>>();
            try {
                reader = new FileReader(file);
                JsonParser jsonParser = new JsonParser();
                jsonParser.parse(reader);
                CLIResource[] cliMapping =
                                           (CLIResource[])ObjectBuilder.createArray(CLIResource[].class, jsonParser.getJsonObject());

                if (cliMapping != null && cliMapping.length > 0) {
                    for (int i = 0; i < cliMapping.length; i++) {
                        cliMapping[i].setPath(normalizePath(cliMapping[i].getPath()));
                        Map<String, CLIResource> sub = newCliResources.get(cliMapping[i].getPath());
                        if (sub == null) {
                            sub = new HashMap<String, CLIResource>();
                            newCliResources.put(cliMapping[i].getPath(), sub);
                        }
                        sub.put(cliMapping[i].getMethod(), cliMapping[i]);
                    }
                }

                cliResources.set(newCliResources);
                lastModified.set(file.lastModified());
            } catch (JsonException e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }

        boolean isOutOfDate() {
            return file.lastModified() != lastModified.get();
        }

        private String normalizePath(String path) {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            return path;
        }
    }

    private final CLIResourceConfig cliResourceConfig;

    public CLIResourceFactory() {
        this(null);
    }

    protected CLIResourceFactory(File file) {
        if (file == null) {
            final String fileName = System.getProperty("org.exoplatform.ide.rest2cli.config", "conf/rest2cli.json");
            URL fileURL = Thread.currentThread().getContextClassLoader().getResource(fileName);
            if (fileURL == null) {
                throw new RuntimeException("Configuration file '" + fileName + "' not found. ");
            }
            file = new File(fileURL.getPath());
        }
        try {
            cliResourceConfig = new CLIResourceConfig(file);
        } catch (IOException nfe) {
            throw new RuntimeException(nfe.getMessage(), nfe);
        }
    }

    public Set<CLIResource> getCLIResources(AbstractResourceDescriptor resource) throws IOException {
        String rootPath = resource.getPathValue().getPath();
        Set<CLIResource> result = new HashSet<CLIResource>();
        processResource(rootPath, resource, result);
        return result;
    }

    private void processResource(String rootPath, AbstractResourceDescriptor resource, Collection<CLIResource> toAdd)
                                                                                                                     throws IOException {
        processResourceMethods(rootPath, resource.getResourceMethods(), toAdd);
        processSubResourceMethods(rootPath, resource.getSubResourceMethods(), toAdd);
        processSubResourceLocators(rootPath, resource.getSubResourceLocators(), toAdd);
    }

    private void processResourceMethods(String rootPath, ResourceMethodMap<ResourceMethodDescriptor> resourceMethods,
                                        Collection<CLIResource> toAdd) throws IOException {
        for (List<ResourceMethodDescriptor> l : resourceMethods.values()) {
            for (ResourceMethodDescriptor resourceMethod : l) {
                CLIResource templ = getCLIResource(rootPath, resourceMethod.getHttpMethod());
                if (templ != null) {
                    toAdd.add(fillFromResourceMethod(templ, resourceMethod));
                }
            }
        }
    }

    private CLIResource getCLIResource(String path, String method) throws IOException {
        if (cliResourceConfig.isOutOfDate()) {
            cliResourceConfig.load();
        }
        return cliResourceConfig.getCLIResource(path, method);
    }

    private void processSubResourceMethods(String rootPath, SubResourceMethodMap subResourceMethods,
                                           Collection<CLIResource> toAdd) throws IOException {
        for (ResourceMethodMap<SubResourceMethodDescriptor> resourceMethods : subResourceMethods.values()) {
            for (List<SubResourceMethodDescriptor> l : resourceMethods.values()) {
                for (SubResourceMethodDescriptor subResourceMethod : l) {
                    String methodPath = subResourceMethod.getPathValue().getPath();
                    String subResourcePath = rootPath + ((methodPath.startsWith("/") && rootPath.endsWith("/")) //
                        ? methodPath.substring(0) //
                        : (methodPath.startsWith("/") || rootPath.endsWith("/")) //
                            ? methodPath //
                            : "/" + methodPath);
                    CLIResource tmpl = getCLIResource(subResourcePath, subResourceMethod.getHttpMethod());
                    if (tmpl != null) {
                        toAdd.add(fillFromResourceMethod(tmpl, subResourceMethod));
                    }
                }
            }
        }
    }

    private void processSubResourceLocators(String rootPath, SubResourceLocatorMap subResourceLocators,
                                            Collection<CLIResource> toAdd) throws IOException {
        for (SubResourceLocatorDescriptor subResourceLocator : subResourceLocators.values()) {
            String methodPath = subResourceLocator.getPathValue().getPath();
            String subResourcePath = rootPath + ((methodPath.startsWith("/") && rootPath.endsWith("/")) //
                ? methodPath.substring(0) //
                : (methodPath.startsWith("/") || rootPath.endsWith("/")) //
                    ? methodPath //
                    : "/" + methodPath);
            AbstractResourceDescriptor subResourceDescriptor =
                                                               new AbstractResourceDescriptorImpl(subResourceLocator.getMethod()
                                                                                                                    .getReturnType(),
                                                                                                  ComponentLifecycleScope.SINGLETON);
            processResource(subResourcePath, subResourceDescriptor, toAdd);
        }
    }

    @SuppressWarnings("rawtypes")
    private CLIResource fillFromResourceMethod(CLIResource cliResource, ResourceMethodDescriptor resourceMethod) {
        List<MediaType> consumes = resourceMethod.consumes();
        cliResource.setConsumes(new HashSet<String>(consumes.size()));
        for (MediaType m : consumes)
            cliResource.getConsumes().add(m.toString());

        List<MediaType> produces = resourceMethod.produces();
        cliResource.setProduces(new HashSet<String>(produces.size()));
        for (MediaType m : produces)
            cliResource.getProduces().add(m.toString());

        Set<CLIResourceParameter> cliParams = cliResource.getParams();
        if (cliParams == null) {
            cliParams = new HashSet<CLIResourceParameter>();
            cliResource.setParams(cliParams);
        }

        List<MethodParameter> methodParameters = resourceMethod.getMethodParameters();
        for (MethodParameter mp : methodParameters) {
            Annotation annotation = mp.getAnnotation();
            if (annotation != null) {
                String restName = null;
                CLIResourceParameter.Type restType = null;

                Class annotationClass = annotation.annotationType();
                if (annotationClass == PathParam.class) {
                    restName = ((PathParam)annotation).value();
                    restType = CLIResourceParameter.Type.PATH;
                } else if (annotationClass == MatrixParam.class) {
                    restName = ((MatrixParam)annotation).value();
                    restType = CLIResourceParameter.Type.MATRIX;
                } else if (annotationClass == QueryParam.class) {
                    restName = ((QueryParam)annotation).value();
                    restType = CLIResourceParameter.Type.QUERY;
                } else if (annotationClass == HeaderParam.class) {
                    restName = ((HeaderParam)annotation).value();
                    restType = CLIResourceParameter.Type.HEADER;
                } else if (annotationClass == FormParam.class) {
                    restName = ((FormParam)annotation).value();
                    restType = CLIResourceParameter.Type.FORM;
                } else if (annotationClass == CookieParam.class) {
                    restName = ((CookieParam)annotation).value();
                    restType = CLIResourceParameter.Type.COOKIE;
                }

                if (restName == null || restType == null) {
                    // Should never happen.
                    continue;
                }

                // Check is description for parameter 'overridden' in configuration.
                // If so then do not touch such parameter.
                if (!isParameterOverridden(restName, restType, cliParams)) {
                    cliParams.add(new CLIResourceParameter(restName, Collections.singleton(("-" + restName)), restType,
                                                           false, true));
                }
            }
        }
        return cliResource;
    }

    private boolean isParameterOverridden(String restName, CLIResourceParameter.Type restType,
                                          Set<CLIResourceParameter> cliParams) {
        if (cliParams != null && cliParams.size() > 0) {
            final String envVarName = "$" + restName;
            for (CLIResourceParameter cliParam : cliParams) {
                if ((envVarName.equals(cliParam.getName()) || restName.equals(cliParam.getName()))
                    && restType == cliParam.getType())
                    return true;
            }
        }
        return false;
    }
}
