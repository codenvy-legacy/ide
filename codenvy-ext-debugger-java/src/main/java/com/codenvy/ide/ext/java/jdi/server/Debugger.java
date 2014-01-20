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
package com.codenvy.ide.ext.java.jdi.server;

import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.java.jdi.server.expression.Evaluator;
import com.codenvy.ide.ext.java.jdi.server.expression.ExpressionParser;
import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;
import com.codenvy.ide.ext.java.jdi.shared.BreakPointEvent;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEvent;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEventList;
import com.codenvy.ide.ext.java.jdi.shared.Field;
import com.codenvy.ide.ext.java.jdi.shared.Location;
import com.codenvy.ide.ext.java.jdi.shared.StackFrameDump;
import com.codenvy.ide.ext.java.jdi.shared.StepEvent;
import com.codenvy.ide.ext.java.jdi.shared.Value;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.ext.java.jdi.shared.VariablePath;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassNotPreparedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.NativeMethodException;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMCannotBeModifiedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.InvalidRequestStateException;
import com.sun.jdi.request.StepRequest;

import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import static com.codenvy.commons.json.JsonHelper.toJson;

/**
 * Connects to JVM over Java Debug Wire Protocol handle its events. All methods of this class may throws
 * DebuggerException. Typically such exception caused by errors in underlying JDI (Java Debug Interface), e.g.
 * connection errors. Instance of Debugger is not thread-safe.
 *
 * @author andrew00x
 * @author Artem Zatsarynnyy
 */
public class Debugger implements EventsHandler {
    private static final Logger                          LOG                  = LoggerFactory.getLogger(Debugger.class);
    private static final AtomicLong                      counter              = new AtomicLong(1);
    private static final ConcurrentMap<String, Debugger> instances            = new ConcurrentHashMap<>();
    private static final String                          EVENTS_CHANNEL       = "debugger:events:";
    private static final String                          DISCONNECTED_CHANNEL = "debugger:disconnected:";

    public static Debugger newInstance(String host, int port) throws VMConnectException {
        Debugger d = new Debugger(host, port);
        instances.put(d.id, d);
        return d;
    }

    public static Debugger getInstance(String name) {
        Debugger d = instances.get(name);
        if (d == null) {
            throw new IllegalArgumentException("Debugger " + name + " not found. ");
        }
        return d;
    }

    final String id = Long.toString(counter.getAndIncrement());
    private final String host;
    private final int    port;
    private final List<DebuggerEvent> events = new ArrayList<>();

    /**
     * A mapping of source file names to breakpoints. This mapping is used to set
     * breakpoints in files that haven't been loaded yet by a target Java VM.
     */
    private final ConcurrentMap<String, List<BreakPoint>>    deferredBreakpoints  = new ConcurrentHashMap<>();
    /** Stores ClassPrepareRequests to prevent making duplicate class prepare requests. */
    private final ConcurrentMap<String, ClassPrepareRequest> classPrepareRequests = new ConcurrentHashMap<>();

    /** Target Java VM representation. */
    private VirtualMachine  vm;
    private EventsCollector eventsCollector;

    /** Current thread. Not <code>null</code> is thread suspended, e.g breakpoint reached. */
    private ThreadReference thread;
    /** Current stack frame. Not <code>null</code> is thread suspended, e.g breakpoint reached. */
    private JdiStackFrame   stackFrame;

    /**
     * Create debugger and connect it to the JVM which already running at the specified host and port.
     *
     * @param host
     *         the host where JVM running
     * @param port
     *         the Java Debug Wire Protocol (JDWP) port
     * @throws VMConnectException
     *         when connection to Java VM is not established
     */
    private Debugger(String host, int port) throws VMConnectException {
        this.host = host;
        this.port = port;
        connect();
    }

