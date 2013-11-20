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
package com.codenvy.ide.ext.java.worker;

import com.codenvy.ide.ext.java.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.java.shared.JavaType;
import com.codenvy.ide.ext.java.shared.ShortTypeInfo;
import com.codenvy.ide.ext.java.worker.core.Flags;
import com.codenvy.ide.ext.java.worker.core.Signature;
import com.codenvy.ide.ext.java.worker.core.compiler.CharOperation;
import com.codenvy.ide.ext.java.worker.core.search.IJavaSearchConstants;
import com.codenvy.ide.ext.java.worker.env.BinaryTypeImpl;
import com.codenvy.ide.ext.java.worker.internal.codeassist.ISearchRequestor;
import com.codenvy.ide.ext.java.worker.internal.compiler.env.AccessRestriction;
import com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryMethod;
import com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType;
import com.codenvy.ide.ext.java.worker.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.ext.java.worker.internal.compiler.env.NameEnvironmentAnswer;
import com.codenvy.ide.json.js.Jso;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link com.codenvy.ide.ext.java.worker.internal.compiler.env.INameEnvironment} interface, use JavaCodeAssistantService
 * for receiving data and SessionStorage for
 * cache Java type data in browser
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 13, 2012 3:10:43 PM evgen $
 */
public class WorkerNameEnvironment implements INameEnvironment {

    private static final String      GET_CLASS_URL        = "/code-assistant/java/class-description?fqn=";
    private static final String      FIND_CLASS_BY_PREFIX = "/code-assistant/java/find-by-prefix/";
    private static       Set<String> packages             = new HashSet<String>();
    protected String restServiceContext;
    private   String vfsId;
    private   String projectId;
    private   String wsName;

    /**
     *
     */
    public WorkerNameEnvironment(String projectId, String restContext, String vfsId, String wsName) {
        this.projectId = projectId;
        restServiceContext = restContext;
        this.vfsId = vfsId;
        this.wsName = wsName;
    }

//    /**
//     * Get Class description (methods, fields etc.) by class FQN
//     *
//     * @param fqn
//     * @param fileHref
//     *         for who autocompletion called (Need for find classpath)
//     * @param callback
//     *         - the callback which client has to implement
//     */
//    public void getClassDescription(String fqn, String projectId, AsyncRequestCallback<TypeInfo> callback) {
//        String url =
//                restServiceContext + wsName + GET_CLASS_URL + fqn + "&projectid=" + projectId + "&vfsid="
//                + "dev-monit";
//        int status[] = {HTTPStatus.NO_CONTENT, HTTPStatus.OK};
//        callback.setSuccessCodes(status);
//        try {
//            AsyncRequest.build(RequestBuilder.GET, url).send(callback);
//        } catch (RequestException e) {
//            Log.error(WorkerNameEnvironment.class, e);
//        }
//    }

//    /**
//     * Find classes by prefix
//     *
//     * @param prefix
//     *         the first letters of class name
//     * @param projectId
//     *         for who autocompletion called (Need for find classpath)
//     * @param callback
//     *         - the callback which client has to implement
//     */
//    public void findClassesByPrefix(String prefix, String projectId, AsyncRequestCallback<TypesList> callback) {
//        String url =
//                restServiceContext + wsName + FIND_CLASS_BY_PREFIX + prefix + "?where=className" + "&projectid=" + projectId
//                //TODO configure vfs id
//                + "&vfsid=" + "dev-monit";
//        try {
//            AsyncRequest.build(RequestBuilder.GET, url).send(callback);
//        } catch (RequestException e) {
//            Log.error(getClass(), e);
//        }
//    }

    private static String convertSearchFilterToModelFilter(int searchFilter) {
        switch (searchFilter) {
            case IJavaSearchConstants.CLASS:
                return JavaType.CLASS.name();
            case IJavaSearchConstants.INTERFACE:
                return JavaType.INTERFACE.name();
            case IJavaSearchConstants.ENUM:
                return JavaType.ENUM.name();
            case IJavaSearchConstants.ANNOTATION_TYPE:
                return JavaType.ANNOTATION.name();
            // TODO
            // case IJavaSearchConstants.CLASS_AND_ENUM:
            // return NameLookup.ACCEPT_CLASSES | NameLookup.ACCEPT_ENUMS;
            // case IJavaSearchConstants.CLASS_AND_INTERFACE:
            // return NameLookup.ACCEPT_CLASSES | NameLookup.ACCEPT_INTERFACES;
            default:
                return null;
        }
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
        if (WorkerTypeInfoStorage.get().containsKey(key)) {
            return new NameEnvironmentAnswer(WorkerTypeInfoStorage.get().getType(key), null);
        }

        if (projectId != null) {

            return loadTypeInfo(key, projectId);
        }
        return null;
    }

