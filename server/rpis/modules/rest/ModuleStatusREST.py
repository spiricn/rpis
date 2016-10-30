import logging
from time import sleep

from ssc.http.HTTP import CODE_OK, MIME_TEXT, MIME_JSON, MIME_HTML
from ssc.servlets.RestServlet import  RestHandler

from rpis.core.UpdatingFileReader import UpdatingFileReader
from rpis.modules.ModuleStatus import ModuleStatus


logger = logging.getLogger(__name__)

class ModuleStatusREST(ModuleStatus):
    def __init__(self, manager):
        ModuleStatus.__init__(self, manager)

    def getRestAPI(self):
        return (
                RestHandler(
                    'status/temperature',
                    lambda: (CODE_OK, MIME_JSON, {'success' : True, 'res' : self.temperature})
                ),

                RestHandler(
                    'status/upTime',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.upTime})
                ),

                RestHandler(
                    'status/memoryUsage',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.memoryUsage._asdict()})
                ),

                RestHandler(
                    'status/ipAddress',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.ipAddress})
                ),

                RestHandler(
                    'status/cpuUsage',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.cpuUsage})
                 ),

                RestHandler(
                    'status/platform',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.platform})
                 ),

                RestHandler(
                    'status/devices',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.devices})
                ),

                RestHandler(
                    'status/ping',
                    lambda: (CODE_OK, MIME_JSON, {'success' : True })
                 ),

                RestHandler(
                    'status/log',
                    self.logRest,
                    '',
                    True
                )
         )

    def generator(self):
        fr = UpdatingFileReader(self.manager.engine.logHandler.path)
        fr.start()

        while True:
            yield fr.getLine(1.0)

    def logRest(self):
        return CODE_OK, MIME_HTML, self.generator()
