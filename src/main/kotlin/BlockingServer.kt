import java.io.PrintWriter
import java.lang.management.ManagementFactory
import java.net.ServerSocket
import kotlin.concurrent.thread

fun main() {
    println("active threads ${ManagementFactory.getThreadMXBean().threadCount}")
    val server = ServerSocket(8080)
    while (true) {
        val newSocket = server.accept()
        thread(true) {
            println("connection accepted")
            println("active threads ${ManagementFactory.getThreadMXBean().threadCount}")
            val printWriter = PrintWriter(newSocket.getOutputStream(), true)
            val reader = newSocket.getInputStream().bufferedReader()
            while (true) {
                val message = reader.readLine()
                if (message == "hello") {
                    printWriter.println("hello too")
                }
            }
        }
    }
}
