<%@ page
    language="java"
    contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page
    import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%><html>
<head>
<meta
    http-equiv="Content-Type"
    content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form
    action="<%=BlobstoreServiceFactory.getBlobstoreService()
                    .createUploadUrl("/pano/raw")%>"
    method="post"
    enctype="multipart/form-data"><input
    type="file"
    name="rawImage"> <input
    type="submit"
    value="Submit"></form>
</body>
</html>