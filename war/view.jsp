<?xml version="1.0" encoding="utf-8"?>
<%@ page language="java" contentType="application/vnd.google-earth.kml+xml; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="net.sourcewalker.kugeln.PersistenceManagerFactory"%>
<%@page import="javax.jdo.PersistenceManager"%>
<%@page import="net.sourcewalker.kugeln.Panorama"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="net.sourcewalker.kugeln.PanoramaStatus"%>
<%@page import="net.sourcewalker.kugeln.TileGenerator"%>
<%
String panoKey = request.getParameter("panoKey");
if (panoKey == null || panoKey.length() == 0) {
    response.sendError(400, "Panorama key required!");
} else {
    PersistenceManager pm = PersistenceManagerFactory.get();
    try {
        Panorama pano = pm.getObjectById(Panorama.class, KeyFactory.stringToKey(panoKey));
        if (pano.getStatus() != PanoramaStatus.OK) {
            response.sendError(409, "Panorama not ready!");
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=" + pano.getTitle() + ".kml");
%>
<kml xmlns="http://www.opengis.net/kml/2.2"
xmlns:gx="http://www.google.com/kml/ext/2.2"
xmlns:kml="http://www.opengis.net/kml/2.2"
xmlns:atom="http://www.w3.org/2005/Atom">
  <PhotoOverlay>
    <name><%=pano.getTitle()%></name>
    <Camera>
      <longitude><%=pano.getLongitude()%></longitude>
      <latitude><%=pano.getLatitude()%></latitude>
      <altitude>11.0</altitude>
      <heading><%=pano.getHeading()%></heading>
      <tilt>90.0</tilt>
      <roll>0.0</roll>
      <altitudeMode>relativeToGround</altitudeMode>
      <gx:altitudeMode>relativeToSeaFloor</gx:altitudeMode>
    </Camera>
    <Style>
      <IconStyle>
        <Icon>
          <href>:/camera_mode.png</href>
        </Icon>
      </IconStyle>
      <ListStyle>
        <listItemType>check</listItemType>
        <ItemIcon>
          <state>open closed error fetching0 fetching1
          fetching2</state>
          <href>
          http://maps.google.com/mapfiles/kml/shapes/camera-lv.png</href>
        </ItemIcon>
        <bgColor>00ffffff</bgColor>
        <maxSnippetLines>2</maxSnippetLines>
      </ListStyle>
    </Style>
    <Point>
      <altitudeMode>relativeToGround</altitudeMode>
      <gx:altitudeMode>relativeToGround</gx:altitudeMode>
      <coordinates><%=pano.getLongitude()%>,<%=pano.getLatitude()%>,11.0</coordinates>
    </Point>
    <ViewVolume>
      <leftFov>-180</leftFov>
      <rightFov>180</rightFov>
      <bottomFov>-90</bottomFov>
      <topFov>90</topFov>
      <near>10</near>
    </ViewVolume>
    <shape>sphere</shape>
    <Icon>
      <href>http://<%=request.getServerName()%>:<%=request.getServerPort()%>/pano/tile?panoKey=<%=panoKey%>&amp;l=$[level]&amp;x=$[x]&amp;y=$[y]</href>
    </Icon>
    <ImagePyramid>
      <tileSize><%=TileGenerator.TILE_SIZE%></tileSize>
      <maxWidth><%=pano.getRawWidth()%></maxWidth>
      <maxHeight><%=pano.getRawHeight()%></maxHeight>
      <gridOrigin>upperLeft</gridOrigin>
    </ImagePyramid>
  </PhotoOverlay>
</kml>
<%
        }
    } finally {
        pm.close();
    }
}
%>
