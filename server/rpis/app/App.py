import argparse
import os
import sys

from rpis.Config import Config
from rpis.core.Engine import Engine


DEFAULT_CONFIG_FILE_PATH = os.path.abspath(os.path.join(os.path.dirname(__file__), '../../default_config.py'))

def programMain():
    parser = argparse.ArgumentParser()

    parser.add_argument('--configFile', help='Configuration file')
    parser.add_argument('-no_rpi_api', action='store_true', help='If set, server will not use platform specific APIs')

    args = parser.parse_args()

    if args.no_rpi_api:
        Config.rpiApi = False

    configFilePath = args.configFile if args.configFile else DEFAULT_CONFIG_FILE_PATH

    engine = Engine(configFilePath)

    engine.start()

    res = engine.wait()

    return res

if __name__ == '__main__':
    sys.exit(programMain())
