<%@ page
    language="java"
    contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page
    import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta
    http-equiv="Content-Type"
    content="text/html; charset=ISO-8859-1">
<link
    type="stylesheet"
    href="kugeln.css" />
<title>Fotokugeln</title>
</head>
<body>
<h1>Fotokugeln</h1>
<h3>Upload new panorama</h3>
<form
    action="<%=BlobstoreServiceFactory.getBlobstoreService()
                    .createUploadUrl("/pano/raw")%>"
    method="post"
    enctype="multipart/form-data">
    <input type="hidden" name="returnUrl" value="<%= request.getParameter("ref") %>" />
<table>
    <tr>
        <th><label for="rawImage">Panorama file (JPG):</label></th>
        <td><input
            type="file"
            name="rawImage" /></td>
    </tr>
    <tr>
        <th><label for="title">Panorama title:</label></th>
        <td><input
            type="text"
            name="title" /></td>
    </tr>
    <tr>
        <th />
        <td><input
            type="submit"
            value="Submit" /></td>
    </tr>
</table>
</form>
</body>
</html>