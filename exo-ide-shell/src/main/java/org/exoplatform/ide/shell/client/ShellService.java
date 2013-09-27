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
package org.exoplatform.ide.shell.client;

import com.codenvy.ide.client.util.logging.Log;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.gwtframework.commons.rest.*;
import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.GnuParser;
import org.exoplatform.ide.shell.client.cli.Parser;
import org.exoplatform.ide.shell.client.cli.Util;
import org.exoplatform.ide.shell.client.model.ClientCommand;
import org.exoplatform.ide.shell.shared.CLIResource;
import org.exoplatform.ide.shell.shared.CLIResourceParameter;
import org.exoplatform.ide.shell.shared.CLIResourceParameter.Type;
import org.exoplatform.ide.shell.shared.Login;
import org.exoplatform.ide.shell.shared.ShellConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Shell service.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 4, 2011 4:31:13 PM anya $
 */
public class ShellService {
    private static ShellService service;

    /** Path to the list of the resources. */
    private final String RESOURCES_PATH = "/cli/resources";

    /** @return {@link ShellService} shell service */
    public static ShellService getService() {
        if (service == null) {
            service = new ShellService();
        }
        return service;
    }

    /**
     * Returns the list of available commands in JSON format.
     *
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void getCommands(AsyncRequestCallback<Set<CLIResource>> callback) throws RequestException {
        String url = getRestContext() + getWorkspaceName() + RESOURCES_PATH;

        AsyncRequest.build(RequestBuilder.GET, url).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * Load application's configuration.
     *
     * @param url
     *         configuration location
     * @param callback
     * @throws RequestException
     */
    public void loadConfiguration(AsyncRequestCallback<ShellConfiguration> callback) throws RequestException {
        String url = getRestContext() + getWorkspaceName() + "/shell/configuration/init";
        AsyncRequest.build(RequestBuilder.GET, url).send(callback);
    }

    /**
     * Process user's command and execute it.
     *
     * @param cmd
     *         command to process
     * @param callback
     */
    public void processCommand(String cmd, AsyncRequestCallback<StringBuilder> callback) {
        List<CLIResource> appropriateCommands = findAppropriateCommands(cmd);
        CLIResource resource = null;
        if (appropriateCommands.size() == 0) {
            CloudShell.console().print(CloudShell.messages.noAppropriateCommandError(cmd));
        } else if (appropriateCommands.size() == 1) {
            resource = appropriateCommands.get(0);
        } else {
            cmd = cmd.trim();
            for (int i = 0; i < appropriateCommands.size(); i++) {
                CLIResource res = appropriateCommands.get(i);
                for (String command : res.getCommand()) {
                    if (cmd.equals(command)) {
                        resource = res;
                        break;
                    }
                }
            }
            if (resource == null) {
                CloudShell.console().print("Can't select command to execute.");
                return;
            }
        }
        try {
            if (resource instanceof ClientCommand) {
                ClientCommand command = (ClientCommand)resource;
                Parser parser = new GnuParser();
                String[] arguments = Util.translateCommandline(cmd);
                CommandLine commandLine = parser.parse(command.getOptions(), arguments);
                command.execute(commandLine);
            } else {
                String url =  (resource.getPath().startsWith("/")) ? getRestContext() + resource.getPath().substring(1) : getRestContext() + resource.getPath();
                AsyncRequest asyncRequest = createAsyncRequest(resource.getMethod(), url, false);
                if (canParseOptions(resource, cmd, asyncRequest)) {
                    CommandLine commandLine = CLIResourceUtil.parseCommandLine(cmd, resource.getParams());
                    url = setPathParameters(url, resource, commandLine, cmd);
                    String query = formQueryString(resource, commandLine, cmd);
                    url = (query != null && !query.isEmpty()) ? url + "?" + query : url;
                    boolean runAsync = commandLine.hasOption("&");
                    // Recreate, because of the URL:
                    asyncRequest = createAsyncRequest(resource.getMethod(), url, runAsync);

                    setHeaderParameters(asyncRequest, resource, commandLine, cmd);
                    setBody(resource, commandLine, cmd, asyncRequest);
                }
                setAcceptTypes(resource, asyncRequest);
                setContentType(resource, asyncRequest);
                asyncRequest.send(callback);
            }
        } catch (MandatoryParameterNotFoundException me) {
            CloudShell.console().print(me.getMessage() + "\n");
            Log.error(getClass(), me);
        } catch (Exception e) {
            CloudShell.console().print(CloudShell.messages.syntaxtError(cmd));
            Log.error(getClass(), e);
        }

    }

