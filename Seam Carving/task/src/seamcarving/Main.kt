package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val inName = args[1]
    val outName = args[3]

    val image = ImageIO.read(File(inName))
    for (x in 0 until image.width)
        for (y in 0 until image.height) {
            val color = Color(image.getRGB(x, y))
            val newRGB = Color(255 - color.red, 255 - color.green, 255 - color.blue, ).rgb
            image.setRGB(x, y, newRGB)
        }
    ImageIO.write(image, "png", File(outName))
}
