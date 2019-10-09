package hu.dkrmg.a13pb.projectdusza;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends Activity {

  TextView szoveg;
  EditText bemenet;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    szoveg = findViewById(R.id.textout);
    bemenet = findViewById(R.id.textin);
  }
  public void requestClick(View v) {
    szoveg.setText(bemenet.getText());
  }
}