    /**
     * Returns whether options can be parsed. Returns <code>false</code> when:
     * <p/>
     * <ol>
     * <li>resource doesn't have any parameters</li>
     * <li>only one parameter is specified and its type is BODY</li>
     * </ol>
     *
     * @param resource
     *         CLI resource
     * @param cmd
     *         command
     * @param asyncRequest
     *         asynchronous request
     * @return {@link Boolean}
     */
    protected boolean canParseOptions(CLIResource resource, String cmd, AsyncRequest asyncRequest) {
        if (resource.getParams() == null || resource.getParams().size() <= 0)
            return false;
        // Check only one parameter is specified and its type is BODY.
        // If there are no options - send whole command line, entered by user.
        if (resource.getParams().size() == 1) {
            CLIResourceParameter parameter = resource.getParams().iterator().next();
            if (Type.BODY.equals(parameter.getType())
                && (parameter.getOptions() == null || parameter.getOptions().size() == 0)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(parameter.getName(), new JSONString(cmd));
                asyncRequest.data(jsonObject.toString());
                return false;
            }
        }
        return true;
    }

    /**
     * Perform login.
     *
     * @param command
     * @param callback
     * @throws RequestException
     */
    public void login(String command, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = getRestContext() + getWorkspaceName() + "/crash/command";

        Login loginBean = CloudShell.AUTO_BEAN_FACTORY.login().as();
        loginBean.setCmd(command);
        String payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(loginBean)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url).data(payload)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);

    }

    /**
     * Set Accept types header for the request. Depends on {@link CLIResource.#getProduces()} value.
     *
     * @param resource
     *         resource
     * @param asyncRequest
     *         asynchronous request
     */
    protected void setAcceptTypes(CLIResource resource, AsyncRequest asyncRequest) {
        if (resource.getProduces() != null && resource.getProduces().size() > 0) {
            String value = "";
            for (String type : resource.getProduces()) {
                value += (MimeType.TEXT_PLAIN.equals(type)) ? "," + type + ";q=0.6" : "," + type + ";q=0.4";
            }
            // Remove last separator:
            value = value.replaceFirst(",", "");
            // TODO temporary solution to get response in "text/plain" format, if there is method, that produces it.
            value = (value.contains(MimeType.TEXT_PLAIN)) ? value : MimeType.TEXT_PLAIN + ";q=0.6," + value;
            asyncRequest.header(HTTPHeader.ACCEPT, value);
        }
    }

    /**
     * Set Content type header for the request. Depends on {@link CLIResource.#getConsumes()} value.
     *
     * @param resource
     *         resource
     * @param asyncRequest
     *         asynchronous request
     */
    protected void setContentType(CLIResource resource, AsyncRequest asyncRequest) {
        if (resource.getConsumes() != null && !resource.getConsumes().isEmpty()) {
            String value = "";
            for (String type : resource.getConsumes()) {
                value += "," + type;
            }
            // Remove last separator:
            value = value.replaceFirst(",", "");
            asyncRequest.header(HTTPHeader.CONTENT_TYPE, value);
        }
    }

    /**
     * Set the post data in body of the request.
     *
     * @param params
     *         command's parameters
     * @param commandLine
     *         command line data
     * @param cmd
     *         string of the command line
     * @param asyncRequest
     *         asynchronous request
     * @throws MandatoryParameterNotFoundException
     *
     */
    protected void setBody(CLIResource resource, CommandLine commandLine, String cmd, AsyncRequest asyncRequest)
            throws MandatoryParameterNotFoundException {
        if (resource.getParams() == null || resource.getParams().isEmpty()) {
            return;
        }

        JSONObject jsonObject = new JSONObject();

        boolean hasBodyParameter = false;
        for (CLIResourceParameter parameter : resource.getParams()) {
            // Check type of the parameter is BODY:
            if (!Type.BODY.equals(parameter.getType())) {
                continue;
            }

            hasBodyParameter = true;
            // Process system property
            if (parameter.getName().startsWith("$")) {
                String value = processSystemProperty(parameter);
                if (value != null) {
                    jsonObject.put(parameter.getName().substring(1), new JSONString(value));
                }
                continue;
            }

            // Process options with no arguments(flags):
            if (!parameter.isHasArg() && parameter.getOptions() != null && parameter.getOptions().size() > 0) {
                jsonObject.put(parameter.getName(),
                               JSONBoolean.getInstance(isOptionPresent(parameter.getOptions(), commandLine)));
            }
            // Process arguments without options:
            else if (parameter.getOptions() == null || parameter.getOptions().size() == 0) {
                List<String> values = getArgumentsWithoutOptions(cmd, resource, commandLine);
                if ((values == null || values.size() == 0) && parameter.isMandatory()) {
                    throw new MandatoryParameterNotFoundException(CloudShell.messages.requiredArgumentNotFound(parameter
                                                                                                                       .getName()));
                } else if (values != null && values.size() > 0) {
                    jsonObject.put(parameter.getName(), getJSONArray(values));
                }
            } else {
                // Get value of the option:
                String value = getOptionValue(parameter.getOptions(), commandLine);

                if (parameter.isMandatory() && value == null) {
                    throw new MandatoryParameterNotFoundException(
                            CloudShell.messages.requiredOptionNotFound(optionsToString(parameter.getOptions())));
                } else if (value != null) {
                    jsonObject.put(parameter.getName(), new JSONString(value));
                }
            }
        }

        if (hasBodyParameter) {
            asyncRequest.data(jsonObject.toString());
        }
    }

    /**
     * Create HTTP asynchronous request with specified HTTP method, and URL.
     *
     * @param method
     *         HTTP Method
     * @param url
     * @param runAsync
     * @return {@link AsyncRequest} build asynchronous request
     */
    protected AsyncRequest createAsyncRequest(String method, String url, boolean runAsync) {
        if (HTTPMethod.POST.equalsIgnoreCase(method)) {
            return AsyncRequest.build(RequestBuilder.POST, url, runAsync);
        } else if (HTTPMethod.GET.equalsIgnoreCase(method)) {
            return AsyncRequest.build(RequestBuilder.GET, url, runAsync);
        } else {
            return AsyncRequest.build(RequestBuilder.GET, url, runAsync).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, method);
        }
    }

    /**
     * Form the string with query parameters.
     *
     * @param params
     *         resource parameters
     * @param commandLine
     *         parsed command line
     * @return {@link String} string with query parameters
     * @throws MandatoryParameterNotFoundException
     *
     */
    protected String formQueryString(CLIResource resource, CommandLine commandLine, String cmd)
            throws MandatoryParameterNotFoundException {
        String query = "";
        for (CLIResourceParameter param : resource.getParams()) {
            if (!Type.QUERY.equals(param.getType())) {
                continue;
            }

            // Process system property
            if (param.getName().startsWith("$")) {
                String value = processSystemProperty(param);
                query += (value != null) ? param.getName().substring(1) + "=" + value + "&" : "";
                continue;
            }

            // Parameter has no arguments, so get option is present or not.
            if (!param.isHasArg()) {
                query += param.getName() + "=" + isOptionPresent(param.getOptions(), commandLine) + "&";
            }
            // Process arguments without options:
            else if (param.getOptions() == null || param.getOptions().size() == 0) {
                List<String> values = getArgumentsWithoutOptions(cmd, resource, commandLine);
                if ((values == null || values.size() == 0) && param.isMandatory()) {
                    throw new MandatoryParameterNotFoundException(CloudShell.messages.requiredArgumentNotFound(param
                                                                                                                       .getName()));
                } else if (values != null && values.size() > 0) {
                    query += (values.get(0) != null) ? param.getName() + "=" + values.get(0) + "&" : "";
                }
            } else {
                String value = getOptionValue(param.getOptions(), commandLine);
                if (param.isMandatory() && value == null) {
                    throw new MandatoryParameterNotFoundException(
                            CloudShell.messages.requiredOptionNotFound(optionsToString(param.getOptions())));
                }
                query += (value != null) ? param.getName() + "=" + value + "&" : "";
            }

        }
        return query.endsWith("&") ? query.substring(0, query.length() - 1) : query;
    }

    /**
     * Get the value of the option.
     *
     * @param options
     * @param commandLine
     * @return {@link String} value of the option
     */
    protected String getOptionValue(Set<String> options, CommandLine commandLine) {
        for (String option : options) {
            option = (option.startsWith("--")) ? option.replaceFirst("--", "") : option;
            option = (option.startsWith("-")) ? option.replaceFirst("-", "") : option;
            String value = commandLine.getOptionValue(option);
            if (value != null)
                return value;
        }
        return null;
    }

    /**
     * Get the flag option value (true or false). If <code>true</code>, then option is present, if <code>false</code> then option
     * is not present.
     * <p/>
     * For example: <br>
     * <p/>
     * <pre>
     * git add --update
     * </pre>
     *
     * @param options
     *         set of options for the current parameter
     * @param commandLine
     *         parsed command line
     * @return {@link Boolean} if <code>true</code>, then option is present
     */
    protected boolean isOptionPresent(Set<String> options, CommandLine commandLine) {
        for (String option : options) {
            option = (option.startsWith("--")) ? option.replaceFirst("--", "") : option;
            option = (option.startsWith("-")) ? option.replaceFirst("-", "") : option;
            boolean value = commandLine.hasOption(option);
            if (value)
                return value;
        }
        return false;
    }

    /**
     * Set all specified header parameters.
     *
     * @param asyncRequest
     *         asynchronous request
     * @param params
     *         parameters
     * @param commandLine
     *         command line
     * @return {@link AsyncRequest} asynchronous request
     * @throws MandatoryParameterNotFoundException
     *
     */
    protected void setHeaderParameters(AsyncRequest asyncRequest, CLIResource resource, CommandLine commandLine,
                                       String cmd) throws MandatoryParameterNotFoundException {
        for (CLIResourceParameter parameter : resource.getParams()) {
            // Check type of the parameter:
            if (!Type.HEADER.equals(parameter.getType())) {
                continue;
            }

            // Process system property
            if (parameter.getName().startsWith("$")) {
                String value = processSystemProperty(parameter);
                if (value != null) {
                    asyncRequest.header(parameter.getName().substring(1), value);
                }
                continue;
            }

            if (!parameter.isHasArg()) {
                boolean present = isOptionPresent(parameter.getOptions(), commandLine);
                asyncRequest.header(parameter.getName(), String.valueOf(present));
            }
            // Process arguments without options:
            else if (parameter.getOptions() == null || parameter.getOptions().size() == 0) {
                List<String> values = getArgumentsWithoutOptions(cmd, resource, commandLine);
                if ((values == null || values.size() == 0) && parameter.isMandatory()) {
                    throw new MandatoryParameterNotFoundException(CloudShell.messages.requiredArgumentNotFound(parameter
                                                                                                                       .getName()));
                } else if (values != null && values.size() > 0 && values.get(0) != null) {
                    asyncRequest.header(parameter.getName(), values.get(0));
                }
            } else {
                String value = getOptionValue(parameter.getOptions(), commandLine);
                if (parameter.isMandatory() && value == null) {
                    throw new MandatoryParameterNotFoundException(
                            CloudShell.messages.requiredOptionNotFound(optionsToString(parameter.getOptions())));
                }
                asyncRequest.header(parameter.getName(), value);
            }
        }
    }

    protected String setPathParameters(String url, CLIResource resource, CommandLine commandLine, String cmd)
            throws MandatoryParameterNotFoundException {
        for (CLIResourceParameter param : resource.getParams()) {
            if (!Type.PATH.equals(param.getType())) {
                continue;
            }

            // Process system property
            if (param.getName().startsWith("$")) {
                String value = processSystemProperty(param);
                if (value != null) {
                    url = url.replace("{" + param.getName().substring(1) + "}", value);
                }
                continue;
            }

            // Parameter has no arguments, so get option is present or not.
            if (!param.isHasArg()) {
                boolean isOptionPresent = isOptionPresent(param.getOptions(), commandLine);
                url = url.replace("{" + param.getName() + "}", String.valueOf(isOptionPresent));
            }
            // Process arguments without options:
            else if (param.getOptions() == null || param.getOptions().size() == 0) {
                List<String> values = getArgumentsWithoutOptions(cmd, resource, commandLine);
                if ((values == null || values.size() == 0) && param.isMandatory()) {
                    throw new MandatoryParameterNotFoundException(CloudShell.messages.requiredArgumentNotFound(param
                                                                                                                       .getName()));
                } else if (values != null && values.size() > 0 && values.get(0) != null) {
                    url = url.replace("{" + param.getName() + "}", values.get(0));
                }
            } else {
                String value = getOptionValue(param.getOptions(), commandLine);
                if (param.isMandatory() && value == null) {
                    throw new MandatoryParameterNotFoundException(
                            CloudShell.messages.requiredOptionNotFound(optionsToString(param.getOptions())));
                }
                url = url.replace("{" + param.getName() + "}", value);
            }
        }
        return url;
    }

    /**
     * Get the system property (environment variable).
     *
     * @param parameter
     * @return {@link String} value of the property
     * @throws MandatoryParameterNotFoundException
     *
     */
    protected String processSystemProperty(CLIResourceParameter parameter) throws MandatoryParameterNotFoundException {
        String propertyName = parameter.getName().substring(1);
        String value = Environment.get().getValue(propertyName);
        if (value == null && parameter.isMandatory()) {
            throw new MandatoryParameterNotFoundException(CloudShell.messages.requiredPropertyNotSet(propertyName));
        }
        return value;
    }

    /**
     * Find the list of appropriate commands for the entered command line. If command line starts with available command or equals
     * it, then this command is added to the list.
     *
     * @param cmd
     *         string command line
     * @return {@link List} list of the appropriate commands
     */
    protected List<CLIResource> findAppropriateCommands(String cmd) {
        List<CLIResource> appropriateCommands = new ArrayList<CLIResource>();
        cmd = cmd.trim();

        for (CLIResource resource : CloudShell.getCommands()) {
            for (String command : resource.getCommand()) {
                if (cmd.startsWith(command + " ") || command.equals(cmd)) {
                    appropriateCommands.add(resource);
                }
            }
        }
        return appropriateCommands;
    }

    /**
     * Get the list of arguments without options. For example: <br>
     * <p/>
     * <pre>
     * git add file1.txt file2.txt --update
     * </pre>
     * <p/>
     * will return list with values "file1.txt" and "file2.txt".
     *
     * @param cmd
     *         command line entered by user
     * @param resource
     *         resource
     * @param commandLine
     *         parsed command line
     * @return {@link List} list of values
     */
    protected List<String> getArgumentsWithoutOptions(String cmd, CLIResource resource, CommandLine commandLine) {
        List<String> values = new ArrayList<String>();

        for (String command : resource.getCommand()) {
            if (cmd.startsWith(command + " ") || command.equals(cmd)) {
                String[] commandParts = command.split(" ");

                String[] arguments = commandLine.getArgs();
                if (arguments.length <= commandParts.length) {
                    return values;
                }
                values = Arrays.asList(arguments).subList(commandParts.length, arguments.length);
                return values;
            }
        }
        return values;
    }

    /**
     * Get {@link JSONArray} from the list.
     *
     * @param list
     *         list of values
     * @return {@link JSONArray} array
     */
    private JSONArray getJSONArray(List<String> list) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            array.set(i, new JSONString(list.get(i)));
        }
        return array;
    }

    /**
     * Form string with options.
     *
     * @param options
     *         set of options
     * @return {@link String} string of options separated with "/"
     */
    private String optionsToString(Set<String> options) {
        String optStr = "";
        for (String option : options) {
            optStr += option + "/";
        }
        return (optStr.endsWith("/")) ? optStr.substring(0, optStr.length() - 1) : optStr;
    }
    
    
    public static native String getRestContext() /*-{
    function endsWith(str, suffix) {
      return str.indexOf(suffix, str.length - suffix.length) !== -1;
    }
    rc = $wnd.appConfig.context;
    
    if (endsWith(rc,'/'))
     return rc;
    else
     return rc + '/';  
}-*/;
    
    public static native String getWorkspaceName() /*-{
    return $wnd.ws;
}-*/;
}