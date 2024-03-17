package com.example.time

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.os.Handler
import android.util.Log
import java.util.*

class TimeView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val paint = Paint();
    private val handler = Handler();
    private lateinit var runnable: Runnable;
    private var customFont: Typeface? = null;

    init {
        startClock();
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.TimeView);
        val fontPath = typedArray.getString(R.styleable.TimeView_customFont);
        typedArray.recycle();

        if (!fontPath.isNullOrEmpty()) {
            customFont = Typeface.createFromAsset(context.assets, fontPath);
        }
    }

    private fun startClock() {
        runnable = Runnable {
            invalidate(); // для перерисовки времени
            handler.postDelayed(runnable, 1000) // обновляем каждую секунду
        }
        handler.post(runnable);
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas);

        val xCentral  = width.toFloat() / 2; // координаты центра часов
        val yCentral  = height.toFloat() / 2;
        val radius = width.toFloat() / 2; // радиус часов

        paint.style = Paint.Style.FILL; // начальные настройки для фона часов
        paint.color = Color.rgb(230,230,230);
        canvas.drawCircle(xCentral, yCentral, radius, paint); // задаем круг с серой заливкой

        paint.style = Paint.Style.STROKE; // меняем кисть для отрисовки рамки
        paint.color = Color.BLACK;
        paint.strokeWidth = 10f;
        canvas.drawCircle(xCentral, yCentral, radius - 5, paint); // делаем обводку для часов (рамку)


        paint.style = Paint.Style.FILL; // меняем кисть для создания точек
        paint.color = Color.BLACK;


        val dotRadius = 2f; // для маленьких точек
        val dotAngleStep = Math.PI * 2 / 60;
        val dotRadius1 = 3f; // для жирных точек
        val dotAngleStep1 = Math.PI * 2 / 12;
        var number = 4;

        for (i in 1..60) { // создаем точки

            if (i % 5 == 0) { // жирные точки для главных чисел (от 1 до 12)
                val angle = i * dotAngleStep1;
                val x = xCentral + (radius - 30) * Math.cos(angle).toFloat();
                val y = yCentral + (radius - 30) * Math.sin(angle).toFloat();
                canvas.drawCircle(x, y, dotRadius1, paint);
                continue;
            }

            val angle = i * dotAngleStep;
            val x = xCentral + (radius - 30) * Math.cos(angle).toFloat();
            val y = yCentral + (radius - 30) * Math.sin(angle).toFloat();
            canvas.drawCircle(x, y, dotRadius, paint);
        }

        paint.textSize = 50F; // устанавливаем размер текста
        val typeface = Typeface.createFromAsset(context.assets, "fontTime.ttf"); // устанавливаем шрифт текста
        paint.typeface = customFont;

        for (i in 1..12) { // создаем числа от 1 до 12

            val angle = i * dotAngleStep1;
            val xNum = xCentral + (radius - 70) * Math.cos(angle).toFloat();
            val yNum = yCentral + (radius - 70) * Math.sin(angle).toFloat();

            val textWidth = paint.measureText(number.toString()); // размеры текста для каждого числа
            val textHeight = paint.fontMetrics.bottom - paint.fontMetrics.top;
            val xOffset = textWidth / 2; // смещение для выравнивания чисел
            val yOffset = textHeight / 2  - 15f;

            canvas.drawText(number.toString(), xNum - xOffset, yNum + yOffset, paint);
            number++;
            if (number > 12) number = 1;
        }

        super.onDraw(canvas);

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
        val hour = calendar.get(Calendar.HOUR_OF_DAY);
        val minute = calendar.get(Calendar.MINUTE);
        val second = calendar.get(Calendar.SECOND);

        val radiusForArrow = xCentral * 0.9f;

        val hourAngle = Math.toRadians(((hour % 12 + minute / 60.0) * 30)); // находим углы стрелок
        val minuteAngle = Math.toRadians((minute * 6).toDouble());
        val secondAngle = Math.toRadians((second * 6).toDouble());

        drawArrow(canvas, xCentral, yCentral, radiusForArrow * 0.5f, hourAngle, 10f, paint); // часовая стрелка
        drawArrow(canvas, xCentral, yCentral, radiusForArrow * 0.8f, minuteAngle, 5f, paint); // минутная стрелка
        drawArrow(canvas, xCentral, yCentral, radiusForArrow * 0.9f, secondAngle, 2f, paint); // секундная стрелка
    }

    private fun drawArrow(canvas: Canvas, xCenter: Float, yCenter: Float, length: Float, angle: Double, strokeWidth: Float, paint: Paint) {
        val x = (xCenter + length * Math.sin(angle)).toFloat();
        val y = (yCenter - length * Math.cos(angle)).toFloat();

        paint.strokeWidth = strokeWidth;
        canvas.drawLine(xCenter, yCenter, x, y, paint);
    }
}