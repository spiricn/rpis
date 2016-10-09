import logging
from threading import Timer

from ssc.http.HTTP import CODE_OK, MIME_TEXT, MIME_JSON, CODE_BAD_REQUEST

from rpis.modules.ModulePower import ModulePower


logger = logging.getLogger(__name__)

class ModulePowerREST(ModulePower):
    DEFAULT_DELAY_MS = 2000

    def __init__(self, manager):
        ModulePower.__init__(self, manager)

    def getRestAPI(self):
        return (
                (
                    'power/shutdown',
                    self.shutdownREST
                ),

                (
                    'power/reboot',
                    self.rebootREST
                ),

                (
                 'power/stop',
                 self.stopREST
                 )
         )

    def stopREST(self, **kwargs):
        delayMs = kwargs.pop('delayMs', None)
        if delayMs == None:
            delayMs = self.DEFAULT_DELAY_MS
        else:
            try:
                delayMs = int(delayMs[0])
            except TypeError:
                return (CODE_BAD_REQUEST, MIME_TEXT, 'Invalid delay argument provided')

        logger.debug('stopping in %d ns' % delayMs)

        Timer(delayMs / 1000.0, self.stop).start()

        return (CODE_OK, MIME_JSON, {'success' : True, 'res' : delayMs})

    def rebootREST(self, **kwargs):
        delayMs = kwargs.pop('delayMs', None)
        if delayMs == None:
            delayMs = self.DEFAULT_DELAY_MS
        else:
            try:
                delayMs = int(delayMs[0])
            except TypeError:
                return (CODE_BAD_REQUEST, MIME_TEXT, 'Invalid delay argument provided')

        logger.debug('rebooting in %d ns' % delayMs)

        Timer(delayMs / 1000.0, self.reboot).start()

        return (CODE_OK, MIME_JSON, {'success' : True, 'res' : delayMs})

    def shutdownREST(self, **kwargs):
        delayMs = kwargs.pop('delayMs', None)
        if delayMs == None:
            delayMs = self.DEFAULT_DELAY_MS
        else:
            try:
                delayMs = int(delayMs[0])
            except TypeError:
                return (CODE_BAD_REQUEST, MIME_TEXT, 'Invalid delay argument provided')

        logger.debug('shutting down in %d ms' % delayMs)

        Timer(delayMs / 1000.0, self.shutdown).start()

        return (CODE_OK, MIME_JSON, {'success' : True, 'res' : delayMs})

