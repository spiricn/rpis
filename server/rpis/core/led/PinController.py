import logging

from rpis.Config import Config
from rpis.core.Color import Color


logger = logging.getLogger(__name__)

try:
    import pigpio
except ImportError:
    logger.warning('pigpio not supported on this platform')

MAX_PIN_BRIGHTNESS = 255

class PinController():
    def __init__(self, redPin, greenPin, bluePin):
        self._initialized = False

        self._color = Color()
        self._redPin = redPin
        self._greenPin = greenPin
        self._bluePin = bluePin

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

        self.setRGB(self._color.r, self._color.g, self._color.b)

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
            self._setPinBrightness(self._redPin, r)
        if g != None:
            self._setPinBrightness(self._greenPin, g)
        if b != None:
            self._setPinBrightness(self._bluePin, b)

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
                self._redPin : Color.COMP_RED,
                self._greenPin : Color.COMP_GREEN,
                self._bluePin : Color.COMP_BLUE}[pin]

        self._color.setComp(comp, val / 255.0)

        return True
