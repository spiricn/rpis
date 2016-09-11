import cmd
import logging
import sys

from rpis.core.Color import Color
from rpis.core.led.StripController import StripController
from rpis.core.led.proc.ColorSetProc import ColorSetProc
from rpis.core.led.proc.CycleProc import CycleProc


logger = logging.getLogger(__name__)

def tokenize(arg):
    return [i for i in arg.split(' ') if i]

class App(cmd.Cmd):
    def __init__(self):
        cmd.Cmd.__init__(self)

        self._ctrl = StripController()
        self._ctrl.startController()
        self._ctrl.init(True)

    def do_on(self, arg):
        self._ctrl.setRGB(255, 255, 255)

    def do_off(self, arg):
        self._ctrl.setRGB(0, 0, 0)

    def do_set(self, arg):
        args = tokenize(arg)

        duration = 0.5

        try:
            r, g, b = [int(i) for i in args[:3]]

            if len(args) == 4:
                duration = float(args[3])

        except Exception as e:
            logger.error(str(e))
            return False

        self._ctrl.runProcess(ColorSetProc(Color(r, g, b), duration))

    def do_get(self, arg):
        logger.debug(str(self._ctrl.pc.getRGB()))

    def do_cycle(self, arg):
        args = tokenize(arg)
        try:
            duration, cycles = [int(i) for i in args]
        except Exception as e:
            logger.error(str(e))
            return False

        self._ctrl.runProcess(CycleProc(duration, cycles))

    def do_exit(self, arg):
        return self._shutdown()

    def do_stop(self, arg):
        return self._shutdown()

    def do_EOF(self, arg):
        return self._shutdown()

    def _shutdown(self):
        self._ctrl.term(True)
        self._ctrl.stopController()

        return True

def main():
    logging.basicConfig(level=logging.DEBUG,
            format='%(levelname)s/%(name)s: %(message)s')

    logger.debug('starting app..')

    App().cmdloop()

    logger.debug('stopped')

    return 0

if __name__ == '__main__':
    sys.exit(main())
