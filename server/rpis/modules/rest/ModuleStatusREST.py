import logging

from ssc.http.HTTP import CODE_OK, MIME_TEXT, MIME_JSON

from rpis.modules.ModuleStatus import ModuleStatus
from ssc.servlets.RestServlet import RestServlet


logger = logging.getLogger(__name__)

class ModuleStatusREST(ModuleStatus):
    def __init__(self, manager):
        ModuleStatus.__init__(self, manager)

    def getRestAPI(self):
        return (
                (
                    'status/temperature',
                    lambda: (CODE_OK, MIME_JSON, {'success' : True, 'res' : self.temperature})
                ),

                (
                    'status/upTime',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.upTime})
                ),

                (
                    'status/memoryUsage',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.memoryUsage._asdict()})
                ),

                (
                    'status/ipAddress',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.ipAddress})
                ),

                (
                    'status/cpuUsage',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.cpuUsage})
                 ),

                (
                    'status/platform',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.platform})
                 ),

                (
                    'status/devices',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.devices})
                ),

                (
                    'status/ping',
                    lambda: (CODE_OK, MIME_JSON, {'success' : True })
                 )
         )

