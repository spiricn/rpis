import logging
from threading import Timer

from ssc.http.HTTP import CODE_OK, MIME_TEXT

from rpis.modules.ModulePower import ModulePower


logger = logging.getLogger(__name__)

class ModulePowerREST(ModulePower):
    COMMAND_DELAY_SEC = 2

    def __init__(self):
        ModulePower.__init__(self)

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
         )


    def rebootREST(self):
        logger.debug('rebooting in %d seconds' % self.COMMAND_DELAY_SEC)

        Timer(self.COMMAND_DELAY_SEC, self.reboot).start()

        return (CODE_OK, MIME_TEXT, self.COMMAND_DELAY_SEC)

    def shutdownREST(self):
        logger.debug('shutting down in %d seconds' % self.COMMAND_DELAY_SEC)

        Timer(self.COMMAND_DELAY_SEC, self.shutdown).start()

        return (CODE_OK, MIME_TEXT, self.COMMAND_DELAY_SEC)

