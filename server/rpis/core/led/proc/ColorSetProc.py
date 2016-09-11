from rpis.core.Color import Color
from rpis.core.Utils import lerp
from rpis.core.led.proc.ProcBase import ProcBase


class ColorSetProc(ProcBase):
    def __init__(self, endColor, duration, startColor=None):
        ProcBase.__init__(self)

        self._startColor = startColor
        self._endColor = endColor
        self._duration = duration

    def getResult(self):
        return True

    def onProcStart(self):
        if not self._startColor:
            self._startColor = self.pc.getRGB()

        self.startMainLoop()

    def mainLoop(self, dt):
        a = self.loopDuration / self._duration

        done = False
        if a >= 1.0:
            a = 1.0
            done = True

        startHSV = self._startColor.toHSV()
        endHSV = self._endColor.toHSV()


        currHsv = []
        for i in range(len(startHSV)):
            currHsv.append(lerp(startHSV[i], endHSV[i], a))

        h, s, v = currHsv

        currColor = Color.fromHSV(h, s, v)

        self.pc.setRGB(currColor.r, currColor.g, currColor.b)

        return done
