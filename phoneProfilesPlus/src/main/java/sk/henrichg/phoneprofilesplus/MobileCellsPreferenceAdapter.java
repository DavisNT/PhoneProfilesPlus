package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

public class MobileCellsPreferenceAdapter extends BaseAdapter
{
    MobileCellsPreference preference;
    //private RadioButton selectedRB;
    //int selectedRBIndex = -1;

    private LayoutInflater inflater;
    private Context context;

    public MobileCellsPreferenceAdapter(Context context, MobileCellsPreference preference)
    {
        this.preference = preference;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return preference.cellsList.size();
    }

    public Object getItem(int position) {
        return preference.cellsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    static class ViewHolder {
          TextView cellId;
          CheckBox checkBox;
          int position;
        }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // SSID to display
        MobileCellsData cellData = preference.cellsList.get(position);
        //System.out.println(String.valueOf(position));

        ViewHolder holder;
        
        View vi = convertView;
        if (convertView == null)
        {
            vi = inflater.inflate(R.layout.mobile_cells_preference_list_item, parent, false);
            holder = new ViewHolder();
            holder.cellId = (TextView)vi.findViewById(R.id.mobile_cells_pref_dlg_item_label);
            holder.checkBox = (CheckBox) vi.findViewById(R.id.mobile_cells_pref_dlg_item_checkbox);
            vi.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)vi.getTag();
        }

        String cellName = "";
        if (!cellData.name.isEmpty())
            cellName = cellData.name + " - ";
        cellName = cellName + cellData.cellId;
        if (cellData.registered)
            cellName = cellName + " (" + context.getString(R.string.event_preferences_mobile_cells_registered_cellId) + ")";
        holder.cellId.setText(cellName);

        holder.checkBox.setTag(position);
        holder.checkBox.setChecked(preference.isCellSelected(cellData.cellId));
        holder.checkBox.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                CheckBox chb = (CheckBox) v;

                int cellId = preference.cellsList.get((Integer)chb.getTag()).cellId;

                if (chb.isChecked())
                    preference.addCellId(cellId);
                else
                    preference.removeCellId(cellId);
            }
        });

        return vi;
    }

}
