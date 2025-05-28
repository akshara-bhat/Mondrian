class MyMondrianPainter {

}

/**
 * Creates a canvas and paints it in the style of Piet Mondrian.
 */
fun main() {
    require(REQUESTED_CANVAS_HEIGHT >= 4 * MIN_RECTANGLE_HEIGHT)
    require(REQUESTED_CANVAS_WIDTH >= 4 * MIN_RECTANGLE_WIDTH)
    MyMondrianPainter()
}
