import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.*

// Some code is from https://claude.ai/chat/c72b268b-e8be-44b7-b1c9-d17628e503c6
// with some modifications. Students should not modify this file.

/**
 * A canvas on which rectangles can be drawn.
 */
class Canvas(
    title: String,
    private val painter: MondrianPainter,
    requestedWidth: Int,
    requestedHeight: Int
) : JFrame(title) {
    private val canvas: CustomCanvas

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(requestedWidth, requestedHeight)
        canvas = CustomCanvas(painter, width, height)
        contentPane.add(canvas)
        isVisible = true

        // Add key listener to the frame
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ESCAPE) {
                    dispose()  // Close the window
                    System.exit(0)  // Exit the application
                }
            }
        })

        // Ensure the frame can receive key events
        focusTraversalKeysEnabled = false
        isFocusable = true
        requestFocus()

        SwingUtilities.invokeLater {
            painter.doMondrian(0, 0, width, height)
        }
    }

    /**
     * Draws a rectangle whose upper-left color is at ([x], [y]) and
     * has width [rectWidth], height [rectHeight], and the specified
     * [outlineColor] and [fillColor].
     */
    fun drawRectangle(x: Int, y: Int, rectWidth: Int, rectHeight: Int,
                      outlineColor: Color, fillColor: Color) {
        canvas.addRectangle(x, y, rectWidth, rectHeight, outlineColor, fillColor)
    }

    /**
     * Gets the color at ([x], [y]).
     */
    fun getColorAt(x: Int, y: Int) = canvas.getColorAt(x, y)

    private class CustomCanvas(
        private val painter: MondrianPainter,
        width: Int,
        height: Int
    ) : JPanel() {
        private data class Rectangle(
            val x: Int,
            val y: Int,
            val width: Int,
            val height: Int,
            val fillColor: Color,
            val outlineColor: Color
        )

        private val rectangles = mutableListOf<Rectangle>()
        private var bufferedImage: BufferedImage? = null

        init {
            preferredSize = java.awt.Dimension(width, height)
            bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    painter.handleClick(e.x, e.y)
                    repaint()
                }
            })
        }

        fun addRectangle(x: Int, y: Int, width: Int, height: Int,
                         outlineColor: Color, fillColor: Color) {
            rectangles.add(Rectangle(x, y, width, height, fillColor, outlineColor))
            repaint()
        }

        fun getColorAt(x: Int, y: Int): Color {
            val rgb: Int = bufferedImage?.getRGB(x, y) ?: throw AssertionError("bufferedImage was unexpected null")
            return Color(rgb)
        }

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            // Draw on buffered image
            val bfg = bufferedImage?.createGraphics() ?: throw AssertionError("bufferedImage was unexpected null")
            bfg.color = Color.WHITE
            bfg.fillRect(0, 0, width, height)

            // Draw all rectangles
            for (rect in rectangles) {
                bfg.color = rect.fillColor
                bfg.fillRect(rect.x, rect.y, rect.width, rect.height)
                bfg.color = rect.outlineColor
                bfg.drawRect(rect.x, rect.y, rect.width, rect.height)
            }

            bfg.dispose()

            // Draw buffered image on the panel
            g.drawImage(bufferedImage, 0, 0, this)
        }
    }
}