    /**
     * Attach to a JVM that is already running at specified host. Calling this method has no effect is Debugger already
     * connected.
     *
     * @throws VMConnectException
     *         when connection to Java VM is not established
     */
    private void connect() throws VMConnectException {
        AttachingConnector connector = connector();
        if (connector == null) {
            throw new VMConnectException("Unable connect to target Java VM. Requested connector not found. ");
        }
        Map<String, Connector.Argument> arguments = connector.defaultArguments();
        arguments.get("hostname").setValue(host);
        ((Connector.IntegerArgument)arguments.get("port")).setValue(port);
        try {
            vm = connector.attach(arguments);
            eventsCollector = new EventsCollector(vm.eventQueue(), this);
        } catch (IOException | IllegalConnectorArgumentsException e) {
            throw new VMConnectException(e.getMessage(), e);
        }
        LOG.debug("Connect {}:{}", host, port);
    }

    private AttachingConnector connector() {
        for (AttachingConnector c : Bootstrap.virtualMachineManager().attachingConnectors()) {
            if ("com.sun.jdi.SocketAttach".equals(c.name())) {
                return c;
            }
        }
        return null;
    }

    /**
     * Close connection to the target JVM.
     *
     * @throws DebuggerException
     *         when failed to close connection
     */
    public void disconnect() throws DebuggerException {
        resume();
        vm.dispose();
        LOG.debug("Close connection to {}:{}", host, port);
    }

    /**
     * Add new breakpoint.
     *
     * @param breakpoint
     *         break point description
     * @throws InvalidBreakPointException
     *         if description of break point is invalid (specified line number or class name is invalid)
     * @throws DebuggerException
     *         when other JDI error occurs
     */
    public void addBreakpoint(BreakPoint breakpoint) throws InvalidBreakPointException, DebuggerException {
        final String className = breakpoint.getLocation().getClassName();
        final int lineNumber = breakpoint.getLocation().getLineNumber();
        List<ReferenceType> classes = vm.classesByName(className);
        // it may mean that class doesn't loaded by a target JVM yet
        if (classes.isEmpty()) {
            deferBreakpoint(breakpoint);
            return;
        }

        ReferenceType clazz = classes.get(0);
        List<com.sun.jdi.Location> locations;
        try {
            locations = clazz.locationsOfLine(lineNumber);
        } catch (AbsentInformationException | ClassNotPreparedException e) {
            throw new DebuggerException(e.getMessage(), e);
        }

        if (locations.isEmpty()) {
            throw new InvalidBreakPointException("Line " + lineNumber + " not found in class " + className);
        }

        com.sun.jdi.Location location = locations.get(0);
        if (location.method() == null) {
            // Line is out of method.
            throw new InvalidBreakPointException("Invalid line " + lineNumber + " in class " + className);
        }

        // Ignore new breakpoint if already have breakpoint at the same location.
        EventRequestManager requestManager = getEventManager();
        for (BreakpointRequest breakpointRequest : requestManager.breakpointRequests()) {
            if (location.equals(breakpointRequest.location())) {
                LOG.debug("Breakpoint at {} already set", location);
                return;
            }
        }

        try {
            EventRequest breakPointRequest = requestManager.createBreakpointRequest(location);
            breakPointRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
            String expression = breakpoint.getCondition();
            if (!(expression == null || expression.isEmpty())) {
                ExpressionParser parser = ExpressionParser.newInstance(expression);
                breakPointRequest.putProperty("com.codenvy.ide.java.debug.condition.expression.parser", parser);
            }
            breakPointRequest.setEnabled(true);
        } catch (NativeMethodException | IllegalThreadStateException | InvalidRequestStateException e) {
            throw new DebuggerException(e.getMessage(), e);
        }
        LOG.debug("Add breakpoint: {}", location);
    }

