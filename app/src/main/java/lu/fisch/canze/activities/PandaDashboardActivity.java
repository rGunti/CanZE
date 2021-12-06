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

public class PandaDashboardActivity extends CanzeActivity {
    private class PandaDashboardRecord {
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
        addField(Sid.DcPowerOut);
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