    private String validateFqn(StringBuilder builder) {
        if (builder.indexOf("<") != -1) {
            builder.setLength(builder.indexOf("<"));
        }
        return builder.toString();
    }

    /**
     * Load and store in TypeInfoStorage type info
     *
     * @param fqn
     *         of the type
     * @param projectId
     *         project
     */
    public NameEnvironmentAnswer loadTypeInfo(final String fqn, String projectId) {
        if(packages.contains(fqn)){
            return null;
        }
        String url =
                restServiceContext + wsName + GET_CLASS_URL + fqn + "&projectid=" + projectId + "&vfsid="
                + vfsId;
        String result = runSyncReques(url);
        if (result != null) {


            Jso jso = Jso.deserialize(result);
            BinaryTypeImpl type = new BinaryTypeImpl(jso);
            WorkerTypeInfoStorage.get().putType(fqn, type);

            return new NameEnvironmentAnswer(type, null);
        } else return null;
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
        //TODO
//      if (TypeInfoStorage.get().getPackages(projectId).contains(key))
//         return null;
        if (WorkerTypeInfoStorage.get().containsKey(key)) {
            return new NameEnvironmentAnswer(WorkerTypeInfoStorage.get().getType(key),
                                             null);
        }
        if (projectId != null) {
            return loadTypeInfo(key, projectId);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPackage(char[][] parentPackageName, char[] packageName) {
        //TODO maybe need more actions on this
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
            String url =
                    restServiceContext + wsName + "/code-assistant/java/find-packages" + "?package=" + p.toString()
                    + "&projectid=" + projectId + "&vfsid=" + vfsId;
            String findPackage = runSyncReques(url);
            if (findPackage != null) {
                JSONArray jsonArray = JSONParser.parseLenient(findPackage).isArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    packages.add(jsonArray.get(i).isString().stringValue());
                }
                return jsonArray.size() > 0;
            }
            return false;

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
        int lastDotIndex = CharOperation.lastIndexOf('.', prefix);
        char[] qualification, simpleName;
        if (lastDotIndex < 0) {
            qualification = null;
            if (camelCaseMatch) {
                simpleName = prefix;
            } else {
                simpleName = CharOperation.toLowerCase(prefix);
            }
        } else {
            qualification = CharOperation.subarray(prefix, 0, lastDotIndex);
            if (camelCaseMatch) {
                simpleName = CharOperation.subarray(prefix, lastDotIndex + 1, prefix.length);
            } else {
                simpleName = CharOperation.toLowerCase(CharOperation.subarray(prefix, lastDotIndex + 1, prefix.length));
            }
        }
        String url =
                restServiceContext + wsName + "/code-assistant/java/classes-by-prefix" + "?prefix=" + new String(simpleName)
                + "&projectid=" + projectId + "&vfsid=" + vfsId;
        try {
            List<IBinaryType> typesByNamePrefix =
                    WorkerTypeInfoStorage.get().getTypesByNamePrefix(new String(prefix), qualification != null);
            for (IBinaryType object : typesByNamePrefix) {
                addConstructor((BinaryTypeImpl)object, requestor);
            }
            String typesJson = runSyncReques(url);
            JSONArray typesFromServer = null;
            if (typesJson != null) {
                typesFromServer = JSONParser.parseLenient(typesJson).isArray();
                for (int i = 0; i < typesFromServer.size(); i++) {
                    JSONObject object = typesFromServer.get(i).isObject();
                    BinaryTypeImpl type = new BinaryTypeImpl(object.getJavaScriptObject().<Jso>cast());
                    if (WorkerTypeInfoStorage.get().containsKey(String.valueOf(type.getFqn()))) {
                        continue;
                    }
                    WorkerTypeInfoStorage.get().putType(new String(type.getFqn()), type);
                    addConstructor(type, requestor);
                }
            }

        } catch (Exception e) {
//            Log.error(getClass(), e);
            //TODO log error
        }
    }

    private void addConstructor(BinaryTypeImpl type, final ISearchRequestor requestor) {
        IBinaryMethod[] methods = type.getMethods();
        boolean hasConstructor = false;
        if (methods != null) {
            for (IBinaryMethod method : methods) {
                if (!method.isConstructor()) {
                    continue;
                }
                int parameterCount = Signature.getParameterCount(method.getMethodDescriptor());
                char[][] parameterTypes = Signature.getParameterTypes(method.getMethodDescriptor());
                requestor.acceptConstructor(method.getModifiers(), type.getSourceName(), parameterCount,
                                            method.getMethodDescriptor(), parameterTypes, method.getArgumentNames(), type.getModifiers(),
                                            Signature.getQualifier(type.getFqn()), 0, new String(type.getSourceName()), null);
                hasConstructor = true;
            }
        }
        if (!hasConstructor) {
            requestor.acceptConstructor(Flags.AccPublic, type.getSourceName(), -1,
                                        null, // signature is not used for source type
                                        CharOperation.NO_CHAR_CHAR, CharOperation.NO_CHAR_CHAR, type.getModifiers(),
                                        Signature.getQualifier(type.getFqn()), 0, new String(type.getSourceName()), null);
        }

    }

    /**
     * Find the packages that start with the given prefix. A valid prefix is a qualified name separated by periods (ex. java.util).
     * The packages found are passed to: ISearchRequestor.acceptPackage(char[][] packageName)
     */
    @Override
    public void findPackages(char[] qualifiedName, final ISearchRequestor requestor) {
        final String pack = new String(qualifiedName);
        for (String key : packages) {
            if (key.startsWith(pack)) {
                requestor.acceptPackage(key.toCharArray());
            }
        }

    }

    private String runSyncReques(String url) {
        XmlHttpWraper xmlhttp = nativeRunSyncReques(url);
        int status = xmlhttp.getStatusCode();
        if (status == 200) {
            return xmlhttp.getResponseText();
        } else {
//            String message = null;
//            if (status == 204) {
//                message = "no content";
//            } else {
//                message = xmlhttp.getResponseText();
//            }
            //throw new RuntimeException("Server return " + message);
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
    public void findTypes(char[] qualifiedName, boolean b, boolean camelCaseMatch, int searchFor,
                          final ISearchRequestor requestor) {
        if (qualifiedName.length == 0) {
            return;
        }
        String searchType = convertSearchFilterToModelFilter(searchFor);
        String url = null;
        if (searchType == null) {
            int lastDotIndex = CharOperation.lastIndexOf('.', qualifiedName);
            String typeSearch;
            if (lastDotIndex < 0) {
                typeSearch = "className";
            } else {
                typeSearch = "fqn";
            }
            url =
                    restServiceContext + wsName + "/code-assistant/java/find-by-prefix/" + new String(qualifiedName) + "?where="
                    + typeSearch + "&projectid=" + projectId + "&vfsid=" + vfsId;
        } else {
            url =
                    restServiceContext + wsName + "/code-assistant/java/find-by-type/" + searchType + "?prefix="
                    + new String(qualifiedName) + "&projectid=" + projectId + "&vfsid="
                    + vfsId;
        }
        try {

            String typesJson = runSyncReques(url);
            if (typesJson != null) {
                DtoClientImpls.TypesListImpl autoBean = DtoClientImpls.TypesListImpl.deserialize(typesJson);

                for (ShortTypeInfo info : autoBean.getTypes().asIterable()) {
                    requestor.acceptType(info.getName().substring(0, info.getName().lastIndexOf(".")).toCharArray(),
                                         info.getName().substring(info.getName().lastIndexOf(".") + 1).toCharArray(),
                                         null,
                                         info.getModifiers(),
                                         null);
                }
            }
//            WorkerTypeInfoStorage.get().setShortTypesInfo(typesJson);
        } catch (Throwable e) {
//            Log.error(getClass(), e);
            throw new RuntimeException(e);
            //TODO log error
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
    public void findExactTypes(final char[] missingSimpleName, boolean b, int type, final ISearchRequestor storage) {
        findTypes(missingSimpleName, b, false, type, new ISearchRequestor() {

            @Override
            public void acceptType(char[] packageName, char[] typeName, char[][] enclosingTypeNames, int modifiers,
                                   AccessRestriction accessRestriction) {
                if (CharOperation.equals(missingSimpleName, typeName)) {
                    storage.acceptType(packageName, typeName, enclosingTypeNames, modifiers, accessRestriction);
                }
            }

            @Override
            public void acceptPackage(char[] packageName) {
            }

            @Override
            public void acceptConstructor(int modifiers, char[] simpleTypeName, int parameterCount, char[] signature,
                                          char[][] parameterTypes, char[][] parameterNames, int typeModifiers, char[] packageName,
                                          int extraFlags,
                                          String path, AccessRestriction access) {
            }
        });
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
