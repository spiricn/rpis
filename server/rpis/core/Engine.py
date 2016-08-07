import logging
import os
import tempfile

from rpis.core.BroadcastListener import BroadcastListener
from rpis.core.ModuleManager import ModuleManager
from rpis.modules.ModuleREST import ModuleREST
from rpis.modules.rest.ModulePowerREST import ModulePowerREST
from rpis.modules.rest.ModuleStatusREST import ModuleStatusREST
from rpis.modules.rest.ModuleStripREST import ModuleStripREST
from ssc.servlets.ServletContainer import ServletContainer
from ssc.utils.Utils import getLocalIp


logger = logging.getLogger(__name__)

class Engine:
    MODULES = (
        ModuleStatusREST,
        ModulePowerREST,
        ModuleStripREST,
        ModuleREST
    )

    BROADCAST_PORT = 13099

    def __init__(self, port):
        self._address = getLocalIp()

        if not self._address:
            raise RuntimeError('Error getting local address %s' % str(self._address))

        self._server = ServletContainer(self._address, port, os.path.join(os.path.dirname(__file__), '../../root'),
                                        tempfile.mkdtemp()
        )

        self._broadcastListener = BroadcastListener(self._address, self.BROADCAST_PORT)
        self._broadcastListener.start()

        self._server.addRestAPI()

        self._moduleManager = ModuleManager(self)
        for module in self.MODULES:
            self._moduleManager.registerModule(module)

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

        return 0

