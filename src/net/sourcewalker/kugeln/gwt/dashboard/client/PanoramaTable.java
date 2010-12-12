package net.sourcewalker.kugeln.gwt.dashboard.client;

import net.sourcewalker.kugeln.gwt.dashboard.shared.PanoramaEntry;
import net.sourcewalker.kugeln.gwt.dashboard.shared.PanoramaService;
import net.sourcewalker.kugeln.gwt.dashboard.shared.PanoramaServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PanoramaTable implements EntryPoint {

    private FlexTable panoramaTable = new FlexTable();
    private Button uploadButton = new Button("Upload new panorama");
    private PanoramaServiceAsync panoramaService = GWT
            .create(PanoramaService.class);

    @Override
    public void onModuleLoad() {
        VerticalPanel panoramaPanel = new VerticalPanel();

        panoramaTable.setText(0, 0, "Thumbnail");
        panoramaTable.setText(0, 1, "Name");
        panoramaTable.setText(0, 2, "Status");
        panoramaTable.setText(0, 3, "Edit");
        panoramaTable.setText(0, 4, "Remove");
        panoramaPanel.add(panoramaTable);

        uploadButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Location.replace("/upload.jsp?ref="
                        + URL.encode(Location.getHref()));
            }
        });
        panoramaPanel.add(uploadButton);

        RootPanel.get("panoramaTable").add(panoramaPanel);

        updatePanoramas();
    }

    private void updatePanoramas() {
        panoramaService.getUserPanoramas(new AsyncCallback<PanoramaEntry[]>() {

            @Override
            public void onSuccess(PanoramaEntry[] result) {
                for (int i = 0; i < result.length; i++) {
                    Button editButton = new Button("Edit");
                    Button removeButton = new Button("-");
                    removeButton.addClickHandler(new RemoveHandler(result[i]
                            .getKey()));

                    if (result[i].hasThumbnail()) {
                        panoramaTable.setWidget(i + 1, 0,
                                new Image("/pano/thumbnail?panoKey="
                                        + result[i].getKey()));
                    }
                    panoramaTable.setText(i + 1, 1, result[i].getName());
                    panoramaTable.setText(i + 1, 2, result[i].getStatus());
                    panoramaTable.setWidget(i + 1, 3, editButton);
                    panoramaTable.setWidget(i + 1, 4, removeButton);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });
    }

    public void removePanorama(String key) {
        panoramaService.removePanorama(key, new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    updatePanoramas();
                } else {
                    Window.alert("Can't delete panorama.");
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error deleting panorama. Try again...");
            }
        });
    }

    private class RemoveHandler implements ClickHandler {

        private String key;

        public RemoveHandler(String key) {
            this.key = key;
        }

        @Override
        public void onClick(ClickEvent event) {
            removePanorama(key);
        }

    }

}
