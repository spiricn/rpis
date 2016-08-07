import logging

from ssc.http.HTTP import CODE_OK, MIME_TEXT

from rpis.modules.ModuleStatus import ModuleStatus


logger = logging.getLogger(__name__)

class ModuleStatusREST(ModuleStatus):
    def __init__(self):
        ModuleStatus.__init__(self)

    def getRestAPI(self):
        return (
                (
                    'status/temperature',
                    lambda: (CODE_OK, MIME_TEXT, self.temperature)
                ),

                (
                    'status/upTime',
                    lambda: (CODE_OK, MIME_TEXT, self.upTime)
                ),

                (
                    'status/memoryUsage',
                    lambda: (CODE_OK, MIME_TEXT, self.memoryUsage)
                ),

                (
                    'status/ipAddress',
                    lambda: (CODE_OK, MIME_TEXT, self.ipAddress)
                ),

                (
                    'status/cpuUsage',
                    lambda: (CODE_OK, MIME_TEXT, self.cpuUsage)
                 ),

                (
                    'status/platform',
                    lambda: (CODE_OK, MIME_TEXT, self.platform)
                 ),

                (
                    'status/devices',
                    lambda: (CODE_OK, MIME_TEXT, self.devices)
                ),
         )

