# RPIS
A Python3 / web-based Raspberry Pi Zero server used for LED strip lighting control.

## Dependencies

* SSC ( https://github.com/spiricn/ssc )
  Used as a HTTP server backend library.

* pigpiod ( http://abyz.co.uk/rpi/pigpio/download.html )
 Native service used for PWM control of LED the strip.

* PyInstaller ( http://www.pyinstaller.org/ )
 Used to package RPIS insto a binary application.

### Building RPIS

To create a debian package run the packaging script:
```sh
cd package
./package.sh
```

This will create a rpis_x.x.x.deb package which you can install with:
```sh
sudo dpkg -i rpis_x.x.x.deb
```
### Service control
RPIS service can be started/stopped/restarted with the following commands:
```sh
sudo /etc/init.d/rpis start
sudo /etc/init.d/rpis stop
sudo /etc/init.d/rpis restart
```

By default RPIS web interface is available on port 13097 ( http://localhost:13097 )

### Configuration
Server settings are stored in a .py file, located in /usr/local/bin/rpis/default_config.py.
By edditing this file you can things like server ports, LED strip pinouts etc. After edditing the file make sure to restart the service.
