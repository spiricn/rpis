import json
import logging
import socket
from threading import Thread
import time

import rpis


logger = logging.getLogger(__name__)

class BroadcastListener(Thread):
    RPIS_LOCATOR_REQUEST = "RPIS.REQUEST.ADDRESS";

    def __init__(self, address, broadcastPort, responsePort):
        Thread.__init__(self)

        self._broadcastPort = broadcastPort
        self._responsePort = responsePort
        self._address = address

    @staticmethod
    def _generateAnnounceJSON(self, address, port):
        return json.dumps({
            'name' : 'rpis',
            'version' : rpis.__version__,
            'address' : 'http://%s:%d' % (address, port),
            'port' : port
        }).encode('ascii')

    def run(self):
        logger.debug('Listening for broadcasts on port %d', self._broadcastPort)

        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.bind((self._address, self._broadcastPort))

        while True:
            try:
                data, server = s.recvfrom(1024)
            except ConnectionResetError:
                time.sleep(1)
                continue;

            address, port = server

            msg = data.decode('ascii')

            if msg == self.RPIS_LOCATOR_REQUEST:
                data = self._generateAnnounceJSON(address, port)

                responseSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

                responseSocket.settimeout(10)
                try:
                    responseSocket.connect((address, self._responsePort))
                    responseSocket.send(data)
                    logger.debug('Responded to %s:%d' % (address, self._responsePort))
                except Exception as e:
                    logger.error('Error sending response to (%s:%d): %r' % (address, self._responsePort, str(e)))

                responseSocket.close()

            else:
                logger.error('Unrecognized message: %r' % msg)
