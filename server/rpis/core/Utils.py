from collections import namedtuple
import os
import subprocess
import tarfile

CommandRes = namedtuple('CommandRes', 'stdout, stderr, rc')

def lerp(val1, val2, ammount):
    return val1 + (val2 - val1) * ammount

def getFileTimestamp(path):
    return os.stat(path).st_mtime

def shellCommand(command):
    if isinstance(command, str):
        command = command.split(' ')

    pipe = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

    res = pipe.communicate()

    return CommandRes(res[0], res[1], pipe.returncode)

def makeDirTree(path):
    if os.path.exists(path) and os.path.isdir(path):
        return

    os.makedirs(path)

def archiveDirectory(archivePath, dirPath):
    t = tarfile.open(archivePath, mode='w')

    t.add(dirPath, arcname=os.path.basename(dirPath))
    t.close()

def humanReadableSize(numBytes):
    units = ['B', 'KB', 'MB', 'GB', 'TB', 'PB']

    size = numBytes
    while True:
        if size < 1024 or len(units) == 1:
            fmt = ''

            if size - int(size) == 0:
                fmt += '%d'
            else:
                fmt += '%0.2f'

            fmt += ' %s'

            return fmt % (size, units[0])

        size /= 1024.0
        units.pop(0)

def humanReadableTime(milliseconds):
    elapsedMs = milliseconds % 1000
    elapsedSec = milliseconds / 1000
    elapsedMin = (elapsedSec / 60) % 60
    elapsedHour = (elapsedSec / 3600)
    elapsedSec = elapsedSec % 60

    res = ''

    if int(elapsedHour) > 0:
        res += '%dh ' % elapsedHour

    if int(elapsedMin) > 0 or int(elapsedHour) > 0:
        res += '%dm ' % elapsedMin

    res += '%.02fs' % (elapsedSec + elapsedMs / 1000.0)

    return res

