# meta-openrc

OpenRC support layer for Yocto based on the initial work of [jsbronder/meta-openrc](https://github.com/jsbronder/meta-openrc)

## Yocto configuration

In your distro coguration file:
```
DISTRO_FEATURES:append = " openrc"
VIRTUAL-RUNTIME_init_manager = "openrc-sysvinit"
VIRTUAL-RUNTIME_initscripts = ""
```

In your image recipe:
```
IMAGE_INSTALL += " \
    ${@bb.utils.contains("DISTRO_FEATURES", "openrc", "openrc", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "openrc", "openrc-base-files", "", d)} \
"
```

Optionnaly, if you want to remove SysVinit init script files from your target's rootfs, still in your image recipe:
```
inherit openrc-image
```

In your machine's configuration file:
```
# Configure with your own serial consoles parameters
SERIAL_CONSOLES = "115200;ttyS0 115200;ttyGS0"

# We don't want inittab to manage the serials since they are already managed by openrc getty script (see openrc-sysinit-inittab recipe).
SYSVINIT_ENABLED_GETTYS = ""

# Not really related to OpenRC but defined often close to SYSVINIT_ENABLED_GETTYS
USE_VT = "0"
```

## Usage in recipes
WIP, a specific repo containing concrete examples is on the way.

Until then, some examples.

### Minimal example

In your package bbappend file:
```
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
    file://<openrc service script>.initd \
    file://<openrc service config>.confd \
"

inherit openrc

# The package name can be also simply ${PN}
OPENRC_SERVICE_${PN} = "<your package>"

# If not given, the OpenRC util will complain but install the scipt in default runlevel anyway.
OPENRC_RUNLEVEL_<your package> = "default"

# Install manually the service script and its configuration file
do_install:append() {
    # Install OpenRC conf script
    openrc_install_config ${WORKDIR}/<openrc service config>.confd

    # Install OpenRC script
    openrc_install_script ${WORKDIR}/<openrc service script>.initd
}
```

### Two services for one recipe
It can be usefull to add multiple service scripts with one recipe. In this case, we need to give a unique package name to our OpenRC utils.

In this case, the package busybox-syslog which needs to install script for klogd and syslogd.

In your package bbappend file:
```
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
    file://busybox-klogd.initd \
    file://busybox-klogd.confd \
    file://busybox-syslogd.initd \
    file://busybox-syslogd.confd \
"

inherit openrc

# We create a unique OpenRC package for this recipe
OPENRC_PACKAGES = "${PN}-syslog"

# But which contains multiple scripts
OPENRC_SERVICE_${PN}-syslog = "busybox-klogd busybox-syslogd"
OPENRC_RUNLEVEL_busybox-syslogd = "default"
OPENRC_RUNLEVEL_busybox-klogd = "default"

do_install:append() {
    # Install as usual
}

# You may need to define to which package of the appended recipe belong the OpenRC files
FILES:${PN}-syslog += " \
    ${OPENRC_CONFDIR}/* \
    ${OPENRC_INITDIR}/* \
"
```

Note: The variable OPENRC_PACKAGES is automatically defined by the openrc class with ${PN} as default value.

## Install a script without enabling it in a runlevel

Once the variable OPENRC_SERVICE_* defined in your recipe, the util will look for a OPENRC_RUNLEVEL_<service> value unless you will get a warning and the service will be installed in default runlevel.

If you want to install the script without making it to start at any runlevel, you can simply define the OPENRC_SERVICE_* as empty:
```
inherit openrc

# We don't define any service to manage
OPENRC_SERVICE_${PN} = ""

do_install:append() {
    # But we still want to have the script available on the target
    # (could be enabled later manually)
    openrc_install_script ${WORKDIR}/<script>.initd
}
```
