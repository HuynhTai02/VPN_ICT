package com.huynhngoctai.vpn_ict.model

data class Country(
    val nameCountry: String,
    val flagCountry: Int,
    val servers: List<Servers>
)