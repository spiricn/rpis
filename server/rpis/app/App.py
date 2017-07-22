import argparse
import os
import sys

from rpis.Config import Config
from rpis.core.Engine import Engine


def programMain():
    parser = argparse.ArgumentParser()

    parser.add_argument('-no_rpi_api', action='store_true', help='If set, server will not use platform specific APIs')
    parser.add_argument('-root', help='Server root directory')

    args = parser.parse_args()

    if args.no_rpi_api:
        Config.rpiApi = False

    serverRoot = os.path.dirname(os.path.abspath(sys.argv[0]))
    if args.root:
        serverRoot = args.root

    engine = Engine(serverRoot)

    engine.start()

    res = engine.wait()

    return res

if __name__ == '__main__':
    sys.exit(programMain())
