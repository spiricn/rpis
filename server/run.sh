#!/bin/bash

main() {
    set -e

    local root=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

    PYTHONPATH=$root:$PYTHONPATH \
        python3 $root/"$@"

    return $?
}

main "$@"

exit $?
