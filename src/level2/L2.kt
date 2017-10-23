package level2

import java.io.File
import java.util.*
import kotlin.collections.LinkedHashMap

val map = LinkedHashMap<String, Account>()

fun main(arr: Array<String>) {
    Locale.setDefault(Locale.US)

    val sc = Scanner(File("""level2-5.txt"""))

    val N = sc.nextInt()
    val accounts = Array(N) {
        Account(sc.next(), sc.next(), sc.nextLong(), sc.nextLong())
    }

    accounts.filter { validAccount(it.nr).valid }
            .forEach { map.put(it.nr, it) }

    val T = sc.nextInt()
    val transactions = Array(T) {
        Transaction(sc.next(), sc.next(), sc.nextLong(), sc.nextLong())
    }

    transactions.sortBy {
        it.submitTime
    }

    transactions.forEach {
        it.performTrans()
    }

    println("${map.size}")

    map.values.forEach {
        println("${it.owner} ${it.balance}")

    }
}

fun validAccount(nr: String): AccountNR {
    val accountID = nr.substring(5)

    val lowers = IntArray(200) //whatever size... need to store a - z
    val uppers = IntArray(200)

    accountID.forEach {
        val index = it.toLowerCase() - 'a'

        if (it.isLowerCase()) {
            lowers[index]++
        } else {
            uppers[index]++
        }
    }

    val isValid = (0..199).none { lowers[it] != uppers[it] }

    var sum = accountID.sumBy { it.toInt() }
    sum += 'C'.toInt()
    sum += 'A'.toInt()
    sum += 'T'.toInt()
    sum += '0'.toInt()
    sum += '0'.toInt()

    sum %= 97
    sum = 98 - sum

    return AccountNR("CAT" + sum + accountID, isValid)
}

data class Account(val owner: String, val nr: String, var balance: Long, val overdraft: Long)

data class Transaction(val from: String, val to: String, val amount: Long, val submitTime: Long) {
    fun performTrans() {
        if (map.containsKey(from) && map.containsKey(to)) {
            val fromAcc = map[from]!!
            val toAccount = map[to]!!

            if (fromAcc.balance + fromAcc.overdraft - amount >= 0) {
                fromAcc.balance -= amount
                toAccount.balance += amount
            }
        }
    }
}

data class AccountNR(val nr: String, var valid: Boolean)