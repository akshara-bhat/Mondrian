import java.awt.Color
import java.awt.ScrollPane
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * The desired canvas width, which must be at least 4 times [MIN_RECTANGLE_WIDTH].
 */
const val REQUESTED_CANVAS_WIDTH = 600

/**
 * The desired canvas height, which must be at least 4 times [MIN_RECTANGLE_HEIGHT].
 */
const val REQUESTED_CANVAS_HEIGHT = 500

/**
 * The minimum width of a colored rectangle.
 */
const val MIN_RECTANGLE_WIDTH = 100

/**
 * The minimum height of a colored rectangle.
 */
const val MIN_RECTANGLE_HEIGHT = 100

// seed for random number generator
private val SEED = 350

class MondrianPainter {
    // Use this property when generating random numbers.
    private val random = Random(SEED)

    private val canvas =
        Canvas(
            "Mondrian Painter",
            this,
            REQUESTED_CANVAS_WIDTH,
            REQUESTED_CANVAS_HEIGHT,
        )

    /**
     * Performs a side-by-side split of the region specified by [x], [y],
     * [width], and [height], if it is wide enough to split, and calls
     * [doMondrian] on each of the two smaller regions. If the original
     * region is too narrow to split, no action is taken.
     *
     * @return true if the original region was wide enough to split, false
     * otherwise
     */
    private fun splitLeftRight(x: Int, y: Int, width: Int, height: Int): Boolean {
        if (width >= 2 * MIN_RECTANGLE_WIDTH) {
            val randomOffset = random.nextInt(MIN_RECTANGLE_WIDTH, width - MIN_RECTANGLE_WIDTH)
            val leftRegion = doMondrian(x, y, randomOffset, height)
            //left region
            val rightRegion = doMondrian(x + randomOffset, y, width - randomOffset, height)
            //right region
            return true
        }
        return false
    }

    /**
     * Performs an over-under split of the region specified by [x], [y],
     * [width], and [height], if it is tall enough to split, and calls
     * [doMondrian] on each of the two smaller regions. If the original
     * region is too short to split, no action is taken.
     *
     * @return true if the original region was tall enough to split, false
     * otherwise
     */
    private fun splitTopBottom(x: Int, y: Int, width: Int, height: Int): Boolean {
        if (height >= 2 * MIN_RECTANGLE_HEIGHT) {
            val randomOffset = random.nextInt(MIN_RECTANGLE_HEIGHT, height - MIN_RECTANGLE_HEIGHT)
            val topRegion = doMondrian(x, y, width, randomOffset)
            //top region
            val bottomRegion = doMondrian(x, y + randomOffset, width, height - randomOffset)
            //bottom region
            return true
        }
        return false
    }
//        if (height >= MIN_RECTANGLE_HEIGHT) {
//            val randomOffset = random.nextInt(0, height)
//            val bottomRegion = doMondrian(x, y, x + width, y + randomOffset)
//            //bottom region
//            val topRegion = doMondrian(x, y + randomOffset, x + width, height + randomOffset)
//            //top region
//            return true
//        }
//        return false
//    }

    /**
     * Performs a horizontal and vertical split of the region specified
     * by [x], [y], [width], and [height], if it is both wide and tall enough
     * to split, and calls [doMondrian] on each of the four smaller regions.
     * If the original region is too small to split, no action is taken.
     *
     * @return true if the original region could be split, false otherwise
     */

    private fun split4Way(x: Int, y: Int, width: Int, height: Int): Boolean {
        if (width >= 2 * MIN_RECTANGLE_WIDTH && height >= 2 * MIN_RECTANGLE_HEIGHT) {
            val randomYOffset = random.nextInt(MIN_RECTANGLE_HEIGHT, height - MIN_RECTANGLE_HEIGHT)
            val randomXOffset = random.nextInt(MIN_RECTANGLE_WIDTH, width - MIN_RECTANGLE_WIDTH)
            val topLeft = doMondrian(x, y, randomXOffset, randomYOffset)
            val bottomLeft = doMondrian(x, y + randomYOffset, randomXOffset, height - randomYOffset)
            val topRight = doMondrian(x + randomXOffset, y, width - randomXOffset, randomYOffset)
            val bottomRight =
                doMondrian(x + randomXOffset, y + randomYOffset, width - randomXOffset, height - randomYOffset)
            return true
        }
        return false
    }


