Fotokugeln
==========

This is a small project for Google App Engine which processes panoramic pictures for display in Google Earth. The project was originally started using the Java SDK (which is still available in the java branch) but later switched to the Python SDK.

Known problems:
-----------

 * Picture processing is done in one single step although it could possibly be parallelized to better use the App Engine infrastructure.
 * Large pictures are handled inefficiently (whole file is read into memory), which limits the maximum image size.
 * Position of panorama "bubble" can not be set by user.
 * No editing of uploaded panoramas possible.