    private void deferBreakpoint(BreakPoint breakpoint) throws DebuggerException {
        final String className = breakpoint.getLocation().getClassName();
        List<BreakPoint> newList = new ArrayList<>();
        List<BreakPoint> list = deferredBreakpoints.putIfAbsent(className, newList);
        if (list == null) {
            list = newList;
        }
        list.add(breakpoint);

        // start listening for the load of the type
        if (!classPrepareRequests.containsKey(className)) {
            ClassPrepareRequest request = getEventManager().createClassPrepareRequest();
            // set class filter in order to reduce the amount of event traffic sent from the target VM to the debugger VM
            request.addClassFilter(className);
            request.enable();
            classPrepareRequests.put(className, request);
        }
    }

    /**
     * Get all break points which set for current debugger.
     *
     * @return list of break points
     * @throws DebuggerException
     *         when any JDI errors occurs when try to access current break points
     */
    public List<BreakPoint> getBreakPoints() throws DebuggerException {
        List<BreakpointRequest> breakpointRequests;
        try {
            breakpointRequests = getEventManager().breakpointRequests();
        } catch (DebuggerException e) {
            Throwable cause = e.getCause();
            if (cause instanceof VMCannotBeModifiedException) {
                // If target VM in read-only state then list of break point always empty.
                return Collections.emptyList();
            }
            throw e;
        }
        List<BreakPoint> breakPoints = new ArrayList<>(breakpointRequests.size());
        for (BreakpointRequest breakpointRequest : breakpointRequests) {
            com.sun.jdi.Location location = breakpointRequest.location();
            // Breakpoint always enabled at the moment. Managing states of breakpoint is not supported for now.
            breakPoints.add(DtoFactory.getInstance().createDto(BreakPoint.class)
                                    .withEnabled(true)
                                    .withLocation(DtoFactory.getInstance().createDto(Location.class)
                                                            .withClassName(location.declaringType().name())
                                                            .withLineNumber(location.lineNumber())));
        }
        Collections.sort(breakPoints, BREAKPOINT_COMPARATOR);
        return breakPoints;
    }

    private static final Comparator<BreakPoint> BREAKPOINT_COMPARATOR = new BreakPointComparator();

    /**
     * Delete break point.
     *
     * @param breakPoint
     *         break point to be removed
     * @throws DebuggerException
     *         when any JDI errors occurs when try to delete break point
     */
    public void deleteBreakPoint(BreakPoint breakPoint) throws DebuggerException {
        final String className = breakPoint.getLocation().getClassName();
        final int lineNumber = breakPoint.getLocation().getLineNumber();
        EventRequestManager requestManager = getEventManager();
        List<BreakpointRequest> snapshot = new ArrayList<>(requestManager.breakpointRequests());
        for (BreakpointRequest breakpointRequest : snapshot) {
            com.sun.jdi.Location location = breakpointRequest.location();
            if (location.declaringType().name().equals(className) && location.lineNumber() == lineNumber) {
                requestManager.deleteEventRequest(breakpointRequest);
                LOG.debug("Delete breakpoint: {}", location);
            }
        }
    }

    /**
     * Delete all break point.
     *
     * @throws DebuggerException
     *         when any JDI errors occurs when try to delete break point
     */
    public void deleteAllBreakPoints() throws DebuggerException {
        getEventManager().deleteAllBreakpoints();
    }

    /**
     * Get next list of debugger events.
     *
     * @return set of the debugger's events which occurred after last visit this method
     * @throws DebuggerException
     *         when any JDI errors occurs when try to get events
     */
    public List<DebuggerEvent> getEvents() throws DebuggerException {
        List<DebuggerEvent> eventsSnapshot;
        synchronized (events) {
            eventsSnapshot = new ArrayList<>(events);
            events.clear();
        }
        return eventsSnapshot;
    }

    /**
     * Resume suspended JVM.
     *
     * @throws DebuggerException
     *         when failed to resume target JVM
     */
    public void resume() throws DebuggerException {
        try {
            vm.resume();
            LOG.debug("Resume VM");
        } catch (VMCannotBeModifiedException e) {
            throw new DebuggerException(e.getMessage(), e);
        } finally {
            resetCurrentThread();
        }
    }

