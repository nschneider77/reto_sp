package com.example.appsophos.dataclasses

data class DocumentsResponse(val Items: List<Map<String,String>>, val Count: Int, val ScannedCount: Int) {
}
