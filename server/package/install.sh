#!/bin/bash

main() {
    sudo /etc/init.d/rpis stop

    ./package.sh

    sudo dpkg -i rpis_*.*.*.deb
}

main "$@"

