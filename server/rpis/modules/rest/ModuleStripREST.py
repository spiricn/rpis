import logging

from ssc.http.HTTP import CODE_OK, MIME_TEXT, CODE_BAD_REQUEST, MIME_JSON

from rpis.core.Color import Color
from rpis.modules.ModuleStrip import ModuleStrip


logger = logging.getLogger(__name__)


class ModuleStripREST(ModuleStrip):
    def __init__(self, manager):
        ModuleStrip.__init__(self, manager)

    def getRestAPI(self):
        return (
                (
                    'strip/color',
                    self.getColorRest
                ),

                (
                    'strip/cycle',
                    self.cycleRest
                ),

                (
                    'strip/stopProcess',
                    self.stopProcesRest
                ),

                (
                    'strip/color/set',
                    self.setColorREST
                ),

                (
                    'strip/powerOn',
                    lambda: (CODE_OK, MIME_JSON, {'success': self.powerOn()})
                ),

                (
                    'strip/powerOff',
                    lambda: (CODE_OK, MIME_JSON, {'success': self.powerOff()})
                ),


                (
                    'strip/poweredOn',
                    self.poweredOnRest
                ),

                (
                 'strip/runPrefab',
                 self.runPrefabRest

                 )
         )

    def runPrefabRest(self, **kwargs):
        prefabId = kwargs.pop('id', None)
        if not prefabId:
            return (CODE_BAD_REQUEST, MIME_TEXT, "Invalid prefab name");
        prefabId = prefabId[0]

        return (CODE_OK, MIME_JSON, {'success' : self.runPrefab(int(prefabId))})

    def stopProcesRest(self):
        return (CODE_OK, MIME_JSON, {'success' : self.stopProcess() })

    def cycleRest(self):
        return (CODE_OK, MIME_JSON, {'success' : self.cycle() })

    def poweredOnRest(self):
        return (CODE_OK, MIME_JSON, {'success' : True, 'res': self.poweredOn})

    def getColorRest(self):
        return (CODE_OK, MIME_JSON, {'success' : True, 'res' :self._colorToJson(self.getColor())})

    @staticmethod
    def _colorToJson(color):
        return {'hue' : color.h, 'saturation': color.s, 'value' : color.v}

    def setColorREST(self, **kwargs):
        h = kwargs.pop('h', None)
        s = kwargs.pop('s', None)
        v = kwargs.pop('v', None)

        if h == None or s == None or v == None:
            return (CODE_BAD_REQUEST, MIME_TEXT, 'Invalid color argument provided')
        try:
            h = float(h[0])
            s = float(s[0])
            v = float(v[0])
        except ValueError as e:
            logger.error('Invalid color argument provided: %r' % str(e))
            return (CODE_BAD_REQUEST, MIME_TEXT, 'Invalid color argument provided: %r' % str(e))

        self.setColor(Color(h, s, v))

        return (CODE_OK, MIME_JSON, {'success' : True})
