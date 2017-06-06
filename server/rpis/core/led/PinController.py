import logging

from rpis.Config import Config
from rpis.core.Color import Color


logger = logging.getLogger(__name__)

try:
    import pigpio
except ImportError:
    logger.warning('pigpio not supported on this platform')

PIN_RED = 18
PIN_GREEN = 23
PIN_BLUE = 24

MAX_PIN_BRIGHTNESS = 255

class PinController():
    def __init__(self):
        self._initialized = False

        self._color = Color()

    @property
    def initialized(self):
        return self._initialized

    def init(self):
        logger.debug('initializing ..')

        if self._initialized:
            logger.error('already initialized')
            return False

        if Config.rpiApi:
            self._pi = pigpio.pi()

        self._initialized = True

        clr = Color(0.5, 1.0, 1.0)

        self.setRGB(clr.r, clr.g, clr.b)

        logger.debug('initialized OK')

        return True

    def term(self):
        logger.debug('terminating ..')

        if not self._initialized:
            logger.error('not initialized')
            return False


        self.setRGB(0, 0, 0)

        if Config.rpiApi:
            self._pi.stop()

        self._initialized = False

        logger.debug('terminated OK')

        return True

    def getRGB(self):
        return Color(self._color.h, self._color.s, self._color.v)

    def setRGB(self, r=None, g=None, b=None):
        if not self._initialized:
            logger.error('not initialized')
            return False

        if r != None:
            self._setPinBrightness(PIN_RED, r)
        if g != None:
            self._setPinBrightness(PIN_GREEN, g)
        if b != None:
            self._setPinBrightness(PIN_BLUE, b)

        return True

    def _setPinBrightness(self, pin, val):
        if isinstance(val, float):
            if val < 0.0 or val > 1.0:
                logger.error('invalid pin value %f', val)
                return False

            val = val * MAX_PIN_BRIGHTNESS

        elif isinstance(val, int):
            if val < 0 or val > MAX_PIN_BRIGHTNESS:
                logger.error('invalid pin value %d', val)
                return False

        if Config.rpiApi:
            self._pi.set_PWM_dutycycle(pin, val)

        comp = {
                PIN_RED : Color.COMP_RED,
                PIN_GREEN : Color.COMP_GREEN,
                PIN_BLUE : Color.COMP_BLUE}[pin]

        self._color.setComp(comp, val / 255.0)

        return True
