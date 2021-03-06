from logging import Formatter
import logging
import os
import tempfile
from time import sleep

from ssc.servlets.ServletContainer import ServletContainer
from ssc.utils.Utils import getLocalIp

from rpis.app.LoggingHandler import LoggingHandler
from rpis.core.AttributeDict import AttributeDict
from rpis.core.BroadcastListener import BroadcastListener
from rpis.core.ModuleManager import ModuleManager
from rpis.core.Settings import Settings
from rpis.modules.ModuleREST import ModuleREST
from rpis.modules.rest.ModulePowerREST import ModulePowerREST
from rpis.modules.rest.ModuleStatusREST import ModuleStatusREST
from rpis.modules.rest.ModuleStripREST import ModuleStripREST


logger = logging.getLogger(__name__)

class Engine:
    MODULES = (
        ModuleStatusREST,
        ModulePowerREST,
        ModuleStripREST,
        ModuleREST
    )

    def __init__(self, serverRoot):
        configFilePath = os.path.join(serverRoot, 'default_config.py')

        self._settings = Settings(os.path.join(serverRoot, '.settings'))

        rootLogger = logging.getLogger()
        rootLogger.setLevel(logging.NOTSET)

        self._logHandler = LoggingHandler(os.path.join(serverRoot, 'log.txt'))
        rootLogger.addHandler(self._logHandler)
        self._logHandler.setFormatter(Formatter('%(levelname)s/%(name)s: %(message)s'))

        logger.debug('server root: %r' % serverRoot)

        self._readConfig(configFilePath)

        self._address = getLocalIp()

        if not self._address:
            raise RuntimeError('Error getting local address %s' % str(self._address))

        self._server = ServletContainer('', self._config.port, os.path.join(serverRoot, 'root'),
                                        tempfile.mkdtemp()
        )

        self._broadcastListener = BroadcastListener(self._address, self._config.port, self._config.announceBroadcastPort, self._config.announceResponsePort)
        self._broadcastListener.start()

        self._server.addRestAPI()

        self._moduleManager = ModuleManager(self)
        for module in self.MODULES:
            self._moduleManager.registerModule(module)

        self._running = True

    @property
    def settings(self):
        return self._settings

    @property
    def logHandler(self):
        return self._logHandler

    def _readConfig(self, configFilePath):
        logger.debug('reading configuration from %r' % configFilePath)

        configDict = {}

        with open(configFilePath, 'r') as fileObj:
            configCode = fileObj.read()

            code = compile(configCode, configDict, 'exec')

            exec(code, configDict)

        self._config = AttributeDict(configDict)

    @property
    def config(self):
        return self._config

    @property
    def rest(self):
        return self._server.rest

    @property
    def server(self):
        return self._server

    @property
    def moduleManager(self):
        return self._moduleManager

    def start(self):
        self._server.env['REngine'] = self
        self._server.start()

    def stop(self):
        logger.debug('stopping broadcast listener')
        self._broadcastListener.stop()

        logger.debug('stopping server')
        self._server.stop()

        logger.debug('stopping modules')
        self._moduleManager.stop()

        logger.debug('engine stopped')

        self._running = False

    def wait(self):
        # TODO semaphores
        while self._running:
            sleep(1)

        # TODO Correct return code
        return 0
