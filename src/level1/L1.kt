package level1

import java.io.File
import java.util.*
import kotlin.collections.LinkedHashMap

val persons = LinkedHashMap<String, Long>()

fun main(arr: Array<String>) {
    Locale.setDefault(Locale.US)

    val sc = Scanner(File("""level1-4.txt"""))

    val N = sc.nextInt()

    for (i in 0..N - 1) {
        persons.put(sc.next(), sc.nextLong())
    }

    val T = sc.nextInt()
    val transactions = Array(T) {
        Transaction(sc.next(), sc.next(), sc.nextLong(), sc.nextLong())
    }

    transactions.forEach {
        persons.put(it.from, persons[it.from]!! - it.amount)
        persons.put(it.to, persons[it.to]!! + it.amount)
    }

    println(N)
    persons.forEach { name, value -> println("$name $value") }
}

data class Transaction(val from: String, val to: String, val amount: Long, val submitTime: Long)
