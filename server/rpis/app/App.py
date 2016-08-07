import argparse
import logging

from rpis.core.Engine import Engine


def programMain():
    # Initialize logging
    logging.basicConfig(level=logging.DEBUG,
            format='%(levelname)s/%(name)s: %(message)s')

    parser = argparse.ArgumentParser()

    parser.add_argument('--port', help='HTTP server port')

    args = parser.parse_args()

    port = int(args.port) if args.port else 80

    engine = Engine(port)

    return engine.start()

if __name__ == '__main__':
    exit(programMain())
