from rpis.core.Color import Color
from rpis.core.led.proc.ProcBase import ProcBase
from collections import namedtuple


ColorKeyFrame = namedtuple('KeyFrame', 'color, time')

class ColorSetProc(ProcBase):
    def __init__(self, keyframes):
        ProcBase.__init__(self)

        self._keyframes = keyframes


    def getResult(self):
        return True

    def onProcStart(self):
        self.startMainLoop()

    def _findFrames(self, currTime):
        for index, frame in enumerate(self._keyframes):
            nextFrame = None if index == len(self._keyframes) - 1 else self._keyframes[index + 1]

            if nextFrame:
                if currTime >= frame.time and currTime < nextFrame.time:
                    dur = nextFrame.time - frame.time

                    pos = (currTime - frame.time) / dur

                    return (frame.color, nextFrame.color, pos)

            else:
                return (frame.color, None, 1.0)

    def mainLoop(self, dt):
        done = False

        start, end, a = self._findFrames(self.loopDuration)

        if start and end:
            currColor = Color.lerp(start, end, a)
        else:
            done = True
            currColor = start

        self.pc.setRGB(currColor.r, currColor.g, currColor.b)

        return done if self.running else True
