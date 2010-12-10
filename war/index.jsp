<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@page import="com.google.appengine.api.users.User"%><html>
<head>
<meta
    http-equiv="Content-Type"
    content="text/html; charset=ISO-8859-1">
<title>Fotokugeln</title>
</head>
<body>
<h1>Fotokugeln</h1>
<h3>Giving your panoramas a spherical home in Google Earth&trade;</h3>
<%
    UserService userService = UserServiceFactory.getUserService();
    User currentUser = userService.getCurrentUser();
    if (currentUser == null) {
%>
<p>Please <a href="<%=userService.createLoginURL("/dashboard.html") %>">login</a> to create or modify panoramas.</p>
<%
    } else {
        response.sendRedirect("/dashboard.html");
    }
%>
</body>
</html>