package seamcarving

import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

fun main(args: Array<String>) {
    val inName = args[1]
    val outName = args[3]
    val image = ImageIO.read(File(inName))
    val lastX = image.width - 1
    val lastY = image.height - 1
    val minEnergySum: Array<Array<Double>> = Array(image.width) { Array(image.height) { 0.0 } }
    for (y in 0..lastY) // Row by row fill top down
        for (x in 0..lastX) {
            val xd = x.coerceIn(1 until lastX) // Shift by 1 near the borders
            val yd = y.coerceIn(1 until lastY)

            val colorX1 = Color(image.getRGB(xd - 1, y))
            val colorX2 = Color(image.getRGB(xd + 1, y))
            val colorY1 = Color(image.getRGB(x, yd - 1))
            val colorY2 = Color(image.getRGB(x, yd + 1))

            // Don't need to store energy due to one pass
            val energyXY = sqrt(deltaSquare(colorX1, colorX2) + deltaSquare(colorY1, colorY2))

            // *** Part Two ***
            minEnergySum[x][y] = energyXY +
                    if (y > 0) {
                        val indices = when (x) { // Use 3 pixels one line above
                            0 -> 0..1       // .. sometimes 2
                            lastX -> x - 1..x
                            else -> x - 1..x + 1
                        }
                        indices.minOf { minEnergySum[it][y - 1] }
                    } else 0.0 // For first line it's just energy
        }

    // Take min sum on the bottom line and reconstruct the shortest path line by line bottom up
    var x = minEnergySum.indices.minByOrNull { minEnergySum[it][lastY] }!!
    image.setRGB(x, lastY, Color.RED.rgb)
    for (y in lastY - 1 downTo 0) {
        val indices = when (x) {
            0 -> 0..1
            lastX -> x - 1..x
            else -> x - 1..x + 1
        }
        x = indices.minByOrNull { minEnergySum[it][y] }!! // X where min sum in 3 (or 2) pixels on the prev line
        image.setRGB(x, y, Color.RED.rgb)
    }

    ImageIO.write(image, "png", File(outName))
}

fun deltaSquare(a: Color, b: Color): Double {
    return (a.red - b.red).toDouble().pow(2.0) +
            (a.green - b.green).toDouble().pow(2.0) +
            (a.blue - b.blue).toDouble().pow(2.0)
}
