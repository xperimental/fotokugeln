import sys

def _readByte(file):
    return ord(file.read(1))

class ReaderState():
    END = 'end'
    BEFORE_START = 'start'
    BEFORE_TAG = 'tag'
    TAG_SIZE = 'size'
    TAG_DATA = 'tag_data'
    DATA = 'scan_data'

class TagConstants(object):
    TAG_START = 0xFF
    
    TYPES = dict([
        (0xD8, 'SOI'),	# Start Of Image
        (0xE0, 'APP0'),	# JFIF tag
        (0xC0, 'SOF0'),	# Baseline DCT
        (0xC1, 'SOF1'),	# Extended sequential DCT
        (0xC2, 'SOF2'),	# Progressive DCT
        (0xC3, 'SOF3'),	# Lossless (sequential)
        (0xC5, 'SOF5'),	# Differential sequential DCT
        (0xC6, 'SOF6'),	# Differential progressive DCT
        (0xC7, 'SOF7'),	# Differential lossless (sequential)
        (0xC8, 'JPG'),	# JPEG extensions
        (0xC9, 'SOF9'),	# Extended sequential DCT
        (0xCA, 'SOF10'),	# Progressive DCT
        (0xCB, 'SOF11'),	# Lossless (sequential)
        (0xCD, 'SOF13'),	# Differential sequential DCT
        (0xCE, 'SOF14'),	# Differential progressive DCT
        (0xCF, 'SOF15'),	# Differential lossless (sequential)
        (0xC4, 'DHT'),	# Definition der Huffman-Tabellen
        (0xCC, 'DAC'),	# Definition der arithmetischen Codierung
        (0xDB, 'DQT'),	# Definition der Quantisierungstabellen
        (0xE1, 'APP1'),	# EXIF-Daten
        (0xE2, 'APP2'), # Allgemeine Zeiger
        (0xE3, 'APP3'), # Allgemeine Zeiger
        (0xE4, 'APP4'), # Allgemeine Zeiger
        (0xE5, 'APP5'), # Allgemeine Zeiger
        (0xE6, 'APP6'), # Allgemeine Zeiger
        (0xE7, 'APP7'), # Allgemeine Zeiger
        (0xE8, 'APP8'), # Allgemeine Zeiger
        (0xE9, 'APP9'), # Allgemeine Zeiger
        (0xEA, 'APP10'), # Allgemeine Zeiger
        (0xEB, 'APP11'), # Allgemeine Zeiger
        (0xEC, 'APP12'), # Allgemeine Zeiger
        (0xED, 'APP13'), # Allgemeine Zeiger
        (0xEE, 'APP14'),	# Oft fuer Copyright-Eintraege
        (0xFE, 'COM'),	# Kommentare
        (0xDA, 'SOS'),	# Start of Scan
        (0xD9, 'EOI')	# End of Image
    ])

    RESTART_CODES = range(0xD0, 0xD7 + 1)

def _getType(type):
    if type in TagConstants.TYPES:
        return TagConstants.TYPES[type]
    else:
        return 'unknown'

class Tag():
    def __init__(self, type, size, data):
        self.type = type
        self.size = size
        self.data = data
        if len(self.data) != size - 2:
            print "warn: size != data.length"

    def __str__(self):
        size1 = int(self.size / 256)
        size2 = int(self.size % 256)
        if _getType(self.type) == 'SOS':
            return "\xFF\xDA%s" % self.data
        else:
            return "\xFF%s%s%s%s" % (chr(self.type), chr(size1), chr(size2), self.data)

class CropReader():
    tags = []
    def __init__(self, file):
        self.fileName = file
        self._readTags()

    def _readTags(self):
        input = open(self.fileName, 'rb')
        state = ReaderState.BEFORE_START
        tagType = 0
        tagSize = 0
        while state != ReaderState.END:
            print "state: %s tagType: %X (%s) tagSize: %d" % (state, tagType, _getType(tagType), tagSize)
            byte = _readByte(input)
            if state == ReaderState.BEFORE_START:
                if byte == TagConstants.TAG_START:
                    byte = _readByte(input)
                    if _getType(byte) == 'SOI':
                        state = ReaderState.BEFORE_TAG
                    else:
                        state = ReaderState.END
            elif state == ReaderState.BEFORE_TAG:
                if byte == TagConstants.TAG_START:
                    tagType = _readByte(input)
                    if _getType(tagType) == 'SOS':
                        state = ReaderState.DATA
                    elif _getType(tagType) == 'EOI':
                        state = ReaderState.END
                    else:
                        state = ReaderState.TAG_SIZE
                else:
                    state = ReaderState.END
            elif state == ReaderState.TAG_SIZE:
                byte2 = _readByte(input)
                tagSize = byte * 256 + byte2
                state = ReaderState.TAG_DATA
            elif state == ReaderState.TAG_DATA:
                data = chr(byte) + input.read(tagSize - 3)
                self.tags.append(Tag(tagType, tagSize, data))
                state = ReaderState.BEFORE_TAG
            elif state == ReaderState.DATA:
                data = '' + chr(byte)
                scan_end = False
                while scan_end == False:
                    byte = _readByte(input)
                    if byte == TagConstants.TAG_START:
                        byte2 = _readByte(input)
                        if byte2 == 0:
                            data += chr(0xFF) + chr(0x00)
                        elif byte2 in TagConstants.RESTART_CODES:
                            data += chr(0xFF) + chr(byte2)
                        else:
                            self.tags.append(Tag(tagType, len(data), data))
                            input.seek(-2, 1)
                            scan_end = True
                            state = ReaderState.BEFORE_TAG
                    else:
                        data += chr(byte)
            else:
                state = ReaderState.END

        input.close()

if __name__ == '__main__':
    if len(sys.argv) < 3:
        print "python jpeg.py [in] [out]"
    else:
        file = sys.argv[1]
        cropReader = CropReader(file)
        total = 0
        for tag in cropReader.tags:
            print "tag start: %s size: %d" % (_getType(tag.type), tag.size)
            total += len(str(tag))

        output = open(sys.argv[2], 'wb')
        output.write('\xFF\xD8')
        for tag in cropReader.tags:
            output.write(str(tag))
        output.write('\xFF\xD9')
        output.close()
