#!/bin/bash

main() {
    set -e

    local install_dir=`pwd`/root

    echo "cleaning up .."
    
    rm -rfv $install_dir/node_modules

    mkdir -p $install_dir

    echo "installing .."

    npm install \
        angular-material md-color-picker \
        --prefix $install_dir

    echo "done"
}

main "$@"

