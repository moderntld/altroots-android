package io.github.otakuchiyan.dnsman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class DnsEditActivity extends Activity {
    private DnsmanCore dnsmanCore;
    private DnsEditText dns1;
    private DnsEditText dns2;
    private String mPrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_dns_edit);

        dnsmanCore = new DnsmanCore(this);

        Intent i = getIntent();
        mPrefix = i.getStringExtra("prefix");
        setTitle(i.getStringExtra("label"));
        setEditText(mPrefix);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.server_strings, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CharSequence key = (CharSequence) adapterView.getItemAtPosition(i);
                String[] ips = DnsmanCore.server2ipMap.get(key.toString());
                dns1 = (DnsEditText) findViewById(R.id.dnsEditText1);
                dns2 = (DnsEditText) findViewById(R.id.dnsEditText2);
                dns1.setText(ips[0]);
                dns2.setText(ips[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setEditText(String prefix){
        dns1 = (DnsEditText) findViewById(R.id.dnsEditText1);
        dns2 = (DnsEditText) findViewById(R.id.dnsEditText2);

        String[] dnsEntry = dnsmanCore.getDnsByKeyPrefix(prefix);

        dns1.setText(dnsEntry[0]);

        if(PreferenceManager.getDefaultSharedPreferences(this).
                getString(ValueConstants.KEY_PREF_METHOD, ValueConstants.METHOD_VPN)
                .equals(ValueConstants.METHOD_IPTABLES)){
            dns2.setHint(R.string.hint_no_available);
            dns2.setEnabled(false);
        }else{
            dns2.setText(dnsEntry[1]);
        }
    }

    public void onApplyButtonClick(View v){
        String[] dnsEntry = new String[2];
        dnsEntry[0] = dns1.getText().toString();
        dnsEntry[1] = dns2.getText().toString();

        if("".equals(dnsEntry[0]) && "".equals(dnsEntry[1])){
            Toast.makeText(this, R.string.toast_no_dns, Toast.LENGTH_SHORT).show();
            return;
        }

        saveDnsEntry();
        ExecuteIntentService.startActionByString(this, dnsmanCore.getDnsByKeyPrefix(mPrefix));
    }

    public void onOkButtonClick(View v){
        saveDnsEntry();
        finish();
    }

    private void saveDnsEntry(){
        String[] dnsEntry = new String[2];
        dnsEntry[0] = dns1.getText().toString();
        dnsEntry[1] = dns2.getText().toString();
        dnsmanCore.putDnsByKeyPrefix(mPrefix, dnsEntry);
        setResult(RESULT_OK);
    }

    public void onClearButtonClick(View v){
        String[] dnsEntry = new String[2];
        dnsEntry[0] = "";
        dnsEntry[1] = "";
        dns1.setText("");
        dns2.setText("");
        dnsmanCore.putDnsByKeyPrefix(mPrefix, dnsEntry);
        Toast.makeText(this, R.string.toast_dns_cleared, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    public void onCancelButtonClick(View v){
        finish();
    }
}
