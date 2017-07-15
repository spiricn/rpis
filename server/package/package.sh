#!/bin/bash


main() {
    set -e

    local name="rpis_1.0.0"
    local packageName=${name}.deb
    local rootDir=${name}
    local installRoot=${rootDir}/usr/local/bin/rpis
    local projectRoot=`pwd`/..

    # Create a binary
    pyinstaller \
        -F \
        --hidden-import=ssc \
        ${projectRoot}/rpis/app/App.py

    # Cleanup
    rm -fv ${packageName}
    rm -rfv ${rootDir}

    mkdir -p ${rootDir}
    mkdir -p ${installRoot}


    # Install main files
    cp -rv \
        ${projectRoot}/root \
        ${projectRoot}/default_config.py \
        ${installRoot}/

    # Install service script
    mkdir -p ${rootDir}/etc/init.d
    cp -rv \
        rpis.sh ${rootDir}/etc/init.d/rpis

    cp -v \
        `pwd`/dist/App \
        ${installRoot}/rpis

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
