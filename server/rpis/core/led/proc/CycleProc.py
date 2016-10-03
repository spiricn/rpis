import colorsys

from rpis.core.led.proc.ProcBase import ProcBase


class CycleProc(ProcBase):
    def __init__(self, cycleTime, numCycles=1, brightness=1.0):
        ProcBase.__init__(self)

        self._cycleTime = cycleTime
        self._numCycles = numCycles
        self._brightness = brightness

    def getResult(self):
        return True

    def onProcStart(self):
        self._elapsed = 0

        self.startMainLoop()

    def mainLoop(self, dt):
        if self._numCycles == 0:
            return True

        s = 1
        v = self._brightness

        self._elapsed += dt

        h = (self._elapsed / self._cycleTime)

        if h > 1:
            self._numCycles -= 1
            self._elapsed = 0
            return

        r, g, b = colorsys.hsv_to_rgb(h, s, v)

        if not self.pc.setRGB(r, g, b):
            return True

        done = not self._running

        return done
