from collections import namedtuple
import logging
from queue import Queue, Empty
from threading import Thread, Semaphore

from rpis.core.led.PinController import PinController


logger = logging.getLogger(__name__)

STATE_ON, \
STATE_OFF, \
STATE_INVALID \
 = range(3)

Command = namedtuple('Command', 'fnc, cb')

class StripController():
    QUEUE_TIMEOUT_SEC = 1.0

    def __init__(self):
        self._queue = Queue()
        self._running = False
        self._state = STATE_INVALID
        self._pc = PinController()
        self._currProc = None

    @property
    def currProc(self):
        return self._currProc

    @property
    def pc(self):
        return self._pc

    def init(self, blocking=False):
        return self._queueCommand(self._pc.init, blocking)

    def term(self, blocking=False):
        return self._queueCommand(self._pc.term, blocking)

    def setRGB(self, r, g, b, blocking=False):
        return self._queueCommand(lambda: self._pc.setRGB(r, g, b), blocking)

    def runProcess(self, proc):
        return self._queueCommand(lambda: self._runProc(proc), False)

    def stopController(self):
        if not self._running:
            raise RuntimeError('already stopped')

        if self._pc.initialized:
            raise RuntimeError('controller initialized')

        self._running = False
        self._thread.join()

    def startController(self):
        if self._running:
            raise RuntimeError('already started')

        self._running = True

        self._thread = Thread(target=self.run, name='StripControllerLoop')
        self._thread.start()

    ############################################################################

    def run(self):
        while self._running:
            try:
                item = self._queue.get(timeout=self.QUEUE_TIMEOUT_SEC)
            except Empty:
                continue

            if self._running:
                res = item.fnc()

                if item.cb:
                    item.cb(res)

    def _runProc(self, proc):
        self._currProc = proc
        proc.start(self)
        proc.wait()
        self._currProc = None

    def _setState(self, state):
        logger.debug('Changing state: %d -> %d', self._state, state)
        self._started = state

    def _queueCommand(self, cmd, blocking):
        if not self._running:
            raise RuntimeError('controller not started')

        def cb(args, res):
            args['res'] = res
            args['sema'].release()

        sema = Semaphore(0)

        args = {}
        args['sema'] = sema

        self._queue.put(Command(cmd, None if not blocking else lambda res: cb(args, res)))

        if blocking:
            sema.acquire()

            return args['res']

        return None
