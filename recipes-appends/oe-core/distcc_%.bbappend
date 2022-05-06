FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://distcc.initd file://distcc.confd"

inherit openrc

OPENRC_PACKAGES = "${PN}-server"
OPENRC_SERVICES:${PN}-server = "distcc"

do_install:append() {
    openrc_install_initd ${WORKDIR}/distcc.initd
    openrc_install_confd ${WORKDIR}/distcc.confd
}
