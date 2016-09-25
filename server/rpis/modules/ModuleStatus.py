from collections import namedtuple
import logging
import random
import socket
import sys

from rpis.core.Module import Module
from rpis.core.Utils import shellCommand


logger = logging.getLogger(__name__)

MemoryInfo = namedtuple('MemoryInfo', 'total, free')

class ModuleStatus(Module):
    def __init__(self, manager):
        Module.__init__(self, manager, 'Status')

    def getRestAPI(self):
        return (
                (
                    'status/temperature',
                    lambda: self.temperature
                ),

                (
                    'status/upTime',
                    lambda: self.upTime
                ),

                (
                    'status/memoryUsage',
                    lambda: self.memoryUsage
                ),

                (
                    'status/ipAddress',
                    lambda: self.ipAddress
                ),

                (
                    'status/cpuUsage',
                    lambda: self.cpuUsage
                ),

                (
                    'status/platform',
                    lambda: self.platform
                ),

                (
                    'status/devices',
                    lambda: self.devices
                ),
         )

    @property
    def temperature(self):
        if sys.platform == 'win32':
            return '%.1f' % (30 + random.random() * 10)

        res = shellCommand(['/opt/vc/bin/vcgencmd', 'measure_temp'])

        if res.rc != 0:
            return None

        return float(str(res.stdout).split('=')[1].split('\'')[0])

    @property
    def upTime(self):
        if sys.platform == 'win32':
            return 'n/a'

        res = shellCommand(['uptime', '-p'])

        if res.rc != 0:
            return None

        # decode bytes
        strRes = res.stdout.decode('ascii')

        # strip whitespace
        strRes = strRes.strip()

        # remove "up " prefix
        stRes = strRes[3:]

        return stRes

    @property
    def memoryUsage(self):
        if sys.platform == 'win32':
            return MemoryInfo(1, 1)

        res = shellCommand(['cat', '/proc/meminfo'])

        if res.rc != 0:
            return None

        totalMemKb = 0
        freeMemKb = 0

        strRes = res.stdout.decode('ascii')

        for line in strRes.splitlines():
            mem = int(line.split(' ')[-2])

            if 'MemFree' in line:
                freeMemKb = mem
            elif 'MemTotal' in line:
                totalMemKb = mem

        return MemoryInfo(totalMemKb, freeMemKb)

    @property
    def cpuUsage(self):
        if sys.platform == 'win32':
            return random.randrange(0, 100)

        res = shellCommand(['top', '-d', '0.5', '-b' , '-n2'])

        if res.rc != 0:
            return None

        res = res.stdout.decode('ascii')

        for line in reversed(res.splitlines()):
            if '%Cpu(s):' in line:
                return float(line.split('%Cpu(s):')[1].split('us')[0].strip())

    @property
    def ipAddress(self):
        return ([(s.connect(('8.8.8.8', 53)), s.getsockname()[0], s.close()) for s in [socket.socket(socket.AF_INET, socket.SOCK_DGRAM)]][0][1])

    @property
    def platform(self):
        if sys.platform == 'win32':
            return 'win32'

        res = shellCommand(['uname', '-a'])

        if res.rc != 0:
            return None

        return res.stdout.decode('ascii')

    @property
    def devices(self):
        if sys.platform == 'win32':
            return {'devices' : ['dummy1', 'dummy2']}

        res = shellCommand(['lsusb'])

        if res.rc != 0:
            return None

        devices = []
        for line in res.stdout.decode('ascii').splitlines():
            devices.append(line)

        return {'devices' : devices}

    def stopModule(self):
        pass
