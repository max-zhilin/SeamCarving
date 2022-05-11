package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    print("Enter rectangle width: ")
    val width = readln().toInt()
    print("Enter rectangle height: ")
    val height = readln().toInt()
    print("Enter output image name: ")
    val name = readln()

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics = image.createGraphics()
    with(graphics) {
        color = Color.RED
        drawLine(0, 0, width - 1, height - 1)
        drawLine(0, height - 1, width - 1, 0)
    }
    ImageIO.write(image, "png", File(name))
}
