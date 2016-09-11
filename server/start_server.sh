#!/bin/bash

main () {
    set -e

    ./run.sh rpis/app/App.py "$@"

    return $
}

main "$@"

exit $?


