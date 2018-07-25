package com.xt.kimi.coregraphics

import android.graphics.*
import com.xt.endo.CGRect
import com.xt.endo.CGSize
import com.xt.kimi.KIMIPackage
import com.xt.kimi.uikit.UIColor
import com.xt.kimi.uikit.UIView
import com.xt.kimi.uikit.scale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created by cuiminghui on 2018/7/20.
 */

private val sharedOuterPath = Path()
private val sharedBackgroundPaint = Paint()

open class CALayer {

    internal var view: UIView? = null
        get() {
            return field ?: this.superlayer?.view
        }

    var frame: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)

    var superlayer: CALayer? = null
        private set

    fun removeFromSuperlayer() {
        superlayer?.let { superlayer ->
            superlayer.sublayers = kotlin.run {
                val sublayers = superlayer.sublayers.toMutableList()
                sublayers.remove(this)
                return@run sublayers.toList()
            }
            superlayer.view?.setNeedsDisplay()
        }
    }

    var sublayers: List<CALayer> = listOf()
        private set

    fun addSublayer(layer: CALayer) {
        if (this == layer) { return }
        if (layer.superlayer != null) {
            layer.removeFromSuperlayer()
        }
        layer.superlayer = this
        sublayers = kotlin.run {
            val sublayers = this.sublayers.toMutableList()
            sublayers.add(layer)
            return@run sublayers.toList()
        }
        this.view?.setNeedsDisplay()
    }

    fun insertSublayerAtIndex(layer: CALayer, index: Int) {
        if (this == layer) { return }
        if (layer.superlayer != null) {
            layer.removeFromSuperlayer()
        }
        layer.superlayer = this
        sublayers = kotlin.run {
            val sublayers = this.sublayers.toMutableList()
            sublayers.add(index, layer)
            return@run sublayers.toList()
        }
        this.view?.setNeedsDisplay()
    }

    fun insertSublayerBelow(layer: CALayer, below: CALayer) {
        if (this == layer) { return }
        sublayers.indexOf(below)?.let { targetIndex ->
            if (layer.superlayer != null) {
                layer.removeFromSuperlayer()
            }
            layer.superlayer = this
            sublayers = kotlin.run {
                val sublayers = this.sublayers.toMutableList()
                sublayers.add(targetIndex, layer)
                return@run sublayers.toList()
            }
            this.view?.setNeedsDisplay()
        }
    }

    fun insertSublayerAbove(layer: CALayer, above: CALayer) {
        if (this == layer) { return }
        sublayers.indexOf(above)?.let { targetIndex ->
            if (layer.superlayer != null) {
                layer.removeFromSuperlayer()
            }
            layer.superlayer = this
            sublayers = kotlin.run {
                val sublayers = this.sublayers.toMutableList()
                sublayers.add(targetIndex + 1, layer)
                return@run sublayers.toList()
            }
            this.view?.setNeedsDisplay()
        }
    }

    fun replaceSublayer(oldLayer: CALayer, newLayer: CALayer) {
        if (this == oldLayer) { return }
        if (this == newLayer) { return }
        sublayers.indexOf(oldLayer)?.let { targetIndex ->
            if (newLayer.superlayer != null) {
                newLayer.removeFromSuperlayer()
            }
            newLayer.superlayer = this
            sublayers = kotlin.run {
                val sublayers = this.sublayers.toMutableList()
                sublayers[targetIndex] = newLayer
                return@run sublayers.toList()
            }
            this.view?.setNeedsDisplay()
        }
    }

    var hidden: Boolean = false
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var mask: CALayer? = null

    var masksToBounds: Boolean = false
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var backgroundColor: UIColor? = null

    var cornerRadius: Double = 0.0
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var borderWidth: Double = 0.0
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var borderColor: UIColor? = null
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var opacity: Double = 1.0
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var shadowColor: UIColor? = null
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var shadowOpacity: Double = 0.0
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var shadowOffset: CGSize? = null
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var shadowRadius: Double = 0.0
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    open fun drawInContext(ctx: Canvas) {
        if (this.hidden) { return }
        val boundsPath = createBoundsPath()
        ctx.save()
        this.drawShadow(ctx)
        if (this.masksToBounds) {
            ctx.clipPath(boundsPath)
        }
        ctx.drawPath(boundsPath, createBackgroundPaint())
        this.sublayers.forEach {
            ctx.save()
            ctx.translate((it.frame.x * scale).toFloat(), (it.frame.y * scale).toFloat())
            it.drawInContext(ctx)
            ctx.restore()
        }
        ctx.restore()
        if (this.borderWidth != 0.0 && this.borderColor != null) {
            ctx.drawPath(createBorderPath(), createBorderPaint())
        }
    }

    internal fun createBoundsPath(): Path {
        sharedOuterPath.reset()
        val outRect = RectF((max(0.0, this.borderWidth) * scale).toFloat(), (max(0.0, this.borderWidth) * scale).toFloat(), ((this.frame.width - max(0.0, this.borderWidth)) * scale).toFloat(), ((this.frame.width - max(0.0, this.borderWidth)) * scale).toFloat())
        sharedOuterPath.addRoundRect(outRect, (this.cornerRadius * scale).toFloat(), (this.cornerRadius * scale).toFloat(), Path.Direction.CCW)
        return sharedOuterPath
    }

    internal fun createBackgroundPaint(): Paint {
        sharedBackgroundPaint.reset()
        sharedBackgroundPaint.isAntiAlias = true
        sharedBackgroundPaint.color = Color.TRANSPARENT
        backgroundColor?.let {
            sharedBackgroundPaint.color = Color.argb(Math.ceil(it.a * 255.0).toInt(), Math.ceil(it.r * 255.0).toInt(), Math.ceil(it.g * 255.0).toInt(), Math.ceil(it.b * 255.0).toInt())
        }
        sharedBackgroundPaint.alpha = kotlin.run {
            var current: CALayer? = this
            var opacity = 1.0
            while (current != null) {
                opacity *= current.opacity
                current = current.superlayer
            }
            return@run max(0, min(255, (opacity * 255.0).toInt()))
        }
        return sharedBackgroundPaint
    }

    internal fun createBorderPath(): Path {
        sharedOuterPath.reset()
        val outRect = RectF((this.borderWidth / 2.0 * scale).toFloat(), (this.borderWidth / 2.0 * scale).toFloat(), ((this.frame.width - this.borderWidth / 2.0) * scale).toFloat(), ((this.frame.width - this.borderWidth / 2.0) * scale).toFloat())
        val radiusRatio = this.cornerRadius / max(this.frame.width, this.frame.height)
        val newCornerRadius = max(outRect.width(), outRect.height()) * radiusRatio
        sharedOuterPath.addRoundRect(outRect, newCornerRadius.toFloat(), newCornerRadius.toFloat(), Path.Direction.CCW)
        return sharedOuterPath
    }

    internal fun createBorderPaint(): Paint {
        sharedBackgroundPaint.reset()
        sharedBackgroundPaint.style = Paint.Style.STROKE
        sharedBackgroundPaint.strokeWidth = (abs(this.borderWidth) * scale).toFloat()
        sharedBackgroundPaint.isAntiAlias = true
        this.borderColor?.let {
            sharedBackgroundPaint.color = Color.argb(Math.ceil(it.a * 255.0).toInt(), Math.ceil(it.r * 255.0).toInt(), Math.ceil(it.g * 255.0).toInt(), Math.ceil(it.b * 255.0).toInt())
        }
        sharedBackgroundPaint.alpha = kotlin.run {
            var current: CALayer? = this
            var opacity = 1.0
            while (current != null) {
                opacity *= current!!.opacity
                current = current!!.superlayer
            }
            return@run max(0, min(255, (opacity * 255.0).toInt()))
        }
        return sharedBackgroundPaint
    }

    private var shadowBitmap: Bitmap? = null
        set(value) {
            field?.let { it.recycle() }
            field = value
        }

    private fun drawShadow(ctx: Canvas, boundsPath: Path = createBorderPath()) {
        val shadowColor = shadowColor ?: return
        val shadowOffset = shadowOffset ?: return
        if (shadowColor.a > 0 && shadowOpacity > 0 && shadowRadius > 0 && this.shadowBitmap == null) {
            val width = (this.frame.width + shadowRadius * 2) * scale
            val height = (this.frame.height + shadowRadius * 2) * scale
            val shadowBitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(shadowBitmap)
            val paint = Paint()
            shadowColor?.let {
                paint.color = Color.argb(Math.ceil(it.a * 255.0).toInt(), Math.ceil(it.r * 255.0).toInt(), Math.ceil(it.g * 255.0).toInt(), Math.ceil(it.b * 255.0).toInt())
            }
            paint.alpha = (shadowOpacity * 255).toInt()
            paint.maskFilter = BlurMaskFilter((shadowRadius * scale).toFloat(), BlurMaskFilter.Blur.NORMAL)
            canvas.translate((shadowRadius * scale).toFloat(), (shadowRadius * scale).toFloat())
            canvas.drawPath(boundsPath, paint)
            this.shadowBitmap = shadowBitmap
        }
        else {
            this.shadowBitmap = null
        }
        this.shadowBitmap?.let {
            sharedBackgroundPaint.reset()
            ctx.drawBitmap(
                    it,
                    ((-shadowRadius + shadowOffset.width) * scale).toFloat(),
                    ((-shadowRadius + shadowOffset.height) * scale).toFloat(),
                    sharedBackgroundPaint
            )
        }
    }

}

