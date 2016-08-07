#!/bin/bash

main() {
    set -e

    local ssc_path=`pwd`/../ssc
    PYTHONPATH=$ssc_path:$PYTHONPATH \
        python3 rpis/app/App.py
}

main "$@"
