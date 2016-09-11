import colorsys

class Color:
    COMP_RED, COMP_GREEN, COMP_BLUE = range(3)

    def __init__(self, r=0.0, g=0.0, b=0.0):
        self._val = [0.0, 0.0, 0.0]

        self.setComp(self.COMP_RED, r)
        self.setComp(self.COMP_GREEN, g)
        self.setComp(self.COMP_BLUE, b)

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

        self._val[comp] = val

    @property
    def r(self):
        return self._val[self.COMP_RED]

    @property
    def g(self):
        return self._val[self.COMP_GREEN]

    @property
    def b(self):
        return self._val[self.COMP_BLUE]

    def toHSV(self):
        return colorsys.rgb_to_hsv(self.r, self.g, self.b)

    @staticmethod
    def fromHSV(h, s, v):
        r, g, b = colorsys.hsv_to_rgb(h, s, v)

        return Color(r, g, b)

    def __str__(self):
        return 'Color(%.2f, %.2f, %.2f)' % (self.r, self.g, self.b)

