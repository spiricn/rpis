import logging

from ssc.http.HTTP import CODE_OK, MIME_TEXT, CODE_BAD_REQUEST, MIME_JSON

from rpis.modules.ModuleStrip import ModuleStrip, Color


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
        color = kwargs.pop('color', None)

        if not color or len(color[0]) != 6:
            return (CODE_BAD_REQUEST, MIME_TEXT, 'Invalid color argument provided')

        color = color[0]

        try:
            red = int(color[0:2], 16)
            green = int(color[2:4], 16)
            blue = int(color[4:6], 16)
        except ValueError as e:
            logger.error('Invalid color argument provided: %r' % str(e))
            return (CODE_BAD_REQUEST, MIME_TEXT, 'Invalid color argument provided: %r' % str(e))

        if red < 0 or red > 255 or green < 0 or green > 255 or blue < 0 or blue > 255:
            logger.error('Invalid color argument provided')
            return (CODE_BAD_REQUEST, 'Invalid color argument provided')

        self.setColor(Color(red, green, blue))

        return (CODE_OK, MIME_JSON, self.getColor())
