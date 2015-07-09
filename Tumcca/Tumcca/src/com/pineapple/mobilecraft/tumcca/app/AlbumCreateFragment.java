package com.pineapple.mobilecraft.tumcca.app;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;

/**
 * Created by yihao on 15/7/3.
 */
public class AlbumCreateFragment extends DialogFragment {
    EditText mEtxTitle;
    EditText mEtxDesc;
    Button mBtnCreate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(android.app.DialogFragment.STYLE_NO_TITLE, R.style.my_dialog_activity_style);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null)
        {
            //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        View view = inflater.inflate(R.layout.fragment_album_create, container, false);
        mEtxTitle = (EditText)view.findViewById(R.id.editText_title);


        mEtxDesc = (EditText)view.findViewById(R.id.editText_description);
        mBtnCreate = (Button)view.findViewById(R.id.button_create);
        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setTitle("正在创建专辑");
                dialog.show();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Album album = new Album(
                                mEtxTitle.getText().toString(), mEtxDesc.getText().toString(), -1);
                        WorksServer.uploadAlbum(UserManager.getInstance().getCurrentToken(), album
                        );
                        WorksManager.getInstance().setLatestAlbum(album);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.currentThread().sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                                dismiss();
                            }
                        });
                    }
                });
                t.start();

            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mEtxTitle.getEditableText().length()>0&&mEtxDesc.getEditableText().length()>0){
                    mBtnCreate.setEnabled(true);
                }
                else{
                    mBtnCreate.setEnabled(false);
                }
            }
        };
        mEtxTitle.addTextChangedListener(textWatcher);
        mEtxDesc.addTextChangedListener(textWatcher);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(true);
            int fullWidth = getDialog().getWindow().getAttributes().width;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                fullWidth = size.x;
            } else {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                fullWidth = display.getWidth();
            }

            final int padding = getResources().getDimensionPixelOffset(R.dimen.dialogfragment_marginleft);

            int w = fullWidth;
            int h = getDialog().getWindow().getAttributes().height;

            getDialog().getWindow().setLayout(w, h);
        }
    }
}
