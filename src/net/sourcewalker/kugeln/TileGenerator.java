package net.sourcewalker.kugeln;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.CompositeTransform;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.images.Composite.Anchor;
import com.google.appengine.api.images.ImagesService.OutputEncoding;

public class TileGenerator {

    public static final int TILE_SIZE = 512;

    public static String getPosition(int level, int column, int row) {
        return String.format("%d:%d:%d", level, column, row);
    }

    private static final OutputSettings outputSettings;

    static {
        outputSettings = new OutputSettings(OutputEncoding.JPEG);
        outputSettings.setQuality(90);
    }

    private static double binLog(double value) {
        return Math.log(value) / Math.log(2);
    }

    private static CompositeTransform getNormalTileTransform(int top,
            int bottom, int left, int right, int rawWidth, int rawHeight) {
        float topF = (float) top / rawHeight;
        float bottomF = (float) bottom / rawHeight;
        float leftF = (float) left / rawWidth;
        float rightF = (float) right / rawWidth;
        CompositeTransform normalTile = ImagesServiceFactory
                .makeCompositeTransform();
        normalTile.concatenate(ImagesServiceFactory.makeCrop(leftF, topF,
                rightF, bottomF));
        normalTile.concatenate(ImagesServiceFactory.makeResize(TILE_SIZE,
                TILE_SIZE));
        return normalTile;
    }

    private final Key panoKey;
    private final Image rawImage;
    private final List<PanoramaTile> tiles;
    private final ImagesService imageService;

    public TileGenerator(Key panoramaKey, Image rawImage) {
        this.panoKey = panoramaKey;
        this.rawImage = rawImage;
        this.tiles = new ArrayList<PanoramaTile>();
        this.imageService = ImagesServiceFactory.getImagesService();
    }

    public boolean run() {
        int inputWidth = rawImage.getWidth();
        int inputHeight = rawImage.getHeight();
        int xTiles = inputWidth / TILE_SIZE;
        int yTiles = inputHeight / TILE_SIZE;
        int levels = (int) binLog(Math.min(inputWidth, inputHeight) / TILE_SIZE) + 2;
        for (int i = levels - 1; i > -1; i--) {
            if (i == 0) {
                Transform thumbnailTransform = ImagesServiceFactory.makeResize(
                        TILE_SIZE, TILE_SIZE / 2);
                Image thumbnailTile = copyAndApply(rawImage, thumbnailTransform);
                List<Composite> composites = new ArrayList<Composite>();
                for (int y = 0; y < xTiles; y++) {
                    int y1 = y * TILE_SIZE / xTiles;
                    composites.add(ImagesServiceFactory.makeComposite(
                            thumbnailTile, 0, y1, 1.0f, Anchor.TOP_LEFT));

                }
                Image thumbnail = imageService.composite(composites, TILE_SIZE,
                        TILE_SIZE, 0, outputSettings);
                writeTile(0, 0, 0, thumbnail);
            } else {
                int xInput = inputWidth / xTiles;
                int yInput = inputHeight / yTiles;
                int count = 0;
                for (int y = 0; y < yTiles; y++) {
                    int top = y * yInput;
                    int bottom = top + yInput;
                    for (int x = 0; x < xTiles; x++) {
                        int left = x * xInput;
                        int right = left + xInput;
                        CompositeTransform transform = getNormalTileTransform(
                                top, bottom, left, right, inputWidth,
                                inputHeight);
                        Image tile = copyAndApply(rawImage, transform);
                        writeTile(i, x, y, tile);
                        count++;
                    }
                }
                xTiles /= 2;
                yTiles /= 2;
            }
        }
        return true;
    }

    private Image copyAndApply(Image input, Transform transform) {
        Image output = ImagesServiceFactory.makeImage(input.getImageData());
        imageService.applyTransform(transform, output, outputSettings);
        return output;
    }

    private void writeTile(int level, int column, int row, Image data) {
        PanoramaTile tile = new PanoramaTile();
        tile.setData(new Blob(data.getImageData()));
        tile.setPanoramaKey(panoKey);
        tile.setPosition(getPosition(level, column, row));
        tiles.add(tile);
    }

    public List<PanoramaTile> getTiles() {
        return tiles;
    }

}
