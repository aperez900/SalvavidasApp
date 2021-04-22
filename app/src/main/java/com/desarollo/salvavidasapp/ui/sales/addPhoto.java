package com.desarollo.salvavidasapp.ui.sales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.desarollo.salvavidasapp.R;

public class addPhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        TextView tv_id_producto = findViewById(R.id.tv_id_producto);

        Intent intent = getIntent();
        if (intent != null){
            Bundle extras = getIntent().getExtras();
            String idProducto = extras.getString("idProducto");
            tv_id_producto.setText(idProducto);
        }
    }
}