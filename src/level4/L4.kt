package level4

import level3.Output
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val map = HashMap<String, Long>()
val valid = ArrayList<Transaction>()

fun main(arr: Array<String>) {
    Locale.setDefault(Locale.US)

    val sc = Scanner(File("""level4-4.txt"""))

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

    val M = sc.nextInt()

    val requests = Array(M) {
        TransactionRequest(sc.next(), sc.next(), sc.next(), sc.nextLong(), sc.nextLong())
    }

    transactions.sortBy { it.time }

    transactions.filter { it.isValid() }
                .forEach { trans ->
                    trans.inputs.forEach {
                        map.remove(it.transId + it.owner)
                    }
                    trans.outputs.forEach {
                        map.put(trans.id + it.owner, it.transAmount)
                    }
                    valid.add(trans)
                }
    
    val opens = map.map {
        OpenOutput(it.key.substring(0, 10), it.key.substring(10), it.value)
    }.sortedBy {
        val transid = it.transId
        transactions.find { it.id == transid }!!.time
    }.toMutableList()
    
    requests.sortBy { it.submitTime }
    
    requests.forEach {
        if (it.amount > 0) {

            val fromOwner = it.fromOwner

            var amount = it.amount

            opens.forEach {
                if (!it.used) {
                    if (it.owner == fromOwner) {
                        amount -= it.amount


                    }
                }
            }

            if (amount <= 0) {
                val trans = Transaction(it.transId, 0, emptyArray(), 0, emptyArray(), it.submitTime)

                val newInputs = ArrayList<Input>()
                val newOutputs = ArrayList<Output>()

                var count = it.amount

                opens.forEach {
                    if (!it.used) {
                        if (it.owner == fromOwner && count > 0) {
                            count -= it.amount

                            it.used = true

                            newInputs.add(Input(it.transId, it.owner, it.amount))
                        }
                    }
                }

                opens.add(OpenOutput(it.transId, it.toOwner, it.amount))
                newOutputs.add(Output(it.toOwner, it.amount))

                if (count < 0) {
                    opens.add(OpenOutput(it.transId, it.fromOwner, -count))
                    newOutputs.add(Output(it.fromOwner, -count))
                }

                trans.inputNR = newInputs.size.toLong()

                trans.inputs = newInputs.toTypedArray()

                trans.outputNR = newOutputs.size.toLong()
                trans.outputs = newOutputs.toTypedArray()

                valid.add(trans)
            }
        }
    }
    
    println(valid.size)

    valid.forEach {
        println(it)
    }
}

data class Transaction(var id: String, var inputNR: Long, var inputs: Array<Input>, var outputNR: Long, var outputs: Array<Output>, var time: Long) {

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
        return "$id $inputNR ${ inputs.map { it.toString() }.reduce { i1, i2 -> i1 + " " + i2 }} $outputNR ${outputs.map { it.toString() }.reduce { i1, i2 -> i1 + " " + i2 }} $time"
    }
}

data class TransactionRequest(val transId: String, val fromOwner: String, val toOwner: String, val amount: Long, val submitTime: Long)

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

data class OpenOutput(val transId: String, val owner: String, val amount: Long, var used: Boolean = false)