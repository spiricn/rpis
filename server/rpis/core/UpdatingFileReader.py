import logging
from queue import Queue, Empty
from threading import Thread
from time import sleep


logger = logging.getLogger(__name__)

class UpdatingFileReader:
    def __init__(self, path):
        self._path = path
        self._queue = Queue()

    def getLine(self, timeout):
        return self._queue.get()

    def start(self):
        self._running = True
        self._thread = Thread(target=self._mainLoop)
        self._thread.start()

    def _mainLoop(self):
        with open(self._path, 'rb') as fileObj:
            while 1:
                where = fileObj.tell()
                line = fileObj.readline()
                if not line:
                    sleep(1)
                    fileObj.seek(where)
                    continue


                line = line.decode('ascii')
                line.rstrip()

                self._queue.put(line)

    def stop(self):
        self._running = False
        self._thread.join()



