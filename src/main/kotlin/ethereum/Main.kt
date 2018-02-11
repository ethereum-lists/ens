package ethereum

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.string
import java.io.File

fun main(args: Array<String>) {
    listOf("ens-darklist.json","ens-lightlist.json")
            .map { File(it) }
            .forEach { it.checkFields(mandatoryFields = listOf("id", "comment")) }
}

fun File.toJSONArray() = Parser().parse(this.reader()) as JsonArray<*>

fun File.checkFields(mandatoryFields: List<String>, optionalFields: List<String> = listOf()) {
    val jsonObjectList = toJSONArray().map { it as JsonObject }
    jsonObjectList.forEach { jsonObject ->
        if (!jsonObject.keys.containsAll(mandatoryFields)) {
            throw IllegalArgumentException("$jsonObject does not contain " + mandatoryFields.minus(jsonObject.keys))
        }

        mandatoryFields.forEach {
            if (jsonObject[it] is String && jsonObject.string(it)?.isBlank() == true) {
                throw IllegalArgumentException("$jsonObject has blank value for $it")
            }
        }


        val unknownFields = jsonObject.keys.minus(mandatoryFields.plus(optionalFields))
        if (unknownFields.isNotEmpty()) {
            throw IllegalArgumentException("$jsonObject contains unknown " + unknownFields)
        }

    }
    if (!optionalFields.isEmpty()) {
        val minimalJSONArray = JsonArray<JsonObject>()
        jsonObjectList.forEach { jsonObject ->
            val minimalJsonObject = JsonObject()
            mandatoryFields.forEach {
                minimalJsonObject.put(it, jsonObject[it])
            }
            minimalJSONArray.add(minimalJsonObject)
        }

        File("build/output").mkdir()
        File("build/output", name.replace(".json", ".min.json")).writeText(minimalJSONArray.toJsonString())
    }
}
