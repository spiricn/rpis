#!/bin/bash


main() {
    set -e

    local name="rpis_1.0.0"
    local packageName=${name}.deb
    local rootDir=${name}

    rm -fv ${packageName}

    rm -rfv ${rootDir}

    mkdir -p ${rootDir}

    mkdir -p ${rootDir}/usr/local/bin

    echo "print('hello world')" >> ${rootDir}/usr/local/bin/main.py

    mkdir -p ${rootDir}/DEBIAN
    cp control ${rootDir}/DEBIAN/control

    dpkg-deb --build ${rootDir}


    echo "###################"
    echo "created: `pwd`/${packageName}"

    return $?
}

main "$@"