    /**
     * Divides the region with the given [x] and [y] coordinates and having
     * width [width] and height [height] into one or more colored rectangles,
     * in the style of Piet Mondrian.
     */
    fun doMondrian(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ) {

        // 1. If the width of the region is more than half the canvas width AND
        //    the height of the region is more than half the canvas height,
        //    call split4Way(x, y, width, height).
        if (width > REQUESTED_CANVAS_WIDTH / 2 && height > REQUESTED_CANVAS_HEIGHT / 2) {
            split4Way(x, y, width, height)
        }
        // 2. Otherwise, if the width of the region is more than half the
        //    canvas width (but the height is not more than half the canvas
        //    height), call splitLeftRight(x, y, width, height).
        else if (width > (1 / 2) * REQUESTED_CANVAS_WIDTH && height <= (1 / 2) * REQUESTED_CANVAS_HEIGHT) {
            splitLeftRight(x, y, width, height)
        }
        // 3. Otherwise, if the height of the region is more than half the
        //    canvas height (but the width is not more than half the canvas
        //    width), call splitTopBottom(x, y, width, height).
        else if (height > (1 / 2) * REQUESTED_CANVAS_HEIGHT && width <= (1 / 2) * REQUESTED_CANVAS_WIDTH) {
            splitTopBottom(x, y, width, height)
        }
        // 4. Otherwise, randomly choose a split type to attempt from these
        //    three options: LeftRight, TopBottom, or Both.
        //    * If LeftRight is chosen and the region is wide enough to split
        //      into two side-by-side regions, do so.
        //    * If TopBottom was chosen and the region is tall enough to split
        //      into two stacked (over-under) regions, do so.
        //    * If Both was chosen and the region is both wide enough and tall
        //      enough to split into four regions, do so.
        else {
            val randomSplit = Random.nextInt(0..2)
            when (randomSplit) {
                0 -> splitLeftRight(x, y, width, height)
                1 -> splitTopBottom(x, y, width, height)
                2 -> split4Way(x, y, width, height)
            }
            // 5. If none of the above conditions code caused a split method to be
            //    called, fill the entire region with a single color. Half the time,
            //    the color should be white. The other half of the time, choose
            //    randomly among red, yellow, and blue. The outline color should
            //    always be black. You can modify the below sample code, which
            //    always draws a yellow rectangle with a black outline.
            if (!split4Way(x, y, width, height) || !splitLeftRight(x, y, width, height) || !splitTopBottom(
                    x,
                    y,
                    width,
                    height
                )
            ) {
                val fillColor = if (random.nextBoolean()) {
                    Color.WHITE
                } else {
                    val randomFill = Random.nextInt(0..2)
                    when (randomFill) {
                        0 -> Color.BLUE
                        1 -> Color.YELLOW
                        2 -> Color.RED
                        else -> Color.WHITE
                    }
                }
                canvas.drawRectangle(
                    x,
                    y,
                    width,
                    height,
                    fillColor = fillColor,
                    outlineColor = Color.BLACK,
                )
            }
        }
    }

    /**
     * Handles a click at the specified [x]-[y] coordinates.
     */
    fun handleClick(x: Int, y: Int) {
        recolorRectangle(x, y)
    }

    /**
     * Changes the fill color of the rectangle containing ([x], [y]).
     */
    data class Rectangle(
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int,
        var color: Color
    )

    private val rectangles = mutableListOf<Rectangle>()

    private fun recolorRectangle(x: Int, y: Int) {

        // You will need to determine the boundaries of the rectangle the
        // user has clicked on.
        // You may find the method canvas.getColorAt(x, y) helpful.
        val clickedColor = canvas.getColorAt(x, y)
        val clickedRectangle = rectangles.find { rectangle ->
            x in rectangle.x until rectangle.x + rectangle.width &&
                    y in rectangle.y until rectangle.y + rectangle.height &&
                    rectangle.color == clickedColor
        }
        if (clickedRectangle != null) {
            val newColor = if (Random.nextBoolean()) {
                Color.WHITE
            } else {
                when (Random.nextInt(3)) {
                    0 -> Color.BLUE
                    1 -> Color.YELLOW
                    else -> Color.RED
                }
            }
            clickedRectangle.color = newColor
            canvas.drawRectangle(
                clickedRectangle.x,
                clickedRectangle.y,
                clickedRectangle.width,
                clickedRectangle.height,
                fillColor = newColor,
                outlineColor = Color.BLACK
            )
        }
    }


    /**
     * Creates a canvas and paints it in the style of Piet Mondrian.
     */
    fun main() {
        require(REQUESTED_CANVAS_HEIGHT >= 4 * MIN_RECTANGLE_HEIGHT)
        require(REQUESTED_CANVAS_WIDTH >= 4 * MIN_RECTANGLE_WIDTH)
        MondrianPainter()
    }
}