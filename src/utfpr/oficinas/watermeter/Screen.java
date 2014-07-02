package utfpr.oficinas.watermeter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * @author Rafael
 *
 * Cria as telas do aplicativo baseando-se no padrão proposto no Layout
 *
 */
public class Screen {

	private String title; //Título da tela
	private RelativeLayout content; //Conteúdo da tela
	private int displayWidth, displayHeight;
	
	private GraficoMedicoes grafico;
	
	private View.OnClickListener voltarListener;
	private View.OnClickListener listenerSincronizar, listenerConsumo;
	
	
	public Screen(String title, RelativeLayout content, int displayWidth, int displayHeight) {
		super();
		this.title = title;
		this.content = content;
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
	}
	
	public Screen() {
		super();
		
	}
	
	public void setDimensions(int width, int height) {
		this.displayWidth = width;
		this.displayHeight = height;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) public void buildAndShow(final RelativeLayout tela, final Context context) {
		
		tela.removeAllViews();
		
		final LinearLayout mainLayout = new LinearLayout(context);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		
		RelativeLayout titleBar = new RelativeLayout(context);
				
		RelativeLayout.LayoutParams titleBarParams = 
				new RelativeLayout.LayoutParams(
	                    RelativeLayout.LayoutParams.MATCH_PARENT, 
	                    RelativeLayout.LayoutParams.MATCH_PARENT);
		
		RelativeLayout.LayoutParams logoTitleBarParams = 
				new RelativeLayout.LayoutParams(
	                    RelativeLayout.LayoutParams.MATCH_PARENT, 
	                    RelativeLayout.LayoutParams.MATCH_PARENT);
		
		RelativeLayout.LayoutParams btnVoltarParams = 
				new RelativeLayout.LayoutParams(
	                    RelativeLayout.LayoutParams.MATCH_PARENT, 
	                    RelativeLayout.LayoutParams.MATCH_PARENT);
		
		ImageView bgLogoSmall = new ImageView(context);
		ImageView bgBtnVoltar = new ImageView(context);
		
		bgLogoSmall.setImageDrawable(context.getResources().getDrawable(R.drawable.logo_small));
		bgBtnVoltar.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_voltar));
				
		titleBarParams.height = (int) (displayHeight/8);
		
		logoTitleBarParams.width = (int) displayWidth/2;

		btnVoltarParams.width = (int) (displayWidth/3);
		btnVoltarParams.rightMargin = (int)(displayWidth/20);
		btnVoltarParams.topMargin = (int)(titleBarParams.height/2.8);
		btnVoltarParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnVoltarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		btnVoltarParams.height = (int) (titleBarParams.height/2);
		
		bgBtnVoltar.setPadding(btnVoltarParams.height/3, btnVoltarParams.height/5, btnVoltarParams.height/3, btnVoltarParams.height/5);
				
		titleBar.addView(bgLogoSmall, logoTitleBarParams);
		titleBar.addView(bgBtnVoltar, btnVoltarParams);
		
		voltarListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setBackgroundResource(R.color.botaoClicado);
				
				mainLayout.animate().translationX(-displayWidth).withLayer().setDuration(400);
				
				new Handler().postDelayed(new Runnable() {

			        @Override
			        public void run() {
			        	
			        	buildMenuWaterMasters(tela, context);
			        	
			        }
			    }, 400);				
				
			}
		};
		
		bgBtnVoltar.setOnClickListener(voltarListener);
		
		mainLayout.addView(titleBar, titleBarParams);
		
//		Fim da barra superior
		
		TextView titulo = new TextView(context);
		titulo.setText(this.title);
		titulo.setTextColor(context.getResources().getColor(R.color.texto));
		titulo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
		titulo.setGravity(Gravity.CENTER_HORIZONTAL);
		titulo.setPadding(0, displayHeight/25, 0, 0);
		
		mainLayout.addView(titulo);
		
//		Fim Título Tela
		
		mainLayout.addView(this.content);
		
//		Adicionar tudo na tela
		
		tela.addView(mainLayout);
		
		AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(400);
        fadeIn.setFillAfter(true);
        
        mainLayout.startAnimation(fadeIn);
			
	}
	
