#!/bin/bash

main() {
    rsync -avHe ssh `pwd` pi@192.168.0.11:/home/pi/dev/rpis
}

main "$@"

