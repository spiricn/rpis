#!/bin/bash


main() {
    set -e

    local name="rpis_1.0.0"
    local packageName=${name}.deb
    local rootDir=${name}
    local installRoot=${rootDir}/usr/local/bin/rpis
    local projectRoot=`pwd`/..

    # Cleanup
    rm -fv ${packageName}
    rm -rfv ${rootDir}

    mkdir -p ${rootDir}
    mkdir -p ${installRoot}


    # Install
    cp -rv \
        ${projectRoot}/root \
        ${projectRoot}/default_config.py \
        ${projectRoot}/run.sh \
        ${projectRoot}/start_server.sh \
        ${projectRoot}/rpis \
        ${installRoot}/

    # Create the manifest
    mkdir -p ${rootDir}/DEBIAN
    cp control ${rootDir}/DEBIAN/control

    # Create the package
    dpkg-deb --build ${rootDir}


    echo "###################"
    echo "created: `pwd`/${packageName}"

    return $?
}

main "$@"
