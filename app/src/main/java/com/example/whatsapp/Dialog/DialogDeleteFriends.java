package com.example.whatsapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.example.whatsapp.R;

public class DialogDeleteFriends extends Dialog {

    private int width , height;
    public TextView dialog_Title , dialog_Body;
    public Button btn_Delete , btn_Cancel;

    public DialogDeleteFriends(@NonNull Context context) {
        super(context);

        setContentView(R.layout.dialog_delete_friends);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        width = WindowManager.LayoutParams.MATCH_PARENT;
        height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setLayout(width, height);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog_Title = findViewById(R.id.dialog_title);
        dialog_Body = findViewById(R.id.dialog_body);
        btn_Delete = findViewById(R.id.btn_delete);
        btn_Cancel = findViewById(R.id.btn_cancel);
        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
