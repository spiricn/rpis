import logging
from multiprocessing import Lock
import pickle


logger = logging.getLogger(__name__)

class Settings:
    KEY_STRIP_LAST_COLOR = 'strip.last_color'

    def __init__(self, path):
        self._path = path

        self._lock = Lock()

        with self._lock:
            self._load()

    @property
    def lastColor(self):
        with self._lock:
            return self._settings[self.KEY_STRIP_LAST_COLOR] if self.KEY_STRIP_LAST_COLOR in self._settings else None

    def setLastColor(self, lastColor):
        with self._lock:
            self._settings[self.KEY_STRIP_LAST_COLOR] = lastColor
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


