#!/bin/bash


main() {
    ./run.sh rpis/core/led/App.py "$@"
    
    return 0
}

main "$@"

exit $?
