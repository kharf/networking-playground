import java.lang.management.ManagementFactory
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

fun main() {
    println("active threads ${ManagementFactory.getThreadMXBean().threadCount}")
    val selector = Selector.open()
    val server = ServerSocketChannel.open()
    server.configureBlocking(false)
    server.bind(InetSocketAddress("127.0.0.1", 8080))
    server.register(selector, SelectionKey.OP_ACCEPT)
    while (true) {
        println("occured events ${selector.select()}")
        val keyIterator = selector.selectedKeys().iterator()
        while (keyIterator.hasNext()) {
            val selectionKey = keyIterator.next()
            keyIterator.remove()
            if (selectionKey.isAcceptable) {
                val newSocketChannel = server.accept()
                if (newSocketChannel != null) {
                    newSocketChannel.configureBlocking(false)
                    newSocketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024))
                    println("connection accepted")
                    println("active threads ${ManagementFactory.getThreadMXBean().threadCount}")
                }
            }
            if (selectionKey.isReadable) {
                val client = selectionKey.channel() as SocketChannel
                val buffer = selectionKey.attachment() as ByteBuffer
                val read = client.read(buffer)
                println(buffer.array().decodeToString(endIndex = read))
                selectionKey.channel().register(selector, SelectionKey.OP_WRITE)
                println("active threads ${ManagementFactory.getThreadMXBean().threadCount}")
            }
            if (selectionKey.isWritable) {
                val socketChannel = selectionKey.channel() as SocketChannel
                val message = "hello too"
                val msgByteArray = message.toByteArray()
                val buffer = ByteBuffer.allocate(msgByteArray.size)
                buffer.put(msgByteArray)
                buffer.flip()
                socketChannel.write(buffer)
                socketChannel.close()
                println("active threads ${ManagementFactory.getThreadMXBean().threadCount}")
            }
        }
    }
}
