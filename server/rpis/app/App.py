import argparse
import os
import sys

from rpis.core.Engine import Engine

DEFAULT_CONFIG_FILE_PATH = os.path.abspath(os.path.join(os.path.dirname(__file__), '../../Config.py'))

def programMain():
    parser = argparse.ArgumentParser()

    parser.add_argument('--configFile', help='Configuration file')

    args = parser.parse_args()

    configFilePath = args.configFile if args.configFile else DEFAULT_CONFIG_FILE_PATH

    engine = Engine(configFilePath)

    engine.start()

    res = engine.wait()

    return res

if __name__ == '__main__':
    sys.exit(programMain())
