package com.example.project

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.json
import java.io.InputStream

class JsonServ {

    companion object{
        private val parser:Parser = Parser.default()

        private fun position(pl: JsonObject): JsonArray<Double>{
            val position = pl["coordinates"] as JsonArray<Double>
            return position
        }

    public fun getData(input : InputStream): List<Plaatsen>{
        val plaats = mutableListOf<Plaatsen>()
        val par = parser.parse(input ) as JsonArray<JsonObject>
        for (i in par){
            val standen = Plaatsen()
            val prop = i["properties"] as JsonObject
            standen.lat = position(i["geometry"] as JsonObject).first()
            standen.long = position(i["geometry"] as JsonObject).last()
            standen.adres = prop["STRAAT"].toString() + "" + prop["NUMMER"] + "," + prop["POSTCODE"] + "" + prop["DISTRICT"]
            standen.geslacht = prop["DOELGROEP"].toString()
            when (prop["INTEGRAAL_TOEGANKELIJK"].toString()) {
                "ja" -> standen.rolstoel = true
                "nee" -> standen.rolstoel = false

            }
            when (prop["LUIERTAFEL"].toString()) {
                "ja" -> standen.luiertafel = true
                "nee" -> standen.luiertafel = false
                else -> standen.luiertafel = false
            }
            plaats.add(standen)
        }
        return plaats
    }
    }


}