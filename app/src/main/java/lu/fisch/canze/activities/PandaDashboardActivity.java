package lu.fisch.canze.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.IdRes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.classes.Sid;
import lu.fisch.canze.interfaces.DebugListener;

public class PandaDashboardActivity extends CanzeActivity implements DebugListener {
    private static class PandaDashboardRecord {
        int textRef;
        String format;

        PandaDashboardRecord(@IdRes int textRef, String format) {
            this.textRef = textRef;
            this.format = format;
        }
    }

    private final Map<String, PandaDashboardRecord> fieldMapping = Collections.unmodifiableMap(new HashMap<String, PandaDashboardRecord>() {
        {
            put(Sid.DcPowerOut, new PandaDashboardRecord(R.id.textDcPwr, "%.1f"));
            put(Sid.DcLoad, new PandaDashboardRecord(R.id.textDcLoad, "%.1f"));
            put(Sid.HvTemp, new PandaDashboardRecord(R.id.textBatteryTemperature, "%.0f°"));
            put(Sid.UserSoC, new PandaDashboardRecord(R.id.textPandaUserSoC, "%.2f"));
        }
    });

    @Override
    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        for (String field : fieldMapping.keySet()) {
            addField(field);
        }
    }

    @Override
    public void onFieldUpdateEvent(final Field field) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                if (fieldMapping.containsKey(fieldId)) {
                    PandaDashboardRecord record = fieldMapping.get(fieldId);
                    TextView view = findViewById(record.textRef);
                    view.setText(formatValue(field.getValue(), record.format));
                }
            }
        });
    }

    private String formatValue(double value, String format) {
        if (Double.isNaN(value)) {
            return "-";
        } else {
            return String.format(Locale.getDefault(), format, value);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panda_dashboard);
    }
}
