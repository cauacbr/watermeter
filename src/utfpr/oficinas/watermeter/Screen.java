package utfpr.oficinas.watermeter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Rafael
 * 
 *         Cria as telas do aplicativo baseando-se no padrão proposto no Layout
 * 
 */
public class Screen {

	private Context appContext;
	private Activity mainActivity;

	private String title; // Título da tela
	private RelativeLayout content; // Conteúdo da tela
	private int displayWidth, displayHeight;

	private GraficoMedicoes grafico;

	private View.OnClickListener voltarListener;
	private View.OnTouchListener listenerSincronizar, listenerConsumo;

	// Configuração Bluetooth
	// bluetooth
	private static final int REQUEST_ENABLE_BT = 1;

	private boolean allowSync;
	
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket socket = null;
	private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
	private BluetoothDevice ourDevice = null;
	private ConnectedThread mConnectedThread = null;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private Handler h;
	private DatabaseHandler db;
	final int RECIEVE_MESSAGE = 1;
	private StringBuilder sb = new StringBuilder();
	private ArrayList<Integer> dia, mes, ano, hora, litro, ml;

	public Screen(String title, RelativeLayout content, int displayWidth,
			int displayHeight) {
		super();
		this.title = title;
		this.content = content;
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
	}

	public Screen(Context context, Activity mainActivity) {
		super();
		this.db = new DatabaseHandler(context);
		this.appContext = context;
		this.mainActivity = mainActivity;
	}

