package com.huynhngoctai.vpn_ict.model

data class Servers(
    val nameCity: String,
    val flagCountry:Int,
    val privateKeyInterface: String,
    val addressInterface: String,
    val dnsInterface: String,
    val publicKeyPeer: String,
    val allowedIPsPeer: String,
    val endpointPeer: String
)