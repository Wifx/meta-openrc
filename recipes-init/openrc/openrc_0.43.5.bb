LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2307fb28847883ac2b0b110b1c1f36e0"

inherit openrc-native

require openrc.inc


PR = "0"
KBRANCH = "0.43.x"
SRCREV = "eedafe0f1a735824f4bcadc56c8baeda431ba571"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}/${PN}-${PV}:"

SRC_URI += " \
    file://0001-mk-break-up-long-SED_REPLACE-line.patch \
    file://0002-fix-alternative-conf-and-init-dir-support.patch \
    file://0003-Modified-service-timeout-from-60-to-120-seconds.patch \
"

do_install:append() {
    # openrc-sysvinit-inittab already provides getty logic
    rm -rf ${D}${OPENRC_INITDIR}/agetty
}