    /**
     * Get dump of fields and local variable of current object and current frame.
     *
     * @return dump of current stack frame
     * @throws DebuggerStateException
     *         when target JVM is not suspended
     * @throws DebuggerException
     *         when any other errors occur when try to access the current state of target JVM
     */
    public StackFrameDump dumpStackFrame() throws DebuggerStateException, DebuggerException {
        StackFrameDump dump = DtoFactory.getInstance().createDto(StackFrameDump.class);
        for (JdiField f : getCurrentFrame().getFields()) {
            dump.getFields().add((Field)DtoFactory.getInstance().createDto(Field.class)
                                                  .withIsFinal(f.isFinal())
                                                  .withIsStatic(f.isStatic())
                                                  .withIsTransient(f.isTransient())
                                                  .withIsVolatile(f.isVolatile())
                                                  .withName(f.getName())
                                                  .withValue(f.getValue().getAsString())
                                                  .withType(f.getTypeName())
                                                  .withVariablePath(
                                                          DtoFactory.getInstance().createDto(VariablePath.class)
                                                                    .withPath(Arrays.asList(f.isStatic() ? "static" : "this", f.getName())))
                                                  .withPrimitive(f.isPrimitive()));
        }
        for (JdiLocalVariable var : getCurrentFrame().getLocalVariables()) {
            dump.getLocalVariables().add(DtoFactory.getInstance().createDto(Variable.class)
                                                   .withName(var.getName())
                                                   .withValue(var.getValue().getAsString())
                                                   .withType(var.getTypeName())
                                                   .withVariablePath(
                                                           DtoFactory.getInstance().createDto(VariablePath.class)
                                                                     .withPath(Collections.singletonList(var.getName())))
                                                   .withPrimitive(var.isPrimitive()));
        }
        return dump;
    }

    /**
     * Get value of variable with specified path. Each item in path is name of variable.
     * <p/>
     * Path must be specified according to the following rules:
     * <ol>
     * <li>If need to get field of this object of current frame then first element in array always should be
     * 'this'.</li>
     * <li>If need to get static field in current frame then first element in array always should be 'static'.</li>
     * <li>If need to get local variable in current frame then first element should be the name of local variable.</li>
     * </ol>
     * <p/>
     * Here is example. <br/>
     * Assume we have next hierarchy of classes and breakpoint set in line: <i>// breakpoint</i>:
     * <pre>
     *    class A {
     *       private String str;
     *       ...
     *    }
     *
     *    class B {
     *       private A a;
     *       ....
     *
     *       void method() {
     *          A var = new A();
     *          var.setStr(...);
     *          a = var;
     *          // breakpoint
     *       }
     *    }
     * </pre>
     * There are two ways to access variable <i>str</i> in class <i>A</i>:
     * <ol>
     * <li>Through field <i>a</i> in class <i>B</i>: ['this', 'a', 'str']</li>
     * <li>Through local variable <i>var</i> in method <i>B.method()</i>: ['var', 'str']</li>
     * </ol>
     *
     * @param variablePath
     *         path to variable
     * @return variable or <code>null</code> if variable not found
     * @throws DebuggerStateException
     *         when target JVM is not suspended
     * @throws DebuggerException
     *         when any other errors occur when try to access the variable
     */
    public Value getValue(VariablePath variablePath) throws DebuggerStateException, DebuggerException {
        List<String> path = variablePath.getPath();
        if (path.size() == 0) {
            throw new IllegalArgumentException("Path to value may not be empty. ");
        }
        JdiVariable variable;
        int offset;
        if ("this".equals(path.get(0)) || "static".equals(path.get(0))) {
            if (path.size() < 2) {
                throw new IllegalArgumentException("Name of field required. ");
            }
            variable = getCurrentFrame().getFieldByName(path.get(1));
            offset = 2;
        } else {
            variable = getCurrentFrame().getLocalVariableByName(path.get(0));
            offset = 1;
        }

        for (int i = offset; variable != null && i < path.size(); i++) {
            variable = variable.getValue().getVariableByName(path.get(i));
        }

        if (variable == null) {
            return null;
        }

        Value value = DtoFactory.getInstance().createDto(Value.class).withValue(variable.getValue().getAsString());
        for (JdiVariable ch : variable.getValue().getVariables()) {
            VariablePath chPath = DtoFactory.getInstance().createDto(VariablePath.class).withPath(new ArrayList<>(path));
            chPath.getPath().add(ch.getName());
            if (ch instanceof JdiField) {
                JdiField f = (JdiField)ch;
                value.getVariables().add(DtoFactory.getInstance().createDto(Field.class)
                                                   .withIsFinal(f.isFinal())
                                                   .withIsStatic(f.isStatic())
                                                   .withIsTransient(f.isTransient())
                                                   .withIsVolatile(f.isVolatile())
                                                   .withName(f.getName())
                                                   .withValue(f.getValue().getAsString())
                                                   .withType(f.getTypeName())
                                                   .withVariablePath(chPath)
                                                   .withPrimitive(f.isPrimitive()));
            } else {
                // Array element.
                value.getVariables().add(DtoFactory.getInstance().createDto(Variable.class)
                                                   .withName(ch.getName())
                                                   .withValue(ch.getValue().getAsString())
                                                   .withType(ch.getTypeName())
                                                   .withVariablePath(chPath)
                                                   .withPrimitive(ch.isPrimitive()));
            }
        }
        return value;
    }

