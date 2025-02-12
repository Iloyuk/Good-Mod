package com.github.theholychicken.managers.apiclients

object TrickedApiClient {

    fun fetchPrice(itemTag: String): Double {
        val response = HttpClient.sendRequest("https://lb.tricked.pro/lowestbin/{itemTag}")
        return response.toDouble()
    }
}