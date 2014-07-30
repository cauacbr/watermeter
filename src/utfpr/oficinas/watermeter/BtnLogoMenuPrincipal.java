package utfpr.oficinas.watermeter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class BtnLogoMenuPrincipal extends View{
	
	private Context appContext;
	private final View currentInstance = this;
	
	private Bitmap logo;
	private Paint defaultPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
	private float fatorEscala;
	
	private Rect areaClique = new Rect();
	
	private OnTouchListener listenerLogo = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
						
			currentInstance.playSoundEffect(SoundEffectConstants.CLICK);
			
			if(areaClique.contains((int) (event.getX()), (int) (event.getY()))) {
				
				currentInstance.playSoundEffect(SoundEffectConstants.CLICK);
				Toast.makeText(appContext, "WaterMeter by\n\nCauã B. Rocha\nHenrique S. Ferreira\nLeandro F. Heroso\nRafael H. Zaleski\n\nOficinas III / 1oSem 2014", Toast.LENGTH_LONG).show();
				
			}
			
			return false;
		}
	};
	
	public BtnLogoMenuPrincipal(Context context, int widthView, int heightView) {
		super(context);
		
		appContext = context;
		logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_menu);
		
		float escala = (float) (logo.getHeight())/heightView;
		Log.d("motion", "fator escala: " + escala);
		
		int largura;
		int altura = (int) (logo.getHeight()/escala);
		
		if((logo.getWidth()/escala) < widthView) {
			largura = widthView;
			altura = heightView;
		}else {
			largura = (int)(logo.getWidth()/escala);
		}
		
		logo = Bitmap.createScaledBitmap(logo, largura, altura, true);
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawBitmap(logo, 0, 0, defaultPaint);
		
		areaClique.set((int) (logo.getWidth()/4.2), (int) (logo.getHeight()*0.05), (int) (logo.getWidth()/1.33), (int) (logo.getHeight()*0.95));
		
		this.setOnTouchListener(listenerLogo);
		
	}

	
	
}
