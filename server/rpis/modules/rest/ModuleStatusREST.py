import logging

from ssc.http.HTTP import CODE_OK, MIME_TEXT, MIME_JSON

from rpis.modules.ModuleStatus import ModuleStatus


logger = logging.getLogger(__name__)

class ModuleStatusREST(ModuleStatus):
    def __init__(self):
        ModuleStatus.__init__(self)

    def getRestAPI(self):
        return (
                (
                    'status/temperature',
                    lambda: (CODE_OK, MIME_JSON, {'success' : True, 'res' : self.temperature})
                ),

                (
                    'status/upTime',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.temperature})
                ),

                (
                    'status/memoryUsage',
                    lambda: (CODE_OK, MIME_TEXT, {'success' : True, 'res' : self.temperature})
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
         )

