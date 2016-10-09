import json
import logging
import socket
from threading import Thread
import time
from uuid import getnode as get_mac
import rpis

import hashlib
logger = logging.getLogger(__name__)

class BroadcastListener(Thread):
    RPIS_LOCATOR_REQUEST = "RPIS.REQUEST.ADDRESS";

    def __init__(self, address, serverPort, broadcastPort, responsePort):
        Thread.__init__(self)

        self._broadcastPort = broadcastPort
        self._responsePort = responsePort
        self._address = address
        self._serverPort = serverPort

        uidStr = 'rpis:uid:' + str(get_mac()) + ':' + self._address + ':' + str(self._serverPort)

        m = hashlib.md5()

        m.update(uidStr.encode('ascii'))

        self._uid = m.hexdigest()

        logger.debug('generated uid: %r ( %r )' % (uidStr, self._uid))

        self._announceJson = self.generateAnnounceJSON()

    def generateAnnounceJSON(self):
        address = 'http://%s:%d' % (self._address, self._serverPort)

        return json.dumps({
            'name' : 'rpis',
            'version' : rpis.__version__,
            'address' : address,
            'rest' : address + '/rest',
            'uid' : self._uid
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
                responseSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
                responseSocket.settimeout(2.0)

                try:
                    responseSocket.sendto(self._announceJson, (address, self._responsePort))
                    logger.debug('Responded to %s:%d' % (address, self._responsePort))
                except Exception as e:
                    logger.error('Error sending response to (%s:%d): %r' % (address, self._responsePort, str(e)))

                responseSocket.close()

            else:
                logger.error('Unrecognized message: %r' % msg)
