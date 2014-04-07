package ro.pub.cs.pdsd.dblab07.app;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private Context context;
    private Database database;

    private Spinner profSpinner;
    private List<Profesor> profesori;
    private ArrayAdapter<Profesor> profesorArrayAdapter;

    private Spinner courseSpinner;
    private List<Curs> cursuri;
    private ArrayAdapter<Curs> cursArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;
        database = Database.getInstance(this);

        // Add professor
        Button adaugaProf = (Button)findViewById(R.id.adaugaProf);
        adaugaProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText prof = (EditText)findViewById(R.id.profText);
                String name = prof.getText().toString().trim();
                prof.setText("");
                if (name == null || name.length() == 0)
                    return;

                if (database.insertProfesor(name)) {
                    profesori.add(database.getProf(name));
                    profesorArrayAdapter.notifyDataSetChanged();
                    profSpinner.setSelected(false);
                }
            }
        });

        // Add course
        Button addCourse = (Button)findViewById(R.id.adaugaCurs);
        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText course = (EditText)findViewById(R.id.cursText);
                String name = course.getText().toString().trim();
                course.setText("");
                if (name == null || name.length() == 0)
                    return;

                if (database.insertCurs(name)) {
                    cursuri.add(database.getCourse(name));
                    cursArrayAdapter.notifyDataSetChanged();
                    courseSpinner.setSelected(false);
                }
            }
        });

        profSpinner = (Spinner)findViewById(R.id.profList);
        profesori = database.getProfessors(0);
        profesorArrayAdapter = new ArrayAdapter<Profesor>(this, R.layout.support_simple_spinner_dropdown_item, profesori);
        profSpinner.setAdapter(profesorArrayAdapter);

        profSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Profesor profesor = (Profesor)adapterView.getItemAtPosition(i);
                List<Curs> cursuri = database.getCourses(profesor.getId());
                String s = "";
                for (Curs curs : cursuri) {
                    s = s + curs.toString() + "\n";
                }

                TextView textView = (TextView)findViewById(R.id.cursuriPtProf);
                textView.setText(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        courseSpinner = (Spinner)findViewById(R.id.cursList);
        cursuri = database.getCourses(0);
        cursArrayAdapter = new ArrayAdapter<Curs>(this, R.layout.support_simple_spinner_dropdown_item, cursuri);
        courseSpinner.setAdapter(cursArrayAdapter);

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Curs curs = (Curs)adapterView.getItemAtPosition(i);
                List<Profesor> profesori = database.getProfessors(curs.getId());
                String s = "";
                for (Profesor p : profesori) {
                    s = s + p.toString() + "\n";
                }

                TextView textView = (TextView)findViewById(R.id.profiPtCurs);
                textView.setText(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Open the dialog
        Button openDialog = (Button)findViewById(R.id.deschideAdaugaAsociere);
        openDialog.setOnClickListener(this);

        Button clearDb = (Button)findViewById(R.id.clearDb);
        clearDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.clearDb();

                profesori.clear();
                profesorArrayAdapter.notifyDataSetChanged();

                cursuri.clear();
                cursArrayAdapter.notifyDataSetChanged();

                ((TextView)findViewById(R.id.profiPtCurs)).setText("");
                ((TextView)findViewById(R.id.cursuriPtProf)).setText("");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        final Spinner pSpinner = (Spinner)dialogView.findViewById(R.id.profSpinner);
        pSpinner.setAdapter(this.profesorArrayAdapter);

        final Spinner cSpinner = (Spinner)dialogView.findViewById(R.id.cursSpinner);
        cSpinner.setAdapter(this.cursArrayAdapter);

        Button button = (Button)dialogView.findViewById(R.id.asociaza);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profesor p = (Profesor)pSpinner.getSelectedItem();
                Curs c = (Curs)cSpinner.getSelectedItem();

                if (p != null && c != null) {
                    database.insertAsociere(p.getId(), c.getId());
                }
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}
