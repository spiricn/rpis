# RPIS Android Client

An Android application used to interface with the RPIS server REST API.

## Dependencies
* Gradle 3.3 - Used as an application build system.
* Android SDK 25
* Java 8

## Dependency installation

##### Android SDK 25.2.3
```sh
# Create a directory where the SDK will be downloaded
mkdir ~/android
cd ~/android

# Download the SDK from Google repo
wget https://dl.google.com/android/repository/tools_r25.2.3-linux.zip

# Unpack
unzip tools_r25.2.3-linux.zip

# Update
./tools/android update sdk --no-ui --all --filter build-tools-23.0.3,android-23,extra-android-m2repository,platform-tools
```
Run this before each build (or add it to your ~/.bashrc file)
```sh
export ANDROID_HOME=~/android
```


##### Gradle build system

```sh
# Download & extract gradle
cd ~

wget https://downloads.gradle.org/distributions/gradle-3.3-bin.zip

unzip gradle-3.3-bin.zip
```

Run this before each build (or add it to your ~/.bashrc file)
```sh
export GRADLE_HOME=~/gradle-3.3
export PATH=$GRADLE_HOME/bin:$PATH
```

##### Java 8
```sh
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
```
Run this before each build (or add it to your ~/.bashrc file)
```sg
export JAVA_HOME=/usr/lib/jvm/java-8-oracle
```

## Build an APK
```sh
gradle clean asssemble
```

## Install
```sh
adb connect <device_ip>
adb install ./app/build/outputs/apk/app-debug.apk
```

