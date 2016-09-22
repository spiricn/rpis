import logging

from rpis.core.Module import Module
from rpis.core.led.StripController import StripController


logger = logging.getLogger(__name__)

class ModuleStrip(Module):
    def __init__(self):
        Module.__init__(self, 'Strip')

        self._powered = False

        self._ctrl = StripController()
        self._ctrl.startController()

    def getColor(self):
        return self._ctrl.pc.getRGB()

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

        self._ctrl.setRGB(color.r, color.g, color.b)

    @property
    def poweredOn(self):
        return self._powered
