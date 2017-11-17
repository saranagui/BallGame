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
import android.util.Log;
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

	//for the countdown
	int count=3000;

	private Handler h;
	private final int FRAME_RATE = 60;
	
	
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

		final int canvasWidth = this.getWidth();
		final int canvasHeight = this.getHeight();

		BitmapDrawable ball = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.ball);  
	    BitmapDrawable line = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.bar);
		BitmapDrawable box  = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.box);

		AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();


		//for the box animation
		Bitmap b = box.getBitmap();
		Bitmap bitmapResized;

		//text style of the countdown
		final Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		c.drawPaint(paint);
		paint.setTextSize(400);
		paint.setColor(Color.GRAY);

		//the countdown
		c.drawText(""+(count/100), canvasWidth/2-200, canvasHeight/2, paint);
		count--;
		if(count==0){
			won=2;
		}

		//won = 0 -> still playing, won = 1 -> player won, won = 2 -> player lost
		switch(won){
			//bitmapResized = Bitmap.createScaledBitmap(b, (box.getBitmap().getWidth()-1), (box.getBitmap().getWidth()-1), false);
			//box = new BitmapDrawable(getResources(), bitmapResized);
			//boxX++;
			//boxY++;
			case 1:	won = -1;
					box.setBounds(boxX, boxY, 1, 1);
					invalidate();
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
			case 2: won = -1;
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
		//initialization of the position of the bar
		if(lineX<0 && lineY<0){
	    	lineX = this.getWidth()/2 - line.getBitmap().getWidth()/2;
			lineY = this.getHeight()*7/8 - line.getBitmap().getHeight()/2;
		}
		//initialization of the position of the goal box
		if(boxX<0 && boxY<0){
			boxX = this.getWidth()/2 - box.getBitmap().getWidth()/2;
			boxY = this.getHeight()/8 - box.getBitmap().getHeight()/2;
		}
		//initialization of the position of the scaling of the goal box (should be used for animation)
		if(scale<0){
			scale = box.getBitmap().getWidth();
		}
		//initialization of the position of the ball
		if (x<0 && y <0) {
	    	x = this.getWidth()/2;
	    	y = this.getHeight()/2;
	    }
	    //movement of the ball
	    else {
	    	x += xVelocity;
	    	y += yVelocity;
	    	//ball hits the bar
			if ((x > lineX - ball.getBitmap().getWidth()) && (x < (lineX+200)) && (y > lineY - ball.getBitmap().getHeight()) && (y < lineY)) {
				xVelocity = xVelocity*-1;
				yVelocity = yVelocity*-1;

				invalidate();
			}
			//ball hits the target box and player wins
			if ((x > boxX - ball.getBitmap().getWidth()) && (x < (boxX+100)) && (y > boxY - ball.getBitmap().getHeight()) && (y < boxY)) {
				won = 1;
				xVelocity = xVelocity*-1;
				yVelocity = yVelocity*-1;
			}
			//ball out of window bounds
	    	if ((x > this.getWidth() - ball.getBitmap().getWidth()) || (x < 0)) {
				xVelocity = xVelocity * -1;
				invalidate();
			}
	    	if ((y > this.getHeight() - ball.getBitmap().getHeight()) || (y < 0)) {
	    		yVelocity = yVelocity*-1;
				invalidate();
			}

	    }

	    //draw everything on canvas
		c.drawBitmap(box.getBitmap(), boxX, boxY, null);
		c.drawBitmap(ball.getBitmap(), x, y, null);
		c.drawBitmap(line.getBitmap(), lineX, lineY, null);


		h.postDelayed(r, FRAME_RATE);

	    	      
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		//when bar is pressed, move it left and right according to the movement of the mouse or touch
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