fun KIMIPackage.installCALayer() {
    exporter.exportClass(CALayer::class.java, "CALayer")
    exporter.exportProperty(CALayer::class.java, "frame")
    exporter.exportProperty(CALayer::class.java, "superlayer", true)
    exporter.exportMethodToJavaScript(CALayer::class.java, "removeFromSuperlayer")
    exporter.exportProperty(CALayer::class.java, "sublayers", true)
    exporter.exportMethodToJavaScript(CALayer::class.java, "addSublayer")
    exporter.exportMethodToJavaScript(CALayer::class.java, "insertSublayerAtIndex")
    exporter.exportMethodToJavaScript(CALayer::class.java, "insertSublayerBelow")
    exporter.exportMethodToJavaScript(CALayer::class.java, "insertSublayerAbove")
    exporter.exportMethodToJavaScript(CALayer::class.java, "replaceSublayer")
    exporter.exportProperty(CALayer::class.java, "hidden")
    exporter.exportProperty(CALayer::class.java, "opacity")
    exporter.exportProperty(CALayer::class.java, "masksToBounds")
    exporter.exportProperty(CALayer::class.java, "backgroundColor")
    exporter.exportProperty(CALayer::class.java, "cornerRadius")
    exporter.exportProperty(CALayer::class.java, "borderWidth")
    exporter.exportProperty(CALayer::class.java, "borderColor")
    exporter.exportProperty(CALayer::class.java, "shadowColor")
    exporter.exportProperty(CALayer::class.java, "shadowOpacity")
    exporter.exportProperty(CALayer::class.java, "shadowOffset")
    exporter.exportProperty(CALayer::class.java, "shadowRadius")
}