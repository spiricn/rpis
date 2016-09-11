from collections import namedtuple
import logging

from rpis.core.Module import Module
from rpis.core.led.StripController import StripController


logger = logging.getLogger(__name__)

Color = namedtuple('Color', 'red, green, blue')

class ModuleStrip(Module):
    def __init__(self):
        Module.__init__(self, 'Strip')

        self._currColor = Color(0, 0, 0)

        self._powered = False

        self._ctrl = StripController()
        self._ctrl.startController()

    def getColor(self):
        return self._currColor

    def powerOn(self):
        logger.debug('powering on')
        self._powered = True

        self._ctrl.init()

    def powerOff(self):
        logger.debug('powering off')
        self._powered = False

        self._ctrl.term()

    def setColor(self, color):
        logger.debug('setting new color %r' % str(color))
        self._currColor = color

        self._ctrl.setRGB(color.red, color.green, color.blue)

    @property
    def poweredOn(self):
        return self._powered
