package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

fun main(args: Array<String>) {
    val inName = args[1]
    val outName = args[3]
    val horizontal = true
    val image = ImageIO.read(File(inName))
    val (lastX, lastY) = if (horizontal) Pair(image.height - 1, image.width - 1)
                                    else Pair(image.width - 1, image.height - 1)

    val minEnergySum: Array<Array<Double>> = if (horizontal) Array(image.height) { Array(image.width) { 0.0 } }
                                                        else Array(image.width) { Array(image.height) { 0.0 } }
    for (y in 0..lastY) // Row by row fill top down
        for (x in 0..lastX) {
            val xd = x.coerceIn(1 until lastX) // Shift by 1 near the borders
            val yd = y.coerceIn(1 until lastY)

            val colorX1 = Color(image.getRGBTransposed(xd - 1, y, horizontal))
            val colorX2 = Color(image.getRGBTransposed(xd + 1, y, horizontal))
            val colorY1 = Color(image.getRGBTransposed(x, yd - 1, horizontal))
            val colorY2 = Color(image.getRGBTransposed(x, yd + 1, horizontal))

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
    image.setRGBTransposed(x, lastY, Color.RED.rgb, horizontal)
    for (y in lastY - 1 downTo 0) {
        val indices = when (x) {
            0 -> 0..1
            lastX -> x - 1..x
            else -> x - 1..x + 1
        }
        x = indices.minByOrNull { minEnergySum[it][y] }!! // X where min sum in 3 (or 2) pixels on the prev line
        image.setRGBTransposed(x, y, Color.RED.rgb, horizontal)
    }

    ImageIO.write(image, "png", File(outName))
}

fun deltaSquare(a: Color, b: Color): Double {
    return (a.red - b.red).toDouble().pow(2.0) +
            (a.green - b.green).toDouble().pow(2.0) +
            (a.blue - b.blue).toDouble().pow(2.0)
}

fun BufferedImage.getRGBTransposed(x: Int, y: Int, transposed: Boolean): Int {
    return if (transposed)
        getRGB(y, x)
    else
        getRGB(x, y)
}

fun BufferedImage.setRGBTransposed(x: Int, y: Int, rgb: Int, transposed: Boolean) {
    if (transposed)
        setRGB(y, x, rgb)
    else
        setRGB(x, y, rgb)
}
