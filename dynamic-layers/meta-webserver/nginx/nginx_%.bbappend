FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://nginx.initd file://nginx.confd"

inherit openrc

do_install:append() {
    openrc_install_initd ${WORKDIR}/nginx.initd
    openrc_install_confd ${WORKDIR}/nginx.confd
}
