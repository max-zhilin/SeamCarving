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
    val reduceWidthBy = args[5].toInt()
    val reduceHeightBy = args[7].toInt()

    val image = ImageIO.read(File(inName))

    var width = image.width
    var height = image.height

    repeat (reduceWidthBy) { removeSeam(image, width--, height, transpose = false) }
    repeat (reduceHeightBy) { removeSeam(image, height--, width, transpose = true) }

    ImageIO.write(image.getSubimage(0, 0, width, height), "png", File(outName))
}

fun removeSeam(image: BufferedImage, width: Int, height: Int, transpose: Boolean) {
    val lastX = width - 1
    val lastY = height - 1
    val minEnergySum: Array<Array<Double>> = Array(width) { Array(height) { 0.0 } }

    for (y in 0..lastY) // Row by row fill top down
        for (x in 0..lastX) {
            val xd = x.coerceIn(1 until lastX) // Shift by 1 near the borders
            val yd = y.coerceIn(1 until lastY)

            val colorX1 = Color(image.getRGBTransposed(xd - 1, y, transpose))
            val colorX2 = Color(image.getRGBTransposed(xd + 1, y, transpose))
            val colorY1 = Color(image.getRGBTransposed(x, yd - 1, transpose))
            val colorY2 = Color(image.getRGBTransposed(x, yd + 1, transpose))

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
                    } else 0.0 // For the first line it's just energy
        }

    // Take min sum on the bottom line and reconstruct the shortest path line by line bottom up
    var x = minEnergySum.indices.minByOrNull { minEnergySum[it][lastY] }!!
    image.cutPixelTransposed(x, lastY, transpose)
    for (y in lastY - 1 downTo 0) {
        val indices = when (x) {
            0 -> 0..1
            lastX -> x - 1..x
            else -> x - 1..x + 1
        }
        x = indices.minByOrNull { minEnergySum[it][y] }!! // X is the min sum in 3 (or 2) pixels on the prev line
        image.cutPixelTransposed(x, y, transpose)
    }
}

fun deltaSquare(a: Color, b: Color): Double {
    return (a.red - b.red).toDouble().pow(2.0) +
            (a.green - b.green).toDouble().pow(2.0) +
            (a.blue - b.blue).toDouble().pow(2.0)
}

fun BufferedImage.getRGBTransposed(x: Int, y: Int, transpose: Boolean): Int {
    return if (transpose)
        getRGB(y, x)
    else
        getRGB(x, y)
}

fun BufferedImage.cutPixelTransposed(x: Int, y: Int, transpose: Boolean) {
    if (transpose)
        for (i in x until height - 1)
            setRGB(y, i, getRGB(y, i + 1))
    else
        for (i in x until width - 1)
            setRGB(i, y, getRGB(i + 1, y))
}
