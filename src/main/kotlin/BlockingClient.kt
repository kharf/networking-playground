import java.io.PrintWriter
import java.lang.management.ManagementFactory
import java.net.Socket
import kotlin.concurrent.thread

fun main() {
    println("active threads ${ManagementFactory.getThreadMXBean().threadCount}")
    for (i in 1..3) {
        thread(true) {
            val socket = Socket("127.0.0.1", 8080)
            println("active threads ${ManagementFactory.getThreadMXBean().threadCount}")
            val printWriter = PrintWriter(socket.getOutputStream(), true)
            val reader = socket.getInputStream().bufferedReader()
            printWriter.println("hello")
            val response = reader.readLine()
            println(response)
        }
    }
}
