import colorsys

from rpis.core.Utils import lerp
from builtins import staticmethod


class Color:
    COMP_HUE, COMP_SATURATION, COMP_VALUE, \
    COMP_RED, COMP_GREEN, COMP_BLUE = range(6)

    def __init__(self, h=0.0, s=0.0, v=0.0):
        self._val = [0.0, 0.0, 0.0]

        self.setComp(self.COMP_HUE, float(h))
        self.setComp(self.COMP_SATURATION, float(s))
        self.setComp(self.COMP_VALUE, float(v))

    def setComp(self, comp, val):
        if isinstance(val, int):
            if val < 0 or val > 255:
                raise RuntimeError('Invalid component %d value %d' % (comp, val))

            val = val / 255.0

        elif isinstance(val, float):
            if val < 0.0 or val > 1.0:
                raise RuntimeError('Invalid component %d value %f' % (comp, val))

        else:
            raise RuntimeError('Invalid component %d value %s' % (comp, str(val)))

        if comp in [self.COMP_RED, self.COMP_GREEN, self.COMP_BLUE]:
            rgb = self.toRGB()

            rgb[comp - self.COMP_RED] = val

            r, g, b = rgb

            self._val = Color.fromRGB(r, g, b)._val

        else:
            self._val[comp] = val

    @property
    def h(self):
        return self._val[self.COMP_HUE]

    @property
    def s(self):
        return self._val[self.COMP_SATURATION]

    @property
    def v(self):
        return self._val[self.COMP_VALUE]

    @property
    def r(self):
        return self.toRGB()[0]

    @property
    def g(self):
        return self.toRGB()[1]

    @property
    def b(self):
        return self.toRGB()[2]

    @staticmethod
    def fromRGB(r, g, b):
        h, s, v = colorsys.rgb_to_hsv(r, g, b)

        return Color(h, s, v)

    def toRGB(self):
        return list(colorsys.hsv_to_rgb(self._val[0], self._val[1], self._val[2]))

    @staticmethod
    def lerp(start, end, a):
        res = []

        for i in range(len(start._val)):
            res.append(lerp(start._val[i], end._val[i], a))

        h, s, v = res

        return Color(h, s, v)

    def __str__(self):
        return 'Color(%.2f, %.2f, %.2f)' % (self.h, self.s, self.v)

