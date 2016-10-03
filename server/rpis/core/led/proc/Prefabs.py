from rpis.core.Color import Color
from rpis.core.led.proc.ColorSetProc import ColorSetProc, ColorKeyFrame
from collections import namedtuple

Prefab = namedtuple('Prefab', 'name, spawn, id')

class Prefabs:
    def __init__(self):
        self._id = 0
        self._prefabs = {}

        self.add('Slow Blue Loop', lambda: ColorSetProc(
           [ColorKeyFrame(Color(0.4, 1, 1), 0),
            ColorKeyFrame(Color(0.7, 1, 1), 3),
            ColorKeyFrame(Color(0.4, 1, 1), 6),
            ColorKeyFrame(Color(0.7, 1, 0.5), 14),
            ColorKeyFrame(Color(0.4, 1, 0.2), 20),
            ColorKeyFrame(Color(0.8, 1, 1), 25),
            ColorKeyFrame(Color(0.4, 1, 1), 30),
            ], True))

        self.add('Flash', lambda: ColorSetProc(
           [ColorKeyFrame(Color(0, 0, 0), 0),
            ColorKeyFrame(Color(0, 0, 1), 0.2),
            ColorKeyFrame(Color(0, 0, 0), 4),
            ], False))

        self.add('Cycle', lambda: ColorSetProc(
           [ColorKeyFrame(Color(0, 1, 1), 0),
            ColorKeyFrame(Color(1, 1, 1), 5),
            ], True, timeScale=0.1))

    def add(self, name, spawn):
        self._id += 1

        self._prefabs[self._id] = Prefab(name, spawn, self._id)

    def get(self, id):
        return self._prefabs[id]

    @property
    def prefabs(self):
        return list(self._prefabs.values())
