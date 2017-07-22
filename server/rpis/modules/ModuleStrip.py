import logging

from rpis.core.Module import Module
from rpis.core.led.StripController import StripController
from rpis.core.led.proc.CycleProc import CycleProc
from rpis.core.led.proc.Prefabs import Prefabs


logger = logging.getLogger(__name__)

class ModuleStrip(Module):
    def __init__(self, manager):
        Module.__init__(self, manager, 'Strip')

        self._firstPowerOn = True
        self._powered = False

        self._ctrl = StripController(
            self.manager.engine.config.ledStripPinout['red'],
            self.manager.engine.config.ledStripPinout['green'],
            self.manager.engine.config.ledStripPinout['blue'])

        self._ctrl.startController()
        self._prefabs = Prefabs()

        self._prefabs.fromManifest(self.manager.engine.config.prefabs)

        if self.manager.engine.settings.stripPoweredOn:
            self.powerOn()

    @property
    def prefabs(self):
        return self._prefabs.prefabs

    def getColor(self):
        return self._ctrl.pc.getRGB()

    def cycle(self):
        if self._ctrl.currProc:
            logger.error('process running')
            return False

        self._ctrl.runProcess(CycleProc(10, 1))

        return True

    def runPrefab(self, prefabId):
        if self._ctrl.currProc:
            logger.error('process running')
            return False

        proc = self._prefabs.get(prefabId).spawn()

        self._ctrl.runProcess(proc)

        return True

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

        logger.debug('last color: ' + str(self.manager.engine.settings.lastColor))
        if self._firstPowerOn and self.manager.engine.settings.lastColor:
            self.setColor(self.manager.engine.settings.lastColor)

        self._firstPowerOn = False

        self.manager.engine.settings.setStripPoweredOn(True)

        return True

    def powerOff(self):
        if self._ctrl.currProc:
            logger.error('process running')
            return False

        logger.debug('powering off')
        self._powered = False

        self._ctrl.term(blocking=True)

        self.manager.engine.settings.setStripPoweredOn(False)

        return True

    def setColor(self, color):
        if self._ctrl.currProc:
            logger.error('process running')
            return False

        logger.debug('setting new color %r' % str(color))
        self._currColor = color

        self._ctrl.setRGB(color.r, color.g, color.b)

        self.manager.engine.settings.setLastColor(color)

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
