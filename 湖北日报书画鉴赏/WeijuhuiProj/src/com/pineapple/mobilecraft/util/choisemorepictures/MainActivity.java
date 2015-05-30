package com.pineapple.mobilecraft.util.choisemorepictures;

import java.util.ArrayList;

import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.util.logic.ImgFileListActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @QQ:794522712
 * @����: kir~song
 * @����: 2014-4-8
 * @��ӭ: ��ӭ���ҽ���.^_^
 * @ʹ�÷���: ��ѡ���ͼƬ�ļ���ݻ��� ImgsActivity.sendfiles�����½��л�ȡ,
 *          ֻ��Ҫ�ڸ÷�������ת����Ҫ��Ľ��漴��
 */
public class MainActivity extends Activity {

	ListView listView;
	ArrayList<String> listfile=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main1);
		listView=(ListView) findViewById(R.id.listView1);
		Bundle bundle= getIntent().getExtras();
	
		if (bundle!=null) {
			if (bundle.getStringArrayList("files")!=null) {
				listfile= bundle.getStringArrayList("files");
				listView.setVisibility(View.VISIBLE);
				ArrayAdapter<String> arryAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listfile);
				listView.setAdapter(arryAdapter);
			}
		}
		
	}

	public void chise(View v){
		Intent intent = new Intent();
		intent.setClass(this,ImgFileListActivity.class);
		startActivity(intent);
	}
}
