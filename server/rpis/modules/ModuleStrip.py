from collections import namedtuple
import logging

from rpis.core.Module import Module


logger = logging.getLogger(__name__)

Color = namedtuple('Color', 'red, green, blue')

class ModuleStrip(Module):
    def __init__(self):
        Module.__init__(self, 'Strip')

        self._currColor = Color(3, 155, 229)

        self._powered = False

    def getColor(self):
        return self._currColor

    def powerOn(self):
        logger.debug('powering on')
        self._powered = True

        # TODO

    def powerOff(self):
        logger.debug('powering off')
        self._powered = False

        # TODO

    def setColor(self, color):
        logger.debug('setting new color %r' % str(color))
        self._currColor = color

        # TODO

    @property
    def poweredOn(self):
        return self._powered
