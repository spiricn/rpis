from collections import namedtuple

from rpis.core.Color import Color
from rpis.core.led.proc.ColorSetProc import ColorSetProc, ColorKeyFrame


Prefab = namedtuple('Prefab', 'name, spawn, id')

class Prefabs:
    def __init__(self):
        self._id = 0
        self._prefabs = {}

    def fromManifest(self, manifest):
        for name, desc in manifest.items():
            duration = float(desc['duration'])
            colors = desc['colors']
            currTime = 0

            keyframes = []
            for color in colors:
                keyframes.append(ColorKeyFrame(Color(rgb=color), currTime))

                currTime += duration
            self.add(name, lambda: ColorSetProc(keyframes, True))

    def add(self, name, spawn):
        self._id += 1

        self._prefabs[self._id] = Prefab(name, spawn, self._id)

    def get(self, prefabId):
        return self._prefabs[prefabId]

    @property
    def prefabs(self):
        return list(self._prefabs.values())
