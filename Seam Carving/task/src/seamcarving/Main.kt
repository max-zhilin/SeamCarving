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
    val energy: Array<Array<Double>> = Array(image.width ) { Array(image.height) { 0.0 } }
    for (x in 0 until image.width)
        for (y in 0 until image.height) {
            val xd = when (x) {
                0 -> 1
                image.width - 1 -> x - 1
                else -> x
            }
            val yd = when (y) {
                0 -> 1
                image.height - 1 -> y - 1
                else -> y
            }

            val colorX1 = Color(image.getRGB(xd - 1, y))
            val colorX2 = Color(image.getRGB(xd + 1, y))
            val colorY1 = Color(image.getRGB(x, yd - 1))
            val colorY2 = Color(image.getRGB(x, yd + 1))

            energy[x][y] = sqrt(deltaSquare(colorX1, colorX2) + deltaSquare(colorY1, colorY2))
        }
    val maxEnergyValue = energy.maxOf { innerArray -> innerArray.maxOf { it } }
    for (x in 0 until image.width)
        for (y in 0 until image.height) {
            val intensity = (255.0 * energy[x][y] / maxEnergyValue).toInt()
            val newRGB = Color(intensity, intensity, intensity).rgb
            image.setRGB(x, y, newRGB)
        }

    // *** Part Two ***
    val minEnergySum: Array<Array<Double>> = Array(image.width ) { Array(image.height) { 0.0 } }

    for (x in 0 until image.width)
        for (y in 0 until image.height)
            minEnergySum[x][y] = energy[x][y] +
                    if (y > 0)
                        minOf(
                            minEnergySum[x][y - 1],
                            if (x > 0) minEnergySum[x - 1][y - 1] else 0.0,
                            if (x < image.width - 1) minEnergySum[x + 1][y - 1] else 0.0
                        )
                    else 0.0
    val minIndexAtBottom = minEnergySum.indices.minByOrNull { minEnergySum[it][image.height - 1] }
    println(minIndexAtBottom)


    ImageIO.write(image, "png", File(outName))
}

fun deltaSquare(a: Color, b: Color): Double {
    return (a.red - b.red).toDouble().pow(2.0) +
            (a.green - b.green).toDouble().pow(2.0) +
            (a.blue - b.blue).toDouble().pow(2.0)
}
