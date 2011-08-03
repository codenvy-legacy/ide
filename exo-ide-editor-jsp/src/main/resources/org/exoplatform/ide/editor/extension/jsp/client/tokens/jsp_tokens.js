[
		{
			"name" : "application",
			"type" : "VARIABLE",
			"varType" : "javax.servlet.ServletContext",
			"fullDescription" : "The servlet context obtained from the servlet conﬁguration object (as in the call getServletConfig().getContext())"
		},
		{
			"name" : "config",
			"type" : "VARIABLE",
			"varType" : "javax.servlet.ServletConfig",
			"fullDescription" : "The ServletConfig for this JSP page"
		},
		{
			"name" : "exception",
			"type" : "VARIABLE",
			"varType" : "java.lang.Throwable",
			"fullDescription" : "The uncaught Throwable that resulted in the error page being invoked."
		},
		{
			"name" : "out",
			"type" : "VARIABLE",
			"varType" : "javax.servlet.jsp.JspWriter",
			"fullDescription" : " An object that writes into the output stream"
		},
		{
			"name" : "page",
			"type" : "VARIABLE",
			"varType" : "java.lang.Object",
			"fullDescription" : "The instance of this page’s implementation class processing the current request <br />(When the scripting language is java then page is a synonym for this in the body of the page.)"
		},
		{
			"name" : "pageContext",
			"type" : "VARIABLE",
			"varType" : "javax.servlet.jsp.PageContext",
			"fullDescription" : "The context for the JSP page. Provides a single API to manage the various scoped attributes described in Sharing Information. This API is used extensively when implementing tag handlers."
		},
		{
			"name" : "request",
			"type" : "VARIABLE",
			"varType" : "javax.servlet.http.HttpServletRequest",
			"fullDescription" : "The request triggering the execution of the JSP page."
		},
		{
			"name" : "response",
			"type" : "VARIABLE",
			"varType" : "javax.servlet.http.HttpServletResponse",
			"fullDescription" : "The response to be returned to the client. Not typically used by JSP page authors."
		}, {
			"name" : "session",
			"type" : "VARIABLE",
			"varType" : "javax.servlet.http.HttpSession",
			"fullDescription" : "The session object for the client. This variable is only valid for HTTP protocols."
		} ]