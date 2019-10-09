package hu.dkrmg.a13pb.projectdusza;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends Activity {

  TextView text;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    text = findViewById(R.id.textout);
  }
  public void requestClick(View v) {
    JSONObject json = new JSONObject();
    text.setText("a");
  }
}
