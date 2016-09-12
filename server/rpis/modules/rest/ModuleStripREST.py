import logging

from ssc.http.HTTP import CODE_OK, MIME_TEXT, CODE_BAD_REQUEST, MIME_JSON

from rpis.core.Color import Color
from rpis.modules.ModuleStrip import ModuleStrip


logger = logging.getLogger(__name__)


class ModuleStripREST(ModuleStrip):
    def __init__(self):

        ModuleStrip.__init__(self)

    def getRestAPI(self):
        return (
                (
                    'strip/color',
                    self.getColorRest
                ),

                (
                    'strip/color/set',
                    self.setColorREST
                ),

                (
                    'strip/powerOn',
                    lambda: (CODE_OK, MIME_TEXT, self.powerOn())
                ),

                (
                    'strip/powerOff',
                    lambda: (CODE_OK, MIME_TEXT, self.powerOff())
                ),


                (
                    'strip/poweredOn',
                    lambda: (CODE_OK, MIME_TEXT, self.poweredOn)
                ),
         )

    def getColorRest(self):
        clr = self.getColor()

        return (CODE_OK, MIME_TEXT, '#%.2x%.x2%.2x' % (clr.red, clr.green, clr.blue))

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

        self.setColor(Color.fromHSV(h, s, v))

        return (CODE_OK, MIME_JSON, self.getColor())
