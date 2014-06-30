/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.worker;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.ext.java.jdt.internal.codeassist.ISearchRequestor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.NameEnvironmentAnswer;
import com.codenvy.ide.ext.java.worker.env.BinaryType;
import com.codenvy.ide.ext.java.worker.env.Util;
import com.codenvy.ide.ext.java.worker.env.json.BinaryTypeJso;
import com.google.gwt.core.client.JavaScriptObject;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment} interface, use RestNameEnvironment
 * for receiving data and
 * cache Java type data in browser
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 13, 2012 3:10:43 PM evgen $
 */
public class WorkerNameEnvironment implements INameEnvironment {

    private static Set<String> packages = new HashSet<>();
    protected String restServiceContext;
    private   String projectPath;
    private Set<String> blackListTypes = new HashSet<>();
    private Set<String> blackListPackages = new HashSet<>();

    /**
     *
     */
    public WorkerNameEnvironment(String context, String restContext, String wsName) {
        restServiceContext = context + "/java-name-environment" + wsName;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    /** {@inheritDoc} */
    @Override
    public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
        StringBuilder b = new StringBuilder();
        for (char[] c : compoundTypeName) {
            b.append(c).append('.');
        }
        b.deleteCharAt(b.length() - 1);

        final String key = validateFqn(b);
        if(blackListTypes.contains(key)){
            return null;
        }
        if (WorkerTypeInfoStorage.get().containsKey(key)) {
            return new NameEnvironmentAnswer(WorkerTypeInfoStorage.get().getType(key), null);
        }

        if (projectPath != null) {
            if (packages.contains(key)) {
                return null;
            }
            StringBuilder builder = new StringBuilder();
            for (char[] chars : compoundTypeName) {
                builder.append(chars).append(',');
            }
            if (builder.length() > 1) builder.deleteCharAt(builder.length() - 1);
            String url =
                    restServiceContext + "/findTypeCompound?compoundTypeName=" + builder.toString() + "&projectpath=" +
                    projectPath;
            String result = runSyncRequest(url);
            if (result != null) {
                Jso jso = Jso.deserialize(result);
                BinaryType type = new BinaryType(jso.<BinaryTypeJso>cast());
                WorkerTypeInfoStorage.get().putType(key, type);

                return new NameEnvironmentAnswer(type, null);
            } else {
                blackListTypes.add(key);
                return null;
            }
        }
        return null;
    }

