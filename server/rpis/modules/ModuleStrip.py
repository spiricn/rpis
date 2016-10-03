import logging

from rpis.core.Module import Module
from rpis.core.led.StripController import StripController
from rpis.core.led.proc.CycleProc import CycleProc


logger = logging.getLogger(__name__)

class ModuleStrip(Module):
    def __init__(self, manager):
        Module.__init__(self, manager, 'Strip')

        self._powered = False

        self._ctrl = StripController()
        self._ctrl.startController()

    def getColor(self):
        return self._ctrl.pc.getRGB()

    def cycle(self):
        if self._ctrl.currProc:
            logger.error('process running')
            return False

        self._ctrl.runProcess(CycleProc(10, 1))

    def stopProcess(self):
        if not self._ctrl.currProc:
            logger.error('process not running')
            return False

        self._ctrl.currProc.stop()

        return True

    def powerOn(self):
        if self._ctrl.currProc:
            logger.error('process running')
            return False

        logger.debug('powering on')
        self._powered = True

        self._ctrl.init()

        return True

    def powerOff(self):
        if self._ctrl.currProc:
            logger.error('process running')
            return False

        logger.debug('powering off')
        self._powered = False

        self._ctrl.term(blocking=True)

        return True

    def setColor(self, color):
        if self._ctrl.currProc:
            logger.error('process running')
            return False

        logger.debug('setting new color %r' % str(color))
        self._currColor = color

        self._ctrl.setRGB(color.r, color.g, color.b)

    @property
    def poweredOn(self):
        return self._powered

    def stopModule(self):
        if self._ctrl.currProc:
            logger.error('process running')
            return False

        if self.poweredOn:
            self.powerOff()
        self._ctrl.stopController()
