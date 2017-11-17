package com.authorwjf.bounce;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

public class AnimatedView extends ImageView{
	Canvas mC;
	private Context mContext;

	//x, y of the ball
	int x = -1;
	int y = -1;
	private int xVelocity = 10;
	private int yVelocity = 5;

	//x, y of the paddle
	int lineX = -1;
	int lineY = -1;

	//x, y of the box
	int boxX = -1;
	int boxY = -1;
	int scale = -1;
	int won = 0;

	private Handler h;
	private final int FRAME_RATE = 30;
	
	
	public AnimatedView(Context context, AttributeSet attrs)  {  
		super(context, attrs);  
		mContext = context;  
		h = new Handler();
    } 
	
	private Runnable r = new Runnable() {
		@Override
		public void run() {
			invalidate(); 
		}
	};
	
	protected void onDraw(final Canvas c) {

		  mC= c;
		final int canvasWidth = this.getWidth();
		final int canvasHeight = this.getHeight();
		BitmapDrawable ball = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.ball);  
	    BitmapDrawable line = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.bar);
		BitmapDrawable box  = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.box);
		Bitmap b = box.getBitmap();
		Bitmap bitmapResized;

		final Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		mC.drawPaint(paint);

		paint.setColor(Color.WHITE);
		paint.setTextSize(200);
		//final TextView mText = new TextView(mContext);

		//CountDownTimer myTimer =

		//c.drawText(mText.getText().toString(), this.getWidth()/2 - mText.getText().length()/2, this.getHeight()/2 - 100, paint);

//		String mString = "";
//		for(int i=30; i>=0;i--){
//			mString = ""+i;
//		}
//		c.drawText(mString, this.getWidth()/2 - 100, this.getHeight()/2 - 100, paint);
		switch(won){
			//bitmapResized = Bitmap.createScaledBitmap(b, (box.getBitmap().getWidth()-1), (box.getBitmap().getWidth()-1), false);
			//box = new BitmapDrawable(getResources(), bitmapResized);
			//boxX++;
			//boxY++;
			case 1:	box.setBounds(boxX, boxY, 1, 1);
					invalidate();
					break;
			case 2: AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
					alertDialog.setTitle("Hard Luck!");
					alertDialog.setMessage("YOU LOST :(");
					alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
					alertDialog.show();
					invalidate();
					return;
		}
		if(lineX<0 && lineY<0){
	    	lineX = this.getWidth()/2 - line.getBitmap().getWidth()/2;
			lineY = this.getHeight()*7/8 - line.getBitmap().getHeight()/2;
		}
		if(boxX<0 && boxY<0){
			boxX = this.getWidth()/2 - box.getBitmap().getWidth()/2;
			boxY = this.getHeight()/8 - box.getBitmap().getHeight()/2;
		}
		if(scale<0){
			scale = box.getBitmap().getWidth();
		}
	    if (x<0 && y <0) {
	    	x = this.getWidth()/2;
	    	y = this.getHeight()/2;
	    } else {
	    	x += xVelocity;
	    	y += yVelocity;
			if ((x > lineX - ball.getBitmap().getWidth()) && (x < (lineX+200)) && (y > lineY - ball.getBitmap().getHeight()) && (y < lineY)) {
				xVelocity = xVelocity*-1;
				yVelocity = yVelocity*-1;

				invalidate();
			}

			if ((x > boxX - ball.getBitmap().getWidth()) && (x < (boxX+100)) && (y > boxY - ball.getBitmap().getHeight()) && (y < boxY)) {
				won = 1;
				xVelocity = xVelocity*-1;
				yVelocity = yVelocity*-1;

				AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
				alertDialog.setTitle("Congratulations");
				alertDialog.setMessage("YOU WON!");
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				alertDialog.show();

				invalidate();
				return;
			}

	    	if ((x > this.getWidth() - ball.getBitmap().getWidth()) || (x < 0)) {
				xVelocity = xVelocity * -1;
				invalidate();
			}
	    	if ((y > this.getHeight() - ball.getBitmap().getHeight()) || (y < 0)) {
	    		yVelocity = yVelocity*-1;
				invalidate();
			}

	    }

	    c.drawBitmap(ball.getBitmap(), x, y, null);
		c.drawBitmap(line.getBitmap(), lineX, lineY, null);
		c.drawBitmap(box.getBitmap(), boxX, boxY, null);
		new CountDownTimer(30000, 1000) {

			public void onTick(long millisUntilFinished) {
				c.drawText(""+(millisUntilFinished / 1000), 100, 100, paint);
			}
			public void onFinish() {

				won=2;

			}
		}.start();
		h.postDelayed(r, FRAME_RATE);

	    	      
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if(event.getAction() == MotionEvent.ACTION_DOWN){
			int mouseX = (int)event.getX();
			int mouseY = (int)event.getY();

			if (mouseX > lineX && mouseX < lineX + 200 && mouseY > lineY && mouseY < lineY + 30){
				lineX = (int)event.getX();
			}
			invalidate();
		}

		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			int mouseX = (int)event.getX();

			lineX = mouseX - 100;
			invalidate();
		}

		if(event.getAction() == MotionEvent.ACTION_UP) {

		}

		invalidate();

		return true;
	}
}
