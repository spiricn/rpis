import logging
import sys

from rpis.Config import Config
from rpis.core.Module import Module
from rpis.core.Utils import shellCommand


logger = logging.getLogger(__name__)

class ModulePower(Module):
    def __init__(self, manager):
        Module.__init__(self, manager, 'Power')

    def reboot(self):
        logger.debug('rebooting now')

        if not Config.rpiApi:
            return

        res = shellCommand(['reboot'])
        if res.rc != 0:
            logger.error('reboot command failed: %s' % res.stderr)
            return False

        return True

    def shutdown(self):
        logger.debug('shutting down now')

        if not Config.rpiApi:
            return

        res = shellCommand(['poweroff'])
        if res.rc != 0:
            logger.error('poweroff command failed: %s' % res.stderr)
            return False

        return True

    def stop(self):
        logger.debug('stopping now')

        self.manager.engine.stop()


    def stopModule(self):
        pass

