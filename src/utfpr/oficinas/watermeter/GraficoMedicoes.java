package utfpr.oficinas.watermeter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GraficoMedicoes extends View{
	
	private Paint background, barsPaint;
	private int barMargin, barWidth, barHeight;
	private Paint bars[];
	private ArrayList<String> quantitiesLabel = new ArrayList<String>(); 
	private ArrayList<String> datesLabel = new ArrayList<String>();
	
	private ArrayList<Integer> quantities = new ArrayList<Integer>();
	
	private FakeManager dbManager = new FakeManager();
	
	private final Path path = new Path();
	
	private static final int MAX_TRANSLATION = 0;
    private int MIN_TRANSLATION;
    
    //************** BARRAS DO GR�FICO
    
    List<Rect> barras = new ArrayList<Rect>();
    Rect auxRect = new Rect();
    
    
    //**************
	
	//Controla a ilus�o de Drag no Gr�fico
	private int actualTranslation = 0;
	
	private OnTouchListener graficoDragListener = new OnTouchListener() {

	    
	    private int viewWidth, viewHeight;
	    private int startX, startY, endX, endY;
	    

	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
	    		    	
	    	viewWidth = v.getWidth();
	    	viewHeight = v.getHeight();
	    	
	        switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN: {
	                //Log.d("motion", "startX = " + event.getX() + "   startY = " + event.getY());
	                
	                startX = (int) event.getX();
	                startY = (int) event.getY();
	                
	                break;
	            }
	            case MotionEvent.ACTION_UP: {
	                //Log.d("motion", "endX = " + event.getX() + "   endY = " + event.getY());
	                
	                endX = (int) event.getX();
	                endY = (int) event.getY();
	                
	                if(-(endX - startX) > viewWidth/4) { //transladar para esquerda
	                	
	                	Log.d("motion", "Transladar Esquerda...");
	                	Log.d("motion", "MIN_TRANSLATION: " + MIN_TRANSLATION);
	                	Log.d("motion", "Atual: " + actualTranslation );
	                	Log.d("motion", "Pretendido: " + (actualTranslation - 2*(barWidth + barMargin)));
	                		                	
                		int aux;
	                	
	                	if((aux = actualTranslation - 2*(barWidth + barMargin)) < MIN_TRANSLATION) {
	                		if(aux != MIN_TRANSLATION) 
	                			actualTranslation = MIN_TRANSLATION; //� maior que a transla��o m�nima, mas n�o � poss�vel transladar 2 vezes
	                	} else {
	                		actualTranslation = aux;
	                	}
	                		                	
	                	v.invalidate();
	                	
	                }else if((endX - startX) > viewWidth/4) { //transladar para direita
	                	
	                	Log.d("motion", "Transladar Direita...");
	                	Log.d("motion", "MAX_TRANSLATION: " + MAX_TRANSLATION);
	                	Log.d("motion", "Atual: " + actualTranslation );
	                	Log.d("motion", "Pretendido: " + (actualTranslation + 2*(barWidth + barMargin)));
	                		                	
                		int aux;
	                	
	                	if((aux = actualTranslation + 2*(barWidth + barMargin)) > MAX_TRANSLATION) {
	                		if(aux != MAX_TRANSLATION) 
	                			actualTranslation = MAX_TRANSLATION; //� maior que a transla��o m�nima, mas n�o � poss�vel transladar 2 vezes
	                	} else {
	                		actualTranslation = aux;
	                	}
	                		                	
	                	v.invalidate();
	                	
	                }
	                
	                if(Math.abs(startX - endX) < 50) { //Deslizou no m�ximo 50 pixels
	                    //Foi um click, verificar qual barra MUDAR ISSO PARA UMA L�GICA XY (Xstart - Xend for pequeno)
	                	
	                	Log.d("motion", "clique na view do gr�fico");
	                }
	            }
	        }
	        return true;
	    }
	
	};
	
	public GraficoMedicoes(Context context) {
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int viewWidth = getWidth();
		int viewHeight = getHeight();
		
		path.addRect(viewWidth/20, 0, (float) (viewWidth*0.95), viewHeight, Direction.CW);
		canvas.clipPath(path);
	    

		int sumConsumoLitros = 0;
		int sumConsumoMililitros = 0;

		background = new Paint(Paint.ANTI_ALIAS_FLAG);
		background.setColor(Color.WHITE);
		background.setStrokeWidth(2);
		
		barsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		barsPaint.setColor(Color.WHITE);
		
		barWidth = (int) (viewWidth/7.7);
		barMargin = (int) (barWidth/6.5);
		
		barHeight = (int) (viewHeight*0.65);
		
		//Desenhando as linhas de Fundo
		
		canvas.drawLine(viewWidth/20, (float) (viewHeight*0.75), (float) (viewWidth*0.95), (float) (viewHeight*0.75), background);
		canvas.drawLine(viewWidth/20, viewHeight/20, viewWidth/20, (float) (viewHeight*0.75), background);
				
		// Desenhando as barras
		
		int maiorMedicaoAcumulada = 0;
		
		for(int i = dbManager.getMinYear() ; i <= dbManager.getMaxYear(); i++) {
			
			for(Medicao medicao: dbManager.getMedicoesByYear(i)) {
				sumConsumoLitros += medicao.getLitro();
				sumConsumoMililitros += medicao.getMiliLitro();
			}
			
			sumConsumoLitros += sumConsumoMililitros/1000;
			sumConsumoMililitros = sumConsumoMililitros%1000;
			
			quantities.add(sumConsumoLitros + sumConsumoMililitros/500); //arredonda para cima o comprimento da barra
			
			quantitiesLabel.add(String.valueOf(sumConsumoLitros) + "," + String.valueOf(sumConsumoMililitros/100 + " L"));
			datesLabel.add(String.valueOf(i));
			
			if((sumConsumoLitros + sumConsumoMililitros%500) > maiorMedicaoAcumulada)
				maiorMedicaoAcumulada = (sumConsumoLitros + sumConsumoMililitros%500);
			
			sumConsumoLitros = sumConsumoMililitros = 0;
			
		}
		
		//*********************************
		
		MIN_TRANSLATION = -1*((dbManager.getMaxYear() - dbManager.getMinYear()) - 5)*(barWidth + barMargin); //total de barras - 5. 6 � o n�mero que cabe na tela
		
		Log.d("motion", "max_year: " + dbManager.getMaxYear());
		
		//*********************************
		
		int offset = barMargin;
		float x0 = viewWidth/20;
		float y0 = (float) (viewHeight*0.75);
		
		Paint teste = new Paint(Paint.ANTI_ALIAS_FLAG);
		teste.setTextSize((float)(barWidth/3));
		teste.setColor(Color.WHITE);
		teste.setStyle(Style.FILL);		
		
		
		for(Integer bar : quantities) {
			
			auxRect.set((int) (x0 + offset + actualTranslation), (int) (y0 - (barHeight * ((float)(bar)/maiorMedicaoAcumulada))), (int) (x0 + offset + actualTranslation + barWidth), (int) (y0));
			
			barras.add(auxRect);
			canvas.drawRect(auxRect, barsPaint);
			
			canvas.save();
			canvas.rotate(-60,  x0 + offset + actualTranslation + (barWidth - teste.measureText(datesLabel.get(quantities.indexOf(bar)))), (float) ((y0 - (barHeight * ((float)(bar)/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()));
			

			// Desenhando as legendas de consumo
			
			canvas.drawText(quantitiesLabel.get(quantities.indexOf(bar)), x0 + offset + actualTranslation + (barWidth - teste.measureText(datesLabel.get(quantities.indexOf(bar)))), (float) ((y0 - (barHeight * ((float)(bar)/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()), teste);
			//canvas.drawText(String.valueOf((float)(bar)/maiorMedicaoAcumulada), x0 + offset + (barWidth - teste.measureText(datesLabel.get(quantities.indexOf(bar)))), (float) ((y0 - (barHeight * ((float)(bar)/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()), teste);
			
			canvas.restore();
			
			// Desenhando as legendas de datas
			
			canvas.drawText(datesLabel.get(quantities.indexOf(bar)), x0 + offset + actualTranslation + ((barWidth - teste.measureText(datesLabel.get(quantities.indexOf(bar)))) /2 ), y0 + teste.getTextSize() + 5, teste);
			
			offset += barMargin + barWidth;
			
		}

		this.setOnTouchListener(graficoDragListener);
		
	}
	
}