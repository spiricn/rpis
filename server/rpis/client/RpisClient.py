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
    def __init__(self):
        pass

    def locate(self):
        senderSocket = socket(AF_INET, SOCK_DGRAM)
        senderSocket.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
        senderSocket.setsockopt(SOL_SOCKET, SO_BROADCAST, 1)
        senderSocket.sendto('RPIS.REQUEST.ADDRESS'.encode('ascii'), ('192.255.255.255', 13099))
        print('sent')

        receiverSocket = socket(AF_INET, SOCK_DGRAM)
        receiverSocket.bind(('', 13098))

        data, server = receiverSocket.recvfrom(1024)

        print(data)


l = Locator()

l.locate()
