package migu.demo;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

public class Utils {
	
	
	public	static	String PLAN_SEQ_TAG="plan_seq_tag";
	public	static	String PLAN_VOTE_COUNT_TAG="plan_vote_COUNT_tag";
	//public	static	String PLAN_VOTE_TAG="plan_vote_tag";
	
	public	static int readAssetsFileCount(Activity acitvity) {
		AssetManager am = acitvity.getAssets();
		String[] path = null;
		try {
			// 列出目录下的文件
			path = am.list("plan");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return	path.length;
	}

	public	static	String BMOB_APP_ID="dc27d4421390e1d181573e1a09ba6296";
	
	public static String FILE_PATH_TAG = "file_path_tag";

	public static void toggleFullscreen(Activity activity, boolean fullScreen) {
		// fullScreen为true时全屏，否则相反

		WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();

		if (fullScreen) {
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		} else {
			attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
		}

		activity.getWindow().setAttributes(attrs);
	}
	
	
	public	static	String	getIMEI(Activity activity){
		TelephonyManager telephonyManager=(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		String imei=telephonyManager.getDeviceId();
		return	imei;
	}
}
