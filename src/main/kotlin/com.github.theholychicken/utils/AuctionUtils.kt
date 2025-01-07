package com.github.theholychicken.utils

data class AuctionResponse(
    val success: Boolean,
    val auctions: List<Auction>
)

data class Auction(
    val uuid: String,
    val item_name: String,
    val starting_bid: Long
)
