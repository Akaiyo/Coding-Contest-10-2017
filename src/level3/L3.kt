package level3

import level4.Output
import java.io.File
import java.util.*
import kotlin.collections.HashMap

val map = HashMap<String, Long>()
val valid = ArrayList<Transaction>()

fun main(arr: Array<String>) {
    Locale.setDefault(Locale.US)

    val sc = Scanner(File("""level3-4.txt"""))

    val N = sc.nextInt()

    val transactions = Array(N) {
        val id = sc.next()
        val inputsNR = sc.nextLong()
        val inputs = Array(inputsNR.toInt()) {
            Input(sc.next(), sc.next(), sc.nextLong())
        }
        val outputsNR = sc.nextLong()
        val outputs = Array(outputsNR.toInt()) {
            Output(sc.next(), sc.nextLong())
        }
        val time = sc.nextLong()
        Transaction(id, inputsNR, inputs, outputsNR, outputs, time)
    }
    
    transactions.sortBy { it.time }

    transactions.forEach { trans -> 
        if (trans.isValid()) {
            trans.inputs.forEach {
                // if (it.owner != "origin") {
                map.remove(it.transId + it.owner)
                // }
            }
            trans.outputs.forEach {
                map.put(trans.id + it.owner, it.transAmount)
            }

            valid.add(trans)
        }
    }
    
    println(valid.size)

    valid.forEach {
        println(it)
    }
}

data class Transaction(val id: String, val inputNR: Long, val inputs: Array<Input>, val outputNR: Long, val outputs: Array<Output>, val time: Long) {
    fun isValid(): Boolean {
        if (inputs.map { it.transAmount }.sum() != outputs.map { it.transAmount }.sum()) {
            return false
        }

        val inputsValid = inputs.all {
            var r = false

            if (it.owner == "origin") {
                r = it.transAmount > 0
            } else {
                val output = map[it.transId + it.owner]

                if (output != null) {
                    if (it.transAmount == output) {
                        if (it.transAmount > 0) {
                            r = true
                        }
                    }
                }
            }

            r
        }


        if (!inputsValid)
            return false

        if (outputs.distinctBy { it.owner }.size != outputs.size) {
            return false
        }

        if (inputs.distinctBy { it.owner + it.transId }.size != inputs.size) {
            return false
        }

        if (!outputs.all { it.transAmount > 0 }) {
            return false
        }

        return true
    }

    override fun toString(): String {
        return "$id $inputNR ${inputs.map { it.toString() }.reduce { i1, i2 -> i1 + " " + i2 }} $outputNR ${outputs.map { it.toString() }.reduce { i1, i2 -> i1 + " " + i2 }} $time"
    }
}

data class Input(val transId: String, val owner: String, val transAmount: Long) {
    override fun toString(): String {
        return "$transId $owner $transAmount"
    }
}

data class Output(val owner: String, val transAmount: Long) {
    override fun toString(): String {
        return "$owner $transAmount"
    }
}