@TargetApi(Build.VERSION_CODES.JELLY_BEAN) public final void buildMenuWaterMasters(RelativeLayout tela, Context context) {
    	
		tela.removeAllViews();
	
    	RelativeLayout containerSincronizar = new RelativeLayout(context);
        RelativeLayout containerConsumo = new RelativeLayout(context);
        RelativeLayout containerLogo = new RelativeLayout(context);

        RelativeLayout.LayoutParams halfTopo = 
                new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, 
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
        
        RelativeLayout.LayoutParams halfBottom = 
                new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, 
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
        
        RelativeLayout.LayoutParams layoutLogoMenu = 
                new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, 
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
        
        RelativeLayout.LayoutParams bgBtnSinc = 
                new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, 
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
        
        RelativeLayout.LayoutParams bgBtnCons = 
                new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, 
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
        
        RelativeLayout.LayoutParams bgLogo = 
                new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, 
                    RelativeLayout.LayoutParams.MATCH_PARENT);
        
        layoutLogoMenu.width = halfBottom.width = halfTopo.width = displayWidth;
        halfBottom.height = halfTopo.height = displayHeight/2;
                
        layoutLogoMenu.topMargin = (int) (displayHeight/3);
        
        halfBottom.topMargin = displayHeight/2;
                                  
        ImageView icon_sincronizar = new ImageView(context);
        ImageView icon_consumo = new ImageView(context);
        ImageView logoMenu = new ImageView(context);
        
        icon_sincronizar.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_sincronizar));
        icon_consumo.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_consumo));
        logoMenu.setImageDrawable(context.getResources().getDrawable(R.drawable.logo_menu));
        
        bgBtnSinc.width = displayWidth;
        bgBtnSinc.height = halfTopo.height/3;
        bgBtnSinc.topMargin = halfTopo.height/5;
        
        bgBtnCons.width = displayWidth;
        bgBtnCons.height = halfBottom.height/3;
        bgBtnCons.topMargin = (int) (halfBottom.height/2.2);
        
        bgLogo.width = displayWidth;
        bgLogo.height = displayHeight/3;
                
        tela.addView(containerSincronizar, halfTopo);
        tela.addView(containerLogo, layoutLogoMenu);
        tela.addView(containerConsumo, halfBottom);
        
        containerSincronizar.addView(icon_sincronizar, bgBtnSinc);
        containerConsumo.addView(icon_consumo, bgBtnCons);
        containerLogo.addView(logoMenu, bgLogo);
        
        //animação
        
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(400);
        fadeIn.setFillAfter(true);
        
        containerSincronizar.startAnimation(fadeIn);
        containerLogo.startAnimation(fadeIn);
        containerConsumo.startAnimation(fadeIn);
        
        //fim animação
        
        //Listeners

        configMenuListeners(context, tela);
		
		containerSincronizar.setOnClickListener(listenerSincronizar);
        containerConsumo.setOnClickListener(listenerConsumo);
    	
    }

@TargetApi(Build.VERSION_CODES.JELLY_BEAN) public final void configMenuListeners(final Context context, final RelativeLayout tela) {
		
		listenerSincronizar = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
								
				Toast.makeText(context, "Procurando Dispositivo...", Toast.LENGTH_LONG).show();
				
			}
			
		};
		
		listenerConsumo = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
								
				RelativeLayout conteudo = new RelativeLayout(context);
				
				grafico = new GraficoMedicoes(context);
				
				conteudo.addView(grafico);
				
				//Apenas Entrega 2
				
				/*ImageView graficoExemplo = new ImageView(context);
				graficoExemplo.setImageDrawable(context.getResources().getDrawable(R.drawable.grafico_exemplo));
				
				conteudoTeste.addView(graficoExemplo);
				
				View.OnClickListener listenerTeste = new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {

						Toast.makeText(context, "Gráfico Ilustrativo por enquanto...", Toast.LENGTH_LONG).show();
						
						
					}
				};
				
				graficoExemplo.setOnClickListener(listenerTeste);*/
				
				//Fim apenas entrega 2
				
				
		        final Screen consumo = new Screen("Consumo", conteudo, displayWidth, displayHeight);
		        
		        for(int i = 0; i < tela.getChildCount(); i++)
		        	tela.getChildAt(i).animate().translationX(displayWidth).withLayer().setDuration(400);
				
				new Handler().postDelayed(new Runnable() {

			        @Override
			        public void run() {
			        	
			        	consumo.buildAndShow(tela, context);
			        	
			        }
			    }, 400);
				
			}
			
		};
		
		
	}
	
}
