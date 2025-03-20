package com.bitmovin.player.samples.multiView

import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.samples.multiView.multiView.Video

val allVideos: List<Video> = listOf(
    "https://d3ikisbsngjr73.cloudfront.net/567c71cf-d87a-4670-ba34-652b6832d925/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/8c8dab80-1fb4-4771-a2ff-8bda38c6b570/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/8cdbfe57-8208-40a2-aaf8-f7d133693906/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/9922228a-c6d8-4bf8-834c-58ecd05c4cd1/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/4277c1f3-4c0c-4eba-8b00-0f2d954796ab/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/cef66b61-3efa-4052-8460-d12614ef586a/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/79993646-8de0-465e-b8fb-d39b50e954ff/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/5807d167-b766-4fb6-b8e6-bf073395c570/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/90597caf-7865-4366-8728-9ad7e7b5c3b9/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/e3d8b6e2-0da7-4f3b-aa65-c416a9fc4894/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/f5f7a84b-518c-434f-b038-6f1d3f106c0f/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/56007779-38b1-497f-b989-0a0f1ed533ac/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/d641d5d7-9833-45fe-9814-7878bfce68e6/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/ffd04b33-8a5e-4bbd-87ce-be700f7f7146/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/570488bb-eefc-49a2-9ba8-c28160582e4d/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/ef043b0d-f8cf-479e-9d4d-15600abe9200/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/126d2812-f562-4d49-aad7-46dc9af6e3f4/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/18809c1a-d472-40dd-9ac0-713a63812b8a/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/d9250032-3687-484b-a3ec-da736ba97628/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/b4cdd86b-e705-4546-a0ef-12afcf7679c9/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/13c46947-db2c-4ed4-a02a-f38394a09ea8/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/ad3fbcbe-1d72-4260-bb1f-92d9523ea02b/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/d94628db-e2de-465c-8ff0-1c95a0f68fe3/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/9ccad1a9-ebdf-42fb-ac15-749231880a21/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/9f65dfd0-5bdb-4c51-8054-e3782bea35d9/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/e8165952-8a15-44ad-be6f-ea1a2eed25d1/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/1dc67599-c131-4816-8fd6-3c2b815b4a14/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/d40553fc-667f-40eb-a268-54a9162b07be/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/a56536f3-8d8e-4aec-bf79-3bc24405871e/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/2b9e0198-8365-4354-b710-021f722af0f2/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/7759400f-6ef6-4469-a9af-8f05cf93c3b8/index.m3u8",
    "https://d3ikisbsngjr73.cloudfront.net/b344c4c5-6160-44c9-8bdd-5d40f51a8b65/index.m3u8",
).map {
    val posterSource = it.substringBeforeLast("/") + "/thumbnails/high/thumbnails-5_0.png"
    Video(
        id = it,
        source = SourceConfig.fromUrl(it),
        posterUrl = posterSource
    )
}
