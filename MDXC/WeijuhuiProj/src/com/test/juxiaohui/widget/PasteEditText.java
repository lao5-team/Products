/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test.juxiaohui.widget;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView.BufferType;


/**
 * 自定义的textview，用来处理复制粘贴的消息
 *
 */
public class PasteEditText extends EditText{
    private Context context;
    
    
    public PasteEditText(Context context) {
        super(context);
        this.context = context;
    }

    public PasteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    public PasteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }
    
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
    public boolean onTextContextMenuItem(int id) {
        return super.onTextContextMenuItem(id);
    }
    
    
    
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {

        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }
    

}
