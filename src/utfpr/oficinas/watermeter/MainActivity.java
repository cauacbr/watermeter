package utfpr.oficinas.watermeter;

import java.io.IOException;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

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

		mainScreen = new Screen(getApplicationContext(), this);
		mainScreen.setDimensions(display.widthPixels, display.heightPixels);
		mainScreen.buildMenuWaterMasters(tela, getApplicationContext());
		
		mainScreen.isAllowSync(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
