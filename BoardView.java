package com.example.slidepuzzle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import java.util.Iterator;

public class BoardView extends View {
	private final Board board;
	private float width;
	private float height;
	private Boolean endGame;
	private Bitmap oldBitmap;


	public BoardView(Context context, Board board) {
		super(context);
		this.board = board;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	public void SetImage(Bitmap bitmap){
		oldBitmap = bitmap;
	}

	public void EndGame(boolean end) {
		endGame = end;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldW, int oldH) {
		this.width = w / this.board.size();
		this.height = h / this.board.size();
//		this.width = 1024;
//		this.height = 1024;

		super.onSizeChanged(w, h, oldW, oldH);
	}

	private Place locatePlace(float x, float y) {
		int ix = (int) (x / width);
		int iy = (int) (y / height);

		return board.at(ix + 1, iy + 1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		Place p = locatePlace(event.getX(), event.getY());
		if (p != null && p.slidAble() && !board.solved()) {
			p.slide();
			invalidate();
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint background = new Paint();
		Bitmap bitmap;

		// 기본 이미지 설정
		if(oldBitmap == null) {
			Resources res = getResources();
			BitmapDrawable drawable = (BitmapDrawable) ResourcesCompat.getDrawable(res, R.drawable.google1, null);

			SetImage(drawable.getBitmap());
		}
		bitmap = Bitmap.createScaledBitmap(oldBitmap, 1000, 1000, true);


		background.setColor(Color.argb(1, 244, 184, 0));
		//canvas.drawBitmap(bitmap, 0, 0, background);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);

		Paint dark = new Paint();
		dark.setColor(Color.BLACK);
		dark.setStrokeWidth(15);

		// Draw the major grid lines
		for (int i = 0; i < this.board.size(); i++) {
			canvas.drawLine(0, i * height, getWidth(), i * height, dark);
			canvas.drawLine(i * width, 0, i * width, getHeight(), dark);
		}

		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
//		foreground.setColor(getResources().getColor(R.color.tile_color));

		foreground.setColor(Color.MAGENTA);
		foreground.setStyle(Paint.Style.FILL);
		foreground.setTextSize(height * 0.25f);

		float x = width / 2;
		Paint.FontMetrics fm = foreground.getFontMetrics();
		float y = (height / 2);// - (fm.ascent + fm.descent) / 2;

		Bitmap bitmapCut[] = new Bitmap[20];
		int widthQuarter = bitmap.getWidth() / 4;
		int heightQuarter = bitmap.getHeight() / 4;
		int index = 0;

		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++) {
				index++;
				bitmapCut[index] = Bitmap.createBitmap(bitmap, widthQuarter * j, heightQuarter * i, widthQuarter, heightQuarter);

			}
		}

		Iterator<Place> it = board.places().iterator();
		for (int i = 0; i < board.size(); i++) {
			for (int j = 0; j < board.size(); j++) {
				if (it.hasNext()) {
					Place p = it.next();
					if (p.hasTile()) {
//						String number = Integer.toString(p.getTile().getNumber());
						int number = p.getTile().getNumber();
						canvas.drawBitmap(bitmapCut[number], width * i + 44, height * j, foreground);


						if(!endGame)
							canvas.drawText(number + "", i * width + x + 50, j * height + y + 100, foreground);
					}
				}
			}
		}
	}
}
