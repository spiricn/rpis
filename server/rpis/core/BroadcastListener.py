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
    def _generateAnnounceJSON(address, port):
        address = 'http://%s:%d' % (address, port)

        return json.dumps({
            'name' : 'rpis',
            'version' : rpis.__version__,
            'address' : address,
            'rest' : address + '/rest'
        }).encode('ascii')

    def stop(self):
        self._running = False
        self.join()

    def run(self):
        self._running = True

        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.settimeout(2.0)
        s.bind(('', self._broadcastPort))

        logger.debug('Listening for broadcasts on port %s:%d', self._address, self._broadcastPort)

        while self._running:
            try:
                data, server = s.recvfrom(1024)
            except socket.timeout:
                continue
            except ConnectionResetError:
                logger.error("connection error while receiving broadcast")
                time.sleep(1)
                continue;

            address, port = server

            msg = data.decode('ascii')

            if msg == self.RPIS_LOCATOR_REQUEST:
                data = self._generateAnnounceJSON(self._address, 80)

                responseSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
                responseSocket.settimeout(2.0)

                try:
                    responseSocket.sendto(data, (address, self._responsePort))
                    logger.debug('Responded to %s:%d' % (address, self._responsePort))
                except Exception as e:
                    logger.error('Error sending response to (%s:%d): %r' % (address, self._responsePort, str(e)))

                responseSocket.close()

            else:
                logger.error('Unrecognized message: %r' % msg)
