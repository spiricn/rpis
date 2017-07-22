import logging
from multiprocessing import Lock
import pickle


logger = logging.getLogger(__name__)

class Settings:
    KEY_STRIP_LAST_COLOR = 'strip.last_color'
    KEY_STRIP_POWERED_ON = 'strip.powered_on'

    def __init__(self, path):
        self._path = path

        self._lock = Lock()

        with self._lock:
            self._load()

    @property
    def lastColor(self):
        return self._getProp(self.KEY_STRIP_LAST_COLOR)

    def setLastColor(self, lastColor):
        return self._setProp(self.KEY_STRIP_LAST_COLOR, lastColor)

    @property
    def stripPoweredOn(self):
        return self._getProp(self.KEY_STRIP_POWERED_ON, False)

    def setStripPoweredOn(self, poweredOn):
        return self._setProp(self.KEY_STRIP_POWERED_ON, poweredOn)

    def _getProp(self, key, default=None):
        with self._lock:
            return self._settings[key] if key in self._settings else default

    def _setProp(self, key, value):
        with self._lock:
            self._settings[key] = value
            self._save()

    def _save(self):
        with open(self._path, 'wb') as fileObj:
            pickle.dump(self._settings, fileObj)

    def _load(self):
        self._settings = {}

        try:
            with open(self._path, 'rb') as fileObj:
                self._settings = pickle.load(fileObj)
        except (FileNotFoundError, EOFError) as e:
            logger.error('error loading settings from %r: %s' % (self._path, str(e)))