    private String validateFqn(StringBuilder builder) {
        if (builder.indexOf("<") != -1) {
            builder.setLength(builder.indexOf("<"));
        }
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
        if ("package-info".equals(new String(typeName))) {
            return null;
        }

        StringBuilder b = new StringBuilder();
        for (char[] c : packageName) {
            b.append(c).append('.');
        }
        b.append(typeName);
        final String key = validateFqn(b);
        if(blackListTypes.contains(key)){
            return null;
        }
        if (WorkerTypeInfoStorage.get().containsKey(key)) {
            return new NameEnvironmentAnswer(WorkerTypeInfoStorage.get().getType(key),
                                             null);
        }
        if (projectPath != null) {
            if (packages.contains(key)) {
                return null;
            }
            StringBuilder builder = new StringBuilder();
            for (char[] chars : packageName) {
                builder.append(chars).append(',');
            }
            if (builder.length() > 1) builder.deleteCharAt(builder.length() - 1);

            String url =
                    restServiceContext + "/findType?packagename=" + builder.toString() + "&typename=" + new String(typeName) +
                    "&projectpath=" + projectPath;
            String result = runSyncRequest(url);
            if (result != null) {
                Jso jso = Jso.deserialize(result);
                BinaryType type = new BinaryType(jso.<BinaryTypeJso>cast());
                WorkerTypeInfoStorage.get().putType(key, type);

                return new NameEnvironmentAnswer(type, null);
            } else {
                blackListTypes.add(key);
                return null;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPackage(char[][] parentPackageName, char[] packageName) {
        StringBuilder p = new StringBuilder();
        try {
            if (parentPackageName != null) {
                for (char[] seg : parentPackageName) {
                    p.append(seg).append('.');
                }
            }
            p.append(packageName);
            if (packages.contains(p.toString())) {
                return true;
            }
            if (blackListPackages.contains(p.toString())) {
                return false;
            }
            StringBuilder builder = new StringBuilder();
            if (parentPackageName != null) {
                for (char[] chars : parentPackageName) {
                    builder.append(chars).append(',');
                }
                if (builder.length() > 1) builder.deleteCharAt(builder.length() - 1);
            }
            String url =
                    restServiceContext + "/package" + "?packagename=" + new String(packageName) + "&parent=" +
                    builder.toString()
                    + "&projectpath=" + projectPath;
            String findPackage = runSyncRequest(url);
            boolean exist = findPackage != null && Boolean.parseBoolean(findPackage);
            if (exist) {
                packages.add(p.toString());
            } else {
                blackListPackages.add(p.toString());
            }
            return exist;

        } catch (Exception e) {
            e.printStackTrace();
            //TODO log errors
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void cleanup() {
    }

    /**
     * Must be used only by CompletionEngine. The progress monitor is used to be able to cancel completion operations
     * <p/>
     * Find constructor declarations that are defined in the current environment and whose name starts with the given prefix. The
     * prefix is a qualified name separated by periods or a simple name (ex. java.util.V or V).
     * <p/>
     * The constructors found are passed to one of the following methods: ISearchRequestor.acceptConstructor(...)
     */
    @Override
    public void findConstructorDeclarations(char[] prefix, boolean camelCaseMatch, final ISearchRequestor requestor) {

        String url =
                restServiceContext + "/findConstructor" + "?prefix=" + new String(prefix) + "&camelcase=" + camelCaseMatch
                + "&projectpath=" + projectPath;
        String cons = runSyncRequest(url);
        if (cons != null) {
            JsoArray<Jso> constructors = Jso.deserialize(cons).cast();
            for (Jso jso : constructors.asIterable()) {
                char[][] parameterTypes = Util.arrayStringToCharArray((Array<String>)jso.getJsObjectField("parameterTypes"));
                char[][] parameterNames = Util.arrayStringToCharArray((Array<String>)jso.getJsObjectField("parameterNames"));
                String signature = jso.getStringField("signature");
                char[] sig = null;
                if(signature != null) {
                   sig = signature.toCharArray();
                }
                requestor.acceptConstructor(jso.getIntField("modifiers"), jso.getStringField("simpleTypeName").toCharArray(),
                                            jso.getIntField("parameterCount"), sig,
                                            parameterTypes, parameterNames, jso.getIntField("typeModifiers"),
                                            jso.getStringField("packageName").toCharArray(), jso.getIntField("extraFlags"), "from server",
                                            null);
            }
        }

    }

    /**
     * Find the packages that start with the given prefix. A valid prefix is a qualified name separated by periods (ex. java.util).
     * The packages found are passed to: ISearchRequestor.acceptPackage(char[][] packageName)
     */
    @Override
    public void findPackages(char[] qualifiedName, final ISearchRequestor requestor) {
        String url =
                restServiceContext + "/findPackages" + "?packagename=" + new String(qualifiedName)
                + "&projectpath=" + projectPath;
        String pak = runSyncRequest(url);
        if (pak != null) {
            JsoArray<String> packages = Jso.deserialize(pak).cast();
            packages.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            for (String s : packages.asIterable()) {
                requestor.acceptPackage(s.toCharArray());
                WorkerNameEnvironment.packages.add(s);
            }
        }
    }

    private String runSyncRequest(String url) {
        XmlHttpWraper xmlhttp = nativeRunSyncReques(url);
        int status = xmlhttp.getStatusCode();
        if (status == 200) {
            return xmlhttp.getResponseText();
        } else {
            // server not find info
            return null;
        }
    }

    private native XmlHttpWraper nativeRunSyncReques(String url)/*-{
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("GET", url, false);
        xmlhttp.send();
        return xmlhttp;
    }-*/;

    /**
     * Must be used only by CompletionEngine. The progress monitor is used to be able to cancel completion operations
     * <p/>
     * Find the top-level types that are defined in the current environment and whose name starts with the given prefix. The prefix
     * is a qualified name separated by periods or a simple name (ex. java.util.V or V).
     * <p/>
     * The types found are passed to one of the following methods (if additional information is known about the types):
     * ISearchRequestor.acceptType(char[][] packageName, char[] typeName) ISearchRequestor.acceptClass(char[][] packageName, char[]
     * typeName, int modifiers) ISearchRequestor.acceptInterface(char[][] packageName, char[] typeName, int modifiers)
     * <p/>
     * This method can not be used to find member types... member types are found relative to their enclosing type.
     */
    @Override
    public void findTypes(char[] qualifiedName, boolean findMembers, boolean camelCaseMatch, int searchFor,
                          final ISearchRequestor requestor) {
        if (qualifiedName.length == 0) {
            return;
        }
        String url =
                restServiceContext + "/findTypes" + "?qualifiedname=" + new String(qualifiedName) + "&camelcase=" + camelCaseMatch
                + "&findmembers=" + findMembers + "&searchfor=" + searchFor
                + "&projectpath=" + projectPath;
        String res = runSyncRequest(url);
        if (res != null) {
            JsoArray<Jso> types = Jso.deserialize(res).cast();
            for (Jso jso : types.asIterable()) {
                char[][] enclosingTypeNames = Util.arrayStringToCharArray((Array<String>)jso.getJsObjectField("enclosingTypeNames"));
                requestor.acceptType(jso.getStringField("packageName").toCharArray(), jso.getStringField("typeName").toCharArray(),
                                     enclosingTypeNames, jso.getIntField("modifiers"), null);
            }
        }

    }

    /**
     * Find the top-level types that are defined
     * in the current environment and whose simple name matches the given name.
     * <p/>
     * The types found are passed to one of the following methods (if additional
     * information is known about the types):
     * ISearchRequestor.acceptType(char[][] packageName, char[] typeName)
     * ISearchRequestor.acceptClass(char[][] packageName, char[] typeName, int modifiers)
     * ISearchRequestor.acceptInterface(char[][] packageName, char[] typeName, int modifiers)
     * <p/>
     * This method can not be used to find member types... member
     * types are found relative to their enclosing type.
     */
    @Override
    public void findExactTypes(char[] missingSimpleName, boolean findMembers, int searchFor, final ISearchRequestor storage) {
        if (missingSimpleName.length == 0) {
            return;
        }
        String url =
                restServiceContext + "/findExactTypes" + "?missingsimplename=" + new String(missingSimpleName)
                + "&findmembers=" + findMembers + "&searchfor=" + searchFor
                + "&projectpath=" + projectPath;
        String res = runSyncRequest(url);
        if (res != null) {
            JsoArray<Jso> types = Jso.deserialize(res).cast();
            for (Jso jso : types.asIterable()) {
                char[][] enclosingTypeNames = Util.arrayStringToCharArray((Array<String>)jso.getJsObjectField("enclosingTypeNames"));
                storage.acceptType(jso.getStringField("packageName").toCharArray(), jso.getStringField("typeName").toCharArray(),
                                     enclosingTypeNames, jso.getIntField("modifiers"), null);
            }
        }
    }

    public void clearBlackList() {
        blackListPackages.clear();
        blackListTypes.clear();
    }

    private static final class XmlHttpWraper extends JavaScriptObject {
        /**
         *
         */
        protected XmlHttpWraper() {
        }

        public native int getStatusCode()/*-{
            return this.status;
        }-*/;

        public native String getResponseText()/*-{
            return this.responseText;
        }-*/;

    }
}
