from _socket import AF_INET, SOCK_DGRAM, SOL_SOCKET, SO_REUSEADDR, SO_BROADCAST
import json
from socket import socket
import urllib.request

from ssc.http.HTTP import CODE_OK

class RestApi:
    def __init__(self, base):
        self._base = base

    def parsedGet(self, query):
        req = urllib.request.urlopen(self._base + '/' + query)

        if req.code != CODE_OK:
            return None

        res = json.loads(req.read().decode('ascii'))

        if not res['success']:
            return None

        return res

class Locator:
    BROADCAST_SOCKET = 13099
    RESPONSE_SOCKET = BROADCAST_SOCKET - 1
    BROADCAST_ADDR = '255.255.255.255'
    LOCATOR_REQUEST = "RPIS.REQUEST.ADDRESS";

    def __init__(self):
        pass

    def locate(self):
        senderSocket = socket(AF_INET, SOCK_DGRAM)
        senderSocket.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
        senderSocket.setsockopt(SOL_SOCKET, SO_BROADCAST, 1)
        senderSocket.sendto(self.LOCATOR_REQUEST.encode('ascii'), (self.BROADCAST_ADDR, self.BROADCAST_SOCKET))
        print('sent')

        receiverSocket = socket(AF_INET, SOCK_DGRAM)

        receiverSocket.bind(('', self.RESPONSE_SOCKET))

        data, server = receiverSocket.recvfrom(1024)

        print(data)


l = Locator()

l.locate()
