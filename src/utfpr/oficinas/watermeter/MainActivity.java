package utfpr.oficinas.watermeter;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	// interface
	private RelativeLayout tela;
	private DisplayMetrics display;
	private Screen mainScreen;
		
	//Bluetooth
	private PowerManager powerManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Não permitir rotação de tela
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// ContentView
		setContentView(R.layout.activity_main);

		// Preparando a tela

		display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);

		tela = (RelativeLayout) findViewById(R.id.MainLayout);

		tela.setBackgroundResource(R.color.fundoTela);

		try {
			
			mainScreen = new Screen(getApplicationContext(), this, new DatabaseHandler(this));
			mainScreen.setDimensions(display.widthPixels, display.heightPixels);
			mainScreen.buildMenuWaterMasters(tela, getApplicationContext());
			
			mainScreen.isAllowSync(true);
			
			boolean firstboot = getSharedPreferences("BOOT_PREF", MODE_PRIVATE)
					.getBoolean("firstboot", true);
			if (firstboot) {
				getSharedPreferences("BOOT_PREF", MODE_PRIVATE).edit()
						.putBoolean("firstboot", false).commit();
				mainScreen.getDbHandler().fakeAdd();
			}
		} catch (Exception e) {
			Log.d("Main_activity", e.getMessage());
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == 1) && (resultCode == 0)) {
			Toast.makeText(getApplicationContext(), "Sincronização cancelada",
					Toast.LENGTH_SHORT).show();
			
			mainScreen.isAllowSync(true);
			
		}
		if ((requestCode == 1) && (resultCode == -1)) {
			mainScreen.CheckBTState();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		if (powerManager.isScreenOn()) {
			if (mainScreen.getSocket() != null) {
				try {
					mainScreen.getSocket().close();
				} catch (IOException e2) {
					Toast.makeText(getApplicationContext(), e2.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mainScreen.getSocket() != null) {
			try {
				mainScreen.getSocket().close();
			} catch (IOException e2) {
				Toast.makeText(getApplicationContext(), e2.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		}
		if (mainScreen.getBtAdapter() != null) {
			mainScreen.getBtAdapter().cancelDiscovery();
			this.unregisterReceiver(mainScreen.getActionFoundReceiver());
		}
	}

}
