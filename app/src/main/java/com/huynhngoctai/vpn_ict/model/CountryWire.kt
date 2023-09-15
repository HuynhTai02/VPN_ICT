package com.huynhngoctai.vpn_ict.model

data class CountryWire(
    val nameCountry: String,
    val flagCountry: Int,
    val serverWires: List<ServerWire>
)