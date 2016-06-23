package com.example.cherubim.FileManager;

import android.content.*;
import android.net.Uri;
import android.view.*;
import android.graphics.*;

public class StartView extends View
{
	float cX = 0, cY = 0, R = 0,X,Y;
	int A;
	boolean v = true,g= false;
	Paint p=new Paint(),p1=new Paint(),p2=new Paint();
	public StartView(Context c){
		super(c);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO: Implement this method
		
		if(v){
			R = getWidth()/20;
			cX = getWidth()/3-R;
			X = getWidth()/2-3*(getWidth()/28);
			cY = getHeight()/3*2-R;
			Y=0;
			A = 0;
			v=false;
		}
		if(g){
			A = A +2;
			if(A>255){
				A=255;
			}
		}
		p2.setColor(Color.rgb(255,255,255));
		p.setColor(Color.rgb(0x00,0x00,0x00));
		p1.setColor(Color.rgb(0x00,0x8a,0xff));
		p1.setAlpha(A);
		p.setTextSize(getWidth()/30);
		p1.setTextSize(getWidth()/7);
		//canvas.drawColor(Color.alpha(0x00));

		canvas.drawText("By Cherubim github.com/qq0313/FileManager",X-60,Y,p2);
		canvas.drawText("文件管理器",getWidth()/2-getWidth()/3,getHeight()/2,p1);
		if(Y<cY){
			
			Y = Y+getHeight()/30;
			invalidate();
		}else if(Y>cY&&Y<getHeight()-R){
			Y = Y + getHeight()/15;
			invalidate();
		}else{
			Y = getHeight()-10;
			g=true;
			invalidate();
		}
		
		super.onDraw(canvas);
	}
	
}
