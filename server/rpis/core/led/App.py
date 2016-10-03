import cmd
import logging
import sys
import time

from rpis.core.Color import Color
from rpis.core.led.StripController import StripController
from rpis.core.led.proc.ColorSetProc import ColorSetProc, ColorKeyFrame
from rpis.core.led.proc.CycleProc import CycleProc
from rpis.core.led.proc.Prefabs import prefabs


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
        self._ctrl.runProcess(ColorSetProc(
                               [ColorKeyFrame(self._ctrl.pc.getRGB(), 0),
                                ColorKeyFrame(Color(255, 255, 255), 1.5)]
                              ))

    def do_off(self, arg):
        self._ctrl.runProcess(ColorSetProc(
                               [ColorKeyFrame(self._ctrl.pc.getRGB(), 0),
                                ColorKeyFrame(Color(0, 0, 0), 1.5)]
                              ))

    def do_printPrefabs(self, arg):
        for index, name in enumerate(prefabs.keys()):
            logger.debug('%d. %s' % (index, name))

    def do_prefab(self, arg):
        try:
            prefab = int(arg)
        except Exception as e:
            logger.error(str(e))
            return

        key = list(prefabs.keys())[prefab]

        process = prefabs[key]()

        logger.debug('Running: %r' % key)

        self._ctrl.runProcess(process)

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

    def do_flash(self, arg):
        self._ctrl.runProcess(ColorSetProc(
                       [ColorKeyFrame(Color(0, 0, 0), 0),
                        ColorKeyFrame(Color(255, 255, 255), 0.1),
                        ColorKeyFrame(Color(0, 0, 0), 0.2)],
                      ))

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
        if not self._ctrl.currProc:
            logger.error('no active process')
            return

        self._ctrl.currProc.stop()

    def do_EOF(self, arg):
        return self._shutdown()

    def _shutdown(self):
        self._ctrl.term(True)
        self._ctrl.stopController()

        return True

    def do_test(self, arg):
        colors = [
                  (255, 0, 0, 'red'),
                  (0, 255, 0, 'green'),
                  (0, 0, 255, 'blue'),
                  (255, 255, 0, 'yellow'),
                  (255, 0, 255, 'magenta'),
                  (0, 255, 255, 'cyan'),
                  (255, 255, 255, 'white'),
                  (0, 0, 0, 'black'),
                  ]

        for r, g, b, name in colors:
            logger.debug(name)
            self._ctrl.setRGB(r, g, b, True)
            time.sleep(1)

def main():
    logging.basicConfig(level=logging.DEBUG,
            format='%(levelname)s/%(name)s: %(message)s')

    logger.debug('starting app..')

    App().cmdloop()

    logger.debug('stopped')

    return 0

if __name__ == '__main__':
    sys.exit(main())
