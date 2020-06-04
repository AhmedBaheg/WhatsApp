package com.example.whatsapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.whatsapp.R;
import com.example.whatsapp.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class DialogCreateNewGroup extends Dialog {

    private int width , height;
    public String group_name;

    public TextInputLayout create_New_Group;
    public Button btn_Cancel , btn_Create;

    private DatabaseReference rootRef;

    public DialogCreateNewGroup(@NonNull Context context) {
        super(context);

        rootRef = FirebaseDatabase.getInstance().getReference(Constants.GROUPS);

        setContentView(R.layout.dialog_create_new_group);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        width = WindowManager.LayoutParams.MATCH_PARENT;
        height = WindowManager.LayoutParams.WRAP_CONTENT;

        getWindow().setLayout(width , height);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        create_New_Group = findViewById(R.id.create_new_group);
        btn_Create = findViewById(R.id.btn_create);
        btn_Cancel = findViewById(R.id.btn_cancel);
        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public void addNewGroup(){

        if (!validationGroupName()){
            create_New_Group.requestFocus();
        }else {

            rootRef.child(group_name).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), group_name + " group is created...", Toast.LENGTH_LONG).show();
                        dismiss();
                    }    
                }
            });

        }

    }

    private final static Pattern PATTERN_NAME = Pattern.compile("[\\u0600-\\u065F\\u066A-\\u06EF\\u06FA-\\u06FFa-zA-Z ]+[\\u0600-\\u065F\\u066A-\\u06EF\\u06FA-\\u06FFa-zA-Z-_ ]");

    public boolean validationGroupName() {
        group_name = create_New_Group.getEditText().getText().toString();
        if (group_name.isEmpty()) {
            create_New_Group.setError("Please enter your group name");
            return false;
        } else if (!PATTERN_NAME.matcher(group_name).matches()) {
            create_New_Group.setError("Please enter alphabet letters only");
            return false;
        } else {
            create_New_Group.setError(null);
            return true;
        }
    }

}