	public void setDimensions(int width, int height) {
		this.displayWidth = width;
		this.displayHeight = height;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void buildAndShow(final RelativeLayout tela, final Context context) {

		tela.removeAllViews();

		final LinearLayout mainLayout = new LinearLayout(context);
		mainLayout.setOrientation(LinearLayout.VERTICAL);

		RelativeLayout titleBar = new RelativeLayout(context);

		RelativeLayout.LayoutParams titleBarParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		RelativeLayout.LayoutParams logoTitleBarParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		RelativeLayout.LayoutParams btnVoltarParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		ImageView bgLogoSmall = new ImageView(context);
		ImageView bgBtnVoltar = new ImageView(context);

		bgLogoSmall.setImageDrawable(context.getResources().getDrawable(
				R.drawable.logo_small));
		bgBtnVoltar.setImageDrawable(context.getResources().getDrawable(
				R.drawable.btn_voltar));

		titleBarParams.height = (int) (displayHeight / 8);

		logoTitleBarParams.width = (int) displayWidth / 2;

		btnVoltarParams.width = (int) (displayWidth / 3);
		btnVoltarParams.rightMargin = (int) (displayWidth / 20);
		btnVoltarParams.topMargin = (int) (titleBarParams.height / 2.8);
		btnVoltarParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnVoltarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		btnVoltarParams.height = (int) (titleBarParams.height / 2);

		bgBtnVoltar.setPadding(btnVoltarParams.height / 3,
				btnVoltarParams.height / 5, btnVoltarParams.height / 3,
				btnVoltarParams.height / 5);

		titleBar.addView(bgLogoSmall, logoTitleBarParams);
		titleBar.addView(bgBtnVoltar, btnVoltarParams);

		voltarListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				v.setBackgroundResource(R.color.botaoClicado);

				mainLayout.animate().translationX(-displayWidth).withLayer()
						.setDuration(400);

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

		// Fim da barra superior

		TextView titulo = new TextView(context);
		titulo.setText(this.title);
		titulo.setTextColor(context.getResources().getColor(R.color.texto));
		titulo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
		titulo.setGravity(Gravity.CENTER_HORIZONTAL);
		titulo.setPadding(0, displayHeight / 25, 0, 0);

		mainLayout.addView(titulo);

		// Fim Título Tela

		mainLayout.addView(this.content);

		// Adicionar tudo na tela

		tela.addView(mainLayout);

		AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(400);
		fadeIn.setFillAfter(true);

		mainLayout.startAnimation(fadeIn);

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public final void buildMenuWaterMasters(RelativeLayout tela, Context context) {

		tela.removeAllViews();

		RelativeLayout containerSincronizar = new RelativeLayout(context);
		RelativeLayout containerConsumo = new RelativeLayout(context);
		RelativeLayout containerLogo = new RelativeLayout(context);

		RelativeLayout.LayoutParams halfTopo = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		RelativeLayout.LayoutParams halfBottom = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		RelativeLayout.LayoutParams layoutLogoMenu = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		RelativeLayout.LayoutParams bgBtnSinc = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		RelativeLayout.LayoutParams bgBtnCons = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		RelativeLayout.LayoutParams bgLogo = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		layoutLogoMenu.width = halfBottom.width = halfTopo.width = displayWidth;
		halfBottom.height = halfTopo.height = displayHeight / 2;

		layoutLogoMenu.topMargin = (int) (displayHeight / 3);

		halfBottom.topMargin = displayHeight / 2;

		ImageView icon_sincronizar = new ImageView(context);
		ImageView icon_consumo = new ImageView(context);
		View logoMenu = new BtnLogoMenuPrincipal(context, displayWidth,
				(int) (displayHeight / 3));

		icon_sincronizar.setImageDrawable(context.getResources().getDrawable(
				R.drawable.btn_sincronizar));
		icon_consumo.setImageDrawable(context.getResources().getDrawable(
				R.drawable.btn_consumo));

		bgBtnSinc.width = displayWidth;
		bgBtnSinc.height = halfTopo.height / 3;
		bgBtnSinc.topMargin = halfTopo.height / 5;

		bgBtnCons.width = displayWidth;
		bgBtnCons.height = halfBottom.height / 3;
		bgBtnCons.topMargin = (int) (halfBottom.height / 2.2);

		bgLogo.width = displayWidth;
		bgLogo.height = displayHeight / 3;

		tela.addView(containerSincronizar, halfTopo);
		tela.addView(containerLogo, layoutLogoMenu);
		tela.addView(containerConsumo, halfBottom);

		containerSincronizar.addView(icon_sincronizar, bgBtnSinc);
		containerConsumo.addView(icon_consumo, bgBtnCons);
		containerLogo.addView(logoMenu, bgLogo);

		// animação

		AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(400);
		fadeIn.setFillAfter(true);

		containerSincronizar.startAnimation(fadeIn);
		containerLogo.startAnimation(fadeIn);
		containerConsumo.startAnimation(fadeIn);

		// fim animação

		// Listeners

		configMenuListeners(context, tela);

		containerSincronizar.setOnTouchListener(listenerSincronizar);
		containerConsumo.setOnTouchListener(listenerConsumo);

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public final void configMenuListeners(final Context context,
			final RelativeLayout tela) {

		listenerSincronizar = new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if ((event.getY() > displayHeight / 3) || (allowSync == false)) {
					return false;
				}

				v.playSoundEffect(SoundEffectConstants.CLICK);

				// Configurar Bluetooth a partir daqui
				
				IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
				filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
				filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

				allowSync = false;
				
				if (btAdapter == null && ourDevice == null) {
					btAdapter = BluetoothAdapter.getDefaultAdapter();
					mainActivity.registerReceiver(ActionFoundReceiver, filter);
					CheckBTState();
				} else if (btAdapter != null && ourDevice == null) {
					mainActivity.registerReceiver(ActionFoundReceiver, filter);
					CheckBTState();
				} else if (btAdapter != null && ourDevice != null
						&& socket == null) {
					try {
						sincronizar();
					} catch (IOException e) {
						ourDevice = null;
						socket = null;
					}
				} else if (btAdapter != null && ourDevice != null
						&& socket != null) {
					dados();
				}

				// Fim configurar Bluetooth

				return false;
			}

		};

		listenerConsumo = new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getY() < displayHeight / 6) {
					return false;
				}

				v.playSoundEffect(SoundEffectConstants.CLICK);

				RelativeLayout conteudo = new RelativeLayout(context);

				grafico = new GraficoMedicoes(context, displayHeight / 18);

				conteudo.addView(grafico);

				final Screen consumo = new Screen("Consumo", conteudo,
						displayWidth, displayHeight);

				for (int i = 0; i < tela.getChildCount(); i++)
					tela.getChildAt(i).animate().translationX(displayWidth)
							.withLayer().setDuration(400);

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {

						consumo.buildAndShow(tela, context);

					}
				}, 400);

				return false;

			}

		};

	}

	public void CheckBTState() {
		if (btAdapter == null) {
			return;
		} else {
			if (btAdapter.isEnabled()) {
				if (ourDevice == null) {
					Toast.makeText(appContext, "Procurando dispositivo",
							Toast.LENGTH_SHORT).show();
					btAdapter.startDiscovery();
				}
			} else {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				mainActivity.startActivityForResult(enableBtIntent,
						REQUEST_ENABLE_BT);
			}
		}
	}

	private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if ("HC-05".equalsIgnoreCase(device.getName())) {
					ourDevice = device;
					btAdapter.cancelDiscovery();
					try {
						sincronizar();
					} catch (IOException e) {
						ourDevice = null;
						socket = null;
						e.printStackTrace();
					}
				}
				btDeviceList.add(device);
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				if (ourDevice == null) {
					Toast.makeText(appContext, "Não foi possível estabelecer a conexão", Toast.LENGTH_SHORT).show();
					
					allowSync = false;
				}
			}
		}
	};

	@SuppressLint("HandlerLeak")
	public void callBluetooth() {
		// out = (TextView) findViewById(R.id.out);
		// sincronizar = (Button) findViewById(R.id.sincronizar);
		// consumo = (Button) findViewById(R.id.consumo);
		
		h = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case RECIEVE_MESSAGE:
					byte[] readBuf = (byte[]) msg.obj;
					String strIncom = new String(readBuf, 0, msg.arg1);
					sb.append(strIncom);
					int endOfLineIndex = sb.indexOf("\r\n");
					if (endOfLineIndex > 0) {
						String sbprint = sb.substring(0, endOfLineIndex);
						sb.delete(0, sb.length());
						if (sbprint.substring(0, 1).equalsIgnoreCase("H")) {
							//int tam = Integer.valueOf(sbprint.substring(1, 3));
							int qnt = Integer.valueOf(sbprint.substring(3, 7));
							int i = 0;
							ano = new ArrayList<Integer>();
							mes = new ArrayList<Integer>();
							dia = new ArrayList<Integer>();
							hora = new ArrayList<Integer>();
							litro = new ArrayList<Integer>();
							ml = new ArrayList<Integer>();
							try {
								for (i = 0; i < qnt; i++) {
									if (sbprint.substring((7 + i * 16),
											(8 + i * 16)).equalsIgnoreCase("D")) {
										Log.d("t4", sbprint.substring(
												(7 + (i * 16)), (8 + (i * 16))));
										ano.add(Integer.valueOf(sbprint
												.substring((8 + (i * 16)),
														(12 + (i * 16)))));
										mes.add(Integer.valueOf(sbprint
												.substring((12 + (i * 16)),
														(14 + (i * 16)))));
										dia.add(Integer.valueOf(sbprint
												.substring((14 + (i * 16)),
														(16 + (i * 16)))));
										hora.add(Integer.valueOf(sbprint
												.substring((16 + (i * 16)),
														(18 + (i * 16)))));
										litro.add(Integer.valueOf(sbprint
												.substring((18 + (i * 16)),
														(22 + (i * 16)))));
										ml.add(Integer.valueOf(sbprint
												.substring((22 + (i * 16)),
														(23 + (i * 16)))));
									}
								}
								if (sbprint.substring((23 + ((qnt - 1) * 16)),
										(24 + ((qnt - 1) * 16)))
										.equalsIgnoreCase("T")) {
									for (int j = 0; j < qnt; j++) {
										db.addContact(new Medicao(dia.get(j),
												mes.get(j), ano.get(j), hora
														.get(j), litro.get(j),
												ml.get(j)));
									}
								}
							} catch (Exception e) {
								/*
								 * Toast.makeText(appContext, e.getMessage(),
								 * Toast.LENGTH_SHORT) .show();
								 */
								Toast.makeText(appContext, "Tente novamente",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
					break;
				}
			};
		};

	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void sincronizar() throws IOException {
		socket = createBluetoothSocket(ourDevice);
		try {
			socket.connect();
		} catch (IOException e) {
			Toast.makeText(appContext,
					"Não foi possível eSstabelecer a conexão",
					Toast.LENGTH_SHORT).show();
		}
		if (mConnectedThread == null) {
			mConnectedThread = new ConnectedThread(socket, appContext, h);
			mConnectedThread.start();
		}

		if (socket.isConnected()) {
			dados();
		}
	}

	public void dados() {
		Toast.makeText(appContext, "Sincronizando", Toast.LENGTH_SHORT).show();
		mConnectedThread.write("1");
		
		allowSync = true;
		
		Toast.makeText(appContext, "Sincronizado", Toast.LENGTH_SHORT).show();
	}

	private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
			throws IOException {
		if (Build.VERSION.SDK_INT >= 10) {
			try {
				Method m = device.getClass().getMethod("createRfcommSocket",
						new Class[] { int.class });
				return (BluetoothSocket) m.invoke(device, 1);
			} catch (Exception e) {
				Toast.makeText(appContext, e.getMessage(), Toast.LENGTH_SHORT)
						.show();
			}
		}
		return device.createRfcommSocketToServiceRecord(MY_UUID);
	}
	
	public BluetoothSocket getSocket() {
		return this.socket;
	}
	
	public BluetoothAdapter getBtAdapter() {
		return this.btAdapter;
	}
	
	public BroadcastReceiver getActionFoundReceiver() {
		return this.ActionFoundReceiver;
	}
	
	public void isAllowSync(boolean value) {
		allowSync = value;
	}

}
