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

/**
 * @author Rafael
 *
 */
public class GraficoMedicoes extends View{
	
	private static final int ZOOM_YEARS = 0;
	private static final int ZOOM_MONTHS = 1;
	private static final int ZOOM_DAYS = 2;
	private static final int ZOOM_HOURS = 3;
	private Integer currentZoom = null;
	private int currentZoomRefAno = -1;
	private int currentZoomRefMes = -1;
	private int currentZoomRefDia = -1;
	
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
	                	
//	                	Log.d("motion", "Transladar Esquerda...");
//	                	Log.d("motion", "MIN_TRANSLATION: " + MIN_TRANSLATION);
//	                	Log.d("motion", "Atual: " + actualTranslation );
//	                	Log.d("motion", "Pretendido: " + (actualTranslation - 2*(barWidth + barMargin)));
	                		                	
                		int aux;
	                	
	                	if((aux = actualTranslation - 2*(barWidth + barMargin)) < MIN_TRANSLATION) {
	                		if(aux != MIN_TRANSLATION) 
	                			actualTranslation = MIN_TRANSLATION; //� maior que a transla��o m�nima, mas n�o � poss�vel transladar 2 vezes
	                	} else {
	                		actualTranslation = aux;
	                	}
	                		                	
	                	v.invalidate();
	                	
	                }else if((endX - startX) > viewWidth/4) { //transladar para direita
	                	
//	                	Log.d("motion", "Transladar Direita...");
//	                	Log.d("motion", "MAX_TRANSLATION: " + MAX_TRANSLATION);
//	                	Log.d("motion", "Atual: " + actualTranslation );
//	                	Log.d("motion", "Pretendido: " + (actualTranslation + 2*(barWidth + barMargin)));
	                		                	
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
	                	
	                	Log.d("motion", "clique na view do gr�fico");
	                	
	                	for(Rect barraAtual: barras) {
	                		
	                		if(barraAtual.contains(startX, startY)) {
	                			//Log.d("motion", "Clique na Barra " + barras.indexOf(barraAtual));
	                			
	                			calcularExpansao(barras.indexOf(barraAtual));
	                			
	                			v.invalidate();
	                			
	                		}
	                		
	                	}
	                	
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
		
		if(currentZoom == null)
			currentZoom = ZOOM_YEARS;
		
		plotarGrafico(canvas);
		
	}
	
	
	private void plotarGrafico(Canvas canvas) {
		
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
		
		barHeight = (int) (viewHeight*0.60);
		
		//Desenhando as linhas de Fundo
		
		canvas.drawLine(viewWidth/20, (float) (viewHeight*0.75), (float) (viewWidth*0.95), (float) (viewHeight*0.75), background);
		canvas.drawLine(viewWidth/20, viewHeight/20, viewWidth/20, (float) (viewHeight*0.75), background);
		
		int maiorMedicaoAcumulada;
		int offset;
		float x0, y0;
		
		Paint teste = new Paint(Paint.ANTI_ALIAS_FLAG);
		teste.setTextSize((float)(barWidth/3));
		teste.setColor(Color.WHITE);
		teste.setStyle(Style.FILL);
		
		offset = barMargin;
		x0 = viewWidth/20;
		y0 = (float) (viewHeight*0.75);	
		
		barras.clear(); //Limpa lista de barras atuais
		quantities.clear(); //Limpa medi��es atuais
		datesLabel.clear();
		quantitiesLabel.clear();
		
		sumConsumoLitros = sumConsumoMililitros = 0;
		maiorMedicaoAcumulada = 0;
		
		switch(currentZoom) {
			
			case ZOOM_YEARS: /********************************************************** ZOOM_YEAR **********************************************************************************/
				
				
				
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
					
					if((sumConsumoLitros + sumConsumoMililitros/500) > maiorMedicaoAcumulada)
						maiorMedicaoAcumulada = (sumConsumoLitros + 1);
					
					sumConsumoLitros = sumConsumoMililitros = 0;
					
					
				}
				
				//*********************************
				
				MIN_TRANSLATION = -1*((dbManager.getMaxYear() - dbManager.getMinYear()) - 5)*(barWidth + barMargin); //total de barras - 5. 6 � o n�mero que cabe na tela
				
				//*********************************
				

				//Log.d("motion", "quantities.size() = " + quantities.size());
				
				for(int i = 0; i < quantities.size(); i++) {
					
					//Log.d("motion", "i = " + i);
					
					auxRect = new Rect();
					auxRect.set((int) (x0 + offset + actualTranslation), (int) (y0 - (barHeight * ((float)(quantities.get(i))/maiorMedicaoAcumulada))), (int) (x0 + offset + actualTranslation + barWidth), (int) (y0));
					
					barras.add(auxRect);
					canvas.drawRect(auxRect, barsPaint);
					
					canvas.save();
					canvas.rotate(-60,  (float) (x0 + offset + actualTranslation + (barWidth/4.5)), (float) ((y0 - (barHeight * ((float)(quantities.get(i))/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()));
					

					// Desenhando as legendas de consumo
					
					canvas.drawText(quantitiesLabel.get(i), (float) (x0 + offset + actualTranslation + (barWidth/4.5)), (float) ((y0 - (barHeight * ((float)(quantities.get(i))/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()), teste);
					//canvas.drawText(String.valueOf((float)(bar)/maiorMedicaoAcumulada), x0 + offset + (barWidth - teste.measureText(datesLabel.get(quantities.indexOf(bar)))), (float) ((y0 - (barHeight * ((float)(bar)/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()), teste);
					
					canvas.restore();
					
					// Desenhando as legendas de datas
					
					canvas.drawText(datesLabel.get(i), x0 + offset + actualTranslation + ((barWidth - teste.measureText(datesLabel.get(i))) /2 ), y0 + teste.getTextSize() + 5, teste);
					
					offset += barMargin + barWidth;
					
					//Log.d("motion", "fim i = " + i);
					
				}

				this.setOnTouchListener(graficoDragListener);
				
				break;
				
				/************************************************************* FIM ZOOM_YEAR **************************************************************************************/
				
				case ZOOM_MONTHS: /********************************************************** ZOOM_MONTH **********************************************************************************/
										
					for(int i = dbManager.getMinMonth(currentZoomRefAno) ; i <= dbManager.getMaxMonth(currentZoomRefAno); i++) {
						
						for(Medicao medicao: dbManager.getMedicoesByMonth(i, currentZoomRefAno)) {
							sumConsumoLitros += medicao.getLitro();
							sumConsumoMililitros += medicao.getMiliLitro();
						}
						
						sumConsumoLitros += sumConsumoMililitros/1000;
						sumConsumoMililitros = sumConsumoMililitros%1000;
						
						quantities.add(sumConsumoLitros + sumConsumoMililitros/500); //arredonda para cima o comprimento da barra
						
						quantitiesLabel.add(String.valueOf(sumConsumoLitros) + "," + String.valueOf(sumConsumoMililitros/100 + " L"));
						
						switch(i) {
						
							case 1:
								datesLabel.add("Jan");
							case 2:
								datesLabel.add("Fev");
							case 3:
								datesLabel.add("Mar");
							case 4:
								datesLabel.add("Abr");
							case 5:
								datesLabel.add("Mai");
							case 6:
								datesLabel.add("Jun");
							case 7:
								datesLabel.add("Jul");
							case 8:
								datesLabel.add("Ago");
							case 9:
								datesLabel.add("Set");
							case 10:
								datesLabel.add("Out");
							case 11:
								datesLabel.add("Nov");
							case 12:
								datesLabel.add("Dez");
						
						}
						 
						if((sumConsumoLitros + sumConsumoMililitros/500) > maiorMedicaoAcumulada)
							maiorMedicaoAcumulada = (sumConsumoLitros + 1);
						
						//Log.d("motion", "M�s " + i + ": " + String.valueOf(sumConsumoLitros) + "," + String.valueOf(sumConsumoMililitros/100 + " L"));
						
						sumConsumoLitros = sumConsumoMililitros = 0;
						
						
												
					}
					
					//*********************************
					
					MIN_TRANSLATION = -1*((dbManager.getMaxMonth(currentZoomRefAno) - dbManager.getMinMonth(currentZoomRefAno)) - 5)*(barWidth + barMargin); //total de barras - 5. 6 � o n�mero que cabe na tela
					
					//*********************************
					
					//Log.d("motion", "maior medi��o acumulada: " + maiorMedicaoAcumulada);
					
					//Log.d("motion", "quantities.size() = " + quantities.size());
					
					for(int i = 0; i < quantities.size(); i++) {
						
						//Log.d("motion", "i = " + i);
						
						auxRect = new Rect();
						auxRect.set((int) (x0 + offset + actualTranslation), (int) (y0 - (barHeight * ((float)(quantities.get(i))/maiorMedicaoAcumulada))), (int) (x0 + offset + actualTranslation + barWidth), (int) (y0));
						
						barras.add(auxRect);
						canvas.drawRect(auxRect, barsPaint);
						
						canvas.save();
						canvas.rotate(-60,  (float) (x0 + offset + actualTranslation + (barWidth/4.5)), (float) ((y0 - (barHeight * ((float)(quantities.get(i))/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()));
						
	
						// Desenhando as legendas de consumo
						
						canvas.drawText(quantitiesLabel.get(i), (float) (x0 + offset + actualTranslation + (barWidth/4.5)), (float) ((y0 - (barHeight * ((float)(quantities.get(i))/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()), teste);
						//canvas.drawText(String.valueOf((float)(bar)/maiorMedicaoAcumulada), x0 + offset + (barWidth - teste.measureText(datesLabel.get(quantities.indexOf(bar)))), (float) ((y0 - (barHeight * ((float)(bar)/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()), teste);
						
						canvas.restore();
						
						// Desenhando as legendas de datas
						
						canvas.drawText(datesLabel.get(i), x0 + offset + actualTranslation + ((barWidth - teste.measureText(datesLabel.get(i))) /2 ), y0 + teste.getTextSize() + 5, teste);
						
						offset += barMargin + barWidth;
						
						//Log.d("motion", "fim i = " + i);
						
					}
	
					this.setOnTouchListener(graficoDragListener);
					
					break;
				
				/************************************************************* FIM ZOOM_MONTH **************************************************************************************/
				
				case ZOOM_DAYS: /********************************************************** ZOOM_DAY **********************************************************************************/
					
					for(int i = dbManager.getMinDay(currentZoomRefMes, currentZoomRefAno) ; i <= dbManager.getMaxDay(currentZoomRefMes, currentZoomRefAno); i++) {
						
						for(Medicao medicao: dbManager.getMedicoesByDay(i, currentZoomRefMes, currentZoomRefAno)) {
							sumConsumoLitros += medicao.getLitro();
							sumConsumoMililitros += medicao.getMiliLitro();
						}
						
						sumConsumoLitros += sumConsumoMililitros/1000;
						sumConsumoMililitros = sumConsumoMililitros%1000;
						
						quantities.add(sumConsumoLitros + sumConsumoMililitros/500); //arredonda para cima o comprimento da barra
						
						quantitiesLabel.add(String.valueOf(sumConsumoLitros) + "," + String.valueOf(sumConsumoMililitros/100 + " L"));
						datesLabel.add(String.valueOf(i));
						
						if((sumConsumoLitros + sumConsumoMililitros/500) > maiorMedicaoAcumulada)
							maiorMedicaoAcumulada = (sumConsumoLitros + 1);
												
						sumConsumoLitros = sumConsumoMililitros = 0;
						
						
												
					}
					
					//*********************************
					
					MIN_TRANSLATION = -1*((dbManager.getMaxDay(currentZoomRefMes, currentZoomRefAno) - dbManager.getMinDay(currentZoomRefMes, currentZoomRefAno)) - 5)*(barWidth + barMargin); //total de barras - 5. 6 � o n�mero que cabe na tela
					
					//*********************************
					
					//Log.d("motion", "maior medi��o acumulada: " + maiorMedicaoAcumulada);
					
					//Log.d("motion", "quantities.size() = " + quantities.size());
					
					for(int i = 0; i < quantities.size(); i++) {
						
						//Log.d("motion", "i = " + i);
						
						auxRect = new Rect();
						auxRect.set((int) (x0 + offset + actualTranslation), (int) (y0 - (barHeight * ((float)(quantities.get(i))/maiorMedicaoAcumulada))), (int) (x0 + offset + actualTranslation + barWidth), (int) (y0));
						
						barras.add(auxRect);
						canvas.drawRect(auxRect, barsPaint);
						
						canvas.save();
						canvas.rotate(-60,  (float) (x0 + offset + actualTranslation + (barWidth/4.5)), (float) ((y0 - (barHeight * ((float)(quantities.get(i))/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()));
						
	
						// Desenhando as legendas de consumo
						
						canvas.drawText(quantitiesLabel.get(i), (float) (x0 + offset + actualTranslation + (barWidth/4.5)), (float) ((y0 - (barHeight * ((float)(quantities.get(i))/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()), teste);
						//canvas.drawText(String.valueOf((float)(bar)/maiorMedicaoAcumulada), x0 + offset + (barWidth - teste.measureText(datesLabel.get(quantities.indexOf(bar)))), (float) ((y0 - (barHeight * ((float)(bar)/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()), teste);
						
						canvas.restore();
						
						// Desenhando as legendas de datas
						
						canvas.drawText(datesLabel.get(i), x0 + offset + actualTranslation + ((barWidth - teste.measureText(datesLabel.get(i))) /2 ), y0 + teste.getTextSize() + 5, teste);
						
						offset += barMargin + barWidth;
						
						//Log.d("motion", "fim i = " + i);
						
					}
	
					this.setOnTouchListener(graficoDragListener);
					
					break;
				
				/************************************************************* FIM ZOOM_DAY **************************************************************************************/
				
				case ZOOM_HOURS: /********************************************************** ZOOM_HOUR **********************************************************************************/
					
					for(int i = dbManager.getMinHour(currentZoomRefDia, currentZoomRefMes, currentZoomRefAno) ; i <= dbManager.getMaxHour(currentZoomRefDia, currentZoomRefMes, currentZoomRefAno); i++) {
						
						for(Medicao medicao: dbManager.getMedicoesByHour(i, currentZoomRefDia, currentZoomRefMes, currentZoomRefAno)) {
							sumConsumoLitros += medicao.getLitro();
							sumConsumoMililitros += medicao.getMiliLitro();
						}
						
						sumConsumoLitros += sumConsumoMililitros/1000;
						sumConsumoMililitros = sumConsumoMililitros%1000;
						
						quantities.add(sumConsumoLitros + sumConsumoMililitros/500); //arredonda para cima o comprimento da barra
						
						quantitiesLabel.add(String.valueOf(sumConsumoLitros) + "," + String.valueOf(sumConsumoMililitros/100 + " L"));
						datesLabel.add(String.valueOf(i));
						
						if((sumConsumoLitros + sumConsumoMililitros/500) > maiorMedicaoAcumulada)
							maiorMedicaoAcumulada = (sumConsumoLitros + 1);
												
						sumConsumoLitros = sumConsumoMililitros = 0;
						
												
					}
					
					//*********************************
					
					MIN_TRANSLATION = -1*((dbManager.getMaxHour(currentZoomRefDia, currentZoomRefMes, currentZoomRefAno) - dbManager.getMinHour(currentZoomRefDia, currentZoomRefMes, currentZoomRefAno)) - 5)*(barWidth + barMargin); //total de barras - 5. 6 � o n�mero que cabe na tela
										
					//*********************************
					
					//Log.d("motion", "maior medi��o acumulada: " + maiorMedicaoAcumulada);
					
					//Log.d("motion", "quantities.size() = " + quantities.size());
					
					for(int i = 0; i < quantities.size(); i++) {
						
						//Log.d("motion", "i = " + i);
						
						auxRect = new Rect();
						auxRect.set((int) (x0 + offset + actualTranslation), (int) (y0 - (barHeight * ((float)(quantities.get(i))/maiorMedicaoAcumulada))), (int) (x0 + offset + actualTranslation + barWidth), (int) (y0));
						
						barras.add(auxRect);
						canvas.drawRect(auxRect, barsPaint);
						
						canvas.save();
						canvas.rotate(-60,  (float) (x0 + offset + actualTranslation + (barWidth/4.5)), (float) ((y0 - (barHeight * ((float)(quantities.get(i))/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()));
						
	
						// Desenhando as legendas de consumo
						
						canvas.drawText(quantitiesLabel.get(i), (float) (x0 + offset + actualTranslation + (barWidth/4.5)), (float) ((y0 - (barHeight * ((float)(quantities.get(i))/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()), teste);
						//canvas.drawText(String.valueOf((float)(bar)/maiorMedicaoAcumulada), x0 + offset + (barWidth - teste.measureText(datesLabel.get(quantities.indexOf(bar)))), (float) ((y0 - (barHeight * ((float)(bar)/maiorMedicaoAcumulada))) - 0.3*teste.getTextSize()), teste);
						
						canvas.restore();
						
						// Desenhando as legendas de datas
						
						canvas.drawText(datesLabel.get(i), x0 + offset + actualTranslation + ((barWidth - teste.measureText(datesLabel.get(i))) /2 ), y0 + teste.getTextSize() + 5, teste);
						
						offset += barMargin + barWidth;
						
						//Log.d("motion", "fim i = " + i);
						
					}
	
					this.setOnTouchListener(graficoDragListener);
					
					break;
				
				/************************************************************* FIM ZOOM_HOUR **************************************************************************************/
				
		
		}
		
		
		MIN_TRANSLATION = (MIN_TRANSLATION > 0) ? 0 : MIN_TRANSLATION; //Corre��o de Bug de Transla��o
		

	}
	
	
	private void calcularExpansao(int index) {
		
		if(currentZoom == ZOOM_HOURS) 
			return; //Vai dar zoom no que?
		
		switch(currentZoom) {
		
			case ZOOM_YEARS:
				currentZoomRefAno = dbManager.getMinYear() + index;
				currentZoom = ZOOM_MONTHS;
				break;
				
			case ZOOM_MONTHS:
				currentZoomRefMes = dbManager.getMinMonth(currentZoomRefAno) + index;
				currentZoom = ZOOM_DAYS;
				break;
				
			case ZOOM_DAYS:
				currentZoomRefDia = dbManager.getMinDay(currentZoomRefMes, currentZoomRefAno) + index;
				currentZoom = ZOOM_HOURS;
				break;
			
		}
		
		actualTranslation = 0;
		
		
		
	}
	
}