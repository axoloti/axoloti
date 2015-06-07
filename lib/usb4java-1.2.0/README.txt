usb4java 1.2.0
http://usb4java.org/
Copyright 2014 usb4java Team <http://usb4java.org/>
See LICENSE.md for licensing information.
------------------------------------------------------------------------------

The lib directory contains the following JAR files:

usb4java-*.jar             (The main usb4java library)
libusb4java-*.jar          (The native libraries for the various platforms)
commons-lang3-*.jar        (Apache Commons Lang library needed by usb4java)

If you don't want usb4java to extract the native libraries into a temporary
directoy on each program start then you might want to distribute them in
extracted form with your application. Just make sure your classpath points
to the directory where you extracted the JARs.