    /**
     * Update the value of variable with the value of an evaluated expression.
     *
     * @param variablePath
     *         path to updated variable
     * @param valueExpression
     *         expression
     * @throws DebuggerException
     *         when any other errors occur when try to update the variable
     */
    public void setValue(VariablePath variablePath, String valueExpression) throws DebuggerException {
        StringBuilder expression = new StringBuilder();
        for (String s : variablePath.getPath()) {
            if ("static".equals(s)) {
                continue;
            }
            if (expression.length() > 0) {
                expression.append('.');
            }
            expression.append(s);
        }
        expression.append('=');
        expression.append(valueExpression);
        expression(expression.toString());
    }

    /**
     * Returns the name of the target Java VM.
     *
     * @return JVM name
     * @throws DebuggerException
     *         when any JDI errors occur
     */
    public String getVmName() throws DebuggerException {
        return vm.name();
    }

    /**
     * Returns the version of the target Java VM.
     *
     * @return JVM version
     * @throws DebuggerException
     *         when any JDI errors occur
     */
    public String getVmVersion() throws DebuggerException {
        return vm.version();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void handleEvents(com.sun.jdi.event.EventSet eventSet) throws DebuggerException {
        boolean resume = true;
        try {
            for (com.sun.jdi.event.Event event : eventSet) {
                LOG.debug("New event: {}", event);
                if (event instanceof com.sun.jdi.event.BreakpointEvent) {
                    resume = processBreakPointEvent((com.sun.jdi.event.BreakpointEvent)event);
                } else if (event instanceof com.sun.jdi.event.StepEvent) {
                    resume = processStepEvent((com.sun.jdi.event.StepEvent)event);
                } else if (event instanceof com.sun.jdi.event.VMDisconnectEvent) {
                    resume = processDisconnectEvent((com.sun.jdi.event.VMDisconnectEvent)event);
                } else if (event instanceof com.sun.jdi.event.ClassPrepareEvent) {
                    resume = processClassPrepareEvent((com.sun.jdi.event.ClassPrepareEvent)event);
                }
            }
        } finally {
            if (resume) {
                eventSet.resume();
            }
        }
    }

    private boolean processBreakPointEvent(com.sun.jdi.event.BreakpointEvent event) throws DebuggerException {
        setCurrentThread(event.thread());
        boolean hitBreakpoint;
        ExpressionParser parser = (ExpressionParser)event.request().getProperty("com.codenvy.ide.java.debug.condition.expression.parser");
        if (parser != null) {
            com.sun.jdi.Value result = evaluate(parser);
            hitBreakpoint = result instanceof com.sun.jdi.BooleanValue && ((com.sun.jdi.BooleanValue)result).value();
        } else {
            // If there is no expression.
            hitBreakpoint = true;
        }

        if (hitBreakpoint) {
            com.sun.jdi.Location location = event.location();
            BreakPointEvent breakPointEvent;
            synchronized (events) {
                // Breakpoint always enabled at the moment. Managing states of breakpoint is not supported for now.
                breakPointEvent = (BreakPointEvent)DtoFactory.getInstance().createDto(BreakPointEvent.class)
                                                             .withBreakPoint(
                                                                     DtoFactory.getInstance().createDto(BreakPoint.class)
                                                                               .withEnabled(true)
                                                                               .withLocation(
                                                                                       DtoFactory.getInstance().createDto(Location.class)
                                                                                                 .withClassName(
                                                                                                         location.declaringType().name())
                                                                                                 .withLineNumber(location.lineNumber())))
                                                             .withType(DebuggerEvent.BREAKPOINT);
                events.add(breakPointEvent);
            }

            List<DebuggerEvent> eventsList = new ArrayList<>();
            eventsList.add(breakPointEvent);
            publishWebSocketMessage(DtoFactory.getInstance().createDto(DebuggerEventList.class).withEvents(eventsList),
                                    EVENTS_CHANNEL + id);
        }

        // Left target JVM in suspended state if result of evaluation of expression is boolean value and true
        // or if condition expression is not set.
        return !hitBreakpoint;
    }

    private boolean processStepEvent(com.sun.jdi.event.StepEvent event) throws DebuggerException {
        setCurrentThread(event.thread());
        com.sun.jdi.Location location = event.location();
        StepEvent stepEvent;
        synchronized (events) {
            stepEvent = (StepEvent)DtoFactory.getInstance().createDto(StepEvent.class)
                                             .withLocation(DtoFactory.getInstance().createDto(Location.class)
                                                                     .withClassName(location.declaringType().name())
                                                                     .withLineNumber(location.lineNumber()))
                                             .withType(DebuggerEvent.STEP);
            events.add(stepEvent);
        }

        List<DebuggerEvent> eventsList = new ArrayList<>();
        eventsList.add(stepEvent);
        publishWebSocketMessage(DtoFactory.getInstance().createDto(DebuggerEventList.class).withEvents(eventsList),
                                EVENTS_CHANNEL + id);
        // Lets target JVM to be in suspend state.
        return false;
    }

    private boolean processDisconnectEvent(com.sun.jdi.event.VMDisconnectEvent event) {
        eventsCollector.stop();
        instances.remove(id);
        publishWebSocketMessage(null, DISCONNECTED_CHANNEL + id);
        return true;
    }

    private boolean processClassPrepareEvent(com.sun.jdi.event.ClassPrepareEvent event) throws DebuggerException {
        final String className = event.referenceType().name();
        // add deferred breakpoints
        List<BreakPoint> breakpointsToAdd = deferredBreakpoints.get(className);
        if (breakpointsToAdd != null) {
            for (BreakPoint b : breakpointsToAdd) {
                addBreakpoint(b);
            }
            deferredBreakpoints.remove(className);

            // All deferred breakpoints for className have been already added,
            // so no need to listen for an appropriate ClassPrepareRequests any more.
            ClassPrepareRequest request = classPrepareRequests.remove(className);
            if (request != null) {
                getEventManager().deleteEventRequest(request);
            }
        }
        return true;
    }

    /**
     * Step to the next line.
     *
     * @throws DebuggerStateException
     *         when target JVM is not suspended
     * @throws DebuggerException
     *         when any other JDI errors occur
     */
    public void stepOver() throws DebuggerException {
        doStep(StepRequest.STEP_OVER);
    }

    /**
     * Step to the next frame.
     *
     * @throws DebuggerStateException
     *         when target JVM is not suspended
     * @throws DebuggerException
     *         when any other JDI errors occur
     */
    public void stepInto() throws DebuggerException {
        doStep(StepRequest.STEP_INTO);
    }

    /**
     * Step out of the current frame.
     *
     * @throws DebuggerStateException
     *         when target JVM is not suspended
     * @throws DebuggerException
     *         when any other JDI errors occur
     */
    public void stepOut() throws DebuggerException {
        doStep(StepRequest.STEP_OUT);
    }

    private void doStep(int depth) throws DebuggerException {
        clearSteps();
        StepRequest request = getEventManager().createStepRequest(getCurrentThread(), StepRequest.STEP_LINE, depth);
        request.addCountFilter(1);
        request.enable();
        resume();
    }

    private void clearSteps() throws DebuggerException {
        List<StepRequest> snapshot = new ArrayList<>(getEventManager().stepRequests());
        for (StepRequest stepRequest : snapshot) {
            if (stepRequest.thread().equals(getCurrentThread())) {
                getEventManager().deleteEventRequest(stepRequest);
            }
        }
    }

    public String expression(String expression) throws DebuggerStateException {
        com.sun.jdi.Value result = evaluate(ExpressionParser.newInstance(expression));
        return result == null ? "null" : result.toString();
    }

    private com.sun.jdi.Value evaluate(ExpressionParser parser) throws DebuggerStateException {
        final long startTime = System.currentTimeMillis();
        try {
            return parser.evaluate(new Evaluator(vm, getCurrentThread()));
        } finally {
            final long endTime = System.currentTimeMillis();
            LOG.debug("==>> Evaluate time: {} ms", (endTime - startTime));
            // Evaluation of expression may update state of frame.
            resetCurrentFrame();
        }
    }

    private ThreadReference getCurrentThread() throws DebuggerStateException {
        if (thread == null) {
            throw new DebuggerStateException("Target Java VM is not suspended. ");
        }
        return thread;
    }

    private JdiStackFrame getCurrentFrame() throws DebuggerException {
        if (stackFrame != null) {
            return stackFrame;
        }
        try {
            stackFrame = new JdiStackFrameImpl(getCurrentThread().frame(0));
        } catch (IncompatibleThreadStateException e) {
            throw new DebuggerException("Thread is not suspended. ", e);
        }
        return stackFrame;
    }

    private void setCurrentThread(ThreadReference t) {
        stackFrame = null;
        thread = t;
    }

    private void resetCurrentFrame() {
        stackFrame = null;
    }

    private void resetCurrentThread() {
        this.stackFrame = null;
        this.thread = null;
    }

    //

    private EventRequestManager getEventManager() throws DebuggerException {
        try {
            return vm.eventRequestManager();
        } catch (VMCannotBeModifiedException e) {
            throw new DebuggerException(e.getMessage(), e);
        }
    }

    /**
     * Publishes the message over WebSocket connection.
     *
     * @param data
     *         the data to be sent to the client
     * @param channelID
     *         channel identifier
     */
    private static void publishWebSocketMessage(Object data, String channelID) {
        ChannelBroadcastMessage message = new ChannelBroadcastMessage();
        message.setChannel(channelID);
        message.setType(ChannelBroadcastMessage.Type.NONE);
        if (data instanceof String) {
            message.setBody((String)data);
        } else if (data != null) {
            message.setBody(toJson(data));
        }

        try {
            WSConnectionContext.sendMessage(message);
        } catch (Exception e) {
            LOG.error("Failed to send message over WebSocket.", e);
        }
    }
}
