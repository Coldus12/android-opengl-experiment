package com.example.fall.game.logic

import com.example.fall.data.game_data.Block
import com.example.fall.data.game_data.BlockTextureTypes
import kotlin.math.roundToInt
import kotlin.random.Random

// Map
//--------------------------------------------------------------------------------------------------
/** The map of the game.
 * Generates a map with the given width, height, and blocksize.
 * @param m width of the map
 * @param n height of the map
 * @param blockSize size of the blocks on the map
 * */
class Map(m: Int, n: Int, private var blockSize: Float = 1f) {

    private var width = m
    private var height = n

    private var data = mutableListOf<Block>()
    private var passableBlocks = mutableListOf<Block>()
    private var binary = BooleanArray(m*n)

    private var random: Random = Random

    private var initX: Float = 0f
    private var initY: Float = 0f

    init {
        generateBinaryData()
        generateBlockData()
    }

    /** Generates a 2D array of booleans which represent the map. If the boolean is false, then
     * there at that position there will be a solid block, however if the boolean is true, then
     * that means that at that position there will be a passableBlock, a floor.
     * Also generates the starting position of the player.
     * */
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

    /** Once the binary generation is done, the block generation may begin.
     * The blocks are generated with the given size and they are placed at a position
     * corresponding to their position in the binary array, and their size.
     * */
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

                when (random.nextInt(0,3)) {
                    0 -> {
                        if (block.passable) {
                            block.type = BlockTextureTypes.Floor1
                            passableBlocks.add(block)
                        } else
                            block.type = BlockTextureTypes.Wall1
                    }

                    1 -> {
                        if (block.passable) {
                            block.type = BlockTextureTypes.Floor2
                            passableBlocks.add(block)
                        } else
                            block.type = BlockTextureTypes.Wall2
                    }

                    2 -> {
                        if (block.passable) {
                            block.type = BlockTextureTypes.Floor3
                            passableBlocks.add(block)
                        } else
                            block.type = BlockTextureTypes.Wall3
                    }

                    else -> {}
                }

                data.add(block)
            }
        }
    }

    /** @return the block at the given "binary" position - i.e.: place in the array
     * */
    private fun getBlock(i: Int, j: Int): Block {
        return if (i in 0 until width && j in 0 until height)
            data[j * width + i]
        else data[0]
    }

    /** Returns the block at the given world coordinates
     * @param x x coordinate
     * @param y y coordinate
     * @return the block at the given world coordinates
     * */
    fun getBlockAt(x: Float, y: Float): Block {
        val iX = (x / blockSize).roundToInt()
        val iY = (y / blockSize).roundToInt()

        return getBlock(iY,iX)
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

    /** Gets a "sample" of the map with a given size around a given point (in world coordinates)
     * @param posX x coordinate
     * @param posY y coordinate
     * @param sampleSize size of the sample (measured in blocks both in the x, and y direction)
     * @return the requested part of the map
     * */
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

    /** @return all the passable blocks
     * */
    fun getPassableBlocks() : MutableList<Block> {
        return passableBlocks
    }

    fun getStartingX(): Float {
        return initX * blockSize
    }

    fun getStartingY(): Float {
        return initY * blockSize
    }
}