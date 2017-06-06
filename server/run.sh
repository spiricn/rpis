#!/bin/bash

main() {
    set -e

    local root=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

    local ssc_path=$root/../../ssc
    PYTHONPATH=$ssc_path:$root:$PYTHONPATH \
        python3 $root/"$@"

    return $?
}

main "$@"

exit $?
