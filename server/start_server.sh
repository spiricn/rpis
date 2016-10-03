#!/bin/bash

main () {
    set -e

    local root=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

    $root/run.sh rpis/app/App.py "$@"

    return $?
}

main "$@"

exit $?


