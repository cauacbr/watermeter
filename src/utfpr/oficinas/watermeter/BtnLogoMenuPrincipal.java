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
				Toast.makeText(appContext, "Exibir Mais Dados sobre o consumo com este botão.", Toast.LENGTH_LONG).show();
				
			}
			
			return false;
		}
	};
	
	public BtnLogoMenuPrincipal(Context context) {
		super(context);
		
		appContext = context;
		logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_menu);
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		fatorEscala = (float) (this.getWidth())/logo.getWidth();
		
		//Log.d("motion", "fator escala: " + fatorEscala);
		
		logo = Bitmap.createScaledBitmap(logo, this.getWidth(), (int) (fatorEscala*logo.getHeight()), true);
		
		canvas.drawBitmap(logo, 0, 0, defaultPaint);
		
		areaClique.set((int) (logo.getWidth()/4.2), (int) (logo.getHeight()*0.05), (int) (logo.getWidth()/1.33), (int) (logo.getHeight()*0.95));
		
		this.setOnTouchListener(listenerLogo);
		
	}

	
	
}
