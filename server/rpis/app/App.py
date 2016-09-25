import argparse
import logging
import sys

from rpis.core.Engine import Engine


def programMain():
    # Initialize logging
    logging.basicConfig(level=logging.DEBUG,
            format='%(levelname)s/%(name)s: %(message)s')

    logger = logging.getLogger(__name__)

    parser = argparse.ArgumentParser()

    parser.add_argument('--port', help='HTTP server port')

    args = parser.parse_args()

    port = int(args.port) if args.port else 13097

    engine = Engine(port)

    engine.start()

    res = engine.wait()

    logger.debug('app exited with %d' % res)

    return res

if __name__ == '__main__':
    sys.exit(programMain())
