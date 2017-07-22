import colorsys

from rpis.core.Utils import lerp


class Color:
    COMP_HUE, COMP_SATURATION, COMP_VALUE, \
    COMP_RED, COMP_GREEN, COMP_BLUE = range(6)

    def __init__(self, **kwargs):
        self._val = [0.0, 0.0, 0.0]

        hsv = kwargs.pop('hsv', None)
        if hsv:
            h, s, v = self._getColorComponents(hsv)

            self.setComp(self.COMP_HUE, h)
            self.setComp(self.COMP_SATURATION, s)
            self.setComp(self.COMP_VALUE, v)
            return

        rgb = kwargs.pop('rgb', None)
        if rgb:
            r, g, b = self._getColorComponents(rgb)

            self.setComp(self.COMP_RED, r)
            self.setComp(self.COMP_GREEN, g)
            self.setComp(self.COMP_BLUE, b)
            return

        if kwargs:
            raise RuntimeError('Invalid color arguments')

    def _getColorComponents(self, *args):
        '''
        Checks if the components are of valid type, and in range
        '''

        if len(args) == 3:
            c1, c2, c3 = args

            if isinstance(c1, int) and isinstance(c2, int) and isinstance(c3, int):
                # It's a RGB color (i.e. 0-255)
                for i in [c1, c2, c3]:
                    if i < 0 or i > 255:
                        raise RuntimeError('Invalid RGB color range: %d, %d, %d' % (c1, c2, c3))
                    else:
                        return c1, c2, c3

            elif isinstance(c1, float)  and isinstance(c2, float) and isinstance(c3, float):
                for i in [c1, c2, c3]:
                    if i < 0.0 or i > 1.0:
                        raise RuntimeError('Invalid RGB color range: %f, %f, %f' % (c1, c2, c3))
                    else:
                        return c1, c2, c3

        elif len(args) == 1:
            arg = args[0]

            # Single list, with 3 members
            if (isinstance(arg, list) or isinstance(arg, tuple)) and len(arg) == 3:
                c1, c2, c3 = arg
                return self._getColorComponents(c1, c2, c3)
            elif isinstance(arg, str):
                # String

                if arg.startswith('#'):
                    arg = arg[1:]

                    c1 = int(arg[0:2], 16)
                    c2 = int(arg[2:4], 16)
                    c3 = int(arg[4:6], 16)

                    return self._getColorComponents(c1, c2, c3)
                else:
                    raise RuntimeError('Invalid color string: %r' % arg)

        raise RuntimeError('Error parsing color components')

    def setComp(self, comp, val):
        if isinstance(val, int):
            maxValue = 255.0 if comp != self.COMP_HUE else 360.0

            if val < 0 or val > maxValue:
                raise RuntimeError('Invalid component %d value %d' % (comp, val))


            val = val / maxValue

        elif isinstance(val, float):
            if val < 0.0 or val > 1.0:
                raise RuntimeError('Invalid component %d value %f' % (comp, val))

        else:
            raise RuntimeError('Invalid component %d value %s' % (comp, str(val)))

        if comp in [self.COMP_RED, self.COMP_GREEN, self.COMP_BLUE]:
            rgb = self.toRGB()

            rgb[comp - self.COMP_RED] = val

            h, s, v = colorsys.rgb_to_hsv(rgb[0], rgb[1], rgb[2])

            self.setComp(self.COMP_HUE, h)
            self.setComp(self.COMP_SATURATION, s)
            self.setComp(self.COMP_VALUE, v)

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

    def toRGB(self):
        return list(colorsys.hsv_to_rgb(self._val[0], self._val[1], self._val[2]))

    @classmethod
    def lerp(cls, start, end, a):
        res = []

        for i in range(len(start._val)):
            startVal = start._val[i]
            endVal = end._val[i]

            distance = abs(startVal - endVal)

            if i == cls.COMP_HUE and distance > 0.5:
                if startVal > 0.5:
                    val = lerp(startVal, 1.0 + endVal, a)

                else:
                    val = lerp(startVal, -1.0 + endVal, a)

                if val > 1.0:
                    val -= 1.0
                elif val < 0.0:
                    val += 1.0

            else:
                val = lerp(startVal, endVal, a)


            res.append(val)

        h, s, v = res

        return Color(hsv=(h, s, v))

    def __str__(self):
        return 'Color(%.2f, %.2f, %.2f)' % (self.h, self.s, self.v)

