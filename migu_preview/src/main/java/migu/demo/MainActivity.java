package migu.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity {

	private Activity activity;

	private MyAdapter adapter;
	private String imei;

	private boolean DISABLE_VOTE = false;
	
	private	Button voteReset;
	
	private	int VOTE_POSITION=0;
	
	private	boolean VOTE_STATUS=false;

	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;

		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setFooterDividersEnabled(false);
		
		findViewById(R.id.voteResult).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyDialogFragment dialog = new MyDialogFragment();
				dialog.show(getFragmentManager(), "zhangphil");
			}
		});
		
		voteReset=(Button) findViewById(R.id.voteReset);
		voteReset.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DISABLE_VOTE=false;
				adapter.notifyDataSetChanged();
				voteReset.setEnabled(DISABLE_VOTE);
			}
		});
		
		voteReset.setEnabled(DISABLE_VOTE);
		
		adapter = new MyAdapter(this, -1);
		listView.setAdapter(adapter);

		Bmob.initialize(this, Utils.BMOB_APP_ID);
		imei = Utils.getIMEI(activity);


		mProgressDialog= new ProgressDialog(activity);
		mProgressDialog.setMessage("提交投票结果...");
		mProgressDialog.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
	}

	private class MyAdapter extends ArrayAdapter {

		private Context context;

		public MyAdapter(Context context, int resource) {
			super(context, resource);
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, null);

			Button button = (Button) convertView.findViewById(R.id.button);
			button.setText("查看设计方案" + (position + 1));

			final String s = (position + 1) + "";
			button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, ViewActivity.class);
					intent.putExtra(Utils.FILE_PATH_TAG, s);
					startActivity(intent);
				}
			});

			final int pos = position;
			final Button voteButton = (Button) convertView.findViewById(R.id.voteButton);
			if (DISABLE_VOTE){
				voteButton.setEnabled(false);
				if(pos==VOTE_POSITION){
					voteButton.setText("已经投票");
				}
				else
					voteButton.setText("我要投票");
			}
			else{
				voteButton.setEnabled(true);
				voteButton.setText("我要投票");
			}
			voteButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					vote(pos + 1, voteButton);
					VOTE_POSITION=pos;
				}
			});
			
			if(VOTE_STATUS){
				voteButton.setVisibility(View.VISIBLE);
			}
			else
				voteButton.setVisibility(View.INVISIBLE);

			return convertView;
		}

		@Override
		public int getCount() {
			return 5;
		}
	}





	private void vote(int pos, final Button button) {
		if(!mProgressDialog.isShowing())
			mProgressDialog.show();

		final Vote v = new Vote();
		v.setIMEI(imei);
		v.setResults(pos + "");

		v.save(this, new SaveListener() {
			@Override
			public void onSuccess() {

				if(mProgressDialog.isShowing())
					mProgressDialog.dismiss();

				// objectId = v.getObjectId();
				Toast.makeText(getApplication(), "投票成功!", Toast.LENGTH_SHORT).show();
				// Toast.makeText(getApplication(), "添加数据成功，返回objectId：" +
				// objectId, Toast.LENGTH_SHORT).show();
				disableVote(button);
			}

			@Override
			public void onFailure(int code, String msg) {
				mProgressDialog.dismiss();


				if (code == 401) {
					Toast.makeText(getApplication(), "请勿重复投票", Toast.LENGTH_SHORT).show();
					// Toast.makeText(getApplication(), "添加数据失败，错误码:" + code + "
					// msg:" + msg, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(code==9016){
					Toast.makeText(getApplication(), "网络连接失败！请务必保持网络畅通！", Toast.LENGTH_SHORT).show();
					return;
				}

				Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
			}
		});
		// } else {
		//
		// v.update(this, objectId, new UpdateListener() {
		//
		// @Override
		// public void onFailure(int code, String msg) {
		// Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
		// }
		//
		// @Override
		// public void onSuccess() {
		// Toast.makeText(getApplication(), "投票成功！", Toast.LENGTH_SHORT).show();
		// disableVote(button);
		// }
		// });
		// }
	}

	private void disableVote(Button voteButton) {
		DISABLE_VOTE = true;
		voteButton.setEnabled(false);
		voteButton.setText("已投票");

		voteReset.setEnabled(DISABLE_VOTE);
		
		adapter.notifyDataSetChanged();
	}

	/*
	 * 
	 * private void readAssets(ArrayList<String> lists) { AssetManager am =
	 * this.getAssets(); String[] path = null; try { // 列出files目录下的文件 path =
	 * am.list("plan"); } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * // 遍历assets目录下，plan文件夹下的所有文件 for (int i = 0; i < path.length; i++) {
	 * lists.add("plan/" + (i + 1) + ".png"); } }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_about) {
			//DISABLE_VOTE=false;
			//adapter.notifyDataSetChanged();
			//Toast.makeText(activity, "产品中心：张飞\nVersion 1.0.0",Toast.LENGTH_LONG).show();

			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage("产品中心：张飞\nVersion 1.0.0")
					//.setNegativeButton("取消", null)
					.setPositiveButton("确定", null)
					.setTitle("关于")
					.show();


			return true;
		}

		if (id == R.id.action_open) {
			VOTE_STATUS=true;
			adapter.notifyDataSetChanged();
			
			voteReset.setVisibility(View.VISIBLE);
			
			return	true;
		}
		
		if (id == R.id.action_close) {
			VOTE_STATUS=false;
			adapter.notifyDataSetChanged();
			
			voteReset.setVisibility(View.INVISIBLE);
			
			return	true;
		}

		return super.onOptionsItemSelected(item);
	}
}
