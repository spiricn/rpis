from threading import Thread
import time


class ProcBase:
    def __init__(self):
        self._threaded = True
        self._running = True

    @property
    def running(self):
        return self._running

    @property
    def parent(self):
        return self._parent

    @property
    def pc(self):
        return self.parent.pc

    def startMainLoop(self, timeScale=1.0):
        self._loopStartTime = time.time()
        self._timeScale = timeScale
        lastTime = None

        while True:
            currTime = time.time()

            dt = currTime - (lastTime if lastTime else self._loopStartTime)

            if self.mainLoop(dt * timeScale):
                break

            lastTime = currTime

    def mainLoop(self, dt):
        raise NotImplemented()

    @property
    def loopDuration(self):
        return (time.time() - self._loopStartTime) * self._timeScale

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

    def stop(self):
        self._running = False

    def wait(self):
        if self._threaded:
            self._thread.join()
        return self.getResult()

