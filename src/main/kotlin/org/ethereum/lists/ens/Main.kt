package org.ethereum.lists.ens

import org.ethereum.lists.cilib.checkFields
import java.io.File

fun main(args: Array<String>) {
    listOf("ens-darklist.json","ens-lightlist.json")
            .map { File(it) }
            .forEach { it.checkFields(mandatoryFields = listOf("id", "comment")) }
}
