package com.example.fall.math

import android.util.Log
import com.example.fall.data.Block
import com.example.fall.data.BlockTextureTypes
import kotlin.math.roundToInt
import kotlin.random.Random

class Map(m: Int, n: Int, blockSize: Float = 1f) {

    private var width = m
    private var height = n
    private var blockSize = blockSize

    private var data = mutableListOf<Block>()
    private var binary = BooleanArray(m*n)

    private var random: Random = Random

    private var initX: Float = 0f
    private var initY: Float = 0f

    init {
        generateBinaryData()
        generateBlockData()
    }

    private fun generateBinaryData() {
        // Setting the seed
        var startX = random.nextInt(width/4,3*width/4)
        var startY = random.nextInt(height/4,3*height/4)

        var otherX = startX
        var otherY = startY

        // Setting the binary map to "false" - all walls
        binary.fill(false,0,width * height)

        // Generating "caves"
        for (i in 0 until 2*width*height/3) {
            binary[startY * width + startX] = true

            if (i == width*height/2) {
                initX = startX.toFloat()
                initY = startY.toFloat()
            }

            when (random.nextInt(0,4)) {
                0 -> {
                    if (startX < width - 1) startX++
                    if (otherY > 0) otherY--
                }

                1 ->  {
                    if (startX > 0) startX--
                    if (otherY < height - 1) otherY++
                }

                2 -> {
                    if (startY < height - 1) startY++
                    if (otherX < width - 1) otherX++
                }

                3 -> {
                    if (startY > 0) startY--
                    if (otherX > 0) otherX--
                }
                else -> {}
            }
        }

        // Setting the borders
        for (i in 0 until width) {
            binary[i] = false
            binary[(height - 1) * width + i] = false
        }

        for (i in 0 until height) {
            binary[i*width] = false
            binary[(i+1)*width-1] = false
        }

        //printBinary()
    }

    private fun generateBlockData() {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val block = Block(
                    i * blockSize,
                    j * blockSize,
                    blockSize,
                    BlockTextureTypes.Wall1,
                    binary[j * width + i]
                )

                when (random.nextInt(0,2)) {
                    0 -> {
                        if (block.passable)
                            block.type = BlockTextureTypes.Floor1
                        else
                            block.type = BlockTextureTypes.Wall1
                    }

                    1 -> {
                        if (block.passable)
                            block.type = BlockTextureTypes.Floor2
                        else
                            block.type = BlockTextureTypes.Wall2
                    }

                    2 -> {
                        if (block.passable)
                            block.type = BlockTextureTypes.Floor3
                        else
                            block.type = BlockTextureTypes.Wall3
                    }

                    else -> {}
                }

                data.add(block)
            }
        }
    }

    private fun getBlock(i: Int, j: Int): Block {
        return if (i in 0 until width && j in 0 until height)
            data[j * width + i]
        else data[0]
    }

    fun printBinary() {
        for ((c, i) in binary.withIndex()) {
            if (c%width == 0)
                print("\n")

            if (i)
                print("-")
            else
                print("#")
        }
    }

    fun getMap() : MutableList<Block> {
        return data
    }

    fun getMapNear(posX: Float, posY: Float, sampleSize: Int): MutableList<Block> {
        val ret = mutableListOf<Block>()

        val roundedX = (posY / blockSize).roundToInt()
        val roundedY = (posX / blockSize).roundToInt()

        val xStart = if (roundedX - sampleSize/2 > 0) roundedX - sampleSize/2 else 0
        val yStart = if (roundedY - sampleSize/2 > 0) roundedY - sampleSize/2 else 0

        val xStop = if (roundedX + sampleSize/2 < width) roundedX + sampleSize/2 else width
        val yStop = if (roundedY + sampleSize/2 < height) roundedY + sampleSize/2 else height

        for (i in xStart until xStop) {
            for (j in yStart until yStop) {
                ret.add(getBlock(i,j))
            }
        }

        return ret
    }

    fun getStartingX(): Float {
        return initX * blockSize
    }

    fun getStartingY(): Float {
        return initY * blockSize
    }
}