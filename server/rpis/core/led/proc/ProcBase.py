from threading import Thread
import time


class ProcBase:
    def __init__(self):
        self._threaded = True

    @property
    def parent(self):
        return self._parent

    @property
    def pc(self):
        return self.parent.pc

    def startMainLoop(self):
        self._loopStartTime = time.time()

        lastTime = None

        while True:
            currTime = time.time()

            dt = currTime - (lastTime if lastTime else self._loopStartTime)

            if self.mainLoop(dt):
                break

            lastTime = currTime

    def mainLoop(self, dt):
        raise NotImplemented()

    @property
    def loopDuration(self):
        return time.time() - self._loopStartTime

    def onProcStart(self):
        raise NotImplementedError()

    def getResult(self):
        raise NotImplementedError()

    def start(self, parent):
        self._parent = parent

        if self._threaded:
            self._thread = Thread(target=self.onProcStart)
            self._thread.start()
        else:
            self.onProcStart()

    def wait(self):
        if self._threaded:
            self._thread.join()
        return self.getResult()
