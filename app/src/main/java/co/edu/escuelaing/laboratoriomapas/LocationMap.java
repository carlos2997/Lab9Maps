package co.edu.escuelaing.laboratoriomapas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LocationMap extends AppCompatActivity implements View.OnClickListener{

    EditText edtName, edtDescription, edtLongitude, edtLatitude;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        edtName = findViewById(R.id.edtName);
        edtDescription = findViewById(R.id.edtDescription);
        edtLongitude = findViewById(R.id.edtLongitude);
        edtLatitude = findViewById(R.id.edtLatitude);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
    }

    private Boolean checkDouble(String number){
        try{
            Double num = Double.parseDouble(number);
            return true;
        }catch(NumberFormatException ne){
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnSend.getId()){
            String name = edtName.getText().toString();
            String description = edtDescription.getText().toString();
            String longitude = edtLongitude.getText().toString();
            String latitude = edtLatitude.getText().toString();
            if(name.length()>0 && description.length()>0 && checkDouble(longitude) && checkDouble(latitude)){
                Intent back = new Intent(LocationMap.this,MapsActivity.class);
                back.putExtra("Name", name);
                back.putExtra("Description",description);
                back.putExtra("Longitude",longitude);
                back.putExtra("Latitude",latitude);
                setResult(Activity.RESULT_OK,back);
                finish();
            }
        }
    }
}
