import logging
import socket
from threading import Thread


logger = logging.getLogger(__name__)

class BroadcastListener(Thread):
    RPIS_LOCATOR_REQUEST = "RPIS.REQUEST.ADDRESS";
    RPIS_LOCATOR_RESPONSE = "RPIS.PROVIDE.ADDRESS";

    def __init__(self, address, port):
        Thread.__init__(self)

        self._address = address
        self._port = port


    def run(self):
        logger.debug('Listening for broadcasts on port %d', self._port)

        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.bind((self._address, self._port))

        while True:
            data, server = s.recvfrom(1024)
            address, port = server

            msg = data.decode('ascii')

            if msg == self.RPIS_LOCATOR_REQUEST:
                s.sendto((self.RPIS_LOCATOR_RESPONSE + 'http://' + self._address).encode('ascii'), (address, self._port))
            else:
                logger.error('Unrecognized message: %r' % msg)
