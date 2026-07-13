/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.visualtesting

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

/**
 * Helper class to interact with the Gemini API for visual verification and action.
 *
 * This version uses org.json for parsing to avoid binary compatibility issues with kotlinx.serialization.
 */
class GeminiVisualTestHelper {

    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 60_000
            socketTimeoutMillis = 60_000
        }
    }

    /**
     * Executes a UI action based on a natural language prompt.
     * It analyzes the current UI hierarchy and asks Gemini to determine the best action.
     */
    suspend fun performActionFromPrompt(prompt: String, uiDevice: UiDevice, apiKey: String) {
        val hierarchyStream = ByteArrayOutputStream()
        uiDevice.dumpWindowHierarchy(hierarchyStream)
        val hierarchyXml = hierarchyStream.toString("UTF-8")

        val systemPrompt = """
            You are an expert Android QA automaton. Your task is to translate a natural language command 
            into a specific action to be performed on a UI. Given a UI hierarchy (in XML format), 
            determine the correct action and selector.

            The available actions are: "click", "longClick", "setText".

            Your response MUST be a single, well-formed JSON object with "action" and "selector" keys.
            The "selector" object must contain exactly one of "text", "contentDescription", or "resourceId".
            If the action is "setText", you must also include a "textValue" field at the top level.

            Example for a click:
            { "action": "click", "selector": { "text": "Login" } }

            Example for setting text:
            { "action": "setText", "selector": { "resourceId": "com.example.app:id/email_input" }, "textValue": "test@example.com" }
        """.trimIndent()

        val fullPrompt = "$systemPrompt\n\nCommand: \"$prompt\"\n\nUI Hierarchy:\n$hierarchyXml"

        val modelName = "gemini-2.5-flash"
        
        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", fullPrompt) })
                    })
                })
            })
        }

        val response: HttpResponse = client.post("https://generativelanguage.googleapis.com/v1/models/$modelName:generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(requestJson.toString())
        }

        if (response.status != HttpStatusCode.OK) {
            val errorBody = response.bodyAsText()
            Log.e("GeminiVisualTestHelper", "Action API Error: ${response.status} $errorBody")
            throw Exception("Gemini Action API returned an error: ${response.status}\n$errorBody")
        }

        val rawBody = response.bodyAsText()
        val jsonResponse = JSONObject(rawBody)
        val actionJson = jsonResponse.getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")

        // Remove markdown code block delimiters if present
        val cleanedActionJson = actionJson.removePrefix("```json\n").removeSuffix("\n```")

        Log.d("GeminiVisualTestHelper", "Received Action JSON: $cleanedActionJson")

        try {
            val aiAction = JSONObject(cleanedActionJson)
            val action = aiAction.getString("action")
            val selectorObj = aiAction.getJSONObject("selector")
            
            val selector = when {
                selectorObj.has("text") -> By.text(selectorObj.getString("text"))
                selectorObj.has("contentDescription") -> By.desc(selectorObj.getString("contentDescription"))
                selectorObj.has("resourceId") -> By.res(selectorObj.getString("resourceId"))
                else -> throw IllegalArgumentException("Selector must have text, contentDescription, or resourceId.")
            }

            val uiObject = uiDevice.wait(Until.findObject(selector), 10000)
                ?: throw Exception("Could not find UI element for selector: $selector")

            when (action.lowercase()) {
                "click" -> uiObject.click()
                "longclick" -> uiObject.longClick()
                "settext" -> {
                    val textToSet = aiAction.getString("textValue")
                    uiObject.text = textToSet
                }
                else -> throw UnsupportedOperationException("Action '$action' is not supported.")
            }
        } catch (e: Exception) {
            Log.e("GeminiVisualTestHelper", "Failed to parse or execute AI action", e)
            throw e
        }
    }

    /**
     * Fetches and logs the list of available Gemini models for the given API key.
     */
    suspend fun listAvailableModels(apiKey: String) {
        try {
            val response: HttpResponse = client.get("https://generativelanguage.googleapis.com/v1/models?key=$apiKey")
            val rawBody = response.bodyAsText()
            val jsonResponse = JSONObject(rawBody)
            val models = jsonResponse.getJSONArray("models")
            val modelNames = StringBuilder()
            for (i in 0 until models.length()) {
                val model = models.getJSONObject(i)
                modelNames.append(" - ${model.getString("name")} (Display Name: ${model.getString("displayName")})\n")
            }
            Log.i("GeminiVisualTestHelper", "Available Gemini Models:\n$modelNames")
        } catch (e: Exception) {
            Log.e("GeminiVisualTestHelper", "Failed to list available models", e)
        }
    }

    /**
     * Analyzes an image with a given prompt using the Gemini API.
     */
    suspend fun analyzeImage(
        bitmap: Bitmap,
        prompt: String,
        apiKey: String
    ): String? {
        // Log available models first for easier debugging.
        listAvailableModels(apiKey)

        val base64Image = bitmap.toBase64EncodedJpeg()
        
        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                        put(JSONObject().apply {
                            put("inline_data", JSONObject().apply {
                                put("mime_type", "image/jpeg")
                                put("data", base64Image)
                            })
                        })
                    })
                })
            })
        }

        val response: HttpResponse = client.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(requestJson.toString())
        }

        if (response.status != HttpStatusCode.OK) {
            val errorBody = response.bodyAsText()
            Log.e("GeminiVisualTestHelper", "API Error: ${response.status} $errorBody")
            throw Exception("Gemini API returned an error: ${response.status}\n$errorBody")
        }

        val rawBody = response.bodyAsText()
        val jsonResponse = JSONObject(rawBody)
        
        val candidates = jsonResponse.optJSONArray("candidates")
        if (candidates == null || candidates.length() == 0) {
            Log.w("GeminiVisualTestHelper", "Gemini API returned empty candidates. Full response: $rawBody")
            throw Exception("Gemini API returned no candidates.")
        }

        return candidates.getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .optString("text")
    }

    private fun Bitmap.toBase64EncodedJpeg(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
