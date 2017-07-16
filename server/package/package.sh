#!/bin/bash


main() {
    set -e
    local projectRoot=`pwd`/..

    # Acquire the server version
    local version=`PYTHONPATH=${projectRoot}:${PYTHONPATH} python3 -c "import rpis; print(rpis.__version__)"`

    local name="rpis_${version}"
    local packageName=${name}.deb
    local rootDir=${name}
    local installRoot=${rootDir}/usr/local/bin/rpis

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

    # Generate control file
    echo "Package: rpis" >> ${rootDir}/DEBIAN/control
    echo "Version: ${version}" >> ${rootDir}/DEBIAN/control
    echo "Section: base" >> ${rootDir}/DEBIAN/control
    echo "Priority: optional" >> ${rootDir}/DEBIAN/control
    echo "Architecture: armhf" >> ${rootDir}/DEBIAN/control
    echo "Maintainer: Nikola Spiric <nikola.spiric.ns@gmail.com>" >> ${rootDir}/DEBIAN/control
    echo "Description: RPIS Server" >> ${rootDir}/DEBIAN/control
    echo "Depends: pigpio" >> ${rootDir}/DEBIAN/control

    cp -v postinst ${rootDir}/DEBIAN/postinst
    chmod -v 0555 ${rootDir}/DEBIAN/postinst

    # Create the package
    dpkg-deb --build ${rootDir}

    echo "###################"
    echo "created: `pwd`/${packageName}"

    return $?
}

main "$@"
