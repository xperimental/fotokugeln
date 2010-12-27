import math
from google.appengine.api import images
from data.panorama import PanoramaTile

TILE_SIZE = 512

def binLog(value):
    return Math.log(value) / Math.log(2);

class TileGenerator():
    panoKey = None
    rawWidth = None
    rawHeight = None
    rawImage = None
    tiles = []

    def __init__(self, panoKey, rawWidth, rawHeight, rawImage):
        self.panoKey = panoKey
        self.rawWidth = rawWidth
        self.rawHeight = rawHeight
        self.rawImage = rawImage
        self.generateTiles()

    def generateTiles(self):
        inputWidth = self.rawWidth
        inputHeight = self.rawHeight
        xTiles = inputWidth / TILE_SIZE
        yTiles = inputHeight / TILE_SIZE
        levels = math.log(min([inputWidth, inputHeight]) / TILE_SIZE, 2) + 2
        for i in range(int(levels - 1), -1, -1):
            if not i:
                mapResize = images.Image(image_data=self.rawImage)
                mapResize.resize(width=TILE_SIZE, height=TILE_SIZE / 2)
                mapTile = mapResize.execute_transforms(output_encoding=images.JPEG)
                inputs = [
                    (mapTile, 0, 0, 1.0, images.TOP_LEFT),
                    (mapTile, 0, TILE_SIZE / 2, 1.0, images.TOP_LEFT)
                ]
                overview = images.composite(inputs, TILE_SIZE, TILE_SIZE, output_encoding=images.JPEG)
                self.createTile(0, 0, 0, overview)
            else:
                xInput = inputWidth / xTiles
                yInput = inputHeight / yTiles
                for y in range(0, yTiles - 1):
                    top = (y * yInput) / float(inputHeight)
                    bottom = (y * yInput + yInput) / float(inputHeight)
                    for x in range(0, xTiles - 1):
                        left = (x * xInput) / float(inputWidth)
                        right = (x * xInput + xInput) / float(inputWidth)

                        cropImage = images.Image(image_data=self.rawImage)
                        cropImage.crop(left, top, right, bottom)
                        cropImage.resize(TILE_SIZE, TILE_SIZE)
                        tileData = cropImage.execute_transforms(output_encoding=images.JPEG)
                        self.createTile(i, x, y, tileData)
                xTiles /= 2
                yTiles /= 2

    def createTile(self, level, column, row, tileData):
        position = "%s:%s:%s" % (level, column, row)
        tile = PanoramaTile(panoramaKey=self.panoKey, position=position, data=tileData)
        self.tiles.append(tile)
