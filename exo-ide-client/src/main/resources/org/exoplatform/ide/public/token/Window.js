var Window = {
	"name" : "Window",
	"type" : "CLASS",
	"code" : "window",
	"fqn" : "Window",
	"fullDescription" : "<p>The window object represents an open window in a browser.</p><p>If a document contain frames (&lt;frame&gt; or &lt;iframe&gt; tags), the browser creates one window object for the HTML document, and one additional window object for each frame.</p>",
	"subTokenList" : [
			{
				"name" : "closed",
				"type" : "PROPERTY",
				"code" : "closed",
				"shortDescription" : " : Boolean",
				"fqn" : "Window",
				"fullDescription" : "<p>The closed property returns a Boolean value indicating whether a window has been closed or not.</p><h4>Syntax</h4><p><pre>window.closed</pre></p>"
			},
			{
				"name" : "defaultStatus",
				"type" : "PROPERTY",
				"code" : "defaultStatus",
				"shortDescription" : " : String",
				"fqn" : "Window",
				"fullDescription" : "<p>Sets or returns the default text in the statusbar of a window.</p><h4>Syntax</h4><p><pre>window.closed</pre></p><h4>Note</h4><p>The defaultStatus property does not work in the default configuration of IE, Firefox, Chrome, or Safari. To allow scripts to change the text of the status, the user must set the dom.disable_window_status_change preference to false in the about:config screen. (or in Firefox: \"Tools - Options - Content -Enable JavaScript / Advanced - Allow scripts to change status bar text\").</p>"
			},
			{
				"name" : "frames",
				"type" : "PROPERTY",
				"code" : "frames",
				"shortDescription" : " : Array",
				"fqn" : "Window",
				"fullDescription" : "<p>The frames property returns an array of all the frames (including iframes) in the current window.</p><h4>Syntax</h4><p><pre>window.frames</pre></p>"
			},
			{
				"name" : "innerWidth",
				"type" : "PROPERTY",
				"code" : "innerWidth",
				"shortDescription" : " : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>The innerWidth property sets or returns the inner width of a window's content area.</p><h4>Syntax</h4><p><pre>window.innerWidth=pixels</pre></p>"
			},
			{
				"name" : "innerHeight",
				"type" : "PROPERTY",
				"code" : "innerHeight",
				"shortDescription" : " : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>The innerHeight property sets or returns the inner height of a window's content area.</p><h4>Syntax</h4><p><pre>window.innerHeight=pixels</pre></p>"
			},
			{
				"name" : "length",
				"type" : "PROPERTY",
				"code" : "length",
				"shortDescription" : " : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>The length property returns the number of frames (including iframes) in the current window.</p><h4>Syntax</h4><p><pre>window.length</pre></p>"
			},
			{
				"name" : "name",
				"type" : "PROPERTY",
				"code" : "name",
				"shortDescription" : " : String",
				"fqn" : "Window",
				"fullDescription" : "<p>Sets or returns the name of a window</p><h4>Syntax</h4><p><pre>window.name</pre></p>"
			},
			{
				"name" : "opener",
				"type" : "PROPERTY",
				"code" : "opener",
				"shortDescription" : " : Object",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns a reference to the window that created the window.</p><h4>Syntax</h4><p><pre>window.opener</pre></p>"
			},
			{
				"name" : "outerHeight",
				"type" : "PROPERTY",
				"code" : "outerHeight",
				"shortDescription" : " : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>Sets or returns the outer height of a window, including toolbars/scrollbars.</p><h4>Syntax</h4><p><pre>window.outerHeight=pixels</pre></p>"
			},
			{
				"name" : "outerWidth",
				"type" : "PROPERTY",
				"code" : "outerWidth",
				"shortDescription" : " : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>Sets or returns the outer width of a window, including toolbars/scrollbars.</p><h4>Syntax</h4><p><pre>window.outerWidth=pixels</pre></p>"
			},
			{
				"name" : "pageXOffset",
				"type" : "PROPERTY",
				"code" : "pageXOffset",
				"shortDescription" : " : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the pixels the current document has been scrolled (horizontally) from the upper left corner of the window</p><h4>Syntax</h4><p><pre>window.pageXOffset</pre></p>"
			},
			{
				"name" : "pageYOffset",
				"type" : "PROPERTY",
				"code" : "pageYOffset",
				"shortDescription" : " : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the pixels the current document has been scrolled (vertically) from the upper left corner of the window.</p><h4>Syntax</h4><p><pre>window.pageYOffset</pre></p>"
			},
			{
				"name" : "parent",
				"type" : "PROPERTY",
				"code" : "parent",
				"shortDescription" : " : Window",
				"fqn" : "Window",
				"fullDescription" : "<p>The parent property returns the parent window of the current window.</p><h4>Syntax</h4><p><pre>window.parent</pre></p>"
			},
			{
				"name" : "screenLeft",
				"type" : "PROPERTY",
				"code" : "screenLeft",
				"shortDescription" : " : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the x coordinate of the window relative to the screen</p><h4>Syntax</h4><p><pre>window.screenLeft</pre></p>"
			},
			{
				"name" : "screenTop",
				"type" : "PROPERTY",
				"code" : "screenTop",
				"shortDescription" : " : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the y coordinate of the window relative to the screen.</p><h4>Syntax</h4><p><pre>window.screenTop</pre></p>"
			},
			{
				"name" : "screenX",
				"type" : "PROPERTY",
				"code" : "screenX",
				"shortDescription" : " : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the x coordinate of the window relative to the screen.</p><h4>Syntax</h4><p><pre>window.screenX</pre></p>"
			},
			{
				"name" : "screenY",
				"type" : "PROPERTY",
				"code" : "screenY",
				"shortDescription" : " : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the y coordinate of the window relative to the screen.</p><h4>Syntax</h4><p><pre>window.screenY</pre></p>"
			},
			{
				"name" : "self",
				"type" : "PROPERTY",
				"code" : "self",
				"shortDescription" : " : Window",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the current window.</p><h4>Syntax</h4><p><pre>window.self</pre></p>"
			},
			{
				"name" : "status",
				"type" : "PROPERTY",
				"code" : "status",
				"shortDescription" : " : String",
				"fqn" : "Window",
				"fullDescription" : "<p>Sets the text in the statusbar of a window.</p><h4>Syntax</h4><p><pre>window.status</pre></p><h4>Note</h4><p>The status property does not work in the default configuration of IE, Firefox, Chrome, or Safari. To allow scripts to change the text of the status, the user must set the dom.disable_window_status_change preference to false in the about:config screen. (or in Firefox: \"Tools - Options - Content -Enable JavaScript / Advanced - Allow scripts to change status bar text\").</p>"
			},
			{
				"name" : "top",
				"type" : "PROPERTY",
				"code" : "top",
				"shortDescription" : " : Window",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the topmost browser window.</p><h4>Syntax</h4><p><pre>window.top</pre></p>"
			},
			{
				"name" : "alert",
				"type" : "METHOD",
				"code" : "alert(message)",
				"shortDescription" : "(message)",
				"fqn" : "Window",
				"fullDescription" : "<p>Displays an alert box with a message and an OK button.</p><h4>Syntax</h4><p><pre>alert(message)</pre></p>"
			},
			{
				"name" : "blur",
				"type" : "METHOD",
				"code" : "blur()",
				"shortDescription" : "()",
				"fqn" : "Window",
				"fullDescription" : "<p>Removes focus from the current window.</p><h4>Syntax</h4><p><pre>alert(message)</pre></p>"
			},
			{
				"name" : "clearInterval",
				"type" : "METHOD",
				"code" : "clearInterval(id_of_setinterval)",
				"shortDescription" : "(id_of_setinterval)",
				"fqn" : "Window",
				"fullDescription" : "<p>The clearInterval() method clears a timer set with the setInterval() method.The ID value returned by setInterval() is used as the parameter for the clearInterval() method.</p><h4>Syntax</h4><p><pre>clearInterval(id_of_setinterval))</pre></p>"
			},
			{
				"name" : "clearTimeout",
				"type" : "METHOD",
				"code" : "clearTimeout(id_of_settimeout)",
				"shortDescription" : "(id_of_settimeout)",
				"fqn" : "Window",
				"fullDescription" : "<p>The clearTimeout() method clears a timer set with the setTimeout() method. The ID value returned by setTimeout() is used as the parameter for the clearTimeout() method.</p><h4>Syntax</h4><p><pre>clearTimeout(id_of_settimeout)</pre></p>"
			},
			{
				"name" : "close",
				"type" : "METHOD",
				"code" : "close()",
				"shortDescription" : "()",
				"fqn" : "Window",
				"fullDescription" : "<p>Closes the current window.</p><h4>Syntax</h4><p><pre>window.close()</pre></p>"
			},
			{
				"name" : "confirm",
				"type" : "METHOD",
				"code" : "confirm(message)",
				"shortDescription" : "(message) : Boolean",
				"fqn" : "Window",
				"fullDescription" : "<p>Displays a dialog box with a specified message, along with an OK and a Cancel button. This method returns true if the visitor clicked \"OK\", and false otherwise.</p><h4>Syntax</h4><p><pre>confirm(message)</pre></p>"
			},
			{
				"name" : "createPopup",
				"type" : "METHOD",
				"code" : "createPopup()",
				"shortDescription" : "() : Window",
				"fqn" : "Window",
				"fullDescription" : "<p>Used to create a pop-up window.</p><h4>Syntax</h4><p><pre>window.createPopup()</pre></p>"
			},
			{
				"name" : "focus",
				"type" : "METHOD",
				"code" : "focus()",
				"shortDescription" : "()",
				"fqn" : "Window",
				"fullDescription" : "<p>Sets focus to the current window.</p><h4>Syntax</h4><p><pre>window.focus()</pre></p>"
			},
			{
				"name" : "moveBy",
				"type" : "METHOD",
				"code" : "moveBy",
				"shortDescription" : "(x,y)",
				"fqn" : "Window",
				"fullDescription" : "<p>Moves a window relative to its current position.</p><h4>Syntax</h4><p><pre>window.moveBy(x,y)</pre></p>"
			},
			{
				"name" : "moveTo",
				"type" : "METHOD",
				"code" : "moveTo(x,y)",
				"shortDescription" : "(x,y)",
				"fqn" : "Window",
				"fullDescription" : "<p>Moves a window to the specified position.</p><h4>Syntax</h4><p><pre>window.moveTo(x,y)</pre></p>"
			},
			{
				"name" : "open",
				"type" : "METHOD",
				"code" : "open(URL,name,specs,replace)",
				"shortDescription" : "(URL,name,specs,replace) : Window",
				"fqn" : "Window",
				"fullDescription" : "<p>Opens a new browser window.</p><h4>Syntax</h4><p><pre>window.open(URL,name,specs,replace)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"5\" border=\"1\" width=\"100%\"><tbody><tr><th>Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr><tr><td>URL</td><td>Optional. Specifies the URL of the page to open. If no URL is specified, a new window with about:blank is opened</td></tr><tr><td>name</td><td>Optional. Specifies the target attribute or the name of the window. The following values are supported:<ul><li>_blank - URL is loaded into a new window. This is default</li><li>_parent - URL is loaded into the parent frame</li><li>_self - URL replaces the current page</li><li>_top - URL replaces any framesets that may be loaded</li><li><i>name</i> - The name of the window</li></ul></td></tr><tr><td>specs</td><td>Optional. A comma-separated list of items. The following values are supported:<br><br><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><td>channelmode=yes|no|1|0</td><td>Whether or not to display the window in theater mode. Default is no. IE only</td></tr><tr><td>directories=yes|no|1|0</td><td>Whether or not to add directory buttons. Default is yes. IE only</td></tr><tr><td>fullscreen=yes|no|1|0</td><td>Whether or not to display the browser in full-screen mode. Default is no. A window in full-screen mode must also be in theater mode. IE only</td></tr><tr><td>height=pixels</td><td>The height of the window. Min. value is 100</td></tr><tr><td>left=pixels</td><td>The left position of the window</td></tr><tr><td>location=yes|no|1|0</td><td>Whether or not to display the address field. Default is yes</td></tr><tr><td>menubar=yes|no|1|0</td><td>Whether or not to display the menu bar. Default is yes</td></tr><tr><td>resizable=yes|no|1|0</td><td>Whether or not the window is resizable. Default is yes</td></tr><tr><td>scrollbars=yes|no|1|0</td><td>Whether or not to display scroll bars. Default is yes</td></tr><tr><td>status=yes|no|1|0</td><td>Whether or not to add a status bar. Default is yes</td></tr><tr><td>titlebar=yes|no|1|0</td><td>Whether or not to display the title bar. Ignored unless the calling application is an HTML Application or a trusted dialog box. Default is yes</td></tr><tr><td>toolbar=yes|no|1|0</td><td>Whether or not to display the browser toolbar. Default is yes</td></tr><tr><td>top=pixels</td><td>The top position of the window. IE only</td></tr><tr><td>width=pixels</td><td>The width of the window. Min. value is 100</td></tr></tbody></table><br></td></tr><tr><td>replace</td><td>Optional.Specifies whether the URL creates a new entry or replaces the current entry in the history list. The following values are supported:<ul><li>true - URL replaces the current document in the history list</li><li>false - URL creates a new entry in the history list</li></ul></td></tr></tbody></table><p></p>"
			},
			{
				"name" : "print",
				"type" : "METHOD",
				"code" : "print()",
				"shortDescription" : "()",
				"fqn" : "Window",
				"fullDescription" : "<p>Prints the content of the current window.</p><h4>Syntax</h4><p><pre>window.print()</pre></p>"
			},
			{
				"name" : "prompt",
				"type" : "METHOD",
				"code" : "prompt(msg,defaultText)",
				"shortDescription" : "(msg,defaultText) : String",
				"fqn" : "Window",
				"fullDescription" : "<p>Displays a dialog box that prompts the visitor for input. This method returns the string the visitor has entered.</p><h4>Syntax</h4><p><pre>prompt(msg,defaultText)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"20%\">Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr><tr><td>msg</td><td>Required. The message to display in the dialog box</td></tr><tr><td>defaultText</td><td>Optional. The default input value</td></tr></tbody></table>"
			},
			{
				"name" : "resizeBy",
				"type" : "METHOD",
				"code" : "resizeBy(width,height)",
				"shortDescription" : "(width,height)",
				"fqn" : "Window",
				"fullDescription" : ""
			},
			{
				"name" : "resizeTo",
				"type" : "METHOD",
				"code" : "resizeTo(width,height)",
				"shortDescription" : "(width,height)",
				"fqn" : "Window",
				"fullDescription" : "<p>Resizes the window by the specified pixels.</p><h4>Note</h4><p>This method moves the bottom right corner of the window by the specified number of pixels defined. The top left corner will not be moved (it stays in its original coordinates).</p><h4>Syntax</h4><p><pre>resizeBy(width,height)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"20%\">Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr>  <tr><td>width</td><td>Required. A positive or a negative number that specifies how many pixels to resize the width by</td></tr><tr><td>height</td><td>Required. A positive or a negative number that specifies how many pixels to resize the height by</td></tr></tbody></table>"
			},
			{
				"name" : "scrollBy",
				"type" : "METHOD",
				"code" : "scrollBy(xnum,ynum)",
				"shortDescription" : "(xnum,ynum)",
				"fqn" : "Window",
				"fullDescription" : "<p>Scrolls the content by the specified number of pixels.</p><h4>Note</h4><p>For this method to work, the visible property of the window's scrollbar must be set to true!</p><h4>Syntax</h4><p><pre>scrollBy(xnum,ynum)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"20%\">Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr><tr><td>xnum</td><td>Required. How many pixels to scroll by, along the x-axis (horizontal)</td></tr><tr><td>ynum</td><td>Required. How many pixels to scroll by, along the y-axis (vertical)</td></tr></tbody></table>"
			},
			{
				"name" : "scrollTo",
				"type" : "METHOD",
				"code" : "scrollTo(xpos,ypos)",
				"shortDescription" : "(xpos,ypos)",
				"fqn" : "Window",
				"fullDescription" : "<p>Scrolls the content to the specified coordinates.</p><h4>Syntax</h4><p><pre>scrollTo(xpos,ypos)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"20%\">Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr><tr><td>xpos</td><td>Required. The coordinate to scroll to, along the x-axis</td></tr><tr><td>ypos</td><td>Required. The coordinate to scroll to, along the y-axis</td></tr></tbody></table>"
			},
			{
				"name" : "setInterval",
				"type" : "METHOD",
				"code" : "setInterval(code,millisec,lang)",
				"shortDescription" : "(code,millisec,lang) : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>Calls a function or evaluates an expression at specified intervals (in milliseconds). The setInterval() method will continue calling the function until <i>clearInterval()</i> is called, or the window is closed. The ID value returned by setInterval() is used as the parameter for the clearInterval() method.</p><h4>Syntax</h4><p><pre>setInterval(code,millisec,lang)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"20%\">Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr><tr><td>code</td><td>Required. A reference to the function or the code to be executed</td></tr><tr><td>millisec</td><td>Required. The intervals (in milliseconds) on how often to execute the code</td></tr><tr><td>lang</td><td>Optional. JScript | VBScript | JavaScript</td></tr></tbody></table>"
			},
			{
				"name" : "setTimeout",
				"type" : "METHOD",
				"code" : "setTimeout(code,millisec,lang)",
				"shortDescription" : "(code,millisec,lang) : Number",
				"fqn" : "Window",
				"fullDescription" : "<p>Calls a function or evaluates an expression after a specified number of milliseconds.</p><h4>Syntax</h4><p><pre>setTimeout(code,millisec,lang)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"20%\">Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr><tr><td>code</td><td>Required. A reference to the function or the code to be executed</td></tr><tr><td>millisec</td><td>Required. The number of milliseconds to wait before executing the code</td></tr><tr><td>lang</td><td>Optional. The scripting language: JScript | VBScript | JavaScript</td></tr></tbody></table>"
			},
			{
				"name" : "navigator",
				"type" : "PROPERTY",
				"code" : "navigator",
				"shortDescription" : " : Navigator",
				"varType" : "Navigator",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the Navigator object for the window</p>"
			},
			{
				"name" : "history",
				"type" : "PROPERTY",
				"code" : "history",
				"shortDescription" : " : History",
				"varType" : "History",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the History object for the window.</p>"
			},
			{
				"name" : "location",
				"type" : "PROPERTY",
				"code" : "location",
				"shortDescription" : " : Location",
				"varType" : "Location",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the Location object for the window.</p>"
			},
			{
				"name" : "screen",
				"type" : "PROPERTY",
				"code" : "screen",
				"shortDescription" : " : Screen",
				"varType" : "Screen",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the Screen object for the window.</p>"
			},
			{
				"name" : "document",
				"type" : "PROPERTY",
				"code" : "document",
				"shortDescription" : " : Document",
				"varType" : "Document",
				"fqn" : "Window",
				"fullDescription" : "<p>Returns the Document object for the window.</p>"
			} ]
}
