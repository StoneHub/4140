package fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cpsc41400.a4140app.MainActivity;
import com.cpsc41400.a4140app.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by young5 on 3/29/2018.
 */

public class GalleryOpenFragment {
    /*EditText name;
    Button imagepick;
    private final static int RESULT_SELECT_IMAGE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = "GalleryUtil";

    String mCurrentPhotoPath;
    File photoFile = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String strtext = getArguments().getString("edttext");
        EditText name;
        View rootView = inflater.inflate(R.layout.dialogfragment, container,
                false);
        getDialog().setTitle("Add Menu Item");
        name = (EditText)rootView.findViewById(R.id.title);
        if(strtext!=""){
            name.setText(strtext);
        }
        imagepick=(Button)rootView.findViewById(R.id.list_image);
        imagepick.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try{
                    //Pick Image From Gallery
                    System.out.println("yo whts up");
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_SELECT_IMAGE);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case RESULT_SELECT_IMAGE:

                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    try{
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA };
                        Context applicationContext = MainActivity.getContextOfApplication();
                        Cursor cursor = applicationContext.getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        System.out.println(picturePath);
                        Bitmap bitmap= BitmapFactory.decodeFile(picturePath);
                        BitmapDrawable bit_background = new BitmapDrawable(getResources(), bitmap);
                        imagepick.setBackground(bit_background);
                    }catch(Exception e){

                    }
                }else{
                    Log.i(TAG,"RESULT_CANCELED");
                    Intent returnFromGalleryIntent = new Intent();

                }
                break;
        }*/
